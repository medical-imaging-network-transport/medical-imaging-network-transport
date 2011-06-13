package org.nema.medical.mint.utils;

import static org.junit.Assert.assertEquals;

import java.text.ParseException;
import java.util.Calendar;
import java.util.TimeZone;

import org.junit.Test;


public class ISO8601DateUtilsTest {
	final ISO8601DateUtils dateUtil = new JodaDateUtils();
	
    final TimeZone utcTZ = TimeZone.getTimeZone("Etc/UTC");
    final TimeZone localTZ = TimeZone.getDefault();
    final TimeZone utcLess5TZ = TimeZone.getTimeZone("GMT-5:00");
    final TimeZone utcPlus530TZ = TimeZone.getTimeZone("GMT+5:30");
    final TimeZone utcPlus4TZ = TimeZone.getTimeZone("GMT+4:00");

    final Calendar utcCal = Calendar.getInstance(utcTZ);
    final Calendar localCal = Calendar.getInstance(localTZ);
    final Calendar utcLess5Cal = Calendar.getInstance(utcLess5TZ);
    final Calendar utcPlus530Cal = Calendar.getInstance(utcPlus530TZ);
    final Calendar utcPlus4Cal = Calendar.getInstance(utcPlus4TZ);
    
    //TODO add the rest of the UTC formats that ISO8601 supports

    @Test
	public void testBasic() throws ParseException {
		localCal.setTime(dateUtil.parseISO8601Basic("20100818T115959.999"));
		assertEquals(2010, localCal.get(Calendar.YEAR));
		assertEquals(7, localCal.get(Calendar.MONTH));
		assertEquals(18, localCal.get(Calendar.DAY_OF_MONTH));
		assertEquals(11, localCal.get(Calendar.HOUR_OF_DAY));
		assertEquals(59, localCal.get(Calendar.MINUTE));
		assertEquals(59, localCal.get(Calendar.SECOND));
		assertEquals(999, localCal.get(Calendar.MILLISECOND));

		utcCal.setTime(dateUtil.parseISO8601Basic("20100818T115959.999Z"));
		assertEquals(11, utcCal.get(Calendar.HOUR_OF_DAY));
		assertEquals(999, utcCal.get(Calendar.MILLISECOND));

		utcLess5Cal.setTime(dateUtil.parseISO8601Basic("20100818T115959.999-0500"));
		assertEquals(11, utcLess5Cal.get(Calendar.HOUR_OF_DAY));
		assertEquals(999, utcLess5Cal.get(Calendar.MILLISECOND));

		utcPlus530Cal.setTime(dateUtil.parseISO8601Basic("20100818T115959.999+0530"));
		assertEquals(11, utcPlus530Cal.get(Calendar.HOUR_OF_DAY));
		assertEquals(59, utcPlus530Cal.get(Calendar.MINUTE));
		assertEquals(999, utcPlus530Cal.get(Calendar.MILLISECOND));

		// test hack for space instead of plus for URL mapping
		utcPlus530Cal.setTime(dateUtil.parseISO8601Basic("20100818T115959.999 0530"));
		assertEquals(11, utcPlus530Cal.get(Calendar.HOUR_OF_DAY));
		assertEquals(59, utcPlus530Cal.get(Calendar.MINUTE));
		assertEquals(999, utcPlus530Cal.get(Calendar.MILLISECOND));

		utcCal.setTime(dateUtil.parseISO8601Basic("20100818T115959Z"));
		assertEquals(11, utcCal.get(Calendar.HOUR_OF_DAY));
		assertEquals(59, utcCal.get(Calendar.MINUTE));
		assertEquals(59, utcCal.get(Calendar.SECOND));
		assertEquals(0, utcCal.get(Calendar.MILLISECOND));

		utcLess5Cal.setTime(dateUtil.parseISO8601Basic("20100818T115959-0500"));
		assertEquals(11, utcLess5Cal.get(Calendar.HOUR_OF_DAY));
		assertEquals(59, utcLess5Cal.get(Calendar.MINUTE));
		assertEquals(59, utcLess5Cal.get(Calendar.SECOND));
		assertEquals(0, utcLess5Cal.get(Calendar.MILLISECOND));

		utcPlus530Cal.setTime(dateUtil.parseISO8601Basic("20100818T115959+0530"));
		assertEquals(11, utcPlus530Cal.get(Calendar.HOUR_OF_DAY));
		assertEquals(59, utcPlus530Cal.get(Calendar.MINUTE));
		assertEquals(59, utcPlus530Cal.get(Calendar.SECOND));
		assertEquals(0, utcPlus530Cal.get(Calendar.MILLISECOND));

		// test hack for space instead of plus for URL mapping
		utcPlus530Cal.setTime(dateUtil.parseISO8601Basic("20100818T115959 0530"));
		assertEquals(11, utcPlus530Cal.get(Calendar.HOUR_OF_DAY));
		assertEquals(59, utcPlus530Cal.get(Calendar.MINUTE));
		assertEquals(59, utcPlus530Cal.get(Calendar.SECOND));
		assertEquals(0, utcPlus530Cal.get(Calendar.MILLISECOND));

		localCal.setTime(dateUtil.parseISO8601Basic("20100818T115959"));
		assertEquals(11, localCal.get(Calendar.HOUR_OF_DAY));
		assertEquals(59, localCal.get(Calendar.MINUTE));
		assertEquals(59, localCal.get(Calendar.SECOND));
		assertEquals(0, localCal.get(Calendar.MILLISECOND));
	}

