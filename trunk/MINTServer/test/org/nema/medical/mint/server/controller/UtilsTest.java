package org.nema.medical.mint.server.controller;

import static org.junit.Assert.assertEquals;

import java.text.ParseException;
import java.util.Calendar;

import org.junit.Test;

public class UtilsTest {
	
	@Test
	public void parseDateTest() throws ParseException {
		Calendar calendar = Calendar.getInstance();
		
		calendar.setTime(Utils.parseDate("2010-08-18T11:59:59.999"));
		assertEquals(2010,calendar.get(Calendar.YEAR));
		assertEquals(7,calendar.get(Calendar.MONTH));
		assertEquals(18,calendar.get(Calendar.DAY_OF_MONTH));
		assertEquals(11,calendar.get(Calendar.HOUR_OF_DAY));
		assertEquals(59,calendar.get(Calendar.MINUTE));
		assertEquals(59,calendar.get(Calendar.SECOND));
		assertEquals(999,calendar.get(Calendar.MILLISECOND));
		
		calendar.setTime(Utils.parseDate("2010-08-18T11:59:59.999Z"));
		assertEquals(6,calendar.get(Calendar.HOUR_OF_DAY));
		assertEquals(999,calendar.get(Calendar.MILLISECOND));
		
		calendar.setTime(Utils.parseDate("2010-08-18T11:59:59.999-05:00"));
		assertEquals(11,calendar.get(Calendar.HOUR_OF_DAY));
		assertEquals(999,calendar.get(Calendar.MILLISECOND));
		
		calendar.setTime(Utils.parseDate("2010-08-18T11:59:59.999 05:30"));
		assertEquals(1,calendar.get(Calendar.HOUR_OF_DAY));
		assertEquals(29,calendar.get(Calendar.MINUTE));
		assertEquals(999,calendar.get(Calendar.MILLISECOND));
		
		calendar.setTime(Utils.parseDate("2010-08-18T11:59:59.999+05:30"));
		assertEquals(1,calendar.get(Calendar.HOUR_OF_DAY));
		assertEquals(29,calendar.get(Calendar.MINUTE));
		assertEquals(999,calendar.get(Calendar.MILLISECOND));
		
		calendar.setTime(Utils.parseDate("2010-08-18T11:59:59Z"));
		assertEquals(6,calendar.get(Calendar.HOUR_OF_DAY));
		assertEquals(59,calendar.get(Calendar.MINUTE));
		assertEquals(59,calendar.get(Calendar.SECOND));
		assertEquals(0,calendar.get(Calendar.MILLISECOND));
		
		calendar.setTime(Utils.parseDate("2010-08-18T11:59:59-05:00"));
		assertEquals(11,calendar.get(Calendar.HOUR_OF_DAY));
		assertEquals(59,calendar.get(Calendar.MINUTE));
		assertEquals(59,calendar.get(Calendar.SECOND));
		assertEquals(0,calendar.get(Calendar.MILLISECOND));
		
		calendar.setTime(Utils.parseDate("2010-08-18T11:59:59+05:30"));
		assertEquals(1,calendar.get(Calendar.HOUR_OF_DAY));
		assertEquals(29,calendar.get(Calendar.MINUTE));
		assertEquals(59,calendar.get(Calendar.SECOND));
		assertEquals(0,calendar.get(Calendar.MILLISECOND));
		
		calendar.setTime(Utils.parseDate("2010-08-18T11:59:59"));
		assertEquals(11,calendar.get(Calendar.HOUR_OF_DAY));
		assertEquals(59,calendar.get(Calendar.MINUTE));
		assertEquals(59,calendar.get(Calendar.SECOND));
		assertEquals(0,calendar.get(Calendar.MILLISECOND));
		
		calendar.setTime(Utils.parseDate("2010-08-19"));
		assertEquals(0,calendar.get(Calendar.HOUR_OF_DAY));
		assertEquals(0,calendar.get(Calendar.MINUTE));
		assertEquals(0,calendar.get(Calendar.SECOND));
		assertEquals(0,calendar.get(Calendar.MILLISECOND));
	}

}
