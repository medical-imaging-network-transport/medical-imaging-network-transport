using MINTLoader;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using System.Collections.Generic;
using System.IO;
using ClearCanvas.ImageViewer.StudyManagement;
using ClearCanvas.Dicom.Iod;

namespace MINTUnitTest
{
    
    
    /// <summary>
    ///This is a test class for StudyListParserTest and is intended
    ///to contain all StudyListParserTest Unit Tests
    ///</summary>
    [TestFixture]
    public class MINTApiTest
    {


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
        ///A test for ParseStudiesResponse
        ///</summary>
        [TestMethod()]
        public void ParseStudiesResponseTest()
        {
            var xhtml =
                @"<html xmlns='http://www.w3.org/1999/xhtml' xml:lang='en' lang='en'> 
                    <meta http-equiv='Content-Type' content='text/html;charset=utf-8' /> 
                    <head> 
                        <title>List of all studies</title> 
                    </head> 
                    <body> 
                        <h1>List of all studies</h1> 
                        <ul>
                            <li> 
                                <ul> 
                                    <li class='UID'>study0</li> 
                                    <li class='meta'><a href='meta0'>Meta Data</a></li> 
                                    <li class='summary'><a href='summary0'>Study Summary</a></li> 
                                </ul> 
                            </li> 
                            <li> 
                                <ul> 
                                    <li class='UID'>study1</li> 
                                    <li class='meta'><a href='meta1'>Meta Data</a></li> 
                                    <li class='summary'><a href='summary1'>Study Summary</a></li> 
                                </ul> 
                            </li> 
                        </ul> 
                    </body> 
                </html> ";

            Stream xmlStrm = new MemoryStream(System.Text.Encoding.GetEncoding("utf-8").GetBytes(xhtml));

            var actual = new List<MINTApi.StudyKey>(MINTApi.ParseStudiesResponse(xmlStrm));

            Assert.AreEqual(2, actual.Count);
            Assert.AreEqual("meta0", actual[0].metadataUri);
            Assert.AreEqual("summary0", actual[0].summaryUri);
            Assert.AreEqual("study0", actual[0].studyUID);

            Assert.AreEqual("meta1", actual[1].metadataUri);
            Assert.AreEqual("summary1", actual[1].summaryUri);
            Assert.AreEqual("study1", actual[1].studyUID);
        }

        static string studySummaryXml =
                @"<html xmlns='http://www.w3.org/1999/xhtml' xml:lang='en' lang='en'> 
                    <meta http-equiv='Content-Type' content='text/html;charset=utf-8' /> 
                <head>
                    <title>Study Summary for 1.2.124.113532.8.29774.40333.20061222.114410.3070.xml</title>
                </head>
                <body>
                    <h1>Study Summary for 1.2.124.113532.8.29774.40333.20061222.114410.3070.xml</h1>
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
        [TestMethod()]
        public void FullStudyItemFromXmlTest()
        {
            var item = new StudyItem("11111", null, "");

            Stream xmlStrm = new MemoryStream(System.Text.Encoding.GetEncoding("utf-8").GetBytes(studySummaryXml));
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
            Assert.AreEqual("1.2.3.4", item.StudyInstanceUid);
        }
    }
}
