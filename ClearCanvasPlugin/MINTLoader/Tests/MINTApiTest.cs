#if	UNIT_TESTS

#pragma warning disable 1591

using MINTLoader;
using System.Collections.Generic;
using System.IO;
using ClearCanvas.ImageViewer.StudyManagement;
using ClearCanvas.Dicom.Iod;
using NUnit.Framework;

namespace MINTUnitTest
{
    
    
    /// <summary>
    ///This is a test class for StudyListParserTest and is intended
    ///to contain all StudyListParserTest Unit Tests
    ///</summary>
    [TestFixture]
    public class MINTApiTest
    {
        [Test]
        public void StudyKeyTest()
        {
            var studyKey = new MINTApi.StudyKey("http://abc/xyz", "1.2.3.4", "xyz/metadata", "jkl/summary");
            Assert.AreEqual("1.2.3.4", studyKey.StudyUid);
            Assert.AreEqual("http://abc", studyKey.HostUri);
            Assert.AreEqual("http://abc/xyz/metadata", studyKey.MetadataUri);
            Assert.AreEqual("http://abc/jkl/summary", studyKey.SummaryUri);

            studyKey = new MINTApi.StudyKey("http://abc", "1.2.3.4", "xyz/metadata", "jkl/summary");
            Assert.AreEqual("1.2.3.4", studyKey.StudyUid);
            Assert.AreEqual("http://abc", studyKey.HostUri);
            Assert.AreEqual("http://abc/xyz/metadata", studyKey.MetadataUri);
            Assert.AreEqual("http://abc/jkl/summary", studyKey.SummaryUri);

            studyKey = new MINTApi.StudyKey("abc", "1.2.3.4", "xyz/metadata", "jkl/summary");
            Assert.AreEqual("1.2.3.4", studyKey.StudyUid);
            Assert.AreEqual("abc", studyKey.HostUri);
            Assert.AreEqual("abc/xyz/metadata", studyKey.MetadataUri);
            Assert.AreEqual("abc/jkl/summary", studyKey.SummaryUri);
        }

        /// <summary>
        ///A test for ParseStudiesResponse
        ///</summary>
        [Test]
        public void ParseStudiesResponseTest()
        {
            var xhtml =
                @"<!DOCTYPE html PUBLIC ""-//W3C//DTD HTML 4.01 Strict//EN"" ""http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd"">
                  <html xmlns='http://www.w3.org/1999/xhtml' xml:lang='en' lang='en'> 
                    <meta http-equiv='Content-Type' content='text/html;charset=utf-8' /> 
                    <head> 
                        <title>Studies</title> 
                    </head> 
                    <body> 
                        <h1>Studies</h1> 
                        <ol>
                            <li> 
                                <dl> 
                                    <dt>StudyID</dt>
                                    <dd class='StudyID'>study0</dd> 
                                    <dt>Links</dt>
                                    <dd class='StudySummary'><a href='summary0'>Summary</a></dd>
                                    <dd class='StudyMetadata'><a href='meta0'>Metadata</a></dd>
                                    <dd class='StudyChangeLog'><a href='meta0'>ChangeLog</a></dd>
                                </dl> 
                            </li> 
                            <li> 
                                <dl> 
                                    <dt>StudyID</dt>
                                    <dd class='StudyID'>study1</dd> 
                                    <dt>Links</dt>
                                    <dd class='StudySummary'><a href='summary1'>Summary</a></dd>
                                    <dd class='StudyMetadata'><a href='meta1'>Metadata</a></dd>
                                    <dd class='StudyChangeLog'><a href='meta1'>ChangeLog</a></dd>
                                </dl> 
                            </li> 
                        </ol> 
                    </body> 
                </html> ";

            Stream xmlStrm = new MemoryStream(System.Text.Encoding.UTF8.GetBytes(xhtml));

            var actual = new List<MINTApi.StudyKey>(MINTApi.ParseStudiesResponse("funnyService", xmlStrm));

            Assert.AreEqual(2, actual.Count);
            Assert.AreEqual("funnyService/meta0", actual[0].MetadataUri);
            Assert.AreEqual("funnyService/summary0", actual[0].SummaryUri);
            Assert.AreEqual("study0", actual[0].StudyUid);

            Assert.AreEqual("funnyService/meta1", actual[1].MetadataUri);
            Assert.AreEqual("funnyService/summary1", actual[1].SummaryUri);
            Assert.AreEqual("study1", actual[1].StudyUid);
        }

