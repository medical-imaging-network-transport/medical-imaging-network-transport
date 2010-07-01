package org.nema.medical.mint.common.domain;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.TimeZone;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


/**
 * @author Rex
 *
 */
@Entity
@Table(name = "mint_job_status")
public class JobInfo {
	public static Timestamp now() {
		return new Timestamp(Calendar.getInstance(TimeZone.getTimeZone("GMT")).getTimeInMillis());
	}
	
	@Id
	@Column(name="jobId")
	private String id;
	
	@Column
	private String studyID;
	
	@Column
	private JobStatusEnum status;
	
	@Column
	private String statusDescription;
	
	@Column
	private Timestamp createTime = new Timestamp(Calendar.getInstance(TimeZone.getTimeZone("GMT")).getTimeInMillis());
	
	@Column
	private Timestamp updateTime = now();

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getStudyID() {
		return studyID;
	}

	public void setStudyID(String studyID) {
		this.studyID = studyID;
	}

	public JobStatusEnum getStatus() {
		return status;
	}

	public void setStatus(JobStatusEnum status) {
		this.status = status;
	}

	public String getStatusDescription() {
		return statusDescription;
	}

	public void setStatusDescription(String statusDescription) {
		this.statusDescription = statusDescription;
	}

	public Timestamp getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}

	public Timestamp getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Timestamp updateTime) {
		this.updateTime = updateTime;
	}
	
	
}
