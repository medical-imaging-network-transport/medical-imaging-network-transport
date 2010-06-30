package org.nema.medical.mint;

import java.util.Set;

public final class DirectoryItem {
	public String getAssociationID() {
		return associationID;
	}
	public void setAssociationID(String associationID) {
		this.associationID = associationID;
	}

	public void setNewFiles(Set<String> newFiles) {
		this.newFiles = newFiles;
	}
	public Set<String> getNewFiles() {
		return newFiles;
	}

	private String associationID;
	private Set<String> newFiles;
}
