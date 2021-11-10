package com.github.sisyphsu.dateparser;

import org.junit.jupiter.api.Test;

import java.time.Month;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Date;
import java.util.TimeZone;

/**
 * @author sulin
 * @since 2019-09-15 15:32:10
 */
public class DateParserTest {

    private DateParser parser = DateParser.newBuilder().preferMonthFirst(true).build();

    @Test
    public void test() {
        OffsetDateTime dateTime = parser.parseOffsetDateTime("12 o’clock PM, PDT");
        assert dateTime.getHour() == 12;

        dateTime = parser.parseOffsetDateTime("0:08 PM, CEST");
        assert dateTime.getHour() == 12;
        assert dateTime.getMinute() == 8;

        dateTime = parser.parseOffsetDateTime("2018-09-16T08:00:00+00:00[Europe/London]");
        assert dateTime.getYear() == 2018;
        assert dateTime.getMonth() == Month.SEPTEMBER;
        assert dateTime.getDayOfMonth() == 16;
        assert dateTime.getHour() == 8;
        assert dateTime.getOffset().getTotalSeconds() == 0;

        dateTime = parser.parseOffsetDateTime("Mon Sep 16 2019 10:44:33 GMT+0800 (中国标准时间)");
        assert dateTime.getYear() == 2019;
        assert dateTime.getMonth() == Month.SEPTEMBER;
        assert dateTime.getDayOfMonth() == 16;
    }

    @Test
    public void testCharArray() {
        DateParser.CharArray array = new DateParser.CharArray(new char[0]);
        assert array.length() == 0;
        try {
            array.subSequence(0, 0);
            assert false;
        } catch (Exception e) {
            assert e instanceof UnsupportedOperationException;
        }
    }

