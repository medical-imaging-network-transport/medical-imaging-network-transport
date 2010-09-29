package org.nema.medical.mint.studies;

import org.nema.medical.mint.studies.Study;
import java.sql.Timestamp;
import java.util.Iterator;
import java.util.List;

public class StudyRoot extends Study {
	public StudyRoot(String studyUUID, Timestamp lastModified, int version, List<String> types) {
		super(studyUUID, lastModified, version);
		this.types = types;
	}

	private List<String> types;

	public Iterator<String> typeIterator() {
		return types.iterator();
	}
	
}
