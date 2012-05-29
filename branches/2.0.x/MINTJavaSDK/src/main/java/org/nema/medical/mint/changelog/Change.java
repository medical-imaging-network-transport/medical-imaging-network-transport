package org.nema.medical.mint.changelog;

import java.sql.Timestamp;

public class Change {

    /**
     * Default constructor for JiBX
     */
    public Change() {
    }

    public Change(final String studyInstanceUid, final int changeNumber, final String type,
                  final Timestamp dateTime, final String remoteHost, final String remoteUser,
                  final ChangeOperation operation) {
        this.studyInstanceUid = studyInstanceUid;
        this.changeNumber = changeNumber;
        this.type = type;
        this.dateTime = dateTime;
        this.remoteHost = remoteHost;
        this.remoteUser = remoteUser;
        this.operation = operation;
    }

    public String getStudyInstanceUid() {
        return studyInstanceUid;
    }

    public void setStudyInstanceUid(final String studyInstanceUid) {
        this.studyInstanceUid = studyInstanceUid;
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

    public ChangeOperation getOperation() {
        return operation;
    }

    public void setOperation(final ChangeOperation operation) {
        this.operation = operation;
    }

    private String studyInstanceUid;
    private int changeNumber;
    private String type;
    private Timestamp dateTime;
    private String remoteHost;
    private String remoteUser;
    private ChangeOperation operation;
}
