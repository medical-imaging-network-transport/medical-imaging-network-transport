package org.nema.medical.mint;

import java.io.File;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

final class InboundStudyMap {

    public static class StudyInfo {
        Date startTime;
        Collection<File> sopInstanceFiles;
    }

    public Map<String, StudyInfo> map = new HashMap<String, StudyInfo>();
}
