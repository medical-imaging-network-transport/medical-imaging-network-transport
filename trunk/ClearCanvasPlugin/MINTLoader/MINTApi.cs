using System;
using System.Collections.Generic;
using System.Text;
using System.Xml;
using System.IO;
using System.Net;
using ClearCanvas.Common;
using ClearCanvas.Common.Utilities;
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
            XmlNamespaceManager nsMan = null;
            var doc = ReadDocFromStream(xmlStream, out nsMan);

            var keys = new List<StudyKey>();
            foreach (XmlNode child in doc.ChildNodes)
            {
                var studyUidNodes = child.SelectNodes("//ns:li/ns:dl", nsMan);
                if (studyUidNodes == null)
                {
                    continue;
                }

                foreach (XmlNode node in studyUidNodes)
                {
                    var uidNode = node.SelectSingleNode("ns:dd[@class='StudyID']", nsMan);
                    if (uidNode == null)
                    {
                        continue;
                    }
                    string studyUid = uidNode.InnerText;
                    if (string.IsNullOrEmpty(studyUid))
                    {
                        continue;
                    }
                    string metaUri = GetHref(node, "StudyMetadata", nsMan);
                    if (string.IsNullOrEmpty(metaUri))
                    {
                        continue;
                    }

                    string summaryUri = GetHref(node, "StudySummary", nsMan);
                    if (string.IsNullOrEmpty(metaUri))
                    {
                        continue;
                    }

                    keys.Add(new StudyKey(serviceUri, studyUid, metaUri, summaryUri));
                }
            }

            return keys;
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
            XmlNamespaceManager nsMan = null;
            var doc = ReadDocFromStream(xmlStream, out nsMan);

            var docElem = doc.DocumentElement;

            var topLevelUlNodes = docElem.SelectNodes("//ns:ul/ns:li/ns:ul", nsMan);
            if (topLevelUlNodes == null)
            {
                return;
            }

            foreach (XmlNode topLevelUlNode in topLevelUlNodes)
            {
                string tag = GetVal(topLevelUlNode, "tag", nsMan);
                if (string.IsNullOrEmpty(tag))
                {
                    continue;
                }

                string value = GetVal(topLevelUlNode, "value", nsMan);
                switch (tag)
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

        private static string GetVal(XmlNode topLevelUlNode, string p, XmlNamespaceManager nsMan)
        {
            var liNode = topLevelUlNode.SelectSingleNode(string.Format("ns:li[@class='{0}']", p), nsMan);
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

        private static XmlDocument ReadDocFromStream(Stream strm, out XmlNamespaceManager nsMan)
        {
            nsMan = null;
            XmlDocument doc = new XmlDocument();

            XmlReaderSettings settings = new XmlReaderSettings();
            settings.ProhibitDtd = false;
            settings.XmlResolver = null;
            using (var reader = XmlReader.Create(strm, settings))
            {
                doc.Load(reader);
            }

            nsMan = new XmlNamespaceManager(doc.NameTable);
            nsMan.AddNamespace("ns", "http://www.w3.org/1999/xhtml");
            return doc;
        }
    }
}
