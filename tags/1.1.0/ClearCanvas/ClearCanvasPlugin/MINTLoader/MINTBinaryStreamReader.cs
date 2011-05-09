using System;
using System.IO;
using System.Collections.Generic;
using System.Text;
using ClearCanvas.Common;
using ClearCanvas.Common.Utilities;
using ClearCanvas.Dicom.ServiceModel.Streaming;
using ClearCanvas.Dicom;

namespace MINTLoader
{
    class MINTBinaryStreamReader
    {
        float binaryClock;
        private Stream binaryStream;
        private FrameStreamingResultMetaData initialMetadata;
        private Dictionary<int, FrameStreamingResultMetaData> metadataDictionary;
        private Dictionary<int, int> contentLengthDictionary;
        byte[] crlfByte;
        byte[] ampByte;
        byte[] leftArrowByte;
        byte[] contentLengthByte;
        byte[] dashByte;

        string crlf = "\r\n";
        string amp = "@";
        string leftArrow = "<";
        string contentlength = "Content-Length: ";
        string dash = "-";

        //Default constructor
        public MINTBinaryStreamReader()
        {
            Initialize();
        }

        //Constructor
        //@param BinaryStream The stream to read from
        public MINTBinaryStreamReader(Stream BinaryStream)
        {
            Initialize();
            SetStream(BinaryStream);
        }

        private void Initialize()
        {
            FrameStreamingResultMetaData initialMetadata = new FrameStreamingResultMetaData();
            metadataDictionary = new Dictionary<int, FrameStreamingResultMetaData>();
            contentLengthDictionary = new Dictionary<int, int>();
            crlfByte = new System.Text.UTF8Encoding().GetBytes(crlf);
            ampByte = new System.Text.UTF8Encoding().GetBytes(amp);
            leftArrowByte = new System.Text.UTF8Encoding().GetBytes(leftArrow);
            contentLengthByte = new System.Text.UTF8Encoding().GetBytes(contentlength);
            dashByte = new System.Text.UTF8Encoding().GetBytes(dash);
        }

        //Sets the stream to use
        //@param BinaryStream The stream to read from
        public void SetStream(Stream BinaryStream)
        {
            binaryStream = BinaryStream;
        }

        //Returns the stream being used
        //@return binaryStream The stream that was/is being used.
        public Stream GetStream()
        {
            return binaryStream;
        }

        public void SetInitialMetadata(FrameStreamingResultMetaData metadata)
        {
            initialMetadata = metadata;
        }

        public FrameStreamingResultMetaData GetMetadata(int contentID)
        {
            metadataDictionary.TryGetValue(contentID, out initialMetadata);
            return initialMetadata;
        }

        public int GetContentLength(int contentID)
        {
            int contentLength;
            contentLengthDictionary.TryGetValue(contentID, out contentLength);
            return contentLength;
        }

        //ReadStreamReads the multipart byte stream into a dictionary of integer
        //and byte array pairs
        //@return Dictionary<int, byte[]> The dictionary that is read in.
        public Dictionary<int, byte[]> ReadStream()
        {
            //Initialize a binary reader, has to be binary to read bytes
            BinaryReader reader = new BinaryReader(binaryStream);

            //Initialize the dictionary
            Dictionary<int, byte[]> binaryItems = new Dictionary<int, byte[]>();

            //Integers being read in form the stream
            int contentID = 0;
            int contentLength = 0;

            //Byte arrays used to read lines
            byte[] delimiterLine = new byte[1024];
            byte[] contentIDLine = new byte[1024];
            byte[] contentTypeLine = new byte[1024];
            byte[] contentLengthLine = new byte[1024];
            byte[] tempLine = new byte[1024];
            byte[] binaryData = new byte[64 * 1024];

            int lineIndex = 0;
            CodeClock clock = new CodeClock();
            clock.Start();
                
            //Main loop
            while(true)
            {
                try
                {
                    //Read delimiter line
                    readLine(ref reader, ref lineIndex, ref delimiterLine);

                    //If there are two dashes at the end of the delimiter line, we are at the end of the stream
                    if (delimiterLine[lineIndex - 1].Equals(dashByte[0]) && delimiterLine[lineIndex - 2].Equals(dashByte[0]))
                    {
                        break;
                    }

                    //Read contenttype, contentid and contentlength lines
                    readLine(ref reader, ref lineIndex, ref contentTypeLine);
                    readLine(ref reader, ref lineIndex, ref contentIDLine);
                    readLine(ref reader, ref lineIndex, ref contentLengthLine);

                    //read a temporary line to get rid of the \n before the binary data
                    readLine(ref reader, ref lineIndex, ref tempLine);

                    //Console.WriteLine("ContentIDLine: " + System.Text.Encoding.UTF8.GetString(contentIDLine));
                    //Compute the contentID given the content id line in a byte array
                    contentID = getContentID(ref contentIDLine);

                    //Compute the content length given the content length line in a byte array
                    contentLength = getContentLength(ref contentLengthLine);

                    binaryData = new byte[contentLength];
                    //Read the binary data of contentLength size
                    readBinaryData(ref reader, ref contentLength, ref binaryData);

                    //read another temporary line to get rid of the \n after the binary data
                    readLine(ref reader, ref lineIndex, ref tempLine);

                    //Add the integer byte array pair to the dictionary
                    binaryItems.Add(contentID, binaryData);

                    initialMetadata.ContentLength = contentLength;
                    metadataDictionary.Add(contentID, initialMetadata);

                    contentLengthDictionary.Add(contentID, contentLength);

                    delimiterLine = new byte[1024];
                    contentIDLine = new byte[1024];
                    contentTypeLine = new byte[1024];
                    contentLengthLine = new byte[1024];
                }
                catch
                {
                    break;
                }
            }
            clock.Stop();
            Console.WriteLine("Time for bulk loading: " + clock.Seconds);

            reader.Close();
            return binaryItems;
        }

