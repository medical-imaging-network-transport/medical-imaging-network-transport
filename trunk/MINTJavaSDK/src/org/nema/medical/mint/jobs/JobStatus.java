package org.nema.medical.mint.jobs;

import java.sql.Timestamp;

public class JobStatus {

	private String jobID;
	private String studyUUID;
	private String jobStatus;
	private Timestamp jobCreated;
	private Timestamp jobUpdated;
	
	public JobStatus (String jobID, String studyUUID, String jobStatus, Timestamp jobCreated, Timestamp jobUpdated){
		this.jobID = jobID;
		this.studyUUID = studyUUID;
		this.jobStatus = jobStatus;
		this.jobCreated = jobCreated;
		this.jobUpdated = jobUpdated;
	}

	public JobStatus(){
		this.jobID = null;
		this.studyUUID = null;
		this.jobStatus = null;
		this.jobCreated = null;
		this.jobUpdated = null;
	}
	
	public String getId(){
		return this.jobID;
	}
	
	public String getStudyUUID(){
		return this.studyUUID;
	}
	
	public String getStatus(){
		return this.jobStatus;
	}
	
	public Timestamp getCreateTime(){
		return this.jobCreated;
	}
	
	public Timestamp getUpdateTime(){
		return this.jobUpdated;
	}

	public void setId(String jobID){
		this.jobID = jobID;
	}
	
	public void setStudyUUID(String studyUUID){
		this.studyUUID = studyUUID;
	}
	
	public void setStatus(String jobStatus){
		this.jobStatus = jobStatus;
	}
	
	public void setCreateTime(Timestamp jobCreated){
		this.jobCreated = jobCreated;
	}
	
	public void setUpdateTime(Timestamp jobUpdated){
		this.jobUpdated = jobUpdated;
	}
}
