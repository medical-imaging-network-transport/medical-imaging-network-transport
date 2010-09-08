#if	UNIT_TESTS

#pragma warning disable 1591

using MINTLoader;
using System.Xml;
using ClearCanvas.Dicom;
using ClearCanvas.ImageViewer.StudyManagement;
using NUnit.Framework;

namespace MINTUnitTest
{
    
    
    /// <summary>
    ///This is a test class for InstanceMINTXmlTest and is intended
    ///to contain all InstanceMINTXmlTest Unit Tests
    ///</summary>
    [TestFixture]
    public class InstanceMINTXmlTest
    {
        private static XmlNode GetPackageNode(XmlDocument doc)
        {
            XmlNode rootNode = doc.FirstChild;
            while (rootNode != null && !rootNode.Name.Equals("MINTPackage"))
                rootNode = rootNode.NextSibling;

            return rootNode;
        }

        /// <summary>
        ///A test for InstanceMINTXml Constructor
        ///</summary>
        [Test]
        public void InstanceMINTXmlConstructorTest()
        {
            string xml =
                @"<Instance>
                    <Attributes>
                        <Attr tag='00100010' vr='PN' val='DOE^JON' />
                        <Attr tag='00080018' vr='UI' val='123.333' />
                        <Attr tag='00080032' vr='TM' val='134437.3' />
                        <Attr tag='00080033' vr='TM' val='134437.4' />
                        <Attr tag='00081140' vr='SQ' >
                            <Item id='0'>
                                <Attr tag='00081150' vr='UI' val='1.2.3.4' />
                                <Attr tag='00081155' vr='UI' val='4.3.2.1' />
                            </Item>
                            <Item id='0'>
                                <Attr tag='00081150' vr='UI' val='5.6.7.8' />
                                <Attr tag='00081155' vr='UI' val='9.8.7.6' />
                            </Item>
                        </Attr>
                        <Attr tag='7fe00010' vr='OW' bid='13' />
                    </Attributes>
                </Instance>"
                ;
            XmlDocument doc = new XmlDocument();
            doc.LoadXml(xml);
            var loaderArgs = new StudyLoaderArgs("123.111", null);
            var studyAttributes = new DicomAttributeCollection();
            var seriesAttributes = new DicomAttributeCollection();

            // Make sure one attribute is in conflict with an attribute parsed from the 
            // instance XML (the instance XML value should not take precedence here). This would
            // typically never happen.
            studyAttributes[DicomTags.StudyInstanceUid].SetStringValue("123.111");
            studyAttributes[DicomTags.PatientsName].SetStringValue("ABC^TUV");
            seriesAttributes[DicomTags.SeriesInstanceUid].SetStringValue("123.222");

            var instance = new InstanceMINTXml(loaderArgs, "http://some-uri/DICOM/metadata",
                doc.DocumentElement, studyAttributes, seriesAttributes, null);

            Assert.AreEqual("123.111", instance[DicomTags.StudyInstanceUid].GetString(0, ""));
            Assert.AreEqual("123.222", instance[DicomTags.SeriesInstanceUid].GetString(0, ""));
            Assert.AreEqual("ABC^TUV", instance[DicomTags.PatientsName].GetString(0, ""));
            Assert.AreEqual("123.333", instance[DicomTags.SopInstanceUid].GetString(0, ""));
            Assert.AreEqual("134437.3", instance[DicomTags.AcquisitionTime].GetString(0, ""));
            Assert.AreEqual("134437.4", instance[DicomTags.ContentTime].GetString(0, ""));
            Assert.IsInstanceOfType(typeof(DicomAttributeSQ), instance[DicomTags.ReferencedImageSequence]);

            var sq = instance[DicomTags.ReferencedImageSequence] as DicomAttributeSQ;
            Assert.IsNotNull(sq);

            Assert.AreEqual(2, sq.Count);
            var item = sq[0];
            Assert.AreEqual("1.2.3.4", item[DicomTags.ReferencedSopClassUid].GetString(0, ""));
            Assert.AreEqual("4.3.2.1", item[DicomTags.ReferencedSopInstanceUid].GetString(0, ""));

            item = sq[1];
            Assert.AreEqual("5.6.7.8", item[DicomTags.ReferencedSopClassUid].GetString(0, ""));
            Assert.AreEqual("9.8.7.6", item[DicomTags.ReferencedSopInstanceUid].GetString(0, ""));

            Assert.AreEqual("http://some-uri/DICOM/binaryitems/13",
                instance.PixelDataUri.AbsoluteUri);
        }
    }
}

#endif