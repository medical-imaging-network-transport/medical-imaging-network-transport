package org.nema.medical.mint.common.domain;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "mint_assoclog")
public class AssociationLog {

	@Id @Column(name="associationID")
	public String getID() {return id;}
	public void setID(String id) {this.id = id;}
	private String id;

	@Column
	public Timestamp getAssocBegin() {return assocBegin;}
	public void setAssocBegin(Timestamp assocBegin) {this.assocBegin = assocBegin;}
	private Timestamp assocBegin;

	@Column
	public Timestamp getAssocEnd() {return assocEnd;}
	public void setAssocEnd(Timestamp assocEnd) {this.assocEnd = assocEnd;}
	private Timestamp assocEnd;

}
