package org.nema.medical.mint.utils;

import java.text.ParseException;
import java.util.Date;

public interface ISO8601DateUtils {
	/**
	 * Parses an ISO8601 basic format string
	 * @param dateStr
	 * 				The string formatted according to the ISO8601 basic format.
	 * 				(yyyyMMdd'T'HHmmss.SSS'Z' or yyyyMMdd'T'HHmmss'Z')
	 * @return the Date object represented by the string
	 * @throws ParseException
	 */
    public Date parseISO8601Basic(final String dateStr) throws ParseException;
    
    /**
     * Parses an ISO8601 basic format string for utc times 
     * @param dateStr
     * 				The string formatted according to the ISO8601 basic format.
     * 				Should be appended with 'Z' to indicate a utc time.
     * @return the Date object represented by the string
     * @throws ParseException
     */
    public Date parseISO8601BasicUTC(String dateStr) throws ParseException;
    /**
     * Parses an ISO8601 basic format date string
     * @param dateStr
     * 				The string formatted according to the ISO8601 basic format (date only).
     * @return the Date object represented by the string
     * @throws ParseException
     */
    public Date parseISO8601DateBasic(final String dateStr) throws ParseException;
}
