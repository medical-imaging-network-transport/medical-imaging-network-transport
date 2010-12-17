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
using System.Web;
using System.Collections.Generic;
using ClearCanvas.Common;
using ClearCanvas.Common.Utilities;
using System.Diagnostics;
using System.Net.Cache;
using ClearCanvas.Dicom.ServiceModel.Streaming;
using ClearCanvas.Dicom;

namespace MINTLoader
{
    public class MINTBinaryStream
    {
        private string baseURI;
        private MINTBinaryStreamReader reader;
        private Dictionary<int, byte[]> binaryItems;
        
        public MINTBinaryStream()
        {
            baseURI = null;
            reader = new MINTBinaryStreamReader();
        }

        public MINTBinaryStream(string BaseURI)
        {
            baseURI = BaseURI;
        }

        public void SetBaseURI(string metadataURI)
        {
            baseURI = metadataURI.Replace("metadata", "binaryitems/all");
        }

        public Dictionary<int, byte[]> GetBinaryItems()
        {
            return binaryItems;
        }

        public byte[] GetBinaryData(int contentID)
        {
            int contentLength = reader.GetContentLength(contentID);
            byte[] data = new byte[contentLength];
            binaryItems.TryGetValue(contentID, out data);
            return data;
        }

        public FrameStreamingResultMetaData GetMetadata(int contentID)
        {
            return reader.GetMetadata(contentID);
        }

        public void RetrievePixelData()
        {
            try
            {
                HttpWebRequest request = (HttpWebRequest)WebRequest.Create(baseURI);
                request.Timeout = 30000;
                request.KeepAlive = false;

                HttpWebResponse response = (HttpWebResponse)request.GetResponse();

                if (response.StatusCode != HttpStatusCode.OK)
                {
                    throw new StreamingClientException(response.StatusCode, HttpUtility.HtmlDecode(response.StatusDescription));
                }

                Stream responseStream = response.GetResponseStream();

                reader.SetStream(responseStream);

                FrameStreamingResultMetaData result = new FrameStreamingResultMetaData();
                result.ResponseMimeType = response.ContentType;
                result.Status = response.StatusCode;
                result.StatusDescription = response.StatusDescription;
                result.Uri = response.ResponseUri;
                reader.SetInitialMetadata(result);

                binaryItems = new Dictionary<int, byte[]>();
                binaryItems = reader.ReadStream();
                
                responseStream.Close();
                response.Close();
            }
            catch (WebException ex)
            {
                if (ex.Status == WebExceptionStatus.ProtocolError && ex.Response is HttpWebResponse)
                {
                    HttpWebResponse response = (HttpWebResponse)ex.Response;
                    throw new StreamingClientException(response.StatusCode, HttpUtility.HtmlDecode(response.StatusDescription));
                }
                throw new StreamingClientException(StreamingClientExceptionType.Network, ex);
            }
        }

    }
}
