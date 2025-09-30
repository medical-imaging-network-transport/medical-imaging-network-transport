package org.nema.medical.mint.utils;

public class DateTimeParseException extends Exception {
	
	public DateTimeParseException(String msg) {
		super(msg);
	}
	
	public DateTimeParseException(String msg, Throwable cause) {
		super(msg, cause);
	}
	
	public DateTimeParseException(Throwable cause) {
		super(cause);
	}
}