    @Test
	public void testBasicUTC() throws ParseException {
		utcCal.setTime(dateUtil.parseISO8601BasicUTC("20100818T115959.999Z"));
		assertEquals(2010, utcCal.get(Calendar.YEAR));
		assertEquals(7, utcCal.get(Calendar.MONTH));	
		assertEquals(18, utcCal.get(Calendar.DAY_OF_MONTH));
		assertEquals(11, utcCal.get(Calendar.HOUR_OF_DAY));
		assertEquals(59, utcCal.get(Calendar.MINUTE));
		assertEquals(59, utcCal.get(Calendar.SECOND));
		assertEquals(999, utcCal.get(Calendar.MILLISECOND));

        utcCal.setTime(dateUtil.parseISO8601BasicUTC("20100818T115959.999+0000"));
        assertEquals(2010, utcCal.get(Calendar.YEAR));
        assertEquals(7, utcCal.get(Calendar.MONTH));	
        assertEquals(18, utcCal.get(Calendar.DAY_OF_MONTH));
        assertEquals(11, utcCal.get(Calendar.HOUR_OF_DAY));
        assertEquals(59, utcCal.get(Calendar.MINUTE));
        assertEquals(59, utcCal.get(Calendar.SECOND));
        assertEquals(999, utcCal.get(Calendar.MILLISECOND));

		utcCal.setTime(dateUtil.parseISO8601BasicUTC("20100818T115959Z"));
		assertEquals(11, utcCal.get(Calendar.HOUR_OF_DAY));
		assertEquals(59, utcCal.get(Calendar.MINUTE));
		assertEquals(59, utcCal.get(Calendar.SECOND));
		assertEquals(0, utcCal.get(Calendar.MILLISECOND));
	}

    @Test
	public void testDateBasic() throws ParseException {
        localCal.setTime(dateUtil.parseISO8601DateBasic("20100819"));
        assertEquals(0, localCal.get(Calendar.HOUR_OF_DAY));
        assertEquals(0, localCal.get(Calendar.MINUTE));
        assertEquals(0, localCal.get(Calendar.SECOND));
        assertEquals(0, localCal.get(Calendar.MILLISECOND));
    }

    //Verify that we are not supporting ISO8601 extended format for dates
    @Test(expected = ParseException.class)
	public void testDateBasic0() throws ParseException {
        dateUtil.parseISO8601DateBasic("2010-08-19");
    }

    //Verify that we are not supporting ISO8601 expanded representations for dates
    @Test(expected = ParseException.class)
	public void testDateBasic01() throws ParseException {
        dateUtil.parseISO8601DateBasic("+0020100819");
    }

    //Verify that we are not supporting ISO8601 expanded representations for dates
    @Test(expected = ParseException.class)
	public void testDateBasic011() throws ParseException {
        dateUtil.parseISO8601DateBasic("-0020100819");
    }

    //Verify that we are not supporting ISO8601 expanded representations for dates
    @Test(expected = ParseException.class)
	public void testDateBasic02() throws ParseException {
        dateUtil.parseISO8601DateBasic("+020100819");
    }

    //Verify that we are not supporting ISO8601 expanded representations for dates
    @Test(expected = ParseException.class)
	public void testDateBasic03() throws ParseException {
        dateUtil.parseISO8601DateBasic("+002010-08-19");
    }

    //Verify that we are not supporting ISO8601 expanded representations for dates
    @Test(expected = ParseException.class)
	public void testDateBasic04() throws ParseException {
        dateUtil.parseISO8601DateBasic("+002010-08");
    }

