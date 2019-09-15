package com.github.sisyphsu.dateparser;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
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

        OffsetDateTime dateTime = DateParserUtils.parseOffsetDateTime("Mon Jan 02 15:04:05 +0700 2006");
        assert dateTime.getYear() == 2006;
        assert dateTime.getMonth() == Month.JANUARY;
        assert dateTime.getDayOfMonth() == 2;
        assert dateTime.getHour() == 15;
        assert dateTime.getMinute() == 4;
        assert dateTime.getSecond() == 5;
        assert dateTime.getOffset().getTotalSeconds() == 25200;
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
    public void testWeek() {
        DateParserUtils.parseDate("monday");
        DateParserUtils.parseDate("mon");
        DateParserUtils.parseDate("tuesday");
        DateParserUtils.parseDate("tue");
        DateParserUtils.parseDate("wednesday");
        DateParserUtils.parseDate("wed");
        DateParserUtils.parseDate("thursday");
        DateParserUtils.parseDate("thu");
        DateParserUtils.parseDate("friday");
        DateParserUtils.parseDate("fri");
        DateParserUtils.parseDate("saturday");
        DateParserUtils.parseDate("sat");
        DateParserUtils.parseDate("sunday");
        DateParserUtils.parseDate("sun");
    }

    @Test
    public void testMonth() {
        DateParserUtils.parseDate("january, 1");
        DateParserUtils.parseDate("february, 1");
        DateParserUtils.parseDate("march, 1");
        DateParserUtils.parseDate("april, 1");
        DateParserUtils.parseDate("may, 1");
        DateParserUtils.parseDate("june, 1");
        DateParserUtils.parseDate("july, 1");
        DateParserUtils.parseDate("august, 1");
        DateParserUtils.parseDate("september, 1");
        DateParserUtils.parseDate("october, 1");
        DateParserUtils.parseDate("november, 1");
        DateParserUtils.parseDate("december, 1");

        DateParser parser = DateParser.newBuilder().addRule("(?<week>\\w+)").build();
        try {
            parser.parseDate("1");
            assert false;
        } catch (Exception e) {
            assert e instanceof DateTimeParseException;
        }
        try {
            parser.parseDate("ta");
            assert false;
        } catch (Exception e) {
            assert e instanceof DateTimeParseException;
        }
        try {
            parser.parseDate("sz");
            assert false;
        } catch (Exception e) {
            assert e instanceof DateTimeParseException;
        }

        try {
            parser.parseDate("sz");
            assert false;
        } catch (Exception e) {
            assert e instanceof DateTimeParseException;
        }

        parser = DateParser.newBuilder().addRule("(?<month>\\w+)").build();
        try {
            parser.parseDate("axx");
            assert false;
        } catch (Exception e) {
            assert e instanceof DateTimeParseException;
        }
        try {
            parser.parseDate("jzz");
            assert false;
        } catch (Exception e) {
            assert e instanceof DateTimeParseException;
        }

        try {
            parser.parseDate("mzz");
            assert false;
        } catch (Exception e) {
            assert e instanceof DateTimeParseException;
        }

        try {
            parser.parseDate("zzzz");
            assert false;
        } catch (Exception e) {
            assert e instanceof DateTimeParseException;
        }
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

    @Test
    public void testFail() {
        assertFail("2019-13-10");
        assertFail("2019-0-10");
        assertFail("2019-12-40");
        assertFail("2019-12-0");

        assertFail("2019-12-12 61:00:00");
        assertFail("2019-12-12 00:88:00");
        assertFail("2019-12-12 00:00:77");

        assertFail("2019-12-12 00:00:00 -30:00");
        assertFail("2019-12-12 00:00:00 +30:00");

        assertFail("2019-12-12 00:00:00 +3xxxxxxxx");

        assertFail("35.5.2000");
        assertFail("5.35.2000");
        assertFail("0.1.2000");
        assertFail("1.0.2000");
        assertFail("13.13.2000");
        assertFail("0.0.2000");

        DateParserUtils.registerStandardRule("^(?<year>\\d{5})$");
        assertFail("20000");

        DateParserUtils.parseDate("12.13.2000");
        DateParserUtils.parseDate("13.12.2000");

        try {
            DateParser.newBuilder().addRule("(?<year>\\d{0})").build().parseDate("");
            assert false;
        } catch (Exception e) {
            assert e instanceof DateTimeParseException;
        }

    }

    private void assertFail(String str) {
        try {
            DateParserUtils.parseDate(str);
            assert false;
        } catch (Exception e) {
            assert e instanceof DateTimeParseException;
        }
    }

}