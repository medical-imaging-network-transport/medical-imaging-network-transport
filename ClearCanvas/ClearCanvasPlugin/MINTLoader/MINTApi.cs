using System;
using System.Collections.Generic;
using System.Text;
using System.Xml;
using System.IO;
using System.Net;
using ClearCanvas.Common;
using ClearCanvas.Common.Utilities;
using ClearCanvas.ImageViewer;
using ClearCanvas.ImageViewer.StudyManagement;
using ClearCanvas.Dicom.Iod;
using System.Globalization;

namespace MINTLoader
{
    static public class MINTApi
    {
        public static readonly string LoaderName = "MINT";
        public static readonly string FinderName = "MINT";

        public class StudyKey
        {
            public StudyKey(
                string serviceUri,
                string studyUid,
                string metadataUri,
                string summaryUri)
            {
                SetHostUri(serviceUri);
                StudyUid = studyUid;
                MetadataUri = HostUri + "/" + metadataUri;
                SummaryUri = HostUri + "/" + summaryUri;
            }

            public string StudyUid { set; get; }
            public string MetadataUri { set; get; }
            public string SummaryUri { set; get; }
            public string HostUri { set; get; }

            private void SetHostUri(string serviceUri)
            {
                int idxSlash = serviceUri.IndexOf('/');
                for (int slashNum = 0;
                    (idxSlash >= 0) && (slashNum < 2);
                    idxSlash = serviceUri.IndexOf('/', idxSlash + 1),
                    ++slashNum)
                {
                }
                HostUri = (idxSlash >= 0) ? serviceUri.Substring(0, idxSlash) : serviceUri;
            }
        }

        public static IEnumerable<StudyKey> ParseStudiesResponse(string serviceUri, Stream xmlStream)
        {
            var doc = ReadDocFromStream(xmlStream);

            var keys = new List<StudyKey>();
            foreach (XmlNode child in doc.ChildNodes)
            {
                if (child.Name == "studySearchResults")
                {

                    var studyUIDNodes = child.ChildNodes;
                    if (studyUIDNodes == null)
                    {
                        continue;
                    }

                    foreach (XmlNode node in studyUIDNodes)
                    {
                        // Get MINT UID.
                        var studyUUID = node.Attributes.GetNamedItem("studyUUID");
                        if (studyUUID == null)
                        {
                            throw new LoadStudyException("Unknown", "MINT studyUUID is null");
                        }

                        string metaUri = "/MINTServer/studies/" + studyUUID.Value + "/DICOM/metadata";
                        string summaryUri = "/MINTServer/studies/" + studyUUID.Value + "/DICOM/summary";

                        // Get the DICOM UID
                        string studyInstanceUID = MINTApi.GetStudyInstanceUID(serviceUri, studyUUID.Value);
                        if (studyInstanceUID == null)
                        {
                            throw new LoadStudyException(studyUUID.Value, "Study Instance UID attribute missing in summary");
                        }

                        keys.Add(new StudyKey(serviceUri, studyInstanceUID, metaUri, summaryUri));
                    }
                }
            }

            return keys;
        }
        
        public static string GetStudyInstanceUID(string serviceUri, string studyUUID)
        {
            string summaryUri = serviceUri + "/studies/" + studyUUID + "/DICOM/summary";
            HttpWebResponse rep = CallService(summaryUri);

            XmlDocument doc = new XmlDocument();

            using (var reader = XmlReader.Create(rep.GetResponseStream()))
            {
                doc.Load(reader);
            }

            foreach (XmlNode child in doc.ChildNodes)
            {
                if (child.Name == "studyMeta")
                {
                    XmlAttributeCollection attributes = child.Attributes;
                    if (attributes == null) return null;

                    XmlNode studyInstanceUID = attributes.GetNamedItem("studyInstanceUID");
                    if (studyInstanceUID == null) return null;
                    return studyInstanceUID.Value;
                }
            }

            return null;
        }

        public static IEnumerable<StudyKey> GetStudies(string serviceUri)
        {
            try
            {
                CodeClock clock = new CodeClock();
                clock.Start();

                HttpWebResponse rep = CallService(AllStudiesUri(serviceUri));

                var keys = ParseStudiesResponse(serviceUri, rep.GetResponseStream());

                clock.Stop();
                PerformanceReportBroker.PublishReport("MINT", "Get study list", clock.Seconds);

                return keys;
            }
            catch (Exception ex)
            {
                Platform.Log(LogLevel.Error, ex, "Problem calling MINT service at {0}", serviceUri);
            }
            return new List<StudyKey>();
        }

        public static StudyKey GetStudyKey(string serviceUri, string studyUID)
        {
            try
            {
                HttpWebResponse rep = CallService(StudyLookupUri(serviceUri, studyUID));

                //just return the first one we find
                foreach (var key in ParseStudiesResponse(serviceUri, rep.GetResponseStream()))
                {
                    return key;
                }
            }
            catch (Exception ex)
            {
                Platform.Log(LogLevel.Error, ex, "Problem calling MINT service at {0} with Study UID = {2}", serviceUri, studyUID);
            }
            return null;
        }

