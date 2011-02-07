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
                if (studyDAO.findStudy(uuid) == null) {
                    final File studyDir = new File(studiesRoot, uuid);
                    {
                        final File dicomDir = new File(studyDir, "DICOM");
                        final StudyMetadata studyMeta = StudyIO.loadStudy(dicomDir);
                        final MINTStudy study = new MINTStudy(uuid, studyMeta);
                        studyDAO.insertStudy(study);
                    }
                    
                    final File changelogDir = new File(studyDir, "changelog");
                    final File[] changeDirs = changelogDir.listFiles();
                    for (final File changeDir: changeDirs) {
                        //Only numbers are allowed as names
                        final int changeNumber = Integer.parseInt(changeDir.getName());

                        final Change change = new Change();
                        change.setDateTime(new Timestamp(changeDir.lastModified()));
                        change.setStudyID(uuid);
                        change.setIndex(changeNumber);
                        final StudyMetadata metadata = StudyIO.loadStudy(changeDir);
                        change.setType(metadata.getType());
                        change.setRemoteHost("localhost");
                        change.setOperation(changeNumber == 0 ?
                                org.nema.medical.mint.changelog.Change.OPERATION_CREATE :
                                org.nema.medical.mint.changelog.Change.OPERATION_UPDATE);
                        updateDAO.saveChange(change);
                    }
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
