package com.github.sisyphsu.dateparser;

import org.junit.jupiter.api.Test;

import java.time.*;
import java.util.Date;
import java.util.Random;
import java.util.TimeZone;

import static org.junit.jupiter.api.Assertions.assertEquals;

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

    @Test
    public void testDemo() {
        final DateParser dp = DateParser.newBuilder().build();
        final String date = "2020-06-08T13:45:05-00:00";
        System.out.println(dp.parseDate(date).toString());
        System.out.println(dp.parseDateTime(date).toString());
        System.out.println(dp.parseOffsetDateTime(date).toString());

        System.out.println(OffsetDateTime.now());
    }

    @Test
    public void testTimestamp() {
        String timestamp = "946656000000";
        Date date = DateParserUtils.parseDate(timestamp);
        System.out.println(date.getTime());
        assert date.getTime() == Long.valueOf(timestamp);
    }

    @Test
    public void testOptimizeForReuseSimilarFormatted(){
        Random random = new Random(123456789l);
        String[] inputs = new String[500000];
        for (int i = 0; i < inputs.length; i++) {
            inputs[i] = String.format("2020-0%d-1%d 00:%d%d:00 UTC",
              random.nextInt(8) + 1,
              random.nextInt(8) + 1,
              random.nextInt(5),
              random.nextInt(9));
        }
        DateParser regular = DateParser.newBuilder().build();
        DateParser optimized = DateParser.newBuilder().optimizeForReuseSimilarFormatted(true).build();

        for (int i = 0; i < inputs.length; i++) {
            String input = inputs[i];
            assertEquals(regular.parseDate(input), optimized.parseDate(input));
        }
    }
}