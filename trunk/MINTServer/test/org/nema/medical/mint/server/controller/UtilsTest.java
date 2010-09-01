package org.nema.medical.mint.server.controller;

import static org.junit.Assert.assertEquals;

import java.text.ParseException;
import java.util.Calendar;

import org.junit.Test;

public class UtilsTest {
	
	@Test
	public void testBasic() throws ParseException {
		Calendar calendar = Calendar.getInstance();
		
		calendar.setTime(Utils.parseISO8601Basic("20100818T115959.999"));
		assertEquals(2010,calendar.get(Calendar.YEAR));
		assertEquals(7,calendar.get(Calendar.MONTH));
		assertEquals(18,calendar.get(Calendar.DAY_OF_MONTH));
		assertEquals(11,calendar.get(Calendar.HOUR_OF_DAY));
		assertEquals(59,calendar.get(Calendar.MINUTE));
		assertEquals(59,calendar.get(Calendar.SECOND));
		assertEquals(999,calendar.get(Calendar.MILLISECOND));
		
		calendar.setTime(Utils.parseISO8601Basic("20100818T115959.999Z"));
		assertEquals(6,calendar.get(Calendar.HOUR_OF_DAY));
		assertEquals(999,calendar.get(Calendar.MILLISECOND));
		
		calendar.setTime(Utils.parseISO8601Basic("20100818T115959.999-0500"));
		assertEquals(11,calendar.get(Calendar.HOUR_OF_DAY));
		assertEquals(999,calendar.get(Calendar.MILLISECOND));
		
		calendar.setTime(Utils.parseISO8601Basic("20100818T115959.999+0530"));
		assertEquals(1,calendar.get(Calendar.HOUR_OF_DAY));
		assertEquals(29,calendar.get(Calendar.MINUTE));
		assertEquals(999,calendar.get(Calendar.MILLISECOND));

		// test hack for space instead of plus for URL mapping
		calendar.setTime(Utils.parseISO8601Basic("20100818T115959.999 0530"));
		assertEquals(1,calendar.get(Calendar.HOUR_OF_DAY));
		assertEquals(29,calendar.get(Calendar.MINUTE));
		assertEquals(999,calendar.get(Calendar.MILLISECOND));
		
		calendar.setTime(Utils.parseISO8601Basic("20100818T115959Z"));
		assertEquals(6,calendar.get(Calendar.HOUR_OF_DAY));
		assertEquals(59,calendar.get(Calendar.MINUTE));
		assertEquals(59,calendar.get(Calendar.SECOND));
		assertEquals(0,calendar.get(Calendar.MILLISECOND));
		
		calendar.setTime(Utils.parseISO8601Basic("20100818T115959-0500"));
		assertEquals(11,calendar.get(Calendar.HOUR_OF_DAY));
		assertEquals(59,calendar.get(Calendar.MINUTE));
		assertEquals(59,calendar.get(Calendar.SECOND));
		assertEquals(0,calendar.get(Calendar.MILLISECOND));
		
		calendar.setTime(Utils.parseISO8601Basic("20100818T115959+0530"));
		assertEquals(1,calendar.get(Calendar.HOUR_OF_DAY));
		assertEquals(29,calendar.get(Calendar.MINUTE));
		assertEquals(59,calendar.get(Calendar.SECOND));
		assertEquals(0,calendar.get(Calendar.MILLISECOND));
		
		// test hack for space instead of plus for URL mapping
		calendar.setTime(Utils.parseISO8601Basic("20100818T115959 0530"));
		assertEquals(1,calendar.get(Calendar.HOUR_OF_DAY));
		assertEquals(29,calendar.get(Calendar.MINUTE));
		assertEquals(59,calendar.get(Calendar.SECOND));
		assertEquals(0,calendar.get(Calendar.MILLISECOND));
		
		calendar.setTime(Utils.parseISO8601Basic("20100818T115959"));
		assertEquals(11,calendar.get(Calendar.HOUR_OF_DAY));
		assertEquals(59,calendar.get(Calendar.MINUTE));
		assertEquals(59,calendar.get(Calendar.SECOND));
		assertEquals(0,calendar.get(Calendar.MILLISECOND));
		
		calendar.setTime(Utils.parseISO8601Basic("20100819"));
		assertEquals(0,calendar.get(Calendar.HOUR_OF_DAY));
		assertEquals(0,calendar.get(Calendar.MINUTE));
		assertEquals(0,calendar.get(Calendar.SECOND));
		assertEquals(0,calendar.get(Calendar.MILLISECOND));
	}

