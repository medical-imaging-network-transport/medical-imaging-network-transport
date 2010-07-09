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
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Timer;

import org.nema.medical.mint.dcmimport.daemon.CheckResponsesTask;
import org.nema.medical.mint.dcmimport.daemon.DirTraverseTask;

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
        if( args.length != 4 ) {
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
        final MINTSender mintSender = new MINTSender(serverURI, useXMLNotGPB);
        final ProcessImportDir importProcessor = new ProcessImportDir(inputDir, mintSender);

        if (runOnceOnly) {
            try {
                //Run the importing process against the specified directory
                //This will process and send the resulting MINT message to the MINTServer
                importProcessor.run();

                //Wait for studies to finish processing on the server; time out after trying for about 30 seconds
                for (int i = 0; i < 10; ++i) {
                    //Wait 3 seconds before hitting the server for updates
                    Thread.sleep(3000);

                    if (mintSender.handleResponses()) {
                        break;
                    }
                }
            } catch( Exception e ) {
                System.err.println("An exception occurred while processing the files in the input directory.");
                e.printStackTrace();
            }
        } else {
            final Timer dirTraverseTimer = new Timer("DirTraverse", true);
            final Timer checkResponsesTimer = new Timer("CheckResponses", true);

            checkResponsesTimer.schedule(new CheckResponsesTask(mintSender), 3000, 3000);
            dirTraverseTimer.schedule(new DirTraverseTask(dirTraverseTimer, importProcessor), 0);

            //TODO Add timer and code to delete processed directories

            try {

                Thread.sleep(Long.MAX_VALUE);
            } catch (final InterruptedException e) {
                //Fall through and terminate - user may have pressed Ctrl-C
            }
        }
    }

    private static void printUsage() {
        System.err.println("Usage: DICOM2MINTImport xml|gpb [DIRECTORY] [URL]");
        System.err.println("Converts any and all DICOM files in the specified directory into the MINT standard");
        System.err.println("format and sends Create Study messages for each study found in the directory");
        System.err.println("to the MINTServer defined by the input URL.");
        System.err.println("  Inputs:");
        System.err.println("    xml|gpb      specify either option to define the DICOM metadata output format");
        System.err.println("    [DIRECTORY]  the file path where DICOM files are located");
        System.err.println("    [URL]        the Base URL path of the MINTServer.");
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