    //Verify that we are not supporting ISO8601 expanded representations for dates
    @Test(expected = ParseException.class)
	public void testDateBasic05() throws ParseException {
        dateUtil.parseISO8601DateBasic("+002010");
    }

    //Verify that we are not supporting ISO8601 expanded representations for dates
    @Test(expected = ParseException.class)
	public void testDateBasic06() throws ParseException {
        dateUtil.parseISO8601DateBasic("+0010");
    }

    //Verify that we are not supporting ISO8601 representations with reduced accuracy
    @Test(expected = ParseException.class)
	public void testDateBasic1() throws ParseException {
        dateUtil.parseISO8601DateBasic("201008");
    }

    //Verify that we are not supporting ISO8601 representations with reduced accuracy
    @Test(expected = ParseException.class)
	public void testDateBasic2() throws ParseException {
        dateUtil.parseISO8601DateBasic("2010-08");
    }

    //Verify that we are not supporting ISO8601 representations with reduced accuracy
    @Test(expected = ParseException.class)
	public void testDateBasic3() throws ParseException {
        dateUtil.parseISO8601DateBasic("2010");
    }

    //Verify that we are not supporting ISO8601 representations with reduced accuracy
    @Test(expected = ParseException.class)
	public void testDateBasic4() throws ParseException {
        dateUtil.parseISO8601DateBasic("10");
    }

    //Verify that we are not supporting ISO8601 Ordinal Dates
    @Test(expected = ParseException.class)
	public void testDateBasic5() throws ParseException {
        dateUtil.parseISO8601DateBasic("2010110");
    }

    //Verify that we are not supporting ISO8601 Ordinal Dates
    @Test(expected = ParseException.class)
	public void testDateBasic51() throws ParseException {
        dateUtil.parseISO8601DateBasic("2010-110");
    }

    //Verify that we are not supporting ISO8601 Ordinal Dates
    @Test(expected = ParseException.class)
	public void testDateBasic52() throws ParseException {
        dateUtil.parseISO8601DateBasic("+002010110");
    }

    //Verify that we are not supporting ISO8601 Ordinal Dates
    @Test(expected = ParseException.class)
	public void testDateBasic53() throws ParseException {
        dateUtil.parseISO8601DateBasic("+002010-110");
    }

    //Verify that we are not supporting ISO8601 week date
    @Test(expected = ParseException.class)
	public void testDateBasic6() throws ParseException {
        dateUtil.parseISO8601DateBasic("1985W155");
    }

    //Verify that we are not supporting ISO8601 week date
    @Test(expected = ParseException.class)
	public void testDateBasic61() throws ParseException {
        dateUtil.parseISO8601DateBasic("1985-W15-5");
    }

    //Verify that we are not supporting ISO8601 week date
    @Test(expected = ParseException.class)
	public void testDateBasic62() throws ParseException {
        dateUtil.parseISO8601DateBasic("1985W15");
    }

    //Verify that we are not supporting ISO8601 week date
    @Test(expected = ParseException.class)
	public void testDateBasic63() throws ParseException {
        dateUtil.parseISO8601DateBasic("1985-W15");
    }

    //Verify that we are not supporting ISO8601 week date
    @Test(expected = ParseException.class)
	public void testDateBasic64() throws ParseException {
        dateUtil.parseISO8601DateBasic("+001985W155");
    }

    //Verify that we are not supporting ISO8601 week date
    @Test(expected = ParseException.class)
	public void testDateBasic65() throws ParseException {
        dateUtil.parseISO8601DateBasic("+001985-W15-5");
    }

    //Verify that we are not supporting ISO8601 week date
    @Test(expected = ParseException.class)
	public void testDateBasic66() throws ParseException {
        dateUtil.parseISO8601DateBasic("+001985W15");
    }

    //Verify that we are not supporting ISO8601 week date
    @Test(expected = ParseException.class)
	public void testDateBasic67() throws ParseException {
        dateUtil.parseISO8601DateBasic("+001985-W15");
    }

