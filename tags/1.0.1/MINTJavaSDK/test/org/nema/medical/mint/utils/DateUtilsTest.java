//------------------------------------------------------------------------------
// Copyright (c) 2010 Vital Images, Inc. All Rights Reserved.
//
// This is UNPUBLISHED PROPRIETARY SOURCE CODE of Vital Images, Inc.;
// the contents of this file may not be disclosed to third parties,
// copied or duplicated in any form, in whole or in part, without the
// prior written permission of Vital Images, Inc.
//
// RESTRICTED RIGHTS LEGEND:
// Use, duplication or disclosure by the Government is subject to
// restrictions as set forth in subdivision (c)(1)(ii) of the Rights
// in Technical Data and Computer Software clause at DFARS 252.227-7013,
// and/or in similar or successor clauses in the FAR, DOD or NASA FAR
// Supplement. Unpublished rights reserved under the Copyright Laws of
// the United States.
//------------------------------------------------------------------------------

package org.nema.medical.mint.utils;

import org.junit.*;

import java.text.ParseException;
import java.util.Calendar;
import java.util.TimeZone;

import static org.junit.Assert.*;

public class DateUtilsTest {

    //TODO add the rest of the UTC formats that ISO8601 supports

    @Test
	public void testBasic() throws ParseException {
        TimeZone.setDefault(TimeZone.getTimeZone("GMT-5:00"));
		Calendar calendar = Calendar.getInstance();

		calendar.setTime(DateUtils.parseISO8601Basic("20100818T115959.999"));
		assertEquals(2010,calendar.get(Calendar.YEAR));
		assertEquals(7,calendar.get(Calendar.MONTH));
		assertEquals(18,calendar.get(Calendar.DAY_OF_MONTH));
		assertEquals(11,calendar.get(Calendar.HOUR_OF_DAY));
		assertEquals(59,calendar.get(Calendar.MINUTE));
		assertEquals(59,calendar.get(Calendar.SECOND));
		assertEquals(999,calendar.get(Calendar.MILLISECOND));

		calendar.setTime(DateUtils.parseISO8601Basic("20100818T115959.999Z"));
		assertEquals(6,calendar.get(Calendar.HOUR_OF_DAY));
		assertEquals(999,calendar.get(Calendar.MILLISECOND));

		calendar.setTime(DateUtils.parseISO8601Basic("20100818T115959.999-0500"));
		assertEquals(11,calendar.get(Calendar.HOUR_OF_DAY));
		assertEquals(999,calendar.get(Calendar.MILLISECOND));

		calendar.setTime(DateUtils.parseISO8601Basic("20100818T115959.999+0530"));
		assertEquals(1,calendar.get(Calendar.HOUR_OF_DAY));
		assertEquals(29,calendar.get(Calendar.MINUTE));
		assertEquals(999,calendar.get(Calendar.MILLISECOND));

		// test hack for space instead of plus for URL mapping
		calendar.setTime(DateUtils.parseISO8601Basic("20100818T115959.999 0530"));
		assertEquals(1,calendar.get(Calendar.HOUR_OF_DAY));
		assertEquals(29,calendar.get(Calendar.MINUTE));
		assertEquals(999,calendar.get(Calendar.MILLISECOND));

		calendar.setTime(DateUtils.parseISO8601Basic("20100818T115959Z"));
		assertEquals(6,calendar.get(Calendar.HOUR_OF_DAY));
		assertEquals(59,calendar.get(Calendar.MINUTE));
		assertEquals(59,calendar.get(Calendar.SECOND));
		assertEquals(0,calendar.get(Calendar.MILLISECOND));

		calendar.setTime(DateUtils.parseISO8601Basic("20100818T115959-0500"));
		assertEquals(11,calendar.get(Calendar.HOUR_OF_DAY));
		assertEquals(59,calendar.get(Calendar.MINUTE));
		assertEquals(59,calendar.get(Calendar.SECOND));
		assertEquals(0,calendar.get(Calendar.MILLISECOND));

		calendar.setTime(DateUtils.parseISO8601Basic("20100818T115959+0530"));
		assertEquals(1,calendar.get(Calendar.HOUR_OF_DAY));
		assertEquals(29,calendar.get(Calendar.MINUTE));
		assertEquals(59,calendar.get(Calendar.SECOND));
		assertEquals(0,calendar.get(Calendar.MILLISECOND));

		// test hack for space instead of plus for URL mapping
		calendar.setTime(DateUtils.parseISO8601Basic("20100818T115959 0530"));
		assertEquals(1,calendar.get(Calendar.HOUR_OF_DAY));
		assertEquals(29,calendar.get(Calendar.MINUTE));
		assertEquals(59,calendar.get(Calendar.SECOND));
		assertEquals(0,calendar.get(Calendar.MILLISECOND));

		calendar.setTime(DateUtils.parseISO8601Basic("20100818T115959"));
		assertEquals(11,calendar.get(Calendar.HOUR_OF_DAY));
		assertEquals(59,calendar.get(Calendar.MINUTE));
		assertEquals(59,calendar.get(Calendar.SECOND));
		assertEquals(0,calendar.get(Calendar.MILLISECOND));

		calendar.setTime(DateUtils.parseISO8601Date("20100819"));
		assertEquals(0,calendar.get(Calendar.HOUR_OF_DAY));
		assertEquals(0,calendar.get(Calendar.MINUTE));
		assertEquals(0,calendar.get(Calendar.SECOND));
		assertEquals(0,calendar.get(Calendar.MILLISECOND));
	}