        //readLine Reads a line from a stream
        //@param BinaryReader The binary reader to read from
        //       ref lineIndex The number of items in the line, passed by reference
        //@return line The line read into a byte[]
        public void readLine(ref BinaryReader reader, ref int lineIndex, ref byte[] line)
        {
            lineIndex = 0;

            while(true)
            {
                //Loop until a \n is encountered
                byte currentByte = reader.ReadByte();
                if(currentByte.Equals(crlfByte[1]))
                {
                    break;
                }
                //Add to the line
                line[lineIndex] = currentByte;
                lineIndex++;
            }
        }

        //getContentId Extracts the content ID from a byte array
        //@param byte[] The byte array to search through
        //@return int The Content id
        public int getContentID(ref byte[] contentIDLine)
        {
            int startIndex = 0;
            int endIndex = 0;
            //Find the index of the left arrow
            while(!contentIDLine[startIndex].Equals(leftArrowByte[0]))
            {
                if (startIndex <= contentIDLine.Length)
                {
                    startIndex++;
                }
            }

            //Find the index of the ampersand
            endIndex = startIndex + 1;
            while (!contentIDLine[endIndex].Equals(ampByte[0]) && endIndex <= contentIDLine.Length)
            {
                endIndex++;
            }
            byte[] contentID = new byte[128];
            Array.Copy(contentIDLine, startIndex + 1, contentID, 0, endIndex - startIndex - 1);
            return Int32.Parse(System.Text.Encoding.UTF8.GetString(contentID));
        }

        //getContentLength Extracts the content length from a byte array
        //@param byte[] The byte array to search through
        //@return int The Content length
        public int getContentLength(ref byte[] contentLengthLine)
        {
            int startIndex = 0;
            int endIndex = 0;
            //Find the position of the "Content-Length"
            while (!contentLengthLine[startIndex].Equals(contentLengthByte[13]) && startIndex <= contentLengthLine.Length)
            {
                startIndex++;
            }
            startIndex += 2;
            endIndex = startIndex + 1;

            //Find the position of the first null byte
            while (!contentLengthLine[endIndex].Equals((byte)0) && endIndex <= contentLengthLine.Length)
            {
                endIndex++;
            }
            byte[] contentLength = new byte[128];
            Array.Copy(contentLengthLine, startIndex + 1, contentLength, 0, endIndex - startIndex - 1);
            return Int32.Parse(System.Text.Encoding.UTF8.GetString(contentLength));
        }

        //readBinaryData Reads a binary stream of given length into a byte array
        public void readBinaryData(ref BinaryReader reader, ref int contentLength, ref byte[] binaryData)
        {
            CodeClock clockBinary = new CodeClock();

            //Try to read contentLength many bytes
            int readBytes = reader.Read(binaryData, 0, contentLength);

            //If the bytes read in were not as many as needed
            clockBinary.Start();
            initialMetadata.Speed.Start();
            while (readBytes < contentLength)
            {
                //Try to read contentLength - (the # of bytes already read in) bytes
                int moreReadBytes = reader.Read(binaryData, readBytes, contentLength - readBytes);
                if (moreReadBytes == 0)
                {
                    break;
                }
                readBytes += moreReadBytes;
            }

            initialMetadata.Speed.End();
            clockBinary.Stop();
            binaryClock += clockBinary.Seconds;            
        }
    }
}