#if	UNIT_TESTS

#pragma warning disable 1591

using MINTLoader;
using System.Xml;
using System.Collections.Generic;
using ClearCanvas.ImageViewer.StudyManagement;
using ClearCanvas.Dicom;
using NUnit.Framework;

namespace MINTUnitTest
{
    
    
    /// <summary>
    ///This is a test class for StudyMINTXmlTest and is intended
    ///to contain all StudyMINTXmlTest Unit Tests
    ///</summary>
    [TestFixture]
    public class StudyMINTXmlTest
    {
        // NOTE: the data in this meta XML sample is for testing purposes only. Not all of the
        // values are correct and many are missing. The structure is what is important here.
        public static readonly string studyMetadataXml =
            @"<?xml version='1.0' ?>
            <StudyMeta studyInstanceUID='123.111'>
                <Attributes>
                    <Attr tag='00080020' vr='DA' val='19100423'/>
                    <Attr tag='00100010' vr='PN' val='John'/>
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
                            <Attr tag='0020000e' vr='UI' val='123.222' />
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
                                <Attributes>
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
                                </Attributes>
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
                            <Attr tag='0020000e' vr='UI' val='123.333' />
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
                                <Attributes>
                                    <Attr tag='00080018' vr='UI' val='1235.222' />
                                    <Attr tag='00080032' vr='TM' val='134437.3' />
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
                                </Attributes>
                            </Instance>
                        </Instances>
                    </Series>
                </SeriesList>
            </StudyMeta>"
            ;

        /// <summary>
        ///A test for SetMemento
        ///</summary>
        [Test]
        public void SetMementoTest()
        {
            var args = new StudyLoaderArgs("123.111", null);
            var target = new StudyMINTXml(args); // TODO: Initialize to an appropriate value
            var doc = new XmlDocument();
            doc.LoadXml(studyMetadataXml);

            target.SetMemento("http://blah:9090/studies/metadata", doc);
            Assert.AreEqual("123.111", target[DicomTags.StudyInstanceUid].GetString(0, ""));
            var instanceList = target.AllInstances;
            Assert.AreEqual(4, instanceList.Count);
            Assert.AreEqual(2, target.SeriesList.Count);
            Assert.IsTrue(target.SeriesList.ContainsKey("123.222"));
            Assert.IsTrue(target.SeriesList.ContainsKey("123.333"));
            Assert.AreEqual(2, target.SeriesList["123.222"].Count);
            Assert.AreEqual(2, target.SeriesList["123.333"].Count);
            VerifyInstance1(target.SeriesList["123.222"][0]);
            VerifyInstance2(target.SeriesList["123.222"][1]);
            VerifyInstance3(target.SeriesList["123.333"][0]);
            VerifyInstance4(target.SeriesList["123.333"][1]);
        }

        private void VerifyStudyLevelAttrs(InstanceMINTXml instance)
        {
            Assert.AreEqual("19100423", instance[DicomTags.StudyDate].GetString(0, ""));
            Assert.AreEqual(DicomVr.DAvr, instance[DicomTags.StudyDate].Tag.VR);
            Assert.AreEqual("John", instance[DicomTags.PatientsName].GetString(0, ""));
            Assert.AreEqual(DicomVr.PNvr, instance[DicomTags.PatientsName].Tag.VR);
            Assert.IsInstanceOfType(typeof(DicomAttributeSQ), instance[DicomTags.ProcedureCodeSequence]);
            var sq = instance[DicomTags.ProcedureCodeSequence] as DicomAttributeSQ;
            Assert.IsNotNull(sq);
            Assert.AreEqual(sq.Count, 2);
            var item = sq[0];
            Assert.AreEqual("4706200", item[DicomTags.CodeValue].GetString(0, ""));
            Assert.AreEqual(DicomVr.SHvr, instance[DicomTags.CodeValue].Tag.VR);
            Assert.AreEqual("Siemens RIS", item[DicomTags.CodingSchemeDesignator].GetString(0, ""));
            Assert.AreEqual(DicomVr.SHvr, item[DicomTags.CodingSchemeDesignator].Tag.VR);
            Assert.AreEqual(string.Empty, item[DicomTags.CodingSchemeVersion].GetString(0, ""));
            Assert.AreEqual(DicomVr.SHvr, item[DicomTags.CodingSchemeVersion].Tag.VR);
            Assert.AreEqual("RIS detail service code", item[DicomTags.CodeMeaning].GetString(0, ""));
            Assert.AreEqual(DicomVr.LOvr, instance[DicomTags.CodeMeaning].Tag.VR);
            item = sq[1];
            Assert.AreEqual("14706200", item[DicomTags.CodeValue].GetString(0, ""));
            Assert.AreEqual(DicomVr.SHvr, instance[DicomTags.CodeValue].Tag.VR);
            Assert.AreEqual("1Siemens RIS", item[DicomTags.CodingSchemeDesignator].GetString(0, ""));
            Assert.AreEqual(DicomVr.SHvr, instance[DicomTags.CodingSchemeDesignator].Tag.VR);
            Assert.AreEqual("1", item[DicomTags.CodingSchemeVersion].GetString(0, ""));
            Assert.AreEqual(DicomVr.SHvr, item[DicomTags.CodingSchemeVersion].Tag.VR);
            Assert.AreEqual("1RIS detail service code", item[DicomTags.CodeMeaning].GetString(0, ""));
            Assert.AreEqual(DicomVr.LOvr, item[DicomTags.CodeMeaning].Tag.VR);
            item = null;
            Assert.AreEqual("123.111", instance[DicomTags.StudyInstanceUid].GetString(0, ""));
            Assert.AreEqual(DicomVr.UIvr, instance[DicomTags.StudyInstanceUid].Tag.VR);
        }

