package org.nema.medical.mint.common.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "mint_config", uniqueConstraints = { @UniqueConstraint(columnNames = { "param" }) })
public class Configuration implements Serializable {

	private static final long serialVersionUID = 6097446410279383285L;

	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	private String id = null;

	@Column(nullable = false)
	private String param = null;

	@Column(nullable = false)
	private String value = null;

	public String getId() {
		return id;
	}

	public String getParam() {
		return param;
	}

	public String getValue() {
		return value;
	}

	public void setId(final String id) {
		this.id = id;
	}

	public void setParam(final String param) {
		this.param = param;
	}

	public void setValue(final String value) {
		this.value = value;
	}
}
