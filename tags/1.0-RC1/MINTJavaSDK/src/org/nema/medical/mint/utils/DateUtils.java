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
package org.nema.medical.mint.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {

    /**
     * Parses a string formatted according to the ISO8601 "extended" date format,
     * the standard xsd:dateTime format used in XML.  This method isn't actually used
     * in our code (JiBX provides xml marshalling/unmarshalling) and we decided
     * to use the ISO8601 "basic" format (no dashes/colons) for URL parsing
     * due to special character issues.
     * @param dateStr formatted according to the ISO 8601 "extended" date format
     * @return Date a the date represented by the provided string
     * @throws java.text.ParseException if the date is not properly formatted
     */
    public static final Date parseISO8601Basic(String dateStr) throws ParseException {
    	String[] xsdDateTime = new String[]{"yyyyMMdd'T'HHmmss.SSSZ","yyyyMMdd'T'HHmmssZ","yyyyMMdd'T'HHmmss.SSS","yyyyMMdd'T'HHmmss"};

    	// fix issue where + is replace with ' ' in a URL
        dateStr = dateStr.replace(' ','+');
        //this is zero time so we need to add that TZ indicator for
        if ( dateStr.endsWith( "Z" ) ) {
        	dateStr = dateStr.substring( 0, dateStr.length() - 1) + "+0000";
        }
    	return parseDate(dateStr,xsdDateTime);
    }

    /**
     * Parses a string formatted according to the ISO8601 "extended" date format,
     * the standard xsd:dateTime format used in XML.  This method isn't actually used
     * in our code (JiBX provides xml marshalling/unmarshalling) and we decided
     * to use the ISO8601 "basic" format (no dashes/colons) for URL parsing
     * due to special character issues.
     * @param dateStr formatted according to the ISO 8601 "extended" date format
     * @return Date a the date represented by the provided string
     * @throws java.text.ParseException if the date is not properly formatted
     */
    public static final Date parseISO8601Extended(String dateStr) throws ParseException {
    	String[] xsdDateTime = new String[]{"yyyy-MM-dd'T'HH:mm:ss.SSSz","yyyy-MM-dd'T'HH:mm:ssz","yyyy-MM-dd'T'HH:mm:ss.SSS","yyyy-MM-dd'T'HH:mm:ss"};
    	// fix issue where + is replace with ' ' in a URL
    	dateStr = dateStr.replace(' ','+'); // fix URL issue
        //this is zero time so we need to add that TZ indicator for
        if ( dateStr.endsWith( "Z" ) ) {
        	dateStr = dateStr.substring( 0, dateStr.length() - 1) + "GMT-00:00";
        } else if (dateStr.contains("T")){
			int timeIndex = dateStr.indexOf('T');
			int signIndex = dateStr.lastIndexOf('+');
        	if (signIndex == -1) signIndex = dateStr.lastIndexOf('-');

			if (signIndex > timeIndex) {
                String s0 = dateStr.substring( 0, signIndex);
                String s1 = dateStr.substring( signIndex, dateStr.length() );
                dateStr = s0 + "GMT" + s1;
        	}
        }
    	return parseDate(dateStr,xsdDateTime);
    }

    public static final Date parseISO8601(String dateStr) throws ParseException {
        if (dateStr.contains("-")){
            return parseISO8601Extended(dateStr);
        }
        else {
            return parseISO8601Basic(dateStr);
        }
    }

    public static final Date parseISO8601Date(String dateStr) throws ParseException {
        String[] xsdDate;
        if (dateStr.contains("-")){
            if (dateStr.length() == 10){
                xsdDate = new String[]{"yyyy-MM-dd"};
            }
            else {
                throw new ParseException("Invalid date:" + dateStr, 0);
            }
        }
        else {
            if (dateStr.length() == 8){
                xsdDate = new String[]{"yyyyMMdd"};
            }
            else {
                throw new ParseException("Invalid date: " + dateStr, 0);
            }
        }
        return parseDate(dateStr,xsdDate);
    }

    public static final Date parseDate(String dateStr, String[] formats) throws ParseException {
    	Date date = null;
        ParseException ex = null;

        for (String format : formats) {
            try {
                date = new SimpleDateFormat(format).parse(dateStr);
                break;
            } catch (ParseException e) {
                // try next format, but throw the last error
                ex = e;
            }
        }
        if (date == null) {
            throw ex;
        }
        return date;
    }

    private DateUtils() {} // no instantiation
}
