#region License

// Copyright (c) 2010, ClearCanvas Inc.
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without modification, 
// are permitted provided that the following conditions are met:
//
//    * Redistributions of source code must retain the above copyright notice, 
//      this list of conditions and the following disclaimer.
//    * Redistributions in binary form must reproduce the above copyright notice, 
//      this list of conditions and the following disclaimer in the documentation 
//      and/or other materials provided with the distribution.
//    * Neither the name of ClearCanvas Inc. nor the names of its contributors 
//      may be used to endorse or promote products derived from this software without 
//      specific prior written permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, 
// THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR 
// PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR 
// CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, 
// OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE 
// GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) 
// HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, 
// STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN 
// ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY 
// OF SUCH DAMAGE.

#endregion

using System;
using System.IO;
using System.Net;
using System.Text;
using System.Text.RegularExpressions;
using System.Web;
using ClearCanvas.Common;
using ClearCanvas.Common.Utilities;
using System.Collections.Generic;
using System.Diagnostics;
using System.Net.Cache;
using ClearCanvas.Dicom.ServiceModel.Streaming;
using ClearCanvas.Dicom;

namespace MINTLoader
{
    /// <summary>
    /// Represents a web client that can be used to retrieve study images or pixel data from a streaming server using WADO protocol.
    /// </summary>
    public class MINTStreamingClient
    {
        private readonly Uri _baseUri;
        private bool _useBulkLoading;
        private MINTBinaryStream _binaryStream;

        /// <summary>
        /// Creates an instance of <see cref="StreamingClient"/> to connect to a streaming server.
        /// </summary>
        /// <param name="baseUri">Base Uri to the location where the streaming server is located (eg http://localhost:1000/wado)</param>
        public MINTStreamingClient(Uri baseUri, bool useBulkLoading, MINTBinaryStream binaryStream)
        {
            _baseUri = baseUri;
            _useBulkLoading = useBulkLoading;
            _binaryStream = binaryStream;
        }

        #region Public Methods

		public RetrievePixelDataResult RetrievePixelData()
        {
			try
			{
                if (_useBulkLoading)
                {
                    //FrameStreamingResultMetaData result = new FrameStreamingResultMetaData();

                    string[] uriSplit = Regex.Split(_baseUri.ToString(), "/");
                    //Console.WriteLine("URI: " + _baseUri);
                    //Console.WriteLine("binary item no: " + uriSplit[uriSplit.Length - 1]); 
                    
                    //byte[] binaryData = new byte[1000000];
                    //_binaryItems.TryGetValue(1, out binaryData);
                    int binaryItemNumber = Int32.Parse(uriSplit[uriSplit.Length - 1]);
                    //byte[] binaryData = _binaryStream.GetBinaryData(binaryItemNumber);
                    //result = _binaryStream.GetMetadata(binaryItemNumber);

                    RetrievePixelDataResult pixelDataResult = new RetrievePixelDataResult(_binaryStream.GetBinaryData(binaryItemNumber), _binaryStream.GetMetadata(binaryItemNumber));
                    return pixelDataResult;
                }
                else
                {                    
                    CodeClock clock = new CodeClock();
                    clock.Start();

                    FrameStreamingResultMetaData result = new FrameStreamingResultMetaData();

                    result.Speed.Start();

                    HttpWebRequest request = (HttpWebRequest)WebRequest.Create(_baseUri);
                    request.Timeout = 30000;
                    request.KeepAlive = false;

                    HttpWebResponse response = (HttpWebResponse)request.GetResponse();

                    if (response.StatusCode != HttpStatusCode.OK)
                    {
                        throw new StreamingClientException(response.StatusCode, HttpUtility.HtmlDecode(response.StatusDescription));
                    }

                    Stream responseStream = response.GetResponseStream();
                    BinaryReader reader = new BinaryReader(responseStream);
                    byte[] buffer = reader.ReadBytes((int)response.ContentLength);
                    reader.Close();
                    responseStream.Close();
                    response.Close();

                    result.Speed.SetData(buffer.Length);
                    result.Speed.End();

                    result.ResponseMimeType = response.ContentType;
                    result.Status = response.StatusCode;
                    result.StatusDescription = response.StatusDescription;
                    result.Uri = response.ResponseUri;
                    result.ContentLength = buffer.Length;
                    result.IsLast = (response.Headers["IsLast"] != null && bool.Parse(response.Headers["IsLast"]));

                    clock.Stop();
                    PerformanceReportBroker.PublishReport("MINT", "RetrievePixelData", clock.Seconds);

                    RetrievePixelDataResult pixelDataResult;
                    if (response.Headers["Compressed"] != null && bool.Parse(response.Headers["Compressed"]))
                        pixelDataResult = new RetrievePixelDataResult(CreateCompressedPixelData(response, buffer), result);
                    else
                        pixelDataResult = new RetrievePixelDataResult(buffer, result);

                    return pixelDataResult;
                }
			}
			catch (WebException ex)
			{
				if (ex.Status == WebExceptionStatus.ProtocolError && ex.Response is HttpWebResponse)
				{
					HttpWebResponse response = (HttpWebResponse) ex.Response;
					throw new StreamingClientException(response.StatusCode, HttpUtility.HtmlDecode(response.StatusDescription));
				}
				throw new StreamingClientException(StreamingClientExceptionType.Network, ex);
			}
		}