    @Test
	public void testBasicUTC() throws ParseException {
        TimeZone.setDefault(TimeZone.getTimeZone("GMT-5:00"));
		final Calendar calendar = Calendar.getInstance();

		calendar.setTime(DateUtils.parseISO8601BasicUTC("20100818T115959.999Z"));
		assertEquals(2010,calendar.get(Calendar.YEAR));
		assertEquals(7,calendar.get(Calendar.MONTH));
		assertEquals(18,calendar.get(Calendar.DAY_OF_MONTH));
		assertEquals(6,calendar.get(Calendar.HOUR_OF_DAY));
		assertEquals(59,calendar.get(Calendar.MINUTE));
		assertEquals(59,calendar.get(Calendar.SECOND));
		assertEquals(999,calendar.get(Calendar.MILLISECOND));

		calendar.setTime(DateUtils.parseISO8601BasicUTC("20100818T115959Z"));
		assertEquals(6,calendar.get(Calendar.HOUR_OF_DAY));
		assertEquals(59,calendar.get(Calendar.MINUTE));
		assertEquals(59,calendar.get(Calendar.SECOND));
		assertEquals(0,calendar.get(Calendar.MILLISECOND));

		calendar.setTime(DateUtils.parseISO8601Date("20100819"));
		assertEquals(0,calendar.get(Calendar.HOUR_OF_DAY));
		assertEquals(0,calendar.get(Calendar.MINUTE));
		assertEquals(0,calendar.get(Calendar.SECOND));
		assertEquals(0,calendar.get(Calendar.MILLISECOND));
	}