        static string studySummaryXml =
                @"<!DOCTYPE html PUBLIC ""-//W3C//DTD HTML 4.01 Strict//EN"" ""http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd"">
                  <html xmlns='http://www.w3.org/1999/xhtml' xml:lang='en' lang='en'> 
                    <meta http-equiv='Content-Type' content='text/html;charset=utf-8' /> 
                    <head>
                        <title>Study Summary for 1.2.3.4</title>
                    </head>
                    <body>
                        <h1>Study Summary for 1.2.3.4</h1>
                        <ul>
                            <li>
                                <ul>
                                    <li class='tag'>00080020</li>
                                    <li class='description'>StudyDate</li>
                                    <li class='value'>20030303</li>
                                </ul>
                            </li>
                            <li>
                                <ul>
                                    <li class='tag'>00080030</li>
                                    <li class='description'>StudyTime</li>
                                    <li class='value'>194001.000000</li>
                                </ul>
                            </li>
                            <li>
                                <ul>
                                    <li class='tag'>00080050</li>
                                    <li class='description'>AccessionNumber</li>
                                    <li class='value'>7570281</li>
                                </ul>
                            </li>
                            <li>
                                <ul>
                                    <li class='tag'>00080090</li>
                                    <li class='description'>ReferringPhysiciansName</li>
                                    <li class='value'>REF^PHYS^HERE</li>
                                </ul>
                            </li>
                            <li>
                                <ul>
                                    <li class='tag'>00081030</li>
                                    <li class='description'>StudyDescription</li>
                                    <li class='value'>STUDY DESCRIPTION HERE</li>
                                </ul>
                            </li>
                            <li>
                                <ul>
                                    <li class='tag'>00081060</li>
                                    <li class='description'>NameOfPhysiciansReadingStudy</li>
                                    <li class='value'>NAME^PHYS^HERE</li>
                                </ul>
                            </li>
                            <li>
                                <ul>
                                    <li class='tag'>00100010</li>
                                    <li class='description'>PatientsName</li>
                                    <li class='value'>NAME^HERE</li>
                                </ul>
                            </li>
                            <li>
                                <ul>
                                    <li class='tag'>00100020</li>
                                    <li class='description'>PatientID</li>
                                    <li class='value'>1122334455</li>
                                </ul>
                            </li>
                            <li>
                                <ul>
                                    <li class='tag'>00100030</li>
                                    <li class='description'>PatientsBirthDate</li>
                                    <li class='value'>19920601</li>
                                </ul>
                            </li>
                            <li>
                                <ul>
                                    <li class='tag'>00100040</li>
                                    <li class='description'>PatientsSex</li>
                                    <li class='value'>M</li>
                                </ul>
                            </li>
                            <li>
                                <ul>
                                    <li class='tag'>00101010</li>
                                    <li class='description'>PatientsAge</li>
                                    <li class='value'>010Y</li>
                                </ul>
                            </li>
                            <li>
                                <ul>
                                    <li class='tag'>0020000D</li>
                                    <li class='description'>StudyInstanceUID</li>
                                    <li class='value'>1.2.3.4</li>
                                </ul>
                            </li>
                            <li>
                                <ul>
                                    <li class='tag'>00200010</li>
                                    <li class='description'>StudyID</li>
                                    <li class='value'>7570281</li>
                                </ul>
                            </li>
                        </ul>
                    </body>
                </html>"
                ;

        /// <summary>
        ///A test for FullStudyItemFromXml
        ///</summary>
        [Test]
        public void FullStudyItemFromXmlTest()
        {
            var item = new StudyItem("11111", null, "");

            Stream xmlStrm = new MemoryStream(System.Text.Encoding.UTF8.GetBytes(studySummaryXml));
            MINTApi.FullStudyItemFromXml(item, xmlStrm);

            Assert.AreEqual("20030303", item.StudyDate);
            Assert.AreEqual("194001.000000", item.StudyTime);
            Assert.AreEqual("7570281", item.AccessionNumber);
            Assert.AreEqual(new PersonName("REF^PHYS^HERE"), item.ReferringPhysiciansName);
            Assert.AreEqual(new PersonName("NAME^HERE"), item.PatientsName);
            Assert.AreEqual("1122334455", item.PatientId);
            Assert.AreEqual("19920601", item.PatientsBirthDate);
            Assert.AreEqual("M", item.PatientsSex);
            Assert.AreEqual("7570281", item.StudyId);
        }
    }
}

#endif
