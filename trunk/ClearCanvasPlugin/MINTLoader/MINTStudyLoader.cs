using System;
using System.Collections.Generic;
using System.Text;
using ClearCanvas.Common;
using ClearCanvas.ImageViewer.StudyManagement;
using ClearCanvas.ImageViewer.Services.Auditing;
using System.Xml;
using ClearCanvas.Dicom;

namespace MINTLoader
{
    [ExtensionOf(typeof(StudyLoaderExtensionPoint))]
    public class MINTStudyLoader : StudyLoader
    {
        private MINTApi.StudyKey _studyKey;
        private IEnumerator<InstanceMINTXml> _instances;

        public MINTStudyLoader()
            : base(MINTApi.LoaderName)
        {
        }

        protected override int OnStart(StudyLoaderArgs studyLoaderArgs)
        {
            _studyKey = studyLoaderArgs.Server as MINTApi.StudyKey;

            EventResult result = EventResult.Success;
            AuditedInstances loadedInstances = new AuditedInstances();
            try
            {
                XmlDocument doc = RetrieveHeaderXml();
                StudyMINTXml studyXml = new StudyMINTXml(studyLoaderArgs);
                studyXml.SetMemento(_studyKey.MetadataUri, doc);

                var allInstances = studyXml.AllInstances;
                _instances = allInstances.GetEnumerator();

                var patientId = studyXml[DicomTags.PatientId].GetString(0, "");
                var patientsName = studyXml[DicomTags.PatientsName].GetString(0, "");
                var studyInstanceUid = studyXml[DicomTags.StudyInstanceUid].GetString(0, "");

                loadedInstances.AddInstance(patientId, patientsName, studyInstanceUid);

                return allInstances.Count;

            }
            catch
            {
                result = EventResult.MajorFailure;
                throw;
            }
            finally
            {
                AuditHelper.LogOpenStudies(new string[] { this.Name }, loadedInstances, EventSource.CurrentUser, result);
            }
        }

        private XmlDocument RetrieveHeaderXml()
        {
            return MINTApi.GetStudyMetadata(_studyKey);
        }

        protected override SopDataSource LoadNextSopDataSource()
        {
            if (!_instances.MoveNext())
                return null;

            return new MINTSopDataSource(_instances.Current);

        }

        protected override Sop CreateSop(ISopDataSource dataSource)
        {
            return base.CreateSop(dataSource);
        }
    }
}
