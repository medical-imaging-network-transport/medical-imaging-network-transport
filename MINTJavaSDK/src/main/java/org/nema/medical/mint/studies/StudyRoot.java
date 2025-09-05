package org.nema.medical.mint.studies;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class StudyRoot extends SearchResultStudy {

    /**
     * Default constructor for JiBX
     */
    public StudyRoot() {
        this.types = new ArrayList<String>();
    }

	public StudyRoot(String studyUUID, Timestamp lastModified, int version, List<String> types) {
		super(studyUUID, lastModified, version);
		this.types = types;
	}

	private List<String> types;

	public Iterator<String> typeIterator() {
		return types.iterator();
	}

    public void addType(final String type) {
        types.add(type);
    }
}
