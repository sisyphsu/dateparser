package com.github.sisyphsu.dateparser;

import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;

/**
 * @author sulin
 * @since 2019-09-14 16:48:50
 */
public class DateBuilderTest {

    @Test
    public void test() {
        DateBuilder dateBuilder = new DateBuilder();
        dateBuilder.reset();
        OffsetDateTime dt = dateBuilder.toOffsetDateTime();
        assert dt != null;
        System.out.println(dt);
    }

}