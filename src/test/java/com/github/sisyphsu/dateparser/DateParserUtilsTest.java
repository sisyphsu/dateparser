package com.github.sisyphsu.dateparser;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.Calendar;
import java.util.Date;

/**
 * Test DateParser's normal function, lots of examples are copied from https://github.com/araddon/dateparse.
 *
 * @author sulin
 * @since 2019-09-14 17:06:19
 */
@SuppressWarnings("ALL")
public class DateParserUtilsTest {

    @Test
    public void testDate() {
        Date date = DateParserUtils.parseDate("Mon Jan 02 15:04:05 -0700 2006");
        assert date.getYear() == 2006 - 1900;
        assert date.getMonth() == Calendar.JANUARY;
        assert date.getDate() == 3;
        assert date.getHours() == 6;
        assert date.getMinutes() == 4;
        assert date.getSeconds() == 5;

        date = DateParserUtils.parseDate("Mon Jan 02 15:04:05 +0700 2006");
        assert date.getYear() == 2006 - 1900;
        assert date.getMonth() == Calendar.JANUARY;
        assert date.getDate() == 2;
        assert date.getHours() == 16;
        assert date.getMinutes() == 4;
        assert date.getSeconds() == 5;

        DateParserUtils.parseOffsetDateTime("2019-01-01 10:20:30.1234[]");
    }

    @Test
    public void testCalendar() {
        Calendar calendar = DateParserUtils.parseCalendar("Fri Jul 03 2015 18:04:07 GMT+0100 (GMT Daylight Time)");
        assert calendar.get(Calendar.YEAR) == 2015;
        assert calendar.get(Calendar.MONTH) == 7;
        assert calendar.get(Calendar.DAY_OF_MONTH) == 3;
        assert calendar.get(Calendar.HOUR_OF_DAY) == 18;
        assert calendar.get(Calendar.MINUTE) == 4;
        assert calendar.get(Calendar.SECOND) == 7;
        assert calendar.getTimeZone().getRawOffset() == 3600000; // GMT+0100
    }

    @Test
    public void testPreferMonthFirst() {
        Calendar calendar;
        DateParserUtils.preferMonthFirst(true);
        calendar = DateParserUtils.parseCalendar("08.03.71");
        assert calendar.get(Calendar.MONTH) == 8;
        assert calendar.get(Calendar.DAY_OF_MONTH) == 3;

        DateParserUtils.preferMonthFirst(false);
        calendar = DateParserUtils.parseCalendar("08.03.71");
        assert calendar.get(Calendar.MONTH) == 3;
        assert calendar.get(Calendar.DAY_OF_MONTH) == 8;
    }

    @Test
    public void testDateTime() {
        LocalDateTime dateTime = DateParserUtils.parseDateTime("Mon Jan 02 15:04:05 -0700 2006");
        assert dateTime.getYear() == 2006;
        assert dateTime.getMonth() == Month.JANUARY;
        assert dateTime.getDayOfMonth() == 3;
        assert dateTime.getHour() == 6;
        assert dateTime.getMinute() == 4;
        assert dateTime.getSecond() == 5;
    }

    @Test
    public void testRegister() {
        DateParserUtils.registerStandardRule("【(?<year>\\d{4})】");
        Calendar calendar = DateParserUtils.parseCalendar("【1991】");
        assert calendar.get(Calendar.YEAR) == 1991;

        DateParserUtils.registerCustomizedRule("民国(\\d{3})年", (input, matcher, dt) -> {
            int offset = matcher.start(1);
            int i0 = input.charAt(offset) - '0';
            int i1 = input.charAt(offset + 1) - '0';
            int i2 = input.charAt(offset + 2) - '0';
            dt.setYear(i0 * 100 + i1 * 10 + i2 + 1911);
        });

        calendar = DateParserUtils.parseCalendar("民国101年");
        assert calendar.get(Calendar.YEAR) == 2012;
    }

}