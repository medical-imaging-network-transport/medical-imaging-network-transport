package org.nema.medical.mint.changelog;

import java.util.Iterator;
import java.util.List;

public class ChangeSet {

	public ChangeSet(List<Change> changes) {
		this.studyUUID = null;
		this.changes = changes;		
	}
	
	public ChangeSet(String studyUUID, List<Change> changes) {
		this.studyUUID = studyUUID;
		this.changes = changes;
	}
	
	public Iterator<Change> changeIterator() {
		return changes.iterator();
	}
	
	public String getStudyUUID() {
		return studyUUID;
	}

	private List<Change> changes;
	private final String studyUUID;
}
