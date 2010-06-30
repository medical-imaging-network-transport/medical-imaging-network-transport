package org.nema.medical.mint;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

import org.apache.log4j.Logger;
import org.dcm4che2.data.DicomObject;
import org.dcm4che2.data.Tag;
import org.dcm4che2.io.DicomInputStream;
import org.nema.medical.mint.InboundStudyMap.StudyInfo;
import org.springframework.beans.factory.annotation.Autowired;

public final class HandleFileItemProcessor {

    public File process(final File item) throws Exception {
        //Ensure it's a plain file
        assert item.isFile();

        final String rxmsgFileName = item.getName();
        assert rxmsgFileName.endsWith(".rxmsg");
        final String dicomFileName = rxmsgFileName.substring(0, rxmsgFileName.length() - ".rxmsg".length());
        final File dir = item.getParentFile();
        //TODO skip for debugging only
//        item.renameTo(new File(item.getParentFile(), dicomFileName + ".rxpend"));

        final File dicomFile = new File(dir, dicomFileName);
        assert dicomFile.exists();

        final DicomInputStream dicomStream = new DicomInputStream(dicomFile);
        final String studyInstanceUID;
        try {
            final DicomObject dcmObj = dicomStream.readDicomObject();
            studyInstanceUID = dcmObj.getString(Tag.StudyInstanceUID);
        } finally {
            dicomStream.close();
        }

        StudyInfo studyInfo = inboundStudyMap.map.get(studyInstanceUID);
        if (studyInfo == null) {
            // todo look up study in DB here
            // load existing study from .gpb file

            studyInfo = new StudyInfo();
            studyInfo.sopInstanceFiles = new ArrayList<File>();
            inboundStudyMap.map.put(studyInstanceUID, studyInfo);
        }

        studyInfo.sopInstanceFiles.add(dicomFile);

        if (studyInfo.startTime == null) {
            studyInfo.startTime = new Date();
        }

        Logger.getLogger(this.getClass()).debug("Found & prepared file: " + dicomFile);
        return dicomFile;
    }

    @Autowired
    InboundStudyMap inboundStudyMap;
}