	@Test
	public void testExtended() throws ParseException {
		Calendar calendar = Calendar.getInstance();
		
		calendar.setTime(Utils.parseISO8601Extended("2010-08-18T11:59:59.999"));
		assertEquals(2010,calendar.get(Calendar.YEAR));
		assertEquals(7,calendar.get(Calendar.MONTH));
		assertEquals(18,calendar.get(Calendar.DAY_OF_MONTH));
		assertEquals(11,calendar.get(Calendar.HOUR_OF_DAY));
		assertEquals(59,calendar.get(Calendar.MINUTE));
		assertEquals(59,calendar.get(Calendar.SECOND));
		assertEquals(999,calendar.get(Calendar.MILLISECOND));
		
		calendar.setTime(Utils.parseISO8601Extended("2010-08-18T11:59:59.999Z"));
		assertEquals(6,calendar.get(Calendar.HOUR_OF_DAY));
		assertEquals(999,calendar.get(Calendar.MILLISECOND));
		
		calendar.setTime(Utils.parseISO8601Extended("2010-08-18T11:59:59.999-05:00"));
		assertEquals(11,calendar.get(Calendar.HOUR_OF_DAY));
		assertEquals(999,calendar.get(Calendar.MILLISECOND));
		
		calendar.setTime(Utils.parseISO8601Extended("2010-08-18T11:59:59.999+05:30"));
		assertEquals(1,calendar.get(Calendar.HOUR_OF_DAY));
		assertEquals(29,calendar.get(Calendar.MINUTE));
		assertEquals(999,calendar.get(Calendar.MILLISECOND));
		
		// test hack for space instead of plus for URL mapping
		calendar.setTime(Utils.parseISO8601Extended("2010-08-18T11:59:59.999 05:30"));
		assertEquals(1,calendar.get(Calendar.HOUR_OF_DAY));
		assertEquals(29,calendar.get(Calendar.MINUTE));
		assertEquals(999,calendar.get(Calendar.MILLISECOND));
		
		calendar.setTime(Utils.parseISO8601Extended("2010-08-18T11:59:59Z"));
		assertEquals(6,calendar.get(Calendar.HOUR_OF_DAY));
		assertEquals(59,calendar.get(Calendar.MINUTE));
		assertEquals(59,calendar.get(Calendar.SECOND));
		assertEquals(0,calendar.get(Calendar.MILLISECOND));
		
		calendar.setTime(Utils.parseISO8601Extended("2010-08-18T11:59:59-05:00"));
		assertEquals(11,calendar.get(Calendar.HOUR_OF_DAY));
		assertEquals(59,calendar.get(Calendar.MINUTE));
		assertEquals(59,calendar.get(Calendar.SECOND));
		assertEquals(0,calendar.get(Calendar.MILLISECOND));
		
		calendar.setTime(Utils.parseISO8601Extended("2010-08-18T11:59:59+05:30"));
		assertEquals(1,calendar.get(Calendar.HOUR_OF_DAY));
		assertEquals(29,calendar.get(Calendar.MINUTE));
		assertEquals(59,calendar.get(Calendar.SECOND));
		assertEquals(0,calendar.get(Calendar.MILLISECOND));
		
		// test hack for space instead of plus for URL mapping
		calendar.setTime(Utils.parseISO8601Extended("2010-08-18T11:59:59 05:30"));
		assertEquals(1,calendar.get(Calendar.HOUR_OF_DAY));
		assertEquals(29,calendar.get(Calendar.MINUTE));
		assertEquals(59,calendar.get(Calendar.SECOND));
		assertEquals(0,calendar.get(Calendar.MILLISECOND));
		
		calendar.setTime(Utils.parseISO8601Extended("2010-08-18T11:59:59"));
		assertEquals(11,calendar.get(Calendar.HOUR_OF_DAY));
		assertEquals(59,calendar.get(Calendar.MINUTE));
		assertEquals(59,calendar.get(Calendar.SECOND));
		assertEquals(0,calendar.get(Calendar.MILLISECOND));
		
		calendar.setTime(Utils.parseISO8601Extended("2010-08-19"));
		assertEquals(0,calendar.get(Calendar.HOUR_OF_DAY));
		assertEquals(0,calendar.get(Calendar.MINUTE));
		assertEquals(0,calendar.get(Calendar.SECOND));
		assertEquals(0,calendar.get(Calendar.MILLISECOND));
	}

}
