/*
 *   Copyright 2010 MINT Working Group
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

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
