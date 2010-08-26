package org.nema.medical.mint;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Hashtable;

/**
 * Binary Stream Reader class. Can be used to read a binary stream via bulk loading
 * (multipart MIME) and also provides the functionality to read binary data from an
 * input stream given the length to read for file-by-file reading.
 * @author gsevinc1
 *
 */
public class MINTBinaryStreamReader 
{
	private URL BinaryAddress;
	private byte[] crlfByte;
	private Hashtable<Integer, byte[]> binaryItems;
	
	/**
	 * Default constructor.
	 * @param binaryAddress
	 * @throws MalformedURLException
	 */
	public MINTBinaryStreamReader() 
	{
		String crlf = "\r\n";
		crlfByte = crlf.getBytes();
		binaryItems = new Hashtable<Integer, byte[]>();
	}
	
	/**
	 * Default constructor.
	 * @param binaryAddress
	 * @throws MalformedURLException
	 */
	public MINTBinaryStreamReader(URL binaryAddress) throws MalformedURLException 
	{
		SetAddress(binaryAddress);
		String crlf = "\r\n";
		crlfByte = crlf.getBytes();
		binaryItems = new Hashtable<Integer, byte[]>();
	}
	
	public void SetAddress(URL binaryAddress) throws MalformedURLException
	{
		BinaryAddress = new URL(binaryAddress, "all");
	}
	
	/**
	 * Readline function. Takes an input stream and reads a line until the carriage feed 
	 * character. 
	 * @param inputStream The input stream to read
	 * @return A string generated from the byte array read in
	 * @throws IOException
	 */
	private String readLine(InputStream inputStream) throws IOException
	{
		// Need to read byte by byte here. Was using BufferedStreamReader to read a line at a time, 
		// which did not behave as expected. Creates a string from the byte array for easier manipulation
		// and data extraction.
		byte[] line = new byte[128];
		int lineIndex = 0;
		while(true)
		{
			Integer currentByteInt = inputStream.read();
			byte currentByte = currentByteInt.byteValue();
			if(currentByte == crlfByte[1])
			{
				break;
			}
			line[lineIndex] = currentByte;
			lineIndex++;
		}
		return new String(line, 0, lineIndex);
	}
	
	/**
	 * getContentID function. Takes the line consisting the content ID - a string - 
	 * and extracts the content id integer.
	 * @param contentIDLine The content id line
	 * @return Content id integer
	 */
	private int getContentID(String contentIDLine)
	{
		int startIndex = contentIDLine.indexOf('<');
		int endIndex = contentIDLine.indexOf('@');
		return Integer.parseInt(contentIDLine.substring(startIndex+1, endIndex));
	}
	
	/**
	 * getContentLength function. Takes the line consisting the content length - a
	 * string - and extracts the content length integer.
	 * @param contentLengthLine The content length line
	 * @return Content length integer
	 */
	private int getContentLength(String contentLengthLine)
	{
		return Integer.parseInt(contentLengthLine.substring(16));
	}
	
	/**
	 * readBinaryData function. Takes an input stream and a content length, and reads contentLength
	 * number of bytes from the inputStream. Returns a byte array consisting of the bytes read in.
	 * @param inputStream The stream to read
	 * @param contentLength The number of bytes to read
	 * @return byte array of contentLength size
	 * @throws IOException
	 */
	public byte[] readBinaryData(InputStream inputStream, int contentLength) throws IOException
	{
		final byte[] binaryData = new byte[contentLength];

		// Try to read contentLength many bytes
		int readBytes = inputStream.read(binaryData, 0, contentLength);
		
		// If not enough bytes read
        while(readBytes < contentLength)
		{
        	try
        	{
        		// Attempt to read the remaining bytes
				int moreReadBytes = inputStream.read(binaryData, readBytes, contentLength - readBytes);
				if(moreReadBytes == 0)
				{
					break;
				}
				readBytes += moreReadBytes;
			} 
        	catch (Exception e)
			{
				break;
			}
			
		}
		return binaryData;
	}
	
	/**
	 * Returns the binary data corresponding to the given bid.
	 * @param contentID bid to search for
	 * @return
	 */
	public byte[] getBinaryData(int contentID)
	{
		return binaryItems.get(contentID);
	}
	
	
	public void readHttpStream() throws IOException
	{
		URLConnection binaryAddressConnection = BinaryAddress.openConnection(); 
		InputStream inputStream = binaryAddressConnection.getInputStream();
		readStream(inputStream);
	}
	
	
	/**
	 * readStream function. Main driver for the bulk loading section, creates an input stream to the URL of bulk items, 
	 * and reads the header information and calls helper functions to extract information.
	 * @throws IOException
	 */
	@SuppressWarnings("unused")
	public void readStream(InputStream inputStream) throws IOException
	{
		String delimiterLine = new String();
		String contentTypeLine = new String();
		String contentIDLine = new String();
		String contentLengthLine = new String();
		String tempLine = new String();
		
		int contentID = 0;
		int contentLength = 0;
		byte[] binaryDataRead;
		
		while(true)
		{
			try 
			{
				// Read lines, check for two dashes at the end of the delimiter line to see if we are at the end of the bulk loaded file.
				delimiterLine = readLine(inputStream);
				if(delimiterLine.charAt(delimiterLine.length() - 1) == '-' && delimiterLine.charAt(delimiterLine.length() - 2) == '-')
				{
					break;
				}
				
				contentTypeLine = readLine(inputStream);
				contentIDLine = readLine(inputStream);
				contentLengthLine = readLine(inputStream);
				tempLine = readLine(inputStream);
				
				// Extract the content id and content length from the lines.
				contentID = getContentID(contentIDLine);
				contentLength = getContentLength(contentLengthLine);
				
				// Read the binary data.
				binaryDataRead = readBinaryData(inputStream, contentLength);
				// Read extra line to get rid of \n
				tempLine = readLine(inputStream);
				// Insert into the hashtable
				binaryItems.put(contentID, binaryDataRead);
				
				//Empty the strings
				delimiterLine = new String();	
				contentTypeLine = new String();
				contentIDLine = new String();		
				contentLengthLine = new String();
				tempLine = new String();
				contentID = 0;
				contentLength = 0;
			}
			catch (Exception e)
			{
				break;
			}
		}
	}
}
