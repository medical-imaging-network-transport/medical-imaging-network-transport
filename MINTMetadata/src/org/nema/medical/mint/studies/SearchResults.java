package org.nema.medical.mint.studies;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SearchResults {

    private final String studyInstanceUID;
    private final String accessionNumber;
    private final String issuerOfAccessionNumber;
    private final String patientID;
    private final String issuerOfPatientID;
    private final Timestamp minStudyDate;
    private final Timestamp minStudyDateTime;
    private final Timestamp maxStudyDate;
    private final Timestamp maxStudyDateTime;
    private final String timeZone;
    private final int offset;
    private final int limit;
	private List<Study> studies;

    public SearchResults(String studyInstanceUID, String accessionNumber, String issuerOfAccessionNumber, String patientID, String issuerOfPatientID, Timestamp minStudyDate, Timestamp minStudyDateTime, Timestamp maxStudyDate, Timestamp maxStudyDateTime, String timeZone, int offset, int limit) {
        this.studyInstanceUID = studyInstanceUID;
        this.accessionNumber = accessionNumber;
        this.issuerOfAccessionNumber = issuerOfAccessionNumber;
        this.patientID = patientID;
        this.issuerOfPatientID = issuerOfPatientID;
        this.minStudyDate = minStudyDate;
        this.minStudyDateTime = minStudyDateTime;
        this.maxStudyDate = maxStudyDate;
        this.maxStudyDateTime = maxStudyDateTime;
        this.timeZone = timeZone;
        this.offset = offset;
        this.limit = limit;
        this.studies = new ArrayList<Study>();
    }
    
    public String getStudyInstanceUID() {
        return studyInstanceUID;
    }

    public String getAccessionNumber() {
        return accessionNumber;
    }

    public String getIssuerOfAccessionNumber() {
        return issuerOfAccessionNumber;
    }

    public String getPatientID() {
        return patientID;
    }

    public String getIssuerOfPatientID() {
        return issuerOfPatientID;
    }

    public Timestamp getMinStudyDate() {
        return minStudyDate;
    }

    public Timestamp getMinStudyDateTime() {
        return minStudyDateTime;
    }

    public Timestamp getMaxStudyDate() {
        return maxStudyDate;
    }

    public Timestamp getMaxStudyDateTime() {
        return maxStudyDateTime;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public int getOffset() {
        return offset;
    }

    public int getLimit() {
        return limit;
    }
    
    public Iterator<Study> resultsIterator() {
    	return studies.iterator();
    }
    
    public void addStudy(Study study) {
    	studies.add(study);
    }
}
