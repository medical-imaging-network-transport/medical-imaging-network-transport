package org.nema.medical.mint.studies;

import java.sql.Timestamp;

public class StudyRoot extends SearchResultStudy {

    /**
     * Default constructor for JiBX
     */
    public StudyRoot() {
    }

	public StudyRoot(final String studyInstanceUid, final Timestamp lastModified, final int version) {
		super(studyInstanceUid, lastModified, version);
	}

}
