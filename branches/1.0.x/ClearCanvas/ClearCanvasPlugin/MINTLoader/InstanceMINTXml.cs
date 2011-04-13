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
using System.Collections;
using System.Collections.Generic;
using System.Globalization;
using System.IO;
using System.Security;
using System.Text;
using System.Text.RegularExpressions;
using System.Xml;
using ClearCanvas.Common;
using ClearCanvas.Dicom;
using ClearCanvas.ImageViewer.StudyManagement;
using System.Diagnostics;

namespace MINTLoader
{
	/// <summary>
	/// Class for representing a SOP Instance as XML.
	/// </summary>
	/// <remarks>
	/// This class may change in a future release.
	/// </remarks>
	public class InstanceMINTXml
	{
		#region Private members
		private TransferSyntax _transferSyntax;

        DicomAttributeCollection _collection = new DicomAttributeCollection();
        private Uri _pixelDataUri;
		#endregion

		#region Public Properties

		public Uri PixelDataUri
		{
            get 
            { 
                return _pixelDataUri; 
            }
			private set
			{
                _pixelDataUri = value;
			}
		}

		public TransferSyntax TransferSyntax
		{
			get
			{
			    return _transferSyntax;
			}
		}

		/// <summary>
		/// Gets the underlying data as a <see cref="DicomAttributeCollection"/>.
		/// </summary>
		/// <remarks>
		/// When parsed from xml, the return type is <see cref="InstanceXmlDicomAttributeCollection"/>, otherwise
		/// it is the source <see cref="DicomAttributeCollection"/>.
		/// </remarks>
		public DicomAttributeCollection Collection
		{
			get
			{
				return _collection;
			}
		}

		public DicomAttribute this[DicomTag tag]
		{
			get
			{
			    return this[tag.TagValue];
			}
		}

		public DicomAttribute this[uint tag]
		{
			get
			{
			    return _collection[tag];
			}
		}

		#endregion

		#region Constructors

        public InstanceMINTXml(
            StudyLoaderArgs studyLoaderArgs,
            string metaUri,
            XmlElement instanceElem,
            DicomAttributeCollection studyAttributes,
            DicomAttributeCollection seriesAttributes,
            DicomAttributeCollection normalizedInstanceAttributes)
		{
            var trfAttr = instanceElem.Attributes["transferSyntaxUID"];
            _transferSyntax = (trfAttr != null)
                ? TransferSyntax.GetTransferSyntax(trfAttr.Value)
                : TransferSyntax.ExplicitVrLittleEndian;

            var result = MINTAttributeCollectionParser.ParseAttributes(studyLoaderArgs, metaUri,
                instanceElem, "attributes");

            _collection = result.Attributes;

            if (result.PixelDataBid != null)
            {
                var uriString = string.Format("{0}/{1}",
                    metaUri.Replace("metadata", "binaryitems"), result.PixelDataBid);
                _pixelDataUri = new Uri(uriString);
            }
            else
            {
                 Platform.Log(LogLevel.Error, "No pixel data item found for instance {0}!",
                    _collection[DicomTags.SopInstanceUid].GetString(0, ""));
            }

            Merge(_collection, normalizedInstanceAttributes);
            Merge(_collection, seriesAttributes);
            Merge(_collection, studyAttributes);
		}

        private static void Merge(DicomAttributeCollection dstAttrs, DicomAttributeCollection srcAttrs)
        {
            if (srcAttrs != null)
            {
                foreach (DicomAttribute attr in srcAttrs)
                {
                    dstAttrs[attr.Tag] = attr;
                }
            }
        }

		#endregion
	}
}