        public static XmlDocument GetStudyMetadata(StudyKey key)
        {
            CodeClock clock = new CodeClock();
            clock.Start();

            HttpWebResponse rep = CallService(key.MetadataUri);

            XmlDocument doc = new XmlDocument();

            using (var reader = XmlReader.Create(rep.GetResponseStream()))
            {
                doc.Load(reader);
            }

            clock.Stop();
            PerformanceReportBroker.PublishReport("MINT", "Metadata Load/Parse", clock.Seconds);

            return doc;
        }


        public static void FillStudyItem(StudyItem item, StudyKey key)
        {
            HttpWebResponse rep = CallService(key.SummaryUri);
            FullStudyItemFromXml(item, rep.GetResponseStream());
        }

        public static void FullStudyItemFromXml(StudyItem item, Stream xmlStream)
        {
            var doc = ReadDocFromStream(xmlStream);

            foreach (XmlNode child in doc.ChildNodes)
            {
                if (child.Name == "studyMeta")
                {

                    var studyMetaChildren = child.ChildNodes;
                    if (studyMetaChildren == null)
                    {
                        continue;
                    }

                    foreach (XmlNode metaNode in studyMetaChildren)
                    {
                        if (metaNode.Name == "attributes")
                        {
                            var attributeNodes = metaNode.ChildNodes;
                            if (attributeNodes == null)
                            {
                                continue;
                            }
                            foreach (XmlNode node in attributeNodes)
                            {
                                // The DICOM study instance UID, not the MINT UUID.

                                var xmlNodeAttr = node.Attributes.GetNamedItem("tag");
                                if (string.IsNullOrEmpty(xmlNodeAttr.Value))
                                {
                                    continue;
                                }
                                var valueNode = node.Attributes.GetNamedItem("val");
                                string value = "";
                                if (valueNode != null)
                                {
                                    value = valueNode.Value;
                                }
                                switch (xmlNodeAttr.Value)
                                {
                                    case "00080020":
                                        item.StudyDate = value;
                                        break;
                                    case "00080030":
                                        item.StudyTime = value;
                                        break;
                                    case "00080050":
                                        item.AccessionNumber = value;
                                        break;
                                    case "00080090":
                                        item.ReferringPhysiciansName = new PersonName(value);
                                        break;
                                    case "00081030":
                                        item.StudyDescription = value;
                                        break;
                                    case "00100010":
                                        item.PatientsName = new PersonName(value);
                                        break;
                                    case "00100020":
                                        item.PatientId = value;
                                        break;
                                    case "00100030":
                                        item.PatientsBirthDate = value;
                                        break;
                                    case "00100040":
                                        item.PatientsSex = value;
                                        break;
                                    case "00200010":
                                        item.StudyId = value;
                                        break;
                                    default:
                                        break;
                                }
                            }
                        }
                    }
                   
                }
            }
        }

        private static string GetVal(XmlNode topLevelUlNode, string p, XmlNamespaceManager nsMan)
        {
            var liNode = topLevelUlNode.SelectSingleNode(string.Format("li[@class='{0}']", p));
            return liNode == null ? "" : liNode.InnerText;
        }

        private static string AllStudiesUri(string serviceUri)
        {
            return string.Format("{0}/studies", serviceUri);
        }

        private static string StudyLookupUri(string serviceUri, string studyUID)
        {
            return string.Format("{0}/studies?studyuid={1}", serviceUri, studyUID);
        }

        private static HttpWebResponse CallService(string serviceUri)
        {
            HttpWebRequest req = (HttpWebRequest)WebRequest.Create(serviceUri);
            req.KeepAlive = false;
            req.Method = "GET";

            return (HttpWebResponse)req.GetResponse();
        }

        private static string GetHref(XmlNode topNode, string tagType, XmlNamespaceManager nsMan)
        {
            var liNode = topNode.SelectSingleNode(string.Format("ns:dd[@class='{0}']", tagType), nsMan);
            if (liNode == null)
            {
                return null;
            }

            var hrefNode = liNode.FirstChild;
            XmlAttribute href = (XmlAttribute)hrefNode.Attributes.GetNamedItem("href");
            return href == null ? null : href.Value;
        }

        private static XmlDocument ReadDocFromStream(Stream strm)
        {
            XmlDocument doc = new XmlDocument();

            XmlReaderSettings settings = new XmlReaderSettings();
            settings.ProhibitDtd = false;
            settings.XmlResolver = null;
            using (var reader = XmlReader.Create(strm, settings))
            {
                doc.Load(reader);
            }

            return doc;
        }
    }
}
