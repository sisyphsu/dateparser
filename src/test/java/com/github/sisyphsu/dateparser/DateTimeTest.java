package com.github.sisyphsu.dateparser;

import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;

/**
 * @author sulin
 * @since 2019-09-14 16:48:50
 */
public class DateTimeTest {

    @Test
    public void test() {
        DateTime dateTime = new DateTime();
        dateTime.reset();
        OffsetDateTime dt = dateTime.toOffsetDateTime();
        assert dt != null;
        System.out.println(dt);
    }

}