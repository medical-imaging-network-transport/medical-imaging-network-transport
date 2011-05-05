using System;
using System.Collections.Generic;
using System.Text;
using ClearCanvas.Dicom;
using System.Xml;
using ClearCanvas.ImageViewer.StudyManagement;

namespace MINTLoader
{
    class SeriesMINTXml
    {
        #region Private Members
        private DicomAttributeCollection _seriesAttributes = null;
        private List<InstanceMINTXml> _instances = new List<InstanceMINTXml>();
        #endregion

        #region Public Properties
        public DicomAttribute this[uint tag]
        {
            get
            {
                // Normalized instance attributes are never considered here...
                return (_seriesAttributes == null) ? null : _seriesAttributes[tag];
            }
        }

        public DicomAttribute this[DicomTag tag]
        {
            get
            {
                return this[tag.TagValue];
            }
        }

        public IList<InstanceMINTXml> Instances
        {
            get
            {
                return _instances;
            }
        }
        #endregion

        #region Constructor

        public SeriesMINTXml(
            StudyLoaderArgs studyLoaderArgs,
            string metaUri,
            XmlElement seriesElem,
            DicomAttributeCollection studyAttributes)
        {
            _seriesAttributes =
                MINTAttributeCollectionParser.ParseAttributes(studyLoaderArgs, metaUri, seriesElem,
                    "attributes").Attributes;
            var normalizedInstanceAttributes =
                MINTAttributeCollectionParser.ParseAttributes(studyLoaderArgs, metaUri, seriesElem,
                    "normalizedInstanceAttributes").Attributes;
            foreach (var subNode in seriesElem)
            {
                var subElem = subNode as XmlElement;
                if ((subElem != null) && subElem.Name.Equals("instances"))
                {
                    foreach (var instanceNode in subElem)
                    {
                        var instanceElem = instanceNode as XmlElement;
                        if ((instanceElem != null) && instanceElem.Name.Equals("instance"))
                        {
                            _instances.Add(new InstanceMINTXml(studyLoaderArgs, metaUri, instanceElem, studyAttributes,
                                _seriesAttributes, normalizedInstanceAttributes));
                        }
                    }
                }
            }
        }

        #endregion
    }
}