        private void VerifySeries1LevelAttrs(InstanceMINTXml instance)
        {
            Assert.AreEqual("20100423", instance[DicomTags.SeriesDate].GetString(0, ""));
            Assert.AreEqual(DicomVr.DAvr, instance[DicomTags.SeriesDate].Tag.VR);
            Assert.AreEqual("134611.000", instance[DicomTags.SeriesTime].GetString(0, ""));
            Assert.AreEqual(DicomVr.TMvr, instance[DicomTags.SeriesTime].Tag.VR);
            Assert.AreEqual("CT", instance[DicomTags.Modality].GetString(0, ""));
            Assert.AreEqual(DicomVr.CSvr, instance[DicomTags.Modality].Tag.VR);
            Assert.AreEqual("LEFT EXTR  3.0  B30f", instance[DicomTags.SeriesDescription].GetString(0, ""));
            Assert.AreEqual(DicomVr.LOvr, instance[DicomTags.SeriesDescription].Tag.VR);
            Assert.AreEqual("EXTREMITY", instance[DicomTags.BodyPartExamined].GetString(0, ""));
            Assert.AreEqual(DicomVr.CSvr, instance[DicomTags.BodyPartExamined].Tag.VR);
            Assert.AreEqual("123.222", instance[DicomTags.SeriesInstanceUid].GetString(0, ""));
            Assert.AreEqual(DicomVr.UIvr, instance[DicomTags.SeriesInstanceUid].Tag.VR);
            Assert.AreEqual("2", instance[DicomTags.SeriesNumber].GetString(0, ""));
            Assert.AreEqual(DicomVr.ISvr, instance[DicomTags.SeriesNumber].Tag.VR);
        }

        private void VerifySeries2LevelAttrs(InstanceMINTXml instance)
        {
            Assert.AreEqual("20100424", instance[DicomTags.SeriesDate].GetString(0, ""));
            Assert.AreEqual(DicomVr.DAvr, instance[DicomTags.SeriesDate].Tag.VR);
            Assert.AreEqual("134612.000", instance[DicomTags.SeriesTime].GetString(0, ""));
            Assert.AreEqual(DicomVr.TMvr, instance[DicomTags.SeriesTime].Tag.VR);
            Assert.AreEqual("MR", instance[DicomTags.Modality].GetString(0, ""));
            Assert.AreEqual(DicomVr.CSvr, instance[DicomTags.Modality].Tag.VR);
            Assert.AreEqual("RIGHT EXTR  3.0  B30f", instance[DicomTags.SeriesDescription].GetString(0, ""));
            Assert.AreEqual(DicomVr.LOvr, instance[DicomTags.SeriesDescription].Tag.VR);
            Assert.AreEqual("EXTREMITY", instance[DicomTags.BodyPartExamined].GetString(0, ""));
            Assert.AreEqual(DicomVr.CSvr, instance[DicomTags.BodyPartExamined].Tag.VR);
            Assert.AreEqual("123.333", instance[DicomTags.SeriesInstanceUid].GetString(0, ""));
            Assert.AreEqual(DicomVr.UIvr, instance[DicomTags.SeriesInstanceUid].Tag.VR);
            Assert.AreEqual("3", instance[DicomTags.SeriesNumber].GetString(0, ""));
            Assert.AreEqual(DicomVr.ISvr, instance[DicomTags.SeriesNumber].Tag.VR);
        }
        
