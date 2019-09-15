package com.github.sisyphsu.dateparser;

import org.junit.jupiter.api.Test;

import java.time.format.DateTimeParseException;
import java.util.Calendar;

/**
 * @author sulin
 * @since 2019-09-15 15:19:30
 */
public class DateParserBuilderTest {

    @Test
    public void build() {
        DateParser parser = DateParser.newBuilder()
                .preferMonthFirst(true)
                .addRule("【(?<year>\\d{4})】")
                .addRule("【(?<year>\\d{4})】") // not work because of repeat
                .addRule("民国(\\d{3})年", (input, matcher, dt) -> {
                    // should be replaced
                })
                .addRule("民国(\\d{3})年", (input, matcher, dt) -> {
                    int offset = matcher.start(1);
                    int i0 = input.charAt(offset) - '0';
                    int i1 = input.charAt(offset + 1) - '0';
                    int i2 = input.charAt(offset + 2) - '0';
                    dt.setYear(i0 * 100 + i1 * 10 + i2 + 1911);
                })
                .build();

        Calendar calendar = parser.parseCalendar("【1991】");
        assert calendar.get(Calendar.YEAR) == 1991;

        calendar = parser.parseCalendar("民国101年");
        assert calendar.get(Calendar.YEAR) == 2012;
    }

    @Test
    public void testInvalidRule() {
        DateParser parser = DateParser.newBuilder().addRule("(?<invalid>\\d{3})").build();
        try {
            parser.parseDate("123");
            assert false;
        } catch (Exception e) {
            assert e instanceof DateTimeParseException;
        }
        parser = DateParser.newBuilder().addRule("(\\d{3})").build();
        try {
            parser.parseDate("123");
            assert false;
        } catch (Exception e) {
            assert e instanceof DateTimeParseException;
        }
        try {
            DateParserUtils.parseDate("2019-01-10 0000000000");
            assert false;
        } catch (Exception e) {
            assert e instanceof DateTimeParseException;
        }
    }

}