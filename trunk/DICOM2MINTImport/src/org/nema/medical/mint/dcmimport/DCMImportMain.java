/*
 *   Copyright 2010 MINT Working Group
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package org.nema.medical.mint.dcmimport;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author Scott DeJarnette
 */
public class DCMImportMain {

    public static void main(final String[] args) {
        //Validate inputs before doing any processing
        //Make sure we have arguments and someone isn't looking for usage
        if( args.length == 0 ) {
            printUsage();
            return;
        } else if (args.length > 0) {
            if( args[0].startsWith("-h") || args[0].startsWith("--h") || args[0].equals("/?") ) {
                printUsage();
                return;
            }
        }

        //Too many or not enough arguments
        if( args.length != 4 && args.length != 5) {
            System.err.println("An invalid number of arguments was specified: once|daemon, xml|gpb, [DIRECTORY], and [URL] must be defined.\n");
            printUsage();
            return;
        }

        //Run once or as a daemon
        final boolean runOnceOnly;
        if (args[0].equalsIgnoreCase("once")) {
            runOnceOnly = true;
        } else if (args[0].equalsIgnoreCase("daemon")) {
            runOnceOnly = false;
        } else {
            System.err.println("Invalid format option (" + args[0] + "): specify either 'once' or 'daemon'.");
            printUsage();
            return;
        }

        //Get the metadata output format
        final boolean useXMLNotGPB;
        if (args[1].equalsIgnoreCase("xml")) {
            useXMLNotGPB = true;
        } else if (args[1].equalsIgnoreCase("gpb")) {
            useXMLNotGPB = false;
        } else {
            System.err.println("Invalid format option (" + args[1] + "): specify either xml or gpb.");
            printUsage();
            return;
        }

        //Attempt to set the DIRECTORY input as a file
        final File inputDir = new File(args[2]);
        if( !inputDir.exists()) {
            System.err.println("Failed to create a file object for the input directory. Check that the directory specified exists.\n");
            printUsage();
            return;
        }

        //Get the Server URL and make sure it is a valid HTTP address
        String serverURL = args[3];
        if( !serverURL.startsWith("http://") ) {
            serverURL = "http://" + serverURL;
        }

        final boolean deletePhysicalFiles;
        if (args.length == 5) {
            if (!args[4].equals("nodelete")) {
                System.err.println("Fifth argument, if specified, must be 'nodelete'");
                printUsage();
                return;
            }
            deletePhysicalFiles = false;
        } else {
            deletePhysicalFiles = true;
        }
        //Now check that the server exists
        if( !checkServerExists(serverURL) ) {
            System.err.println("Could not connect to the input MINTServer URL (" + serverURL + ")");
            System.err.println("Check that the specified URL is correct and currently active.\n");
            printUsage();
            return;
        }

        final URI serverURI;
        try {
            serverURI = new URI(serverURL);
        } catch (final URISyntaxException e) {
            System.err.println("Invalid server URL \"" + serverURL + "\": " + e.toString());
            return;
        }

        //Finished validating inputs, call the actual processing code
        //Create an instance of the Directory Processing Class
        final ProcessImportDir importProcessor = new ProcessImportDir(inputDir, serverURI, useXMLNotGPB, deletePhysicalFiles);

        if (runOnceOnly) {
            try {
                //Run the importing process against the specified directory
                //This will process and send the resulting MINT message to the MINTServer
                importProcessor.processDir();

                //Wait for studies to finish processing on the server
                for (;;) {
                    //Wait 3 seconds before hitting the server for updates
                    Thread.sleep(3000);

                    if (importProcessor.handleResponses()) {
                        break;
                    }
                }
            } catch( Exception e ) {
                System.err.println("An exception occurred while processing the files in the input directory.");
                e.printStackTrace();
            }
        } else {
            final ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(2);
            final Runnable checkResponsesTask = new Runnable() {
                public void run() {
                    try {
                        importProcessor.handleResponses();
                    } catch(final IOException e) {
                        System.err.println("An exception occurred while checking for upload responses from the server:");
                        e.printStackTrace();
                    }
                }
            };
            executor.scheduleWithFixedDelay(checkResponsesTask, 3, 3, TimeUnit.SECONDS);

            final Runnable dirTraverseTask = new Runnable() {
                public void run() {
                    importProcessor.processDir();
                }
            };
            executor.scheduleWithFixedDelay(dirTraverseTask, 0, 3, TimeUnit.SECONDS);

            try {
                executor.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
            } catch (final InterruptedException e) {
                System.err.println("Shutting down...");
                if (!executor.isShutdown()) {
                    executor.shutdown();
                }
                //Fall through and terminate - user may have pressed Ctrl-C
            }
        }
    }

    private static void printUsage() {
        System.err.println("Usage: DICOM2MINTImport once|daemon xml|gpb {DIRECTORY} {URL} [nodelete]");
        System.err.println("Converts any and all DICOM files in the specified directory into the MINT standard");
        System.err.println("format and sends Create Study messages for each study found in the directory");
        System.err.println("to the MINTServer defined by the input URL.");
        System.err.println("  Inputs:");
        System.err.println("    once|daemon  specify either option to traverse the directory just once or to");
        System.err.println("                 keep monitoring the directory as a daemon process");
        System.err.println("    xml|gpb      specify either option to define the DICOM metadata output format");
        System.err.println("    {DIRECTORY}  the file path where DICOM files are located");
        System.err.println("    {URL}        the Base URL path of the MINTServer.");
        System.err.println("    nodelete     an optional argument specifying not to delete DICOM files which" +
                           "                 were sucessfully uploaded to the server.");
        System.err.println();
        System.err.println("The DICOM file/directory format expected by this tool is that generated by the");
        System.err.println("dcmrcv tool of the dcm4che2 toolkit. All files under the specified directory and");
        System.err.println("subdirectories will be interpreted as DICOM, except those with a name ending in '.part'.");
        System.err.println("The tool prints a message for each file where reading it as DICOM does not succeed.");
    }

    private static boolean checkServerExists(String urlName) {
        urlName = urlName.trim();
        if (!urlName.endsWith("/")) {
            urlName += '/';
        }
        try {
            HttpURLConnection.setFollowRedirects(false);
            HttpURLConnection con = (HttpURLConnection) new URL(urlName).openConnection();
            con.setRequestMethod("HEAD");
            return (con.getResponseCode() == HttpURLConnection.HTTP_OK);
        } catch (Exception e) {
            System.err.println(e.toString());
            return false;
        }
    }
}
