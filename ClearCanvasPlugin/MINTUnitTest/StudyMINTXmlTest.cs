using MINTLoader;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using System.Xml;
using System.Collections.Generic;
using ClearCanvas.ImageViewer.StudyManagement;
using ClearCanvas.Dicom;

namespace MINTUnitTest
{
    
    
    /// <summary>
    ///This is a test class for StudyMINTXmlTest and is intended
    ///to contain all StudyMINTXmlTest Unit Tests
    ///</summary>
    [TestClass()]
    public class StudyMINTXmlTest
    {
        // NOTE: the data in this meta XML sample is for testing purposes only. Not all of the
        // values are correct and many are missing. The structure is what is important here.
        public static readonly string studyMetadataXml =
            @"<?xml version='1.0' ?>
            <StudyMeta studyInstanceUID='123.111'>
                <Attributes>
                    <Attr tag='00080020' vr='DA' val='19100423'/>
                    <Attr tag='00080090' vr='PN' bid='John'/>
                    <Attr tag='00081032' vr='SQ'>
                        <Item id='0'>
                            <Attr tag='00080100' vr='SH' val='4706200' />
                            <Attr tag='00080102' vr='SH' val='Siemens RIS' />
                            <Attr tag='00080103' vr='SH' val='' />
                            <Attr tag='00080104' vr='LO' val='RIS detail service code' />
                        </Item>
                        <Item id='1'>
                            <Attr tag='00080100' vr='SH' val='14706200' />
                            <Attr tag='00080102' vr='SH' val='1Siemens RIS' />
                            <Attr tag='00080103' vr='SH' val='1' />
                            <Attr tag='00080104' vr='LO' val='1RIS detail service code' />
                        </Item>
                    </Attr>
                    <Attr tag='0020000d' vr='UI' val='123.111' />
                </Attributes>
                <SeriesList>
                    <Series seriesInstanceUID='123.222'>
                        <Attributes>
                            <Attr tag='00080021' vr='DA' val='20100423' />
                            <Attr tag='00080031' vr='TM' val='134611.000' />
                            <Attr tag='00080060' vr='CS' val='CT' />
                            <Attr tag='0008103e' vr='LO' val='LEFT EXTR  3.0  B30f' />
                            <Attr tag='00180015' vr='CS' val='EXTREMITY' />
                            <Attr tag='0020000e' vr='UI' val='123.222 />
                            <Attr tag='00200011' vr='IS' val='2' />
                        </Attributes>
                        <NormalizedInstanceAttributes>
                            <Attr tag='00020000' vr='UL' val='196' />
                            <Attr tag='00020001' vr='OB' bid='15' />
                            <Attr tag='00020002' vr='UI' val='1.2.840.10008.5.1.4.1.1.2' />
                            <Attr tag='00020010' vr='UI' val='1.2.840.10008.1.2.4.90' />
                            <Attr tag='00020012' vr='UI' val='1.2.826.0.1.3680043.2.133.1.1' />
                            <Attr tag='00020013' vr='SH' val='5.30.19' />
                            <Attr tag='00080008' vr='CS' val='ORIGINAL\PRIMARY\AXIAL\CT_SOM5 SPI' />
                        </NormalizedInstanceAttributes>
                        <Instances>
                            <Instance transferSyntaxUID='1.2.840.10008.1.2.1' sopInstanceUID='1234.111'>
                                <Attr tag='00080018' vr='UI' val='1234.111' />
                                <Attr tag='00080032' vr='TM' val='134414.454106' />
                                <Attr tag='00080033' vr='TM' val='134414.454106' />
                                <Attr tag='00081140' vr='SQ' >
                                    <Item id='0'>
                                        <Attr tag='00081150' vr='UI' val='1.2.840.10008.5.1.4.1.1.2' />
                                        <Attr tag='00081155' vr='UI' val='1.3.12.2.1107.5.1.4.54587.30000010042317111407800000777' />
                                    </Item>
                                </Attr>
                                <Attr tag='00082112' vr='SQ' >
                                    <Item id='0'>
                                        <Attr tag='00081150' vr='UI' val='1.3.12.2.1107.5.9.1' />
                                        <Attr tag='00081155' vr='UI' val='1.3.12.2.1107.5.1.4.54587.30000010042317111407800001012' />
                                    </Item>
                                </Attr>
                            </Instance>
                            <Instance transferSyntaxUID='1.2.840.10008.1.2.1' sopInstanceUID='1234.222'>
                                <Attributes>
                                    <Attr tag='00080018' vr='UI' val='1234.222' />
                                    <Attr tag='00080032' vr='TM' val='134435.373003' />
                                    <Attr tag='00080033' vr='TM' val='134435.373003' />
                                    <Attr tag='00081140' vr='SQ' >
                                        <Item id='0'>
                                            <Attr tag='00081150' vr='UI' val='1.2.840.10008.5.1.4.1.1.2' />
                                            <Attr tag='00081155' vr='UI' val='1.3.12.2.1107.5.1.4.54587.30000010042317111407800000777' />
                                        </Item>
                                    </Attr>
                                    <Attr tag='00082112' vr='SQ' >
                                        <Item id='0'>
                                            <Attr tag='00081150' vr='UI' val='1.3.12.2.1107.5.9.1' />
                                            <Attr tag='00081155' vr='UI' val='1.3.12.2.1107.5.1.4.54587.30000010042317111407800001012' />
                                        </Item>
                                    </Attr>
                                </Attributes>
                            </Instance>
                        </Instances>
                    </Series>
                    <Series seriesInstanceUID='123.333'>
                        <Attributes>
                            <Attr tag='00080021' vr='DA' val='20100424' />
                            <Attr tag='00080031' vr='TM' val='134612.000' />
                            <Attr tag='00080060' vr='CS' val='MR' />
                            <Attr tag='0008103e' vr='LO' val='RIGHT EXTR  3.0  B30f' />
                            <Attr tag='00180015' vr='CS' val='EXTREMITY' />
                            <Attr tag='0020000e' vr='UI' val='123.333 />
                            <Attr tag='00200011' vr='IS' val='3' />
                        </Attributes>
                        <NormalizedInstanceAttributes>
                            <Attr tag='00020000' vr='UL' val='196' />
                            <Attr tag='00020001' vr='OB' bid='0' />
                            <Attr tag='00020002' vr='UI' val='1.2.840.10008.5.1.4.1.1.2' />
                            <Attr tag='00020010' vr='UI' val='1.2.840.10008.1.2.4.90' />
                            <Attr tag='00020012' vr='UI' val='1.2.826.0.1.3680043.2.133.1.1' />
                            <Attr tag='00020013' vr='SH' val='5.30.19' />
                            <Attr tag='00080008' vr='CS' val='ORIGINAL\PRIMARY\AXIAL\CT_SOM5 SPI' />
                        </NormalizedInstanceAttributes>
                        <Instances>
                            <Instance transferSyntaxUID='1.2.840.10008.1.2.1' sopInstanceUID='1235.111'>
                                <Attributes>
                                    <Attr tag='00080018' vr='UI' val='1235.111' />
                                    <Attr tag='00080032' vr='TM' val='134437.1' />
                                    <Attr tag='00080033' vr='TM' val='134437.2' />
                                    <Attr tag='00081140' vr='SQ' >
                                        <Item id='0'>
                                            <Attr tag='00081150' vr='UI' val='1.2.840.10008.5.1.4.1.1.2' />
                                            <Attr tag='00081155' vr='UI' val='1.3.12.2.1107.5.1.4.54587.30000010042317111407800000777' />
                                        </Item>
                                    </Attr>
                                    <Attr tag='00082112' vr='SQ' >
                                        <Item id='0'>
                                            <Attr tag='00081150' vr='UI' val='1.3.12.2.1107.5.9.1' />
                                            <Attr tag='00081155' vr='UI' val='1.3.12.2.1107.5.1.4.54587.30000010042317111407800001012' />
                                        </Item>
                                    </Attr>
                                </Attributes>
                            </Instance>
                            <Instance transferSyntaxUID='1.2.840.10008.1.2.1' sopInstanceUID='1235.222'>
                                <Attr tag='00080018' vr='UI' val='1235.222' />
                                <Attr tag='00080032' vr='TM' val='134437.3 />
                                <Attr tag='00080033' vr='TM' val='134437.4' />
                                <Attr tag='00081140' vr='SQ' >
                                    <Item id='0'>
                                        <Attr tag='00081150' vr='UI' val='1.2.840.10008.5.1.4.1.1.2' />
                                        <Attr tag='00081155' vr='UI' val='1.3.12.2.1107.5.1.4.54587.30000010042317111407800000777' />
                                    </Item>
                                </Attr>
                                <Attr tag='00082112' vr='SQ' >
                                    <Item id='0'>
                                        <Attr tag='00081150' vr='UI' val='1.3.12.2.1107.5.9.1' />
                                        <Attr tag='00081155' vr='UI' val='1.3.12.2.1107.5.1.4.54587.30000010042317111407800001012' />
                                    </Item>
                                </Attr>
                            </Instance>
                        </Instances>
                    </Series>
                </SeriesList>
            </StudyMeta>"
            ;

