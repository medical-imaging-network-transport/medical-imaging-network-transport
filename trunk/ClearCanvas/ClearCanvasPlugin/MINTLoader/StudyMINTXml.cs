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
using System.Xml;
using ClearCanvas.Common;
using ClearCanvas.Dicom;
using ClearCanvas.ImageViewer;
using ClearCanvas.ImageViewer.StudyManagement;

namespace MINTLoader
{
    /// <summary>
    /// Class that can represent a study as XML data.
    /// </summary>
    public class StudyMINTXml
    {
        #region Private members

        private readonly Dictionary<string, IList<InstanceMINTXml>> _seriesList =
            new Dictionary<string, IList<InstanceMINTXml>>();
        private DicomAttributeCollection _studyAttributes;
        private StudyLoaderArgs _studyLoaderArgs;

        #endregion

        #region Public Properties

        public DicomAttribute this[uint tag]
        {
            get
            {   
                return (_studyAttributes == null) ? null : _studyAttributes[tag];
            }
        }

        public DicomAttribute this[DicomTag tag]
        {
            get
            {
                return this[tag.TagValue];
            }
        }

        public IDictionary<string, IList<InstanceMINTXml>> SeriesList
        {
            get
            {
                return _seriesList;
            }
        }

        public IList<InstanceMINTXml> AllInstances
        {
            get
            {
                var allInstances = new List<InstanceMINTXml>();
                foreach (var series in _seriesList.Values)
                {
                    allInstances.AddRange(series);
                }

                return allInstances;
            }
        }

        #endregion

        #region Constructors

        public StudyMINTXml(StudyLoaderArgs studyLoaderArgs)
        {
            _studyLoaderArgs = studyLoaderArgs;
        }

        #endregion

        #region Public Methods

        /// <summary>
        /// Populate this <see cref="StudyXml"/> object based on the supplied XML document.
        /// </summary>
        /// <param name="theDocument"></param>
        public void SetMemento(string metaUri, XmlDocument theDocument)
        {
            if (!theDocument.HasChildNodes)
            {
                throw new LoadStudyException(_studyLoaderArgs.StudyInstanceUid, "Empty StudyMeta XML document");
            }

            // There should be one root node.
            var rootNode = theDocument.DocumentElement;
            if (!rootNode.Name.Equals("studyMeta"))
            {
                throw new LoadStudyException(_studyLoaderArgs.StudyInstanceUid, "Document root element name must be 'studyMeta'");
            }
            _studyAttributes = MINTAttributeCollectionParser.ParseAttributes(_studyLoaderArgs, metaUri,
                (XmlElement)rootNode, "attributes").Attributes;
            foreach (var node in rootNode)
            {
                var elem = node as XmlElement;
                if (elem == null)
                {
                    continue;
                }
                // Just search for the first study node, parse it, then break
                if (elem.Name.Equals("seriesList"))
                {
                    if (_studyAttributes == null)
                    {
                        throw new LoadStudyException(_studyLoaderArgs.StudyInstanceUid,
                            "Malformed StudyMeta XML document: 'seriesList' found before 'attributes'");
                    }

                    foreach (var subNode in elem)
                    {
                        var seriesElem = subNode as XmlElement;
                        if (seriesElem != null)
                        {
                            var series = new SeriesMINTXml(_studyLoaderArgs, metaUri, seriesElem, _studyAttributes);
                            _seriesList[series[DicomTags.SeriesInstanceUid].GetString(0, "")] = series.Instances;
                        }
                    }
                }
            }
        }

        /// <summary>
        /// Returns a boolean indicating whether the specified series exists in the study XML.
        /// </summary>
        /// <param name="seriesUid">The Series Instance UID of the series to check</param>
        /// <returns>True if the series exists in the study XML</returns>
        public bool Contains(string seriesUid)
        {
            return _seriesList.ContainsKey(seriesUid);
        }

        #endregion
    }
}
