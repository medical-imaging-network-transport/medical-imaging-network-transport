using System;
using System.Collections.Generic;
using System.Text;
using ClearCanvas.ImageViewer.Explorer.Dicom;
using ClearCanvas.Common;
using ClearCanvas.Desktop.Actions;
using ClearCanvas.ImageViewer;
using ClearCanvas.Desktop;
using ClearCanvas.ImageViewer.StudyManagement;
using ClearCanvas.ImageViewer.Configuration;
using ClearCanvas.ImageViewer.Common;
using ClearCanvas.Dicom.Iod;

namespace MINTLoader
{
    [ButtonAction("MINT", "dicomstudybrowser-toolbar/ToolbarOpenMINT", "OpenStudy")]
    [MenuAction("MINT", "dicomstudybrowser-contextmenu/MenuOpenMINT", "OpenStudy")]
    [EnabledStateObserver("MINT", "Enabled", "EnabledChanged")]
    [Tooltip("MINT", "TooltipOpenMINT")]
    [IconSet("MINT", IconScheme.Colour, "Icons.OpenToolSmall.png", "Icons.OpenToolSmall.png", "Icons.OpenToolSmall.png")]

    [ViewerActionPermission("MINT", ClearCanvas.ImageViewer.AuthorityTokens.Study.Open)]

    [ExtensionOf(typeof(StudyBrowserToolExtensionPoint))]
    class OpenMINTStudyTool : StudyBrowserTool
    {
        protected override void OnSelectedServerChanged(object sender, EventArgs e)
        {
            UpdateEnabled();
        }

        protected override void OnSelectedStudyChanged(object sender, EventArgs e)
        {
            UpdateEnabled();
        }

        private void UpdateEnabled()
        {
            if (Context.SelectedStudy != null)
            {
                foreach (var studyItem in Context.SelectedStudies)
                {
                    if (MINTStudyKey(studyItem) != null)
                    {
                        Enabled = true;
                        break;
                    }
                }
            }
            else
            {
                Enabled = false;
            }
        }

        public void OpenStudy()
        {
            try
            {
                int numberOfSelectedStudies = GetNumberOfSelectedStudies();
                if (numberOfSelectedStudies == 0)
                    return;

                if (!PermissionsHelper.IsInRole(ClearCanvas.ImageViewer.AuthorityTokens.Study.Open))
                {
                    Context.DesktopWindow.ShowMessageBox(SR.MessageOpenStudyPermissionDenied, MessageBoxActions.Ok);
                    return;
                }

                int numberOfLoadableStudies = GetNumberOfLoadableStudies();
                if (numberOfLoadableStudies != numberOfSelectedStudies)
                {
                    int numberOfNonLoadableStudies = numberOfSelectedStudies - numberOfLoadableStudies;
                    string message;
                    if (numberOfSelectedStudies == 1)
                    {
                        message = SR.MessageCannotOpenNonStreamingStudy;
                    }
                    else
                    {
                        if (numberOfNonLoadableStudies == 1)
                            message = SR.MessageOneNonStreamingStudyCannotBeOpened;
                        else
                            message = String.Format(SR.MessageFormatXNonStreamingStudiesCannotBeOpened, numberOfNonLoadableStudies);
                    }

                    Context.DesktopWindow.ShowMessageBox(message, MessageBoxActions.Ok);
                    return;
                }

                OpenStudyHelper helper = new OpenStudyHelper();
                helper.WindowBehaviour = ViewerLaunchSettings.WindowBehaviour;
                helper.AllowEmptyViewer = ViewerLaunchSettings.AllowEmptyViewer;
                helper.LoadPriors = false;

                foreach (StudyItem study in Context.SelectedStudies)
                {
                    var key = MINTStudyKey(study);

                    if (key != null)
                    {
                        helper.AddStudy(study.StudyInstanceUid, study.Server, study.StudyLoaderName);
                    }
                }

                helper.Title = ImageViewerComponent.CreateTitle(GetSelectedPatients());
                helper.OpenStudies();
            }
            catch (Exception e)
            {
                ExceptionHandler.Report(e, Context.DesktopWindow);
            }
        }

        private IEnumerable<IPatientData> GetSelectedPatients()
        {
            if (base.Context.SelectedStudy != null)
            {
                foreach (StudyItem studyItem in base.Context.SelectedStudies)
                    yield return studyItem;
            }
        }

        private int GetNumberOfSelectedStudies()
        {
            if (Context.SelectedStudy == null)
                return 0;

            return Context.SelectedStudies.Count;
        }

        private int GetNumberOfLoadableStudies()
        {
            int number = 0;

            if (Context.SelectedStudy != null)
            {
                foreach (StudyItem study in Context.SelectedStudies)
                {
                    if (MINTStudyKey(study) != null)
                    {
                        ++number;
                    }
                }
            }

            return number;
        }

        private static MINTApi.StudyKey MINTStudyKey(StudyItem studyItem)
        {
            MINTApi.StudyKey key = studyItem.Server as MINTApi.StudyKey;
            if (key == null && Properties.Settings.Default.EnableXRef)
            {
                //we must be looking at studies listed in another server - see if the study is available in
                //the MINT service and cache it if it is.
                if (!_cachedKeys.TryGetValue(studyItem.StudyInstanceUid, out key))
                {
                    key = MINTApi.GetStudyKey(Properties.Settings.Default.MINTHostname, 
                                              studyItem.StudyInstanceUid);

                    if (key != null)
                    {
                        _cachedKeys[studyItem.StudyInstanceUid] = key;
                    }
                }
            }
            return key;
        }

        private static Dictionary<string, MINTApi.StudyKey> _cachedKeys = new Dictionary<string, MINTApi.StudyKey>();
    }
}