    //Verify support for ISO8601 date/time
    @Test
	public void testDateTimeBasic1() throws ParseException {
        localCal.setTime(dateUtil.parseISO8601Basic("19850412T101530"));
        assertEquals(1985, localCal.get(Calendar.YEAR));
        assertEquals(3, localCal.get(Calendar.MONTH));	
        assertEquals(12, localCal.get(Calendar.DAY_OF_MONTH));
        assertEquals(10, localCal.get(Calendar.HOUR_OF_DAY));
        assertEquals(15, localCal.get(Calendar.MINUTE));
        assertEquals(30, localCal.get(Calendar.SECOND));
        assertEquals(0, localCal.get(Calendar.MILLISECOND));
        utcCal.setTime(dateUtil.parseISO8601Basic("19850412T101530Z"));
        assertEquals(1985, utcCal.get(Calendar.YEAR));
        assertEquals(3, utcCal.get(Calendar.MONTH));
        assertEquals(12, utcCal.get(Calendar.DAY_OF_MONTH));
        assertEquals(10, utcCal.get(Calendar.HOUR_OF_DAY));
        assertEquals(15, utcCal.get(Calendar.MINUTE));
        assertEquals(30, utcCal.get(Calendar.SECOND));
        assertEquals(0, utcCal.get(Calendar.MILLISECOND));
        utcPlus4Cal.setTime(dateUtil.parseISO8601Basic("19850412T101530+0400"));
        assertEquals(1985, utcPlus4Cal.get(Calendar.YEAR));
        assertEquals(3, utcPlus4Cal.get(Calendar.MONTH));
        assertEquals(12, utcPlus4Cal.get(Calendar.DAY_OF_MONTH));
        assertEquals(10, utcPlus4Cal.get(Calendar.HOUR_OF_DAY));
        assertEquals(15, utcPlus4Cal.get(Calendar.MINUTE));
        assertEquals(30, utcPlus4Cal.get(Calendar.SECOND));
        assertEquals(0, utcPlus4Cal.get(Calendar.MILLISECOND));
        dateUtil.parseISO8601Basic("19850412T101530+04");
        assertEquals(1985, utcPlus4Cal.get(Calendar.YEAR));
        assertEquals(3, utcPlus4Cal.get(Calendar.MONTH));
        assertEquals(12, utcPlus4Cal.get(Calendar.DAY_OF_MONTH));
        assertEquals(10, utcPlus4Cal.get(Calendar.HOUR_OF_DAY));
        assertEquals(15, utcPlus4Cal.get(Calendar.MINUTE));
        assertEquals(30, utcPlus4Cal.get(Calendar.SECOND));
        assertEquals(0, utcPlus4Cal.get(Calendar.MILLISECOND));
    }

    //Verify support for ISO8601 date/time
    @Test(expected = ParseException.class)
	public void testDateTimeBasic21() throws ParseException {
        dateUtil.parseISO8601Basic("1985-04-12T10:15:30");
    }

    //Verify support for ISO8601 date/time
    @Test(expected = ParseException.class)
	public void testDateTimeBasic22() throws ParseException {
        dateUtil.parseISO8601Basic("1985-04-12T10:15:30Z");
    }

    //Verify support for ISO8601 date/time
    @Test(expected = ParseException.class)
	public void testDateTimeBasic23() throws ParseException {
        dateUtil.parseISO8601Basic("1985-04-12T10:15:30+04:00");
    }

    //Verify support for ISO8601 date/time
    @Test(expected = ParseException.class)
	public void testDateTimeBasic24() throws ParseException {
        dateUtil.parseISO8601Basic("1985-04-12T10:15:30+04");
    }

    //Verify support for ISO8601 date/time
    @Test(expected = ParseException.class)
	public void testDateTimeBasic31() throws ParseException {
        dateUtil.parseISO8601Basic("19850412T1015");
    }

    //Verify support for ISO8601 date/time
    @Test(expected = ParseException.class)
	public void testDateTimeBasic32() throws ParseException {
        dateUtil.parseISO8601Basic("19850412T1015Z");
    }

    //Verify support for ISO8601 date/time
    @Test(expected = ParseException.class)
	public void testDateTimeBasic33() throws ParseException {
        dateUtil.parseISO8601Basic("1985-04-12T10:15");
    }

    //Verify support for ISO8601 date/time
    @Test(expected = ParseException.class)
	public void testDateTimeBasic34() throws ParseException {
        dateUtil.parseISO8601Basic("1985-04-12T10:15Z");
    }

    //Verify support for ISO8601 date/time
    @Test(expected = ParseException.class)
	public void testDateTimeBasic41() throws ParseException {
        dateUtil.parseISO8601Basic("1985W155T1015+0400");
    }

    //Verify support for ISO8601 date/time
    @Test(expected = ParseException.class)
	public void testDateTimeBasic42() throws ParseException {
        dateUtil.parseISO8601Basic("1985-W15-5T10:15+04");
    }

