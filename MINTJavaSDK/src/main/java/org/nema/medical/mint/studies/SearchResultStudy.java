package org.nema.medical.mint.studies;

import java.sql.Timestamp;

/**
 * User: tdawson
 * Date: Sep 18, 2010
 * Time: 2:24:01 PM
 */
public class SearchResultStudy {

    private String studyUUID;
    private Timestamp lastModified;
    private int version;

    /**
     * Default constructor for JiBX
     */
    public SearchResultStudy() {
    }

    public SearchResultStudy(String studyUUID, Timestamp lastModified, int version) {
        this.version = version;
        this.lastModified = lastModified;
        this.studyUUID = studyUUID;
    }

    public void setStudyUUID(String studyUUID) {
		this.studyUUID = studyUUID;
	}

	public void setLastModified(Timestamp lastModified) {
		this.lastModified = lastModified;
	}

	public void setVersion(int version) {
		this.version = version;
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
