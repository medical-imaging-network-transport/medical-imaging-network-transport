package org.nema.medical.mint.common.domain;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.TimeZone;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "mint_device")
public class Device {

	@Id @GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="deviceID")
	private long id;

	@Column
	private String aeTitle;
	
	@Column
	private String ipAddress;

	@Column
	private Timestamp createTime = new Timestamp(Calendar.getInstance(TimeZone.getTimeZone("GMT")).getTimeInMillis());

	public String getAeTitle() {
		return aeTitle;
	}

	public void setAeTitle(String aeTitle) {
		this.aeTitle = aeTitle;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public Timestamp getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
}
