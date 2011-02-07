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
package org.nema.medical.mint.server.domain;

import org.hibernate.annotations.GenericGenerator;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.TimeZone;

import javax.persistence.*;

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
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
	@Column(name="changeId")
	private String id;

	@Column(nullable = false)
	private String studyID;

	@Column(nullable = false)
	private int changeIndex;

	@Column(nullable = false)
	private String changeType;

	@Column(name="changeDateTime", nullable = false)
	private Timestamp dateTime = now();

	@Column
	private String remoteUser;

    @Column(nullable = false)
	private String remoteHost;

	@Column
	private String principal;

    @Column(nullable = false)
    private String operation;

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

	public String getType() {
		return changeType;
	}

	public void setType(String changeType) {
		this.changeType = changeType;
	}

	public Timestamp getDateTime() {
		return dateTime;
	}

	public void setDateTime(Timestamp dateTime) {
		this.dateTime = dateTime;
	}

	public String getStudyUUID() {
		return studyID;
	}

	public int getChangeNumber() {
		return changeIndex;
	}

	public String getRemoteUser() {
		return remoteUser;
	}

	public void setRemoteUser(String remoteUser) {
		this.remoteUser = remoteUser;
	}

	public String getRemoteHost() {
		return remoteHost;
	}

	public void setRemoteHost(String sourceHost) {
		this.remoteHost = sourceHost;
	}

	public String getPrincipal() {
		return principal;
	}

	public void setPrincipal(String principal) {
		this.principal = principal;
	}

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }
}
