package org.nema.medical.mint.server.domain;

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
@Table(name = "mint_updateinfo")
public class UpdateInfo {
	public static Timestamp now() {
		return new Timestamp(Calendar.getInstance(TimeZone.getTimeZone("GMT")).getTimeInMillis());
	}
	
	@Id
	@Column(name="updateId")
	private String id;
	
	@Column
	private String studyID;
	
	@Column(nullable=false)
	private int updateIndex;
	
	@Column
	private String updateDescription;
	
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

	public int getUpdateIndex() {
		return updateIndex;
	}

	public void setUpdateIndex(int updateIndex) {
		this.updateIndex = updateIndex;
	}

	public String getUpdateDescription() {
		return updateDescription;
	}

	public void setUpdateDescription(String updateDescription) {
		this.updateDescription = updateDescription;
	}

	public Timestamp getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Timestamp updateTime) {
		this.updateTime = updateTime;
	}
	
	
}
