package org.nema.medical.mint.studies;

import java.sql.Timestamp;

/**
 * User: tdawson
 * Date: Sep 18, 2010
 * Time: 2:24:01 PM
 */
public class SearchResultStudy {

    private String studyInstanceUid;
    private Timestamp lastModified;
    private int version;

    /**
     * Default constructor for JiBX
     */
    public SearchResultStudy() {
    }

    public SearchResultStudy(String studyInstanceUid, Timestamp lastModified, int version) {
        this.version = version;
        this.lastModified = lastModified;
        this.studyInstanceUid = studyInstanceUid;
    }

    public void setStudyInstanceUid(String studyInstanceUid) {
		this.studyInstanceUid = studyInstanceUid;
	}

	public void setLastModified(Timestamp lastModified) {
		this.lastModified = lastModified;
	}

	public void setVersion(int version) {
		this.version = version;
	}

    public String getStudyInstanceUid() {
           return studyInstanceUid;
       }

    public Timestamp getLastModified() {
        return lastModified;
    }

    public int getVersion() {
        return version;
    }
}
