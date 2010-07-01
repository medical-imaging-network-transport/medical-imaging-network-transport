package org.nema.medical.mint.common.domain;

public enum JobStatusEnum {
	IN_PROGRESS("IN_PROGRESS"), SUCCESS("SUCCESS"), FAILED("FAILED");
	
	private String value;
	
	private JobStatusEnum(String value)
	{
		this.value = value;
	}
	
	@Override
	public String toString()
	{
		return value;
	}
}
