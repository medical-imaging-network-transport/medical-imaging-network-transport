#define FAST


using System;
using System.Globalization;
using System.Text.RegularExpressions;
using System.Xml;
using ClearCanvas.Dicom;
using ClearCanvas.ImageViewer;
using ClearCanvas.ImageViewer.StudyManagement;
using ClearCanvas.Common;


namespace MINTLoader
{
    class MINTAttributeCollectionParser
    {
        /// <summary>
        /// We cannot store any binary items from the MINT XML in the DicomAttributeCollection as
        /// they are represented as textual numbers in the XML. In order to get the pixel data
        /// binary item id (bid), we need to store it separately. ParseAttributes(), below, will
        /// store the pixel data binary id in the PixelDataBid attribute of this class if it is
        /// found. If not found, PixelDataBid will remain null. The PixelDataBid is used upstream
        /// to create the full Uri to the binary item in the MINT.
        /// </summary>
        public class ParseResult
        {
            public DicomAttributeCollection Attributes { get; set; }
            public String PixelDataBid { get; set; }
        };

        public static ParseResult ParseAttributes(
            StudyLoaderArgs studyLoaderArgs,
            string metaUri,
            XmlElement elem,
            string subElementName)
        {
            foreach (XmlElement subElem in elem)
            {
                if (subElem.Name.Equals(subElementName))
                {
                    var ret = new ParseResult();
                    ret.Attributes = new DicomAttributeCollection();
                    DoParseAttributes(studyLoaderArgs, ret, metaUri, subElem);
                    return ret;
                }
            }
            throw new LoadStudyException(studyLoaderArgs.StudyInstanceUid,
                string.Format("{0} -- Failed to find sub-element: {1}", elem.Name, subElementName));
        }

        private static void DoParseAttributes(
            StudyLoaderArgs studyLoaderArgs,
            ParseResult result,
            string metaUri,
            XmlElement elem)
        {
            var collection = result.Attributes;
            collection.ValidateVrValues = false;
            collection.ValidateVrLengths = false;
            foreach (var node in elem)
            {
                var attrElem = node as XmlElement;
                if (attrElem == null)
                {
                    continue;
                }
                if (!attrElem.Name.Equals("Attr"))
                {
                    continue;
                }
                var dcmTag = GetTagFromAttrElement(attrElem);
                DicomAttribute attr = collection[dcmTag];
                DicomAttributeSQ sq = attr as DicomAttributeSQ;
                if (sq != null)
                {
                    sq.ClearSequenceItems();
                    foreach (XmlNode itemNode in attrElem)
                    {
                        var itemElem = itemNode as XmlElement;
                        if (itemElem != null)
                        {
                            if (itemElem.Name.Equals("Item"))
                            {
                                var subResult = new ParseResult();
                                var subSequence = new DicomSequenceItem();
                                subResult.Attributes = subSequence;
                                DoParseAttributes(studyLoaderArgs, subResult, metaUri, itemElem);
                                sq.AddSequenceItem(subSequence);
                            }
                        }
                    }
                }
                else
                {
                    string val = null;
                    if (attrElem.Attributes["val"] != null)
                    {
                        val = attrElem.Attributes["val"].Value;
                        attr.SetStringValue(val);
                    }
                    else if (attrElem.Attributes["bid"] != null)
                    {
                        // This tag's value is binary. We're not interested in binary items other
                        // than the pixel data so we ignore them -- they will be added to the
                        // sequence but they will not have any value set.
                        if (attr.Tag.TagValue == DicomTags.PixelData)
                        {
                            result.PixelDataBid = attrElem.Attributes["bid"].Value;
                        }
                    }
                    else
                    {
                        Platform.Log(LogLevel.Warn, "Attr element {0} missing ",
                                     attr.Tag, val);
                    }
                }
            }
        }

        private static string XmlUnescapeString(string input)
        {
            string result = input ?? string.Empty;

            // unescape any value-encoded XML entities
            result = Regex.Replace(result, "&#[Xx]([0-9A-Fa-f]+);", m => ((char)int.Parse(m.Groups[1].Value, NumberStyles.AllowHexSpecifier)).ToString());
            result = Regex.Replace(result, "&#([0-9]+);", m => ((char)int.Parse(m.Groups[1].Value)).ToString());

            // unescape any entities encoded by SecurityElement.Escape (only <>'"&)
            result = result.Replace("&lt;", "<").
                Replace("&gt;", ">").
                Replace("&quot;", "\"").
                Replace("&apos;", "'").
                Replace("&amp;", "&");

            return result;
        }

        private static DicomTag GetTagFromAttrElement(XmlElement attrElem)
        {
            String tag = attrElem.Attributes["tag"].Value;

            DicomTag theTag;
            if (tag.StartsWith("$"))
            {
                theTag = DicomTagDictionary.GetDicomTag(tag.Substring(1));
            }
            else
            {
                var tagValue = uint.Parse(tag, NumberStyles.HexNumber);
                theTag = DicomTagDictionary.GetDicomTag(tagValue);
                var xmlVr = DicomVr.GetVR(attrElem.Attributes["vr"].Value);
                if (theTag == null)
                {
                    theTag = new DicomTag(tagValue, "Unknown tag", "UnknownTag", xmlVr, false, 1, uint.MaxValue, false);
                }
                if (!theTag.VR.Equals(xmlVr))
                {
                    theTag = new DicomTag(tagValue, theTag.Name, theTag.VariableName, xmlVr, theTag.MultiVR, theTag.VMLow,
                                          theTag.VMHigh, theTag.Retired);
                }
            }
            return theTag;
        }
    }
}