    //Verify support for ISO8601 UTC date/time
    @Test
	public void testDateTimeBasicUTC1() throws ParseException {
        utcCal.setTime(dateUtil.parseISO8601BasicUTC("19850412T101530Z"));
        assertEquals(1985, utcCal.get(Calendar.YEAR));
        assertEquals(3, utcCal.get(Calendar.MONTH));	
        assertEquals(12, utcCal.get(Calendar.DAY_OF_MONTH));
        assertEquals(10, utcCal.get(Calendar.HOUR_OF_DAY));
        assertEquals(15, utcCal.get(Calendar.MINUTE));
        assertEquals(30, utcCal.get(Calendar.SECOND));
        assertEquals(0, utcCal.get(Calendar.MILLISECOND));
        utcCal.setTime(dateUtil.parseISO8601BasicUTC("19850412T101530+0000"));
        assertEquals(1985, utcCal.get(Calendar.YEAR));
        assertEquals(3, utcCal.get(Calendar.MONTH)); 
        assertEquals(12, utcCal.get(Calendar.DAY_OF_MONTH));
        assertEquals(10, utcCal.get(Calendar.HOUR_OF_DAY));
        assertEquals(15, utcCal.get(Calendar.MINUTE));
        assertEquals(30, utcCal.get(Calendar.SECOND));
        assertEquals(0, utcCal.get(Calendar.MILLISECOND));
        utcCal.setTime(dateUtil.parseISO8601BasicUTC("19850412T101530+00"));
        assertEquals(1985, utcCal.get(Calendar.YEAR));
        assertEquals(3, utcCal.get(Calendar.MONTH)); 
        assertEquals(12, utcCal.get(Calendar.DAY_OF_MONTH));
        assertEquals(10, utcCal.get(Calendar.HOUR_OF_DAY));
        assertEquals(15, utcCal.get(Calendar.MINUTE));
        assertEquals(30, utcCal.get(Calendar.SECOND));
        assertEquals(0, utcCal.get(Calendar.MILLISECOND));
    }

    //Verify support for ISO8601 UTC date/time
    @Test(expected = ParseException.class)
	public void testDateTimeBasicUTC2() throws ParseException {
        dateUtil.parseISO8601BasicUTC("19850412T101530");
    }

    //Verify support for ISO8601 UTC date/time
    @Test(expected = ParseException.class)
	public void testDateTimeBasicUTC3() throws ParseException {
        dateUtil.parseISO8601BasicUTC("19850412T101530+0400");
    }

    //Verify support for ISO8601 UTC date/time
    @Test(expected = ParseException.class)
	public void testDateTimeBasicUTC4() throws ParseException {
        dateUtil.parseISO8601BasicUTC("19850412T101530+04");
    }

    //Verify support for ISO8601 UTC date/time
    @Test(expected = ParseException.class)
	public void testDateTimeBasicUTC5() throws ParseException {
        dateUtil.parseISO8601BasicUTC("19850412T101530-0000");
    }

    //Verify support for ISO8601 UTC date/time
    @Test(expected = ParseException.class)
	public void testDateTimeBasicUTC61() throws ParseException {
        dateUtil.parseISO8601BasicUTC("1985-04-12T10:15:30Z");
    }

    //Verify support for ISO8601 UTC date/time
    @Test(expected = ParseException.class)
	public void testDateTimeBasicUTC62() throws ParseException {
        dateUtil.parseISO8601BasicUTC("1985-04-12T10:15:30+0000");
    }

    //Verify support for ISO8601 UTC date/time
    @Test(expected = ParseException.class)
	public void testDateTimeBasicUTC63() throws ParseException {
        dateUtil.parseISO8601BasicUTC("1985-04-12T10:15:30+00");
    }

    //Verify support for ISO8601 UTC date/time
    @Test(expected = ParseException.class)
	public void testDateTimeBasicUTC71() throws ParseException {
        dateUtil.parseISO8601BasicUTC("19850412T1015Z");
    }

    //Verify support for ISO8601 UTC date/time
    @Test(expected = ParseException.class)
	public void testDateTimeBasicUTC72() throws ParseException {
        dateUtil.parseISO8601BasicUTC("19850412T1015+0000");
    }

    //Verify support for ISO8601 UTC date/time
    @Test(expected = ParseException.class)
	public void testDateTimeBasicUTC73() throws ParseException {
        dateUtil.parseISO8601BasicUTC("19850412T1015+00");
    }

    //Verify support for ISO8601 UTC date/time
    @Test(expected = ParseException.class)
	public void testDateTimeBasicUTC81() throws ParseException {
        dateUtil.parseISO8601BasicUTC("1985W155T1015Z");
    }
}
