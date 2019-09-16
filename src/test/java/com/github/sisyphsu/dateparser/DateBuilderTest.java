package com.github.sisyphsu.dateparser;

import org.junit.jupiter.api.Test;

import java.time.*;
import java.util.Date;
import java.util.TimeZone;

/**
 * @author sulin
 * @since 2019-09-14 16:48:50
 */
@SuppressWarnings("ALL")
public class DateBuilderTest {

    @Test
    public void test() {
        DateBuilder dateBuilder = new DateBuilder();
        dateBuilder.reset();
        OffsetDateTime dt = dateBuilder.toOffsetDateTime();
        assert dt != null;
        System.out.println(dt);
    }

    @Test
    public void testTime() {
        long second = System.currentTimeMillis() / 1000;
        DateBuilder builder = new DateBuilder();
        builder.setUnixsecond(second);
        Date date = builder.toDate();

        assert date.equals(new Date(builder.getUnixsecond() * 1000));

        LocalDateTime dateTime = builder.toLocalDateTime();
        assert dateTime.toEpochSecond(ZoneOffset.UTC) == second;
    }

    @Test
    public void testNormal() {
        DateBuilder builder = new DateBuilder();
        builder.reset();
        builder.setYear(2019);
        builder.setMonth(10);
        builder.setDay(1);
        Date date = builder.toDate();
        assert date.getYear() == builder.getYear() - 1900;
        assert date.getMonth() == builder.getMonth() - 1;
        assert date.getDate() == builder.getDay();
    }

    @Test
    public void testLocalDateTime() {
        DateBuilder builder = new DateBuilder();
        builder.reset();
        builder.setYear(2019);
        builder.setMonth(10);
        builder.setDay(1);
        builder.setHour(10);
        builder.setMinute(20);
        builder.setSecond(30);
        builder.setNs(900000000);
        builder.setZone(TimeZone.getDefault());

        LocalDateTime dateTime = builder.toLocalDateTime();
        assert dateTime.getYear() == builder.getYear();
        assert dateTime.getMonth() == Month.of(builder.getMonth());
        assert dateTime.getDayOfMonth() == builder.getDay();
        assert dateTime.getHour() == builder.getHour();
        assert dateTime.getMinute() == builder.getMinute();
        assert dateTime.getSecond() == builder.getSecond();
        assert dateTime.getNano() == builder.getNs();
        assert builder.getZone().equals(TimeZone.getDefault());
    }

    @Test
    public void testInvalidCalendar() {
        DateBuilder builder = new DateBuilder();
        builder.reset();
        builder.setWeek(1);
        builder.setZoneOffsetSetted(true);
        builder.setZoneOffset(111);
        try {
            builder.toCalendar();
            assert false;
        } catch (Exception e) {
            assert e instanceof DateTimeException;
        }
    }

    @Test
    public void testCoverage() {
        DateBuilder builder = new DateBuilder();
        builder.setWeek(1);
        builder.setZoneOffsetSetted(true);
        builder.setZoneOffset(123);
        builder.setPm(true);
        builder.setAm(false);

        assert builder.getWeek() == 1;
        assert builder.getZoneOffset() == 123;
        assert builder.isPm();
        assert !builder.isAm();
        assert builder.isZoneOffsetSetted();
    }

}