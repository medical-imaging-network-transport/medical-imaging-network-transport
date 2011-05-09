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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

//TODO Some of this needs to be refactored, as the parsing code is somewhat flawed. Joda Time is something to look into
//as a replacement.
public final class DateUtils {

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
    public static Date parseISO8601Basic(final String dateStr) throws ParseException {
        return parseISO8601Basic(dateStr, false);
    }

    /**
     * Parses a string formatted according to the ISO8601 "extended" date format,
     * the standard xsd:dateTime format used in XML. The XML approach is not actually used
     * in our code (JiBX provides xml marshalling/unmarshalling) and we decided
     * to use the ISO8601 "basic" format (no dashes/colons) for URL parsing
     * due to special character issues, and further restricted to only UTC time strings
     * (with an appended 'Z' character)
     * @param dateStr formatted according to the ISO 8601 "basic" date format, with appended 'Z' character.
     * @return Date a the date represented by the provided string
     * @throws java.text.ParseException if the date is not properly formatted
     */
    public static Date parseISO8601BasicUTC(String dateStr) throws ParseException {
        return parseISO8601Basic(dateStr, true);
    }

    private static Date parseISO8601Basic(String dateStr, final boolean utcOnly) throws ParseException {
    	final String[] xsdDateTime;
        if (utcOnly) {
            xsdDateTime = new String[]{"yyyyMMdd'T'HHmmss.SSSZ", "yyyyMMdd'T'HHmmssZ"};
        } else {
            xsdDateTime = new String[]{
                    "yyyyMMdd'T'HHmmss.SSSZ", "yyyyMMdd'T'HHmmssZ", "yyyyMMdd'T'HHmmss.SSS", "yyyyMMdd'T'HHmmss"};
        }

    	// fix issue where + is replaced with ' ' in a URL
        dateStr = dateStr.replace(' ', '+');
        final int dateStrLen = dateStr.length();
        //These are the ways to explicitly express UTC. "-0000" or "-00" are not valid according to ISO8601:2004
        if (utcOnly && !dateStr.endsWith("Z") && !dateStr.endsWith("+0000") && !dateStr.endsWith("+00")) {
            throw new ParseException("Date/time string lacks UTC designator", dateStrLen);
        }
        //this is zero time so we need to add that TZ indicator for UTC
        if (dateStr.endsWith("Z")) {
            dateStr = dateStr.substring(0, dateStrLen - 1) + "+0000";
        }
        return parseDate(dateStr, xsdDateTime);
    }

    public static Date parseISO8601DateBasic(final String dateStr) throws ParseException {
        if (dateStr.length() != 8) {
            throw new ParseException("Invalid date: " + dateStr, 0);
        }
        final String[] xsdDate = new String[]{"yyyyMMdd"};
        return parseDate(dateStr, xsdDate);
    }

    private static Date parseDate(final String dateStr, final String[] formats)
            throws ParseException {
        if (formats.length == 0) {
            throw new AssertionError("Must have at least one format to parse");
        }

        ParseException ex = null;
        for (final String format: formats) {
            try {
                return new SimpleDateFormat(format).parse(dateStr);
            } catch (final ParseException e) {
                // try next format, but throw the last error
                ex = e;
            }
        }

        throw ex;
    }

    private DateUtils() {
        throw new AssertionError("Instantiation not allowed");
    }
}