        private void VerifyInstance1(InstanceMINTXml instance)
        {
            VerifyStudyLevelAttrs(instance);
            VerifySeries1LevelAttrs(instance);

            // Not bothering to verify VR here because we've pretty much verified it is being
            // parsed properly before this.
            Assert.AreEqual("1.2.840.10008.1.2.1", instance.TransferSyntax.UidString);
            Assert.AreEqual("1234.111", instance[DicomTags.SopInstanceUid].GetString(0, ""));
            Assert.AreEqual("134414.454106", instance[DicomTags.AcquisitionTime].GetString(0, ""));
            Assert.AreEqual("134414.454106", instance[DicomTags.ContentTime].GetString(0, ""));

            Assert.IsInstanceOfType(typeof(DicomAttributeSQ), instance[DicomTags.ReferencedImageSequence]);
            var sq = instance[DicomTags.ReferencedImageSequence] as DicomAttributeSQ;
            Assert.IsNotNull(sq);
            Assert.AreEqual(1, sq.Count);
            var item = sq[0];
            Assert.AreEqual("1.2.840.10008.5.1.4.1.1.2", item[DicomTags.ReferencedSopClassUid].GetString(0, ""));
            Assert.AreEqual("1.3.12.2.1107.5.1.4.54587.30000010042317111407800000777", item[DicomTags.ReferencedSopInstanceUid].GetString(0, ""));

            Assert.IsInstanceOfType(typeof(DicomAttributeSQ), instance[DicomTags.SourceImageSequence]);
            sq = instance[DicomTags.SourceImageSequence] as DicomAttributeSQ;
            Assert.IsNotNull(sq);
            Assert.AreEqual(1, sq.Count);
            item = sq[0];
            Assert.AreEqual("1.3.12.2.1107.5.9.1", item[DicomTags.ReferencedSopClassUid].GetString(0, ""));
            Assert.AreEqual("1.3.12.2.1107.5.1.4.54587.30000010042317111407800001012", item[DicomTags.ReferencedSopInstanceUid].GetString(0, ""));
        }

        private void VerifyInstance2(InstanceMINTXml instance)
        {
            VerifyStudyLevelAttrs(instance);
            VerifySeries1LevelAttrs(instance);

            // Not bothering to verify VR here because we've pretty much verified it is being
            // parsed properly before this.
            Assert.AreEqual("1.2.840.10008.1.2.1", instance.TransferSyntax.UidString);
            Assert.AreEqual("1234.222", instance[DicomTags.SopInstanceUid].GetString(0, ""));
            Assert.AreEqual("134435.373003", instance[DicomTags.AcquisitionTime].GetString(0, ""));
            Assert.AreEqual("134435.373003", instance[DicomTags.ContentTime].GetString(0, ""));

            Assert.IsInstanceOfType(typeof(DicomAttributeSQ), instance[DicomTags.ReferencedImageSequence]);
            var sq = instance[DicomTags.ReferencedImageSequence] as DicomAttributeSQ;
            Assert.IsNotNull(sq);
            Assert.AreEqual(1, sq.Count);
            var item = sq[0];
            Assert.AreEqual("1.2.840.10008.5.1.4.1.1.2", item[DicomTags.ReferencedSopClassUid].GetString(0, ""));
            Assert.AreEqual("1.3.12.2.1107.5.1.4.54587.30000010042317111407800000777", item[DicomTags.ReferencedSopInstanceUid].GetString(0, ""));

            Assert.IsInstanceOfType(typeof(DicomAttributeSQ), instance[DicomTags.SourceImageSequence]);
            sq = instance[DicomTags.SourceImageSequence] as DicomAttributeSQ;
            Assert.IsNotNull(sq);
            Assert.AreEqual(1, sq.Count);
            item = sq[0];
            Assert.AreEqual("1.3.12.2.1107.5.9.1", item[DicomTags.ReferencedSopClassUid].GetString(0, ""));
            Assert.AreEqual("1.3.12.2.1107.5.1.4.54587.30000010042317111407800001012", item[DicomTags.ReferencedSopInstanceUid].GetString(0, ""));
        }

