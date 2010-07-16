using System;
using System.Collections.Generic;
using System.Text;
using ClearCanvas.ImageViewer.StudyManagement;
using ClearCanvas.Common;
using System.Net;
using ClearCanvas.Dicom.Iod;
using System.IO;
using System.Xml;

namespace MINTLoader
{
    [ExtensionOf(typeof(StudyFinderExtensionPoint))]
    public class MINTStudyFinder : StudyFinder
    {
        public MINTStudyFinder()
            : base(MINTApi.FinderName)
        {

        }

        public override StudyItemList Query(QueryParameters queryParams, object targetServer)
        {
            ApplicationEntity selectedServer = (ApplicationEntity)targetServer;

            StudyItemList list = new StudyItemList();
            foreach (var key in MINTApi.GetStudies(selectedServer.Host))
            {
                var item = new StudyItem(key.StudyUid, key, MINTApi.LoaderName);

                MINTApi.FillStudyItem(item, key);
                list.Add(item);
            }

            return list;
        }
    }
}
