package org.nema.medical.mint.changelog;

import java.sql.Timestamp;

public class Change {

	public Change(String studyUUID, int changeNumber, String type, Timestamp datetime) {
		this.studyUUID = studyUUID;
		this.changeNumber = changeNumber;
		this.type = type;
		this.datetime = datetime;
	}
	
	public String getStudyUUID() {
		return studyUUID;
	}

	public int getChangeNumber() {
		return changeNumber;
	}
	
	public String getType() {
		return type;
	}
	
	public Timestamp getDateTime() {
		return datetime;
	}

	private final String studyUUID;
	private final int changeNumber;
	private final String type;
	private final Timestamp datetime;
}