    @Test
    public void allTest() {
        assert match("yyyy-MM-dd HH:mm:ss Z", "2009-05-08 17:57:51 +0000", "May 8, 2009 5:57:51 PM");
        assert match("yyyy-MM-dd HH:mm:ss Z", "1970-10-07 00:00:00 +0000", "oct 7, 1970");
        assert match("yyyy-MM-dd HH:mm:ss Z", "1970-10-07 00:00:00 +0000", "oct 7, '70");
        assert match("yyyy-MM-dd HH:mm:ss Z", "1970-10-07 00:00:00 +0000", "oct. 7, 1970");
        assert match("yyyy-MM-dd HH:mm:ss Z", "1970-10-07 00:00:00 +0000", "oct. 7, 70");
        assert match("yyyy-MM-dd HH:mm:ss Z", "2006-01-02 15:04:05 +0000", "Mon Jan  2 15:04:05 2006");
        assert match("yyyy-MM-dd HH:mm:ss Z", "2006-01-02 15:04:05 -0700", "Mon Jan  2 15:04:05 MST 2006");
        assert match("yyyy-MM-dd HH:mm:ss Z", "2006-01-02 15:04:05 -0700", "Mon Jan 02 15:04:05 -0700 2006");
        assert match("yyyy-MM-dd HH:mm:ss Z", "2006-01-02 15:04:05 -0700", "Monday, 02-Jan-06 15:04:05 MST");
        assert match("yyyy-MM-dd HH:mm:ss Z", "2006-01-02 15:04:05 -0700", "Mon, 02 Jan 2006 15:04:05 MST");
        assert match("yyyy-MM-dd HH:mm:ss Z", "2017-07-11 16:28:13 +0200", "Tue, 11 Jul 2017 16:28:13 +0200 (CEST)");
        assert match("yyyy-MM-dd HH:mm:ss Z", "2006-01-02 15:04:05 -0700", "Mon, 02 Jan 2006 15:04:05 -0700");
        assert match("yyyy-MM-dd HH:mm:ss Z", "2018-01-04 17:53:36 +0000", "Thu, 4 Jan 2018 17:53:36 +0000");
        assert match("yyyy-MM-dd HH:mm:ss Z", "2015-08-10 15:44:11 +0100", "Mon Aug 10 15:44:11 UTC+0100 2015");
        assert match("yyyy-MM-dd HH:mm:ss Z", "2018-01-04 17:53:36 +0000", "Thu, 4 Jan 2018 17:53:36 +0000");
        assert match("yyyy-MM-dd HH:mm:ss Z", "2015-08-10 15:44:11 +0100", "Mon Aug 10 15:44:11 UTC+0100 2015");
        assert match("yyyy-MM-dd HH:mm:ss Z", "2015-07-03 18:04:07 +0100", "Fri Jul 03 2015 18:04:07 GMT+0100 (GMT Daylight Time)");
        assert match("yyyy-MM-dd HH:mm:ss Z", "2012-09-17 10:09:00 +0000", "September 17, 2012 10:09am");
        assert match("yyyy-MM-dd HH:mm:ss Z", "2012-09-17 10:09:00 -0800", "September 17, 2012 at 10:09am PST-08");
        assert match("yyyy-MM-dd HH:mm:ss Z", "2012-09-17 10:10:09 +0000", "September 17, 2012, 10:10:09");
        assert match("yyyy-MM-dd HH:mm:ss Z", "1970-10-07 00:00:00 +0000", "October 7, 1970");
        assert match("yyyy-MM-dd HH:mm:ss Z", "1970-10-07 00:00:00 +0000", "October 7th, 1970");
        assert match("yyyy-MM-dd HH:mm:ss Z", "2006-02-12 19:17:00 +0000", "12 Feb 2006, 19:17");
        assert match("yyyy-MM-dd HH:mm:ss Z", "2006-02-12 19:17:00 +0000", "12 Feb 2006 19:17");
        assert match("yyyy-MM-dd HH:mm:ss Z", "1970-10-07 00:00:00 +0000", "7 oct 70");
        assert match("yyyy-MM-dd HH:mm:ss Z", "1970-10-07 00:00:00 +0000", "7 oct 1970");
        assert match("yyyy-MM-dd HH:mm:ss Z", "2013-02-03 00:00:00 +0000", "03 February 2013");
        assert match("yyyy-MM-dd HH:mm:ss Z", "2013-07-01 00:00:00 +0000", "1 July 2013");
        assert match("yyyy-MM-dd HH:mm:ss Z", "2013-02-03 00:00:00 +0000", "2013-Feb-03");

        assert match("yyyy-MM-dd HH:mm:ss Z", "2014-03-31 00:00:00 +0000", "3/31/2014");
        assert match("yyyy-MM-dd HH:mm:ss Z", "2014-03-31 00:00:00 +0000", "03/31/2014");
        assert match("yyyy-MM-dd HH:mm:ss Z", "1971-08-21 00:00:00 +0000", "08/21/71");
        assert match("yyyy-MM-dd HH:mm:ss Z", "1971-08-01 00:00:00 +0000", "8/1/71");
        assert match("yyyy-MM-dd HH:mm:ss Z", "2014-04-08 22:05:00 +0000", "4/8/2014 22:05");
        assert match("yyyy-MM-dd HH:mm:ss Z", "2014-04-08 22:05:00 +0000", "04/08/2014 22:05");
        assert match("yyyy-MM-dd HH:mm:ss Z", "2014-04-08 22:05:00 +0000", "4/8/14 22:05");
        assert match("yyyy-MM-dd HH:mm:ss Z", "2014-04-02 03:00:51 +0000", "04/2/2014 03:00:51");
        assert match("yyyy-MM-dd HH:mm:ss Z", "2014-04-02 03:00:51 +0000", "04/2/2014 03:00:51");
        assert match("yyyy-MM-dd HH:mm:ss Z", "1965-08-08 00:00:00 +0000", "8/8/1965 12:00:00 AM");
        assert match("yyyy-MM-dd HH:mm:ss Z", "1965-08-08 13:00:01 +0000", "8/8/1965 01:00:01 PM");
        assert match("yyyy-MM-dd HH:mm:ss Z", "1965-08-08 13:00:00 +0000", "8/8/1965 01:00 PM");
        assert match("yyyy-MM-dd HH:mm:ss Z", "1965-08-08 13:00:00 +0000", "8/8/1965 1:00 PM");
        assert match("yyyy-MM-dd HH:mm:ss Z", "1965-08-08 00:00:00 +0000", "8/8/1965 12:00 AM");
        assert match("yyyy-MM-dd HH:mm:ss Z", "2014-04-02 03:00:51 +0000", "4/02/2014 03:00:51");
        assert match("yyyy-MM-dd HH:mm:ss Z", "2014-04-02 03:00:51 +0000", "4/02/2014 03:00:51");
        assert match("yyyy-MM-dd HH:mm:ss Z", "2012-03-19 10:11:59 +0000", "03/19/2012 10:11:59");
        assert match("yyyy-MM-dd HH:mm:ss.SSSSSS Z", "2012-03-19 10:11:59.318636 +0000", "03/19/2012 10:11:59.318636");
        assert match("yyyy-MM-dd HH:mm:ss Z", "2014-03-31 00:00:00 +0000", "2014/3/31");
        assert match("yyyy-MM-dd HH:mm:ss Z", "2014-03-31 00:00:00 +0000", "2014/03/31");
        assert match("yyyy-MM-dd HH:mm:ss Z", "2014-04-08 22:05:00 +0000", "2014/4/8 22:05");
        assert match("yyyy-MM-dd HH:mm:ss Z", "2014-04-08 22:05:00 +0000", "2014/04/08 22:05");
        assert match("yyyy-MM-dd HH:mm:ss Z", "2014-04-02 03:00:51 +0000", "2014/04/2 03:00:51");
        assert match("yyyy-MM-dd HH:mm:ss Z", "2014-04-02 03:00:51 +0000", "2014/4/02 03:00:51");
        assert match("yyyy-MM-dd HH:mm:ss Z", "2012-03-19 10:11:59 +0000", "2012/03/19 10:11:59");
        assert match("yyyy-MM-dd HH:mm:ss.SSSSSS Z", "2012-03-19 10:11:59.318636 +0000", "2012/03/19 10:11:59.318636");

        assert match("yyyy-MM-dd HH:mm:ss Z", "2014-04-08 00:00:00 +0000", "2014年04月08日");
        assert match("yyyy-MM-dd HH:mm:ss Z", "2006-01-02 15:04:05 +0000", "2006-01-02T15:04:05+0000");
        assert match("yyyy-MM-dd HH:mm:ss Z", "2009-08-12 22:15:09 -0700", "2009-08-12T22:15:09-07:00");
        assert match("yyyy-MM-dd HH:mm:ss Z", "2009-08-12 22:15:09 +0000", "2009-08-12T22:15:09");
        assert match("yyyy-MM-dd HH:mm:ss Z", "2009-08-12 22:15:09 +0000", "2009-08-12T22:15:09Z");
        assert match("yyyy-MM-dd HH:mm:ss.SSSSSS Z", "2014-04-26 17:24:37.318636 +0000", "2014-04-26 17:24:37.318636");
        assert match("yyyy-MM-dd HH:mm:ss.SSS Z", "2012-08-03 18:31:59.257 +0000", "2012-08-03 18:31:59.257000000");
        assert match("yyyy-MM-dd HH:mm:ss.SSS Z", "2014-04-26 17:24:37.123 +0000", "2014-04-26 17:24:37.123");
        assert match("yyyy-MM-dd HH:mm:ss Z", "2013-04-01 22:43:00 +0000", "2013-04-01 22:43");
        assert match("yyyy-MM-dd HH:mm:ss Z", "2013-04-01 22:43:22 +0000", "2013-04-01 22:43:22");
        assert match("yyyy-MM-dd HH:mm:ss Z", "2014-12-16 06:20:00 +0000", "2014-12-16 06:20:00 UTC");
        assert match("yyyy-MM-dd HH:mm:ss Z", "2014-12-16 06:20:00 +0000", "2014-12-16 06:20:00 GMT");
        assert match("yyyy-MM-dd HH:mm:ss Z", "2014-04-26 17:24:37 +0000", "2014-04-26 05:24:37 PM");
        assert match("yyyy-MM-dd HH:mm:ss Z", "2014-04-26 13:13:43 +0800", "2014-04-26 13:13:43 +0800");
        assert match("yyyy-MM-dd HH:mm:ss Z", "2014-04-26 13:13:43 +0800", "2014-04-26 13:13:43 +0800 +08");
        assert match("yyyy-MM-dd HH:mm:ss Z", "2014-04-26 13:13:44 +0900", "2014-04-26 13:13:44 +09:00");
        assert match("yyyy-MM-dd HH:mm:ss.SSS Z", "2012-08-03 18:31:59.257 +0000", "2012-08-03 18:31:59.257000000 +0000 UTC");
        assert match("yyyy-MM-dd HH:mm:ss.SSSSSSSS Z", "2015-09-30 18:48:56.35272715 +0000", "2015-09-30 18:48:56.35272715 +0000 UTC");
        assert match("yyyy-MM-dd HH:mm:ss Z", "2015-02-18 00:12:00 +0000", "2015-02-18 00:12:00 +0000 GMT");
        assert match("yyyy-MM-dd HH:mm:ss Z", "2015-02-18 00:12:00 +0000", "2015-02-18 00:12:00 +0000 UTC");
        assert match("yyyy-MM-dd HH:mm:ss Z", "2015-02-08 03:02:00 +0300", "2015-02-08 03:02:00 +0300 MSK m=+0.000000001");
        assert match("yyyy-MM-dd HH:mm:ss.SSS Z", "2015-02-08 03:02:00.001 +0300", "2015-02-08 03:02:00.001 +0300 MSK m=+0.000000001");
        assert match("yyyy-MM-dd HH:mm:ss Z", "2017-07-19 03:21:51 +0000", "2017-07-19 03:21:51+00:00");
        assert match("yyyy-MM-dd HH:mm:ss Z", "2014-04-26 00:00:00 +0000", "2014-04-26");
        assert match("yyyy-MM-dd HH:mm:ss Z", "2014-04-01 00:00:00 +0000", "2014-04");
        assert match("yyyy-MM-dd HH:mm:ss Z", "2014-01-01 00:00:00 +0000", "2014");
        assert match("yyyy-MM-dd HH:mm:ss.SSS Z", "2014-05-11 08:20:13.787 +0000", "2014-05-11 08:20:13,787");

        assert match("yyyy-MM-dd HH:mm:ss Z", "2014-03-31 00:00:00 +0000", "3。31.2014");
        assert match("yyyy-MM-dd HH:mm:ss Z", "2014-03-31 00:00:00 +0000", "3.31.2014");
        assert match("yyyy-MM-dd HH:mm:ss Z", "2014-03-31 00:00:00 +0000", "03.31.2014");
        assert match("yyyy-MM-dd HH:mm:ss Z", "1971-08-21 00:00:00 +0000", "08.21.71");
        assert match("yyyy-MM-dd HH:mm:ss Z", "2014-03-01 00:00:00 +0000", "2014.03");
        assert match("yyyy-MM-dd HH:mm:ss Z", "2014-03-30 00:00:00 +0000", "2014.03.30");

        // test ZoneOffset
        assert match("yyyy-MM-dd HH:mm:ss Z", "2014-03-30 00:00:00 -1400", "2014.03.30 00:00:00 -1400");
        assert matchStamp("yyyy-MM-dd HH:mm:ss Z", "2014-03-30 14:00:00 +0000", "2014.03.30 00:00:00 -1400");
        assert match("yyyy-MM-dd HH:mm:ss Z", "2014-03-30 00:00:00 +1300", "2014.03.30 00:00:00 +1300");
        assert matchStamp("yyyy-MM-dd HH:mm:ss Z", "2014-03-30 00:00:00 +0000", "2014.03.30 13:00:00 +1300");

        assert match("yyyy-MM-dd HH:mm:ss Z", "2014-06-01 00:00:00 +0000", "20140601");
        assert match("yyyy-MM-dd HH:mm:ss Z", "2014-07-22 10:52:03 +0000", "20140722105203");

        assert match("yyyy-MM-dd HH:mm:ss Z", "2012-03-19 10:11:59 +0000", "1332151919");
        assert match("yyyy-MM-dd HH:mm:ss.SSS Z", "2013-11-12 00:32:47.189 +0000", "1384216367189");
        assert match("yyyy-MM-dd HH:mm:ss.SSSSSS Z", "2013-11-12 00:32:47.111222 +0000", "1384216367111222");
        assert match("yyyy-MM-dd HH:mm:ss.SSSSSSSSS Z", "2013-11-12 00:32:47.111222333 +0000", "1384216367111222333");

        // non-zero minutes negative time zone test
        assert match("yyyy-MM-dd HH:mm:ss Z", "2014-04-26 13:13:44 -0230", "2014-04-26 13:13:44 -02:30");
        assert match("yyyy-MM-dd HH:mm:ss Z", "2014-04-26 13:13:44 -0930", "2014-04-26 13:13:44 -09:30");
    }