    @Test
    public void testExtended() throws ParseException {
        TimeZone.setDefault(TimeZone.getTimeZone("GMT-5:00"));
        Calendar calendar = Calendar.getInstance();

        calendar.setTime(DateUtils.parseISO8601Extended("2010-08-18T11:59:59.999"));
        assertEquals(2010,calendar.get(Calendar.YEAR));
        assertEquals(7,calendar.get(Calendar.MONTH));
        assertEquals(18,calendar.get(Calendar.DAY_OF_MONTH));
        assertEquals(11,calendar.get(Calendar.HOUR_OF_DAY));
        assertEquals(59,calendar.get(Calendar.MINUTE));
        assertEquals(59,calendar.get(Calendar.SECOND));
        assertEquals(999,calendar.get(Calendar.MILLISECOND));

        calendar.setTime(DateUtils.parseISO8601Extended("2010-08-18T11:59:59.999Z"));
        assertEquals(6,calendar.get(Calendar.HOUR_OF_DAY));
        assertEquals(999,calendar.get(Calendar.MILLISECOND));

        calendar.setTime(DateUtils.parseISO8601Extended("2010-08-18T11:59:59.999-05:00"));
        assertEquals(11,calendar.get(Calendar.HOUR_OF_DAY));
        assertEquals(999,calendar.get(Calendar.MILLISECOND));

        calendar.setTime(DateUtils.parseISO8601Extended("2010-08-18T11:59:59.999+05:30"));
        assertEquals(1,calendar.get(Calendar.HOUR_OF_DAY));
        assertEquals(29,calendar.get(Calendar.MINUTE));
        assertEquals(999,calendar.get(Calendar.MILLISECOND));

        // test hack for space instead of plus for URL mapping
        calendar.setTime(DateUtils.parseISO8601Extended("2010-08-18T11:59:59.999 05:30"));
        assertEquals(1,calendar.get(Calendar.HOUR_OF_DAY));
        assertEquals(29,calendar.get(Calendar.MINUTE));
        assertEquals(999,calendar.get(Calendar.MILLISECOND));

        calendar.setTime(DateUtils.parseISO8601Extended("2010-08-18T11:59:59Z"));
        assertEquals(6,calendar.get(Calendar.HOUR_OF_DAY));
        assertEquals(59,calendar.get(Calendar.MINUTE));
        assertEquals(59,calendar.get(Calendar.SECOND));
        assertEquals(0,calendar.get(Calendar.MILLISECOND));

        calendar.setTime(DateUtils.parseISO8601Extended("2010-08-18T11:59:59-05:00"));
        assertEquals(11,calendar.get(Calendar.HOUR_OF_DAY));
        assertEquals(59,calendar.get(Calendar.MINUTE));
        assertEquals(59,calendar.get(Calendar.SECOND));
        assertEquals(0,calendar.get(Calendar.MILLISECOND));

        calendar.setTime(DateUtils.parseISO8601Extended("2010-08-18T11:59:59+05:30"));
        assertEquals(1,calendar.get(Calendar.HOUR_OF_DAY));
        assertEquals(29,calendar.get(Calendar.MINUTE));
        assertEquals(59,calendar.get(Calendar.SECOND));
        assertEquals(0,calendar.get(Calendar.MILLISECOND));

        // test hack for space instead of plus for URL mapping
        calendar.setTime(DateUtils.parseISO8601Extended("2010-08-18T11:59:59 05:30"));
        assertEquals(1,calendar.get(Calendar.HOUR_OF_DAY));
        assertEquals(29,calendar.get(Calendar.MINUTE));
        assertEquals(59,calendar.get(Calendar.SECOND));
        assertEquals(0,calendar.get(Calendar.MILLISECOND));

        calendar.setTime(DateUtils.parseISO8601Extended("2010-08-18T11:59:59"));
        assertEquals(11,calendar.get(Calendar.HOUR_OF_DAY));
        assertEquals(59,calendar.get(Calendar.MINUTE));
        assertEquals(59,calendar.get(Calendar.SECOND));
        assertEquals(0,calendar.get(Calendar.MILLISECOND));

        calendar.setTime(DateUtils.parseISO8601Date("2010-08-19"));
        assertEquals(0,calendar.get(Calendar.HOUR_OF_DAY));
        assertEquals(0,calendar.get(Calendar.MINUTE));
        assertEquals(0,calendar.get(Calendar.SECOND));
        assertEquals(0,calendar.get(Calendar.MILLISECOND));
    }

	@Test
	public void testExtendedUTC() throws ParseException {
        TimeZone.setDefault(TimeZone.getTimeZone("GMT-5:00"));
		final Calendar calendar = Calendar.getInstance();

		calendar.setTime(DateUtils.parseISO8601ExtendedUTC("2010-08-18T11:59:59.999Z"));
		assertEquals(2010,calendar.get(Calendar.YEAR));
		assertEquals(7,calendar.get(Calendar.MONTH));
		assertEquals(18,calendar.get(Calendar.DAY_OF_MONTH));
		assertEquals(6,calendar.get(Calendar.HOUR_OF_DAY));
		assertEquals(59,calendar.get(Calendar.MINUTE));
		assertEquals(59,calendar.get(Calendar.SECOND));
		assertEquals(999,calendar.get(Calendar.MILLISECOND));

		calendar.setTime(DateUtils.parseISO8601ExtendedUTC("2010-08-18T11:59:59Z"));
		assertEquals(6,calendar.get(Calendar.HOUR_OF_DAY));
		assertEquals(59,calendar.get(Calendar.MINUTE));
		assertEquals(59,calendar.get(Calendar.SECOND));
		assertEquals(0,calendar.get(Calendar.MILLISECOND));

		calendar.setTime(DateUtils.parseISO8601Date("2010-08-19"));
		assertEquals(0,calendar.get(Calendar.HOUR_OF_DAY));
		assertEquals(0,calendar.get(Calendar.MINUTE));
		assertEquals(0,calendar.get(Calendar.SECOND));
		assertEquals(0,calendar.get(Calendar.MILLISECOND));
	}
}
