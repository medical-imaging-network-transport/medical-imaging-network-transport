package org.nema.medical.mint.common.domain;

public enum JobStatus {
	IN_PROGRESS("IN_PROGRESS"), SUCCESS("SUCCESS"), FAILED("FAILED");
	
	private String value;
	
	private JobStatus(String value)
	{
		this.value = value;
	}
	
	@Override
	public String toString()
	{
		return value;
	}
}