    /**
     * Test case for DateParser.parseDate(String) method.
     * The test ensures that summer and winter date with/without zone information is correctly parsed in any possible
     * time zone.
     */
    @Test
    public void testParseDate() {
        final TimeZone backUpTimeZone = TimeZone.getDefault();
        try {
            // run for all time zones to test expected behavior for any possible usage
            for (String stzone : TimeZone.getAvailableIDs()) {
                TimeZone.setDefault(TimeZone.getTimeZone(stzone));
                assert matchParseDate("yyyy-MM-dd HH:mm:ss Z", "1970-12-07 00:00:00 +0000", "dec 7, 1970", false);
                assert matchParseDate("yyyy-MM-dd HH:mm:ss Z", "1970-07-07 00:00:00 +0000", "jul 7, 1970", false);
                assert matchParseDate("yyyy-MM-dd HH:mm:ss Z", "2006-07-02 15:04:05 -0700", "02 Jul 2006 15:04:05 -0700", true);
                assert matchParseDate("yyyy-MM-dd HH:mm:ss Z", "2015-02-18 00:12:00 +0000", "2015-02-18 00:12:00 +0000 GMT", true);
            }
        }
        finally {
            TimeZone.setDefault(backUpTimeZone);
        }
    }

    private boolean match(String format, String datetime, String freeDatetime) {
        DateTimeFormatter formatter = new DateTimeFormatterBuilder().appendPattern(format).toFormatter();
        OffsetDateTime dt1 = OffsetDateTime.parse(datetime, formatter);
        OffsetDateTime dt2 = parser.parseOffsetDateTime(freeDatetime);

        return dt1.equals(dt2);
    }