        #endregion Public Methods

		#region Private Methods

		private string BuildImageUrl(string serverAE, string studyInstanceUid, string seriesInstanceUid, string sopInstanceUid)
		{
			Platform.CheckForEmptyString(serverAE, "serverAE");
			Platform.CheckForEmptyString(studyInstanceUid, "studyInstanceUid");
			Platform.CheckForEmptyString(seriesInstanceUid, "seriesInstanceUid");
			Platform.CheckForEmptyString(sopInstanceUid, "sopInstanceUid");


            Console.WriteLine("URI At this point: " + _baseUri);
            Console.WriteLine("serverAE: " + serverAE);
            Console.WriteLine("Study instance uid: " + studyInstanceUid);
            Console.WriteLine("Series instance uid: " + seriesInstanceUid);
            Console.WriteLine("sopInstanceUID: " + sopInstanceUid);

			StringBuilder url = new StringBuilder();
			if (_baseUri.ToString().EndsWith("/"))
			{
				url.AppendFormat("{0}{1}", _baseUri, serverAE);
			}
			else
			{
				url.AppendFormat("{0}/{1}", _baseUri, serverAE);
			}

			url.AppendFormat("?requesttype=WADO&studyUID={0}&seriesUID={1}&objectUID={2}", studyInstanceUid, seriesInstanceUid, sopInstanceUid);
			return url.ToString();
		}

		

		private static DicomCompressedPixelData CreateCompressedPixelData(HttpWebResponse response, byte[] pixelDataBuffer)
		{
			string transferSyntaxUid = response.Headers["TransferSyntaxUid"];
			TransferSyntax transferSyntax = TransferSyntax.GetTransferSyntax(transferSyntaxUid);
			ushort bitsAllocated = ushort.Parse(response.Headers["BitsAllocated"]);
			ushort bitsStored = ushort.Parse(response.Headers["BitsStored"]);
			ushort height = ushort.Parse(response.Headers["ImageHeight"]);
			ushort width = ushort.Parse(response.Headers["ImageWidth"]);
			ushort samples = ushort.Parse(response.Headers["SamplesPerPixel"]);

			DicomAttributeCollection collection = new DicomAttributeCollection();
			collection[DicomTags.BitsAllocated].SetUInt16(0, bitsAllocated);
			collection[DicomTags.BitsStored].SetUInt16(0, bitsStored);
			collection[DicomTags.HighBit].SetUInt16(0, ushort.Parse(response.Headers["HighBit"]));
			collection[DicomTags.Rows].SetUInt16(0, height);
			collection[DicomTags.Columns].SetUInt16(0, width);
			collection[DicomTags.PhotometricInterpretation].SetStringValue(response.Headers["PhotometricInterpretation"]);
			collection[DicomTags.PixelRepresentation].SetUInt16(0, ushort.Parse(response.Headers["PixelRepresentation"]));
			collection[DicomTags.SamplesPerPixel].SetUInt16(0, samples);
			collection[DicomTags.DerivationDescription].SetStringValue(response.Headers["DerivationDescription"]);
			collection[DicomTags.LossyImageCompression].SetStringValue(response.Headers["LossyImageCompression"]);
			collection[DicomTags.LossyImageCompressionMethod].SetStringValue(response.Headers["LossyImageCompressionMethod"]);
			collection[DicomTags.LossyImageCompressionRatio].SetFloat32(0, float.Parse(response.Headers["LossyImageCompressionRatio"]));
			collection[DicomTags.PixelData] = new DicomFragmentSequence(DicomTags.PixelData);

			ushort planar;
			if (ushort.TryParse(response.Headers["PlanarConfiguration"], out planar))
				collection[DicomTags.PlanarConfiguration].SetUInt16(0, planar);

			DicomCompressedPixelData cpd = new DicomCompressedPixelData(collection);
			cpd.TransferSyntax = transferSyntax;
			cpd.AddFrameFragment(pixelDataBuffer);

			return cpd;
		}

		#endregion
	}
}
