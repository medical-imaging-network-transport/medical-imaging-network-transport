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
@Table(name = "mint_change")
public class Change {
	public static Timestamp now() {
		return new Timestamp(Calendar.getInstance(TimeZone.getTimeZone("GMT")).getTimeInMillis());
	}
	
	@Id
	@Column(name="changeId")
	private String id;
	
	@Column
	private String studyID;
	
	@Column(nullable=false)
	private int changeIndex;
	
	@Column
	private String changeDescription;
	
	@Column(name="changeDateTime")
	private Timestamp dateTime = now();

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

	public int getIndex() {
		return changeIndex;
	}

	public void setIndex(int updateIndex) {
		this.changeIndex = updateIndex;
	}

	public String getDescription() {
		return changeDescription;
	}

	public void setDescription(String changeDescription) {
		this.changeDescription = changeDescription;
	}

	public Timestamp getDateTime() {
		return dateTime;
	}

	public void setDateTime(Timestamp dateTime) {
		this.dateTime = dateTime;
	}
	
	
}
