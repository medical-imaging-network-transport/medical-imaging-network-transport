package org.nema.medical.mint.utils;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

public class JodaDateUtils implements ISO8601DateUtils {

	private final static DateTimeFormatter[] timezone_formatters = 
			{ISODateTimeFormat.basicDateTimeNoMillis(), ISODateTimeFormat.basicDateTime()};
	private final static DateTimeFormatter[] local_formatters = 
			{DateTimeFormat.forPattern("yyyyMMdd'T'HHmmss.SSS"), DateTimeFormat.forPattern("yyyyMMdd'T'HHmmss")};

	@Override
	public Date parseISO8601Basic(String dateStr) throws ParseException {	
		return parseISO8601(dateStr, false);
	}

	@Override
	public Date parseISO8601BasicUTC(String dateStr) throws ParseException {
		//These are the ways to explicitly express UTC. "-0000" or "-00" are not valid according to ISO8601:2004	
        if (!dateStr.endsWith("Z") && !dateStr.endsWith("+0000") && !dateStr.endsWith("+00")) {
            throw new ParseException("Date/time string lacks UTC designator", dateStr.length());
        }
        
		return parseISO8601(dateStr, true);
	}
	
	/**
	 * Parse a date string according to ISO8601 basic format
	 * @param dateStr 
	 * 				the string to parse in the format yyyyMMdd'T'HHmmss.SSS'Z' or yyyyMMdd'T'HHmmss'Z' where Z 
	 * 				represents the timezone offset if available.
	 * @param utcOnly
	 * 				whether or not the string should be parsed with formatters that handle timezones
	 * @return the fully parsed ISO8601 string as a Java Date object
	 * @throws ParseException
	 */
	private Date parseISO8601(String dateStr, boolean utcOnly) throws ParseException {		

		dateStr = dateStr.replace(' ', '+');
		//run through parsers that handle timezones
		for(DateTimeFormatter formatter : timezone_formatters) {
			try{
				return getDate(formatter.parseDateTime(dateStr));	
			} catch(IllegalArgumentException e) {
				//do nothing, try other formats
			}
		}

		if(!utcOnly){
			//run through rest of parsers without timezones
			for(DateTimeFormatter formatter : local_formatters) {
				try{
					return getDate(formatter.parseDateTime(dateStr));				
				} catch(IllegalArgumentException e) {
					//do nothing, try other formats
				}
			}
		}
		
		//dateStr not in valid format
		throw new ParseException("Invalid date: ", dateStr.length()-1);
	}

	@Override
	public Date parseISO8601DateBasic(String dateStr) throws ParseException {
		if(dateStr.startsWith("+") || dateStr.startsWith("-")) {
			throw new ParseException("Invalid date: ", 0);
		}
		final DateTimeFormatter formatter = ISODateTimeFormat.basicDate();
		try{
			return getDate(formatter.parseDateTime(dateStr));
		} catch(IllegalArgumentException e) {
			throw new ParseException("Invalid date: " + dateStr, 0);
		}
	}
	
	/**
	 * Converts the given Joda DateTime object into a Java Date object
	 * @param dt the Joda DateTime object to convert
	 * @return the equivalent Java Date object 
	 */
	private Date getDate(DateTime dt) {
		Calendar calendar = dt.toCalendar(null);
		return calendar.getTime();
	}
}
