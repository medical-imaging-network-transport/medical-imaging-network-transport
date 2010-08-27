package org.nema.medical.mint.changelog;

import java.util.Iterator;
import java.util.List;

public class ChangeSet {

	private List<Change> changes;

	public ChangeSet(List<Change> changes) {
		this.changes = changes;
	}
	
	public Iterator<Change> changeIterator() {
		return changes.iterator();
	}
}