        private void VerifyInstance3(InstanceMINTXml instance)
        {
            VerifyStudyLevelAttrs(instance);
            VerifySeries2LevelAttrs(instance);

            // Not bothering to verify VR here because we've pretty much verified it is being
            // parsed properly before this.
            Assert.AreEqual("1.2.840.10008.1.2.1", instance.TransferSyntax.UidString);
            Assert.AreEqual("1235.111", instance[DicomTags.SopInstanceUid].GetString(0, ""));
            Assert.AreEqual("134437.1", instance[DicomTags.AcquisitionTime].GetString(0, ""));
            Assert.AreEqual("134437.2", instance[DicomTags.ContentTime].GetString(0, ""));

            Assert.IsInstanceOfType(typeof(DicomAttributeSQ), instance[DicomTags.ReferencedImageSequence]);
            var sq = instance[DicomTags.ReferencedImageSequence] as DicomAttributeSQ;
            Assert.IsNotNull(sq);
            Assert.AreEqual(1, sq.Count);
            var item = sq[0];
            Assert.AreEqual("1.2.840.10008.5.1.4.1.1.2", item[DicomTags.ReferencedSopClassUid].GetString(0, ""));
            Assert.AreEqual("1.3.12.2.1107.5.1.4.54587.30000010042317111407800000777", item[DicomTags.ReferencedSopInstanceUid].GetString(0, ""));

            Assert.IsInstanceOfType(typeof(DicomAttributeSQ), instance[DicomTags.SourceImageSequence]);
            sq = instance[DicomTags.SourceImageSequence] as DicomAttributeSQ;
            Assert.IsNotNull(sq);
            Assert.AreEqual(1, sq.Count);
            item = sq[0];
            Assert.AreEqual("1.3.12.2.1107.5.9.1", item[DicomTags.ReferencedSopClassUid].GetString(0, ""));
            Assert.AreEqual("1.3.12.2.1107.5.1.4.54587.30000010042317111407800001012", item[DicomTags.ReferencedSopInstanceUid].GetString(0, ""));
        }

        private void VerifyInstance4(InstanceMINTXml instance)
        {
            VerifyStudyLevelAttrs(instance);
            VerifySeries2LevelAttrs(instance);

            // Not bothering to verify VR here because we've pretty much verified it is being
            // parsed properly before this.
            Assert.AreEqual("1.2.840.10008.1.2.1", instance.TransferSyntax.UidString);
            Assert.AreEqual("1235.222", instance[DicomTags.SopInstanceUid].GetString(0, ""));
            Assert.AreEqual("134437.3", instance[DicomTags.AcquisitionTime].GetString(0, ""));
            Assert.AreEqual("134437.4", instance[DicomTags.ContentTime].GetString(0, ""));

            Assert.IsInstanceOfType(typeof(DicomAttributeSQ), instance[DicomTags.ReferencedImageSequence]);
            var sq = instance[DicomTags.ReferencedImageSequence] as DicomAttributeSQ;
            Assert.IsNotNull(sq);
            Assert.AreEqual(1, sq.Count);
            var item = sq[0];
            Assert.AreEqual("1.2.840.10008.5.1.4.1.1.2", item[DicomTags.ReferencedSopClassUid].GetString(0, ""));
            Assert.AreEqual("1.3.12.2.1107.5.1.4.54587.30000010042317111407800000777", item[DicomTags.ReferencedSopInstanceUid].GetString(0, ""));

            Assert.IsInstanceOfType(typeof(DicomAttributeSQ), instance[DicomTags.SourceImageSequence]);
            sq = instance[DicomTags.SourceImageSequence] as DicomAttributeSQ;
            Assert.IsNotNull(sq);
            Assert.AreEqual(1, sq.Count);
            item = sq[0];
            Assert.AreEqual("1.3.12.2.1107.5.9.1", item[DicomTags.ReferencedSopClassUid].GetString(0, ""));
            Assert.AreEqual("1.3.12.2.1107.5.1.4.54587.30000010042317111407800001012", item[DicomTags.ReferencedSopInstanceUid].GetString(0, ""));
        }
    }

}

#endif
