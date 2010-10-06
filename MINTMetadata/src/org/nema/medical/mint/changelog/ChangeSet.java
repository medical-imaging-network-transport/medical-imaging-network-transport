package org.nema.medical.mint.changelog;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ChangeSet {

    /**
     * Default constructor for JiBX
     */
    public ChangeSet() {
        this(new ArrayList<Change>());
    }
	public ChangeSet(final List<Change> changes) {
        this(null, changes);
	}
	
	public ChangeSet(final String studyUUID, final List<Change> changes) {
		this.studyUUID = studyUUID;
		this.changes = changes;
	}
	
	public Iterator<Change> changeIterator() {
		return changes.iterator();
	}

    public void addChange(final Change change) {
        this.changes.add(change);
    }

	public String getStudyUUID() {
		return studyUUID;
	}

    public void setStudyUUID(final String studyUUID) {
        this.studyUUID = studyUUID;
    }

	private final List<Change> changes;
	private String studyUUID;
}
