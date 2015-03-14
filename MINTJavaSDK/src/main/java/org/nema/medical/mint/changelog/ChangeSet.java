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

    public ChangeSet(final String studyInstanceUid, final List<Change> changes) {
        this.studyInstanceUid = studyInstanceUid;
        this.changes = changes;
    }

    public Iterator<Change> changeIterator() {
        return changes.iterator();
    }

    public void addChange(final Change change) {
        this.changes.add(change);
    }

    public String getStudyInstanceUid() {
        return studyInstanceUid;
    }

    public void setStudyInstanceUid(final String studyInstanceUid) {
        this.studyInstanceUid = studyInstanceUid;
    }

    private final List<Change> changes;
    private String studyInstanceUid;
}
