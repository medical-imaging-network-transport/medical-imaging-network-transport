package org.nema.medical.mint.studies;

import java.sql.Timestamp;

/**
 * User: tdawson
 * Date: Sep 18, 2010
 * Time: 2:24:01 PM
 */
public class Study {

    private final String studyUUID;
    private final Timestamp lastModified;
    private final int version;

    public Study(String studyUUID, Timestamp lastModified, int version) {
        this.version = version;
        this.lastModified = lastModified;
        this.studyUUID = studyUUID;
    }

    public String getStudyUUID() {
        return studyUUID;
    }

    public Timestamp getLastModified() {
        return lastModified;
    }

    public int getVersion() {
        return version;
    }
}
