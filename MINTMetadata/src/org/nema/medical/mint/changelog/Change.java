package org.nema.medical.mint.changelog;

import java.sql.Timestamp;

public class Change {

	public Change(String studyUUID, int changeNumber, String type, Timestamp datetime, 
			String remoteHost, String remoteUser, String principal) {
		this.studyUUID = studyUUID;
		this.changeNumber = changeNumber;
		this.type = type;
		this.datetime = datetime;
		this.remoteHost = remoteHost;
		this.remoteUser = remoteUser;
		this.principal = principal;
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

	public String getRemoteHost() {
		return remoteHost;
	}

	public String getRemoteUser() {
		return remoteUser;
	}

	public String getPrincipal() {
		return principal;
	}

	private final String studyUUID;
	private final int changeNumber;
	private final String type;
	private final Timestamp datetime;
	private final String remoteHost;
	private final String remoteUser;
	private final String principal;
}
