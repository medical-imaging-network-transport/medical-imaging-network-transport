package org.nema.medical.mint.server.controller;

import org.nema.medical.mint.metadata.StudyIO;
import org.nema.medical.mint.metadata.StudyMetadata;
import org.nema.medical.mint.server.domain.Change;
import org.nema.medical.mint.server.domain.ChangeDAO;
import org.nema.medical.mint.server.domain.MINTStudy;
import org.nema.medical.mint.server.domain.StudyDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.annotation.PostConstruct;
import java.io.File;
import java.sql.Timestamp;

/**
 * @author Uli Bubenheimer
 */
@Controller
public class AdminController {

    @PostConstruct
    private void loadMissingStudies() throws Exception {
        if (studiesRoot != null && studiesRoot.exists()) {
            for (String uuid : studiesRoot.list()) {
                MINTStudy study = studyDAO.findStudy(uuid);
                if (study == null) {
                    // todo handle types other than DICOM
                    File studyDir = new File(studiesRoot, uuid);
                    File dicomDir = new File(studyDir, "DICOM");
                    StudyMetadata studyMeta = StudyIO.loadStudy(dicomDir);
                    File changelogDir = new File(studyDir, "changelog");
                    study = new MINTStudy(uuid, studyMeta);
                    studyDAO.insertStudy(study);

                    int changeNumber = 0;
                    while (true) {
                        File changeDir = new File(changelogDir, "" + changeNumber);
                        if (!changeDir.exists()) break;

                        Change change = new Change();
                        change.setDateTime(new Timestamp(changeDir.lastModified()));
                        change.setStudyID(uuid);
                        change.setIndex(changeNumber);
                        change.setType("DICOM");
                        change.setRemoteHost("localhost");
                        updateDAO.saveChange(change);

                        changeNumber++;
                    }
                    if (study.getStudyVersion() == null) {
                        study.setStudyVersion("" + (changeNumber - 1));
                    }
                    studyDAO.updateStudy(study);
                }
            }
        }
    }

    @Autowired
    protected File studiesRoot;
    @Autowired
    protected StudyDAO studyDAO = null;
    @Autowired
    protected ChangeDAO updateDAO = null;
}
