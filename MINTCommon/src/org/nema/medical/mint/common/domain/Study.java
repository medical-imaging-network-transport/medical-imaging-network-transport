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

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;
import java.util.TimeZone;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "mint_study")
/*
 * @org.hibernate.annotations.Table(appliesTo = "study", indexes = {
 * @Index(name = "accessionNumberIndex", columnNames = { "accessionNumber" }),
 * @Index(name = "patientIdIndex", columnNames = { "patientId" }),
 * @Index(name = "patientNameIndex", columnNames = { "patientName" }),
 * @Index(name = "studyIdIndex", columnNames = { "studyId" }),
 * @Index(name = "uidHashIndex", columnNames = { "uidHash" }) })
 */
public class Study implements Serializable {

	private static final long serialVersionUID = 5002137289912160452L;

	public static Timestamp now() {
		return new Timestamp(Calendar.getInstance(TimeZone.getTimeZone("GMT")).getTimeInMillis());
	}

	// Accession Number (0008,0050) T2
	// A RIS generated number that identifies the order for the Study.
	// INDEXED
	// The accession number is a string that is frequently used to find studies
	// in a system. It is also frequently displayed to the
	// user. This value may be an empty string
	@Column
	private String accessionNumber;

	// set of devices that submitted instances for this study
	@ManyToMany(cascade = CascadeType.PERSIST)
	private Set<Device> devices = new HashSet<Device>();

	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	@Column(name = "studyID")
	private String id;

	// Patient ID (0010,0020) T2
	// Primary hospital identification number or code for the patient.
	// INDEXED
	// While this value is typically provided, it is not necessarily unique or
	// 100% reliable:
	// - It can be an empty string
	// - It can be the wrong value (someone typed it in wrong)
	// - There can be other patient's with the same ID
	// - A given patient can have multiple patient IDs
	// The patient ID is a string that is frequently used to find studies on a
	// system. It is also frequently displayed to the user.
	// Users often try to find what they are looking for with the minimum amount
	// of typing. Here are some
	// possible search use cases
	// - What studies exist for patient whose patient ID that begins with
	// "A1256"
	// - What studies exist for patient whose patient ID exactly matches
	// "A1256245"
	// - What studies exist for patient whose patient ID begins with the numeric
	// characters "1256"
	// - What studies exist for patient whose patient ID contains he numeric
	// characters "1256256"
	@Column
	private String patientID;

	// Patient's name (0010,0010) T2
	// Patient's full name.
	// The patient's name in DICOM VR PN (family name complex^given name
	// complex^middle name^name prefix^name suffix). While this value
	// is typically provided, it is not 100% reliable:
	// - It can be an empty string
	// - It may not conform to the PN formatting. The main case here is when it
	// is manually typed in and the user
	// types the whole name into the "first name field" on the scanner
	// - A fake name may be used for patients whose identity is unknown (patient
	// is unconscience, etc)
	// - If a patient's name is changed (married, legal name change, etc) -
	// older studies may exist with the old name
	// - Other information systems may have different names for the same patient
	// (e.g. Chris E. Hafey vs Christopher Edward Hafey)
	// INDEXED
	// We store whatever we get for (0010,0010) here so we can access it without
	// opening the original DICOM instances. This field
	// is frequently searched on. Users often try to find what they are looking
	// for with the minimum amount of typing. Here are some
	// possible search use cases
	// - What studies exist for patient whose last name begins with "HAF".
	// - What studies exist for patient whose last name exactly matches "HAFEY".
	// - What studies exist for patient whose last name exactly matches "HAFEY"
	// and first name begins with "C".
	// - What studies exist for patient whos last name begins with "HAF" and
	// first name begins with "C"
	@Column
	private String patientName;

	// The conjoined studyDate and studyTime from DICOM. If a parse error
	// occurs, this is set to null.
	// INDEXED
	@Column
	private Timestamp studyDateTime;

	// DICOM StudyInstanceUID (0020,0010) T1
	// Unique identifier for the Study.
	// NOT NULL
	// This is the "primary key" for DICOM systems use to identify studies.
	// Since this the Study is the least granular entity in DICOM, the Study UID
	// is required
	@Column
	private String studyInstanceUID;

	@Column
	private Timestamp updateTime = now();

	@Override
	public boolean equals(final Object object) {
		if (!(object instanceof Study)) {
			return false;
		}

		final Study study = (Study) object;
		return id != null ? id.equals(study.id) : study.id == null;
	}

	public String getAccessionNumber() {
		return accessionNumber;
	}

	public Set<Device> getDevices() {
		return devices;
	}

	public String getId() {
		return id;
	}

	public String getPatientID() {
		return patientID;
	}

	public String getPatientName() {
		return patientName;
	}

	public Timestamp getStudyDateTime() {
		return studyDateTime;
	}

	public String getStudyInstanceUID() {
		return studyInstanceUID;
	}

	public Timestamp getUpdateTime() {
		return updateTime;
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}

	public void setAccessionNumber(final String accessionNumber) {
		this.accessionNumber = accessionNumber;
	}

	public void setDevices(final Set<Device> devices) {
		this.devices = devices;
	}

	public void setId(final String id) {
		this.id = id;
	}

	public void setPatientID(final String patientID) {
		this.patientID = patientID;
	}

	public void setPatientName(final String patientName) {
		this.patientName = patientName;
	}

	public void setStudyDateTime(final Timestamp studyDateTime) {
		this.studyDateTime = studyDateTime;
	}

	public void setStudyInstanceUID(final String studyInstanceUID) {
		this.studyInstanceUID = studyInstanceUID;
	}

	public void setUpdateTime(final Timestamp updateTime) {
		this.updateTime = updateTime;
	}

	@Override
	public String toString() {
		return String.format("Study{id=%s, instanceUID=%s}", id, studyInstanceUID);
	}

}
