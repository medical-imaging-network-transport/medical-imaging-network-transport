package org.nema.medical.mint.changelog;

import java.sql.Timestamp;

public class Change {

    public static final String OPERATION_CREATE = "CREATE";
    public static final String OPERATION_UPDATE = "UPDATE";
    public static final String OPERATION_DELETE = "DELETE";

    /**
     * Default constructor for JiBX
     */
    public Change() {
    }

	public Change(final String studyUUID, final int changeNumber, final String type, final Timestamp dateTime,
			final String remoteHost, final String remoteUser, final String principal, final String operation) {
		this.studyUUID = studyUUID;
		this.changeNumber = changeNumber;
		this.type = type;
		this.dateTime = dateTime;
		this.remoteHost = remoteHost;
		this.remoteUser = remoteUser;
		this.principal = principal;
        this.operation = operation;
	}
	
	public String getStudyUUID() {
		return studyUUID;
	}

    public void setStudyUUID(final String studyUUID) {
        this.studyUUID = studyUUID;
    }

    public int getChangeNumber() {
		return changeNumber;
	}
	
    public void setChangeNumber(final int changeNumber) {
        this.changeNumber = changeNumber;
    }

	public String getType() {
		return type;
	}
	
    public void setType(String type) {
        this.type = type;
    }

	public Timestamp getDateTime() {
		return dateTime;
	}

    public void setDateTime(final Timestamp dateTime) {
        this.dateTime = dateTime;
    }

	public String getRemoteHost() {
		return remoteHost;
	}

    public void setRemoteHost(final String remoteHost) {
        this.remoteHost = remoteHost;
    }

	public String getRemoteUser() {
		return remoteUser;
	}

    public void setRemoteUser(final String remoteUser) {
        this.remoteUser = remoteUser;
    }

	public String getPrincipal() {
		return principal;
	}

    public void setPrincipal(final String principal) {
        this.principal = principal;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(final String operation) {
        this.operation = operation;
    }

	private String studyUUID;
	private int changeNumber;
	private String type;
	private Timestamp dateTime;
	private String remoteHost;
	private String remoteUser;
	private String principal;
    private String operation;
}
