package org.nema.medical.mint.dcmimport;

import java.io.*;
import java.net.*;

public class DCMImportMain {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//Validate inputs before doing any processing
		//Make sure we have arguments and someone isn't looking for usage
		if( args.length == 0 )
		{
			PrintUsage();
			return;
		}
		else if (args.length > 0)
		{
			if( args[0].toString().startsWith("-h") || args[0].startsWith("--h") || args[0].toString().equals("/?") )
			{
				PrintUsage();
				return;
			}			
		}
		
		//Too many or not enough arguments
		if( args.length != 2 )
		{
			System.out.println("An invalid number of arguments was specified, [DIRECTORY] and [URL] must be defined.\n");
			PrintUsage();
			return;
		}

		//Attempt to set the DIRECTORY input as a file
		File inputDir;
		try
		{
			inputDir = new File(args[0].toString());
			if( !inputDir.exists())
			{
				Exception noExist = new Exception("File path is a valid format but the path does not exist on this machine.");
				throw noExist;
				
			}
			
		}
		catch(Exception e)
		{
			System.out.println(e.toString());
			System.out.println("Failed to create a file object for the input directory. Check that the directory specified exists.\n");
			PrintUsage();
			return;
		}
		
		//Get the Server URL and make sure it is a valid HTTP address
		String serverURL = args[1].toString();
		if( !serverURL.startsWith("http://") )
		{
			serverURL = "http://" + serverURL;
			
		}
		
		//Now check that the server exists
		if( !CheckServerExists(serverURL) )
		{
			System.out.println("Could not connect to the input MINTServer URL (" + serverURL + ")");
			System.out.println("Check that the specified URL is correct and currently active.\n");
			PrintUsage();
			return;
		}
		
		//Finished validating inputs, call the actual processing code
		try
		{
			//Create an instance of the Directory Processing Class
			ProcessImportDir importProcessor = new ProcessImportDir(inputDir, serverURL);

			//Run the importing process against the specified directory
			//This will process and send the resulting MINT message to the MINTServer
			importProcessor.run();
		}
		catch( Exception e )
		{
			System.out.println("An exception occurred while processing the files in the input directory.");
			System.out.println(e.toString());
			System.out.println(e.getStackTrace().toString());
		}
		
	}
	
	private static void PrintUsage()
	{
		System.out.println("Usage: DICOM2MINTImport [DIRECTORY] [URL]");
		System.out.println("Converts any and all DICOM files in the specified directory into the MINT standard");
		System.out.println("format and sends Create Study messages for each study found in the directory");
		System.out.println("to the MINTServer defined by the input URL.");
		System.out.println("  Inputs:");
		System.out.println("    [DIRECTORY]  the file path where DICOM files are located");
		System.out.println("    [URL]        the Base URL path of the MINTServer.");
		
	}
	
	private static boolean CheckServerExists(String URLName){
		try
		{
			HttpURLConnection.setFollowRedirects(false);
			HttpURLConnection con = (HttpURLConnection) new URL(URLName).openConnection();
			con.setRequestMethod("HEAD");
			return (con.getResponseCode() == HttpURLConnection.HTTP_OK);
		}
		catch (Exception e)
		{
			System.out.println(e.toString());
			return false;
		}
	} 

}