        private TestContext testContextInstance;

        /// <summary>
        ///Gets or sets the test context which provides
        ///information about and functionality for the current test run.
        ///</summary>
        public TestContext TestContext
        {
            get
            {
                return testContextInstance;
            }
            set
            {
                testContextInstance = value;
            }
        }

        #region Additional test attributes
        // 
        //You can use the following additional attributes as you write your tests:
        //
        //Use ClassInitialize to run code before running the first test in the class
        //[ClassInitialize()]
        //public static void MyClassInitialize(TestContext testContext)
        //{
        //}
        //
        //Use ClassCleanup to run code after all tests in a class have run
        //[ClassCleanup()]
        //public static void MyClassCleanup()
        //{
        //}
        //
        //Use TestInitialize to run code before running each test
        //[TestInitialize()]
        //public void MyTestInitialize()
        //{
        //}
        //
        //Use TestCleanup to run code after each test has run
        //[TestCleanup()]
        //public void MyTestCleanup()
        //{
        //}
        //
        #endregion


        /// <summary>
        ///A test for SetMemento
        ///</summary>
        [TestMethod()]
        public void SetMementoTest()
        {
            //var args = new StudyLoaderArgs("123.111", null);
            //StudyMINTXml target = new StudyMINTXml(args); // TODO: Initialize to an appropriate value
            //XmlDocument doc = new XmlDocument();
            //doc.LoadXml(studyMetadataXml);

            //target.SetMemento("http://blah:9090/studies/metadata", doc);
            //Assert.AreEqual("123.111", target[DicomTags.StudyInstanceUid].GetString(0, ""));
            //var instanceList = new List<InstanceMINTXml>(target.AllInstances);
            //Assert.AreEqual(4, instanceList.Count);
        }
    }
}
