package org.nema.medical.mint.changelog;

import java.sql.Timestamp;

public interface Change {

	public String getStudyUUID();
	public int getChangeNumber();
	public String getType();
	public Timestamp getDateTime();
}
