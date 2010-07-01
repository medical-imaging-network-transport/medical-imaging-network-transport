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
package org.nema.medical.mint.common.domain;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.TimeZone;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "mint_instance_log")
public class InstanceLog {

	@Id @GeneratedValue(strategy=GenerationType.AUTO)
	public long getInstanceLogID() {return instanceLogID;}
	public void setInstanceLogID(long instanceLogID) {this.instanceLogID = instanceLogID;}
	private long instanceLogID;

	@ManyToOne
	public Study getStudy() {return study;}
	public void setStudy(Study study) {this.study = study;}
	private Study study;

	@ManyToOne
	public AssociationLog getAssociationLog() {return assoc;}
	public void setAssociationLog(AssociationLog assoc) {this.assoc = assoc;}
	private AssociationLog assoc;

	@Column
	public Timestamp getReceiveBegin() {return receiveBegin;}
	public void setReceiveBegin(Timestamp receiveBegin) {this.receiveBegin = receiveBegin;}
	private Timestamp receiveBegin = new Timestamp(Calendar.getInstance(TimeZone.getTimeZone("GMT")).getTimeInMillis());

	@Column
	public Timestamp getReceiveComplete() {return receiveComplete;}
	public void setReceiveComplete(Timestamp receiveComplete) {this.receiveComplete = receiveComplete;}
	private Timestamp receiveComplete = new Timestamp(Calendar.getInstance(TimeZone.getTimeZone("GMT")).getTimeInMillis());

	@Column
	public String getSopClassUID() {return sopClassUID;}
	public void setSopClassUID(String sopClassUID) {this.sopClassUID = sopClassUID;}
	private String sopClassUID;

	@Column
	public String getSopInstanceUID() {return sopInstanceUID;}
	public void setSopInstanceUID(String sopInstanceUID) {this.sopInstanceUID = sopInstanceUID;}
	private String sopInstanceUID;

	@Column
	public String getXferSyntax() {return xferSyntax;}
	public void setXferSyntax(String xferSyntax) {this.xferSyntax = xferSyntax;}
	private String xferSyntax;

	@Column
	public long getSize() {return size;}
	public void setSize(long size) {this.size = size;}
	private long size;
}
