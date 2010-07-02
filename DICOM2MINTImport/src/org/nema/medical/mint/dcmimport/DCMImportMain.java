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
import java.net.URL;

public class DCMImportMain {

    /**
     * @param args
     */
    public static void main(String[] args) {
        //Validate inputs before doing any processing
        //Make sure we have arguments and someone isn't looking for usage
        if( args.length == 0 )
        {
            printUsage();
            return;
        }
        else if (args.length > 0)
        {
            if( args[0].toString().startsWith("-h") || args[0].startsWith("--h") || args[0].toString().equals("/?") )
            {
                printUsage();
                return;
            }
        }

        //Too many or not enough arguments
        if( args.length != 3 )
        {
            System.err.println("An invalid number of arguments was specified: xml|gpb, [DIRECTORY], and [URL] must be defined.\n");
            printUsage();
            return;
        }

        //Get the metadata output format
        final boolean useXMLNotGPB;
        final String formatString = args[0];
        if (formatString.equalsIgnoreCase("xml")) {
            useXMLNotGPB = true;
        } else if (formatString.equalsIgnoreCase("gpb")) {
            useXMLNotGPB = false;
        } else {
            System.err.println("Invalid format option (" + formatString + "): specify either xml or gpb.");
            printUsage();
            return;
        }

        //Attempt to set the DIRECTORY input as a file
        final File inputDir = new File(args[1].toString());
        if( !inputDir.exists())
        {
            System.err.println("Failed to create a file object for the input directory. Check that the directory specified exists.\n");
            printUsage();
            return;
        }

        //Get the Server URL and make sure it is a valid HTTP address
        String serverURL = args[2].toString();
        if( !serverURL.startsWith("http://") )
        {
            serverURL = "http://" + serverURL;

        }

        //Now check that the server exists
        if( !checkServerExists(serverURL) )
        {
            System.err.println("Could not connect to the input MINTServer URL (" + serverURL + ")");
            System.err.println("Check that the specified URL is correct and currently active.\n");
            printUsage();
            return;
        }

        //Finished validating inputs, call the actual processing code
        try
        {
            //Create an instance of the Directory Processing Class
            final MINTSend mintSender = new MINTSender(new URI(serverURL), useXMLNotGPB);
            final ProcessImportDir importProcessor = new ProcessImportDir(inputDir, mintSender);

            //Run the importing process against the specified directory
            //This will process and send the resulting MINT message to the MINTServer
            importProcessor.run();
        }
        catch( Exception e )
        {
            System.err.println("An exception occurred while processing the files in the input directory.");
            System.err.println(e.toString());
            e.printStackTrace();
        }

    }

    private static void printUsage()
    {
        System.err.println("Usage: DICOM2MINTImport xml|gpb [DIRECTORY] [URL]");
        System.err.println("Converts any and all DICOM files in the specified directory into the MINT standard");
        System.err.println("format and sends Create Study messages for each study found in the directory");
        System.err.println("to the MINTServer defined by the input URL.");
        System.err.println("  Inputs:");
        System.err.println("    xml|gpb      specify either option to define the DICOM metadata output format");
        System.err.println("    [DIRECTORY]  the file path where DICOM files are located");
        System.err.println("    [URL]        the Base URL path of the MINTServer.");

    }

    private static boolean checkServerExists(String URLName){
        try
        {
            HttpURLConnection.setFollowRedirects(false);
            HttpURLConnection con = (HttpURLConnection) new URL(URLName).openConnection();
            con.setRequestMethod("HEAD");
            return (con.getResponseCode() == HttpURLConnection.HTTP_OK);
        }
        catch (Exception e)
        {
            System.err.println(e.toString());
            return false;
        }
    }

}