    private boolean matchStamp(String format, String datetime, String freeDatetime) {
        DateTimeFormatter formatter = new DateTimeFormatterBuilder().appendPattern(format).toFormatter();
        OffsetDateTime dt1 = OffsetDateTime.parse(datetime, formatter);
        OffsetDateTime dt2 = parser.parseOffsetDateTime(freeDatetime);

        return dt1.toEpochSecond() == dt2.toEpochSecond();
    }

    /**
     * Compare two dates with time zone parsed by java DateTimeFormatter and DateParser.parseDate(..).
     * @param format reference date format
     * @param datetime reference date
     * @param freeDatetime tested date
     * @param hasTimezone true when <code>freeDatetime</code> contains a time zone indication
     * @return true if match, false otherwise
     */
    private boolean matchParseDate(String format, String datetime, String freeDatetime, boolean hasTimezone) {
        DateTimeFormatter formatter = new DateTimeFormatterBuilder().appendPattern(format).toFormatter();
        OffsetDateTime dt1 = OffsetDateTime.parse(datetime, formatter);
        Date d1 = Date.from(dt1.toInstant());
        Date d2 = parser.parseDate(freeDatetime);

        if (hasTimezone) {
            return d1.equals(d2);
        }
        else {
            // remove local time zone offset, to compare result as epoch time (UTC)
            long offset = TimeZone.getDefault().getOffset(d2.getTime());
            Date d3 = new Date(d2.getTime() + offset);
            return d1.equals(d3);
        }
    }
}
