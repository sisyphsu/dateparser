package com.github.sisyphsu.dateparser;

import com.github.sisyphsu.retree.ReMatcher;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.*;

/**
 * TODO optimize Exception
 *
 * @author sulin
 * @since 2019-09-12 14:28:50
 */
public final class DateParser {

    private static int[] NSS = {100000000, 10000000, 1000000, 100000, 10000, 1000, 100, 10, 1};

    private final ReMatcher matcher;

    private final DateBuilder dt = new DateBuilder();

    private final List<String> rules = new ArrayList<>();
    private final Set<String> standardRules = new HashSet<>();
    private final Map<String, RuleHandler> customizedRuleMap = new HashMap<>();

    private String input;

    private boolean preferMonthFirst = false;

    public DateParser(String... rules) {
        // predefined standard rules
        this.rules.addAll(Rules.STANDARD_RULES);
        this.standardRules.addAll(Rules.STANDARD_RULES);
        // predefined customized rules
        this.rules.addAll(Rules.CUSTOMIZED_RULES);
        this.customizedRuleMap.putAll(Rules.CUSTOMIZED_RULE_MAP);
        // parameterized rules
        if (rules != null) {
            Collections.addAll(this.rules, rules);
            Collections.addAll(this.standardRules, rules);
        }
        matcher = new ReMatcher(this.rules.toArray(new String[0]));
    }

    /**
     * Parse the specified String into Date
     *
     * @param str The original String like '2019-10-01 00:10:20 +0800'
     * @return The parsed Date
     */
    public Date parseDate(String str) {
        this.dt.reset();
        this.input = str;
        this.parse(buildInput(str));
        return dt.toDate();
    }

    /**
     * Parse the specified String into Calendar
     *
     * @param str The original String like '2019-10-01 00:10:20 +0800'
     * @return The parsed Calendar
     */
    public Calendar parseCalendar(String str) {
        this.dt.reset();
        this.input = str;
        this.parse(buildInput(str));
        return dt.toCalendar();
    }

    /**
     * Parse the specified string into LocalDateTime
     *
     * @param str The original String
     * @return The parsed LocalDateTime
     */
    public LocalDateTime parseDateTime(String str) {
        this.dt.reset();
        this.input = str;
        this.parse(buildInput(str));
        return dt.toLocalDateTime();
    }

    /**
     * Parse the specified string into OffsetDateTime
     *
     * @param str The original String
     * @return The parsed OffsetDateTime
     */
    public OffsetDateTime parseOffsetDateTime(String str) {
        this.dt.reset();
        this.input = str;
        this.parse(buildInput(str));
        return dt.toOffsetDateTime();
    }

    /**
     * Execute datetime's parsing
     */
    private void parse(final CharArray input) {
        matcher.reset(input);
        int offset = 0;
        while (matcher.find(offset)) {
            if (standardRules.contains(matcher.re())) {
                for (int index = 1; index <= matcher.groupCount(); index++) {
                    final String groupName = matcher.groupName(index);
                    final int startOff = matcher.start(index);
                    final int endOff = matcher.end(index);
                    if (groupName == null) {
                        throw error(offset, "Hit invalid standard rule: " + matcher.re());
                    }
                    switch (groupName) {
                        case "week":
                            dt.week = parseWeek(input, startOff);
                            break;
                        case "year":
                            dt.year = parseYear(input, startOff, endOff);
                            break;
                        case "month":
                            dt.month = parseMonth(input, startOff, endOff);
                            if (dt.month <= 0 || dt.month > 12) {
                                throw error(startOff, "Invalid month at " + startOff);
                            }
                            break;
                        case "day":
                            dt.day = parseNum(input, startOff, endOff);
                            if (dt.day <= 0 || dt.day > 31) {
                                throw error(startOff, "Invalid day at " + startOff);
                            }
                            break;
                        case "hour":
                            dt.hour = parseNum(input, startOff, endOff);
                            if (dt.hour < 0 || dt.hour >= 24) {
                                throw error(startOff, "Invalid hour at " + startOff);
                            }
                            break;
                        case "minute":
                            dt.minute = parseNum(input, startOff, endOff);
                            if (dt.minute < 0 || dt.minute >= 60) {
                                throw error(startOff, "Invalid minute at " + startOff);
                            }
                            break;
                        case "second":
                            dt.second = parseNum(input, startOff, endOff);
                            if (dt.second < 0 || dt.second >= 60) {
                                throw error(startOff, "Invalid second at " + startOff);
                            }
                            break;
                        case "ns":
                            dt.ns = parseNano(input, startOff, endOff);
                            break;
                        case "m":
                            if (input.charAt(startOff) == 'p') {
                                dt.pm = true;
                            } else {
                                dt.am = true;
                            }
                            break;
                        case "zero":
                            dt.zoneOffsetSetted = true;
                            dt.zoneOffset = 0;
                            break;
                        case "zoneOffset":
                            dt.zoneOffsetSetted = true;
                            dt.zoneOffset = parseZoneOffset(input, startOff, endOff);
                            if (dt.zoneOffset < -720 || dt.zoneOffset > 720) {
                                throw error(startOff, "Invalid ZoneOffset at " + startOff);
                            }
                            break;
                        case "zoneName":
                            // don't support by now
                            break;
                        case "dayOrMonth":
                            parseDayOrMonth(input, startOff, endOff);
                            break;
                        case "unixsecond":
                            dt.unixsecond = parseNum(input, startOff, startOff + 10);
                            break;
                        case "millisecond":
                            dt.unixsecond = parseNum(input, startOff, startOff + 10);
                            dt.ns = parseNum(input, startOff + 10, endOff) * 1000000;
                            break;
                        case "microsecond":
                            dt.unixsecond = parseNum(input, startOff, startOff + 10);
                            dt.ns = parseNum(input, startOff + 10, endOff) * 1000;
                            break;
                        case "nanosecond":
                            dt.unixsecond = parseNum(input, startOff, startOff + 10);
                            dt.ns = parseNum(input, startOff + 10, endOff);
                            break;
                        default:
                            throw error(offset, "Hit invalid standard rule: " + matcher.re());
                    }
                }
            } else {
                RuleHandler handler = customizedRuleMap.get(matcher.re());
                handler.handle(input, matcher.start(), matcher.end(), dt);
            }
            offset = matcher.end();
        }
        if (offset != input.length()) {
            throw error(offset);
        }
    }

    /**
     * Parse an subsequence which represent dd/mm or mm/dd, it should be more smart for different locales.
     */
    void parseDayOrMonth(CharArray input, int from, int to) {
        char next = input.data[from + 1];
        int a, b;
        if (next < '0' || next > '9') {
            a = parseNum(input, from, from + 1);
            b = parseNum(input, from + 2, to);
        } else {
            a = parseNum(input, from, from + 2);
            b = parseNum(input, from + 3, to);
        }
        if (a > 31 || b > 31 || a == 0 || b == 0 || (a > 12 && b > 12)) {
            throw error(from, "Invalid DayOrMonth at " + from);
        }
        if (b > 12 || preferMonthFirst) {
            dt.month = a;
            dt.day = b;
        } else {
            dt.day = a;
            dt.month = b;
        }
    }

    /**
     * Parse an subsequence which represent year, like '2019', '19' etc
     */
    int parseYear(CharArray input, int from, int to) {
        switch (to - from) {
            case 4:
                return parseNum(input, from, to);
            case 2:
                int num = parseNum(input, from, to);
                return (num > 50 ? 1900 : 2000) + num;
            case 0:
                return 0;
            default:
                throw error(from, "Invalid year at " + from);
        }
    }

    /**
     * Parse an subsequence which represent the offset of timezone, like '+0800', '+08', '+08:00' etc
     */
    int parseZoneOffset(CharArray input, int from, int to) {
        boolean neg = input.data[from] == '-';
        int hour = parseNum(input, from + 1, from + 3);
        int minute = 0;
        switch (to - from) {
            case 5:
                minute = parseNum(input, from + 3, from + 5);
                break;
            case 6:
                minute = parseNum(input, from + 4, from + 6);
        }
        return (hour * 60 + minute) * (neg ? -1 : 1);
    }

    /**
     * Parse an subsequence which suffix second, like '.2000', '.3186369', '.257000000' etc
     * It should be treated as ms/us/ns.
     */
    int parseNano(CharArray input, int from, int to) {
        int len = to - from;
        if (len < 1) {
            return 0;
        }
        int num = parseNum(input, from, to);
        return NSS[len - 1] * num;
    }

    /**
     * Parse an subsequence which represent week, like 'Monday', 'mon' etc
     */
    int parseWeek(CharArray input, int from) {
        switch (input.data[from]) {
            case 'm':
                return 1; // monday
            case 'w':
                return 3; // wednesday
            case 'f':
                return 5; // friday
            case 't':
                switch (input.data[from + 1]) {
                    case 'u':
                        return 2; // tuesday
                    case 'h':
                        return 4; // thursday
                }
            case 's':
                switch (input.data[from + 1]) {
                    case 'a':
                        return 6; // saturday
                    case 'u':
                        return 7; // sunday
                }
        }
        throw error(from, "Invalid week at " + from);
    }

    /**
     * Parse an subsequence which represent month, like '12', 'Feb' etc
     */
    int parseMonth(CharArray input, int from, int to) {
        if (to - from <= 2) {
            return parseNum(input, from, to);
        }
        switch (input.data[from]) {
            case 'a':
                if (input.data[from + 1] == 'p') {
                    return 4; // april
                }
                return 8; // august
            case 'j':
                if (input.data[from + 1] == 'a') {
                    return 1; // january
                }
                if (input.data[from + 2] == 'n') {
                    return 6; // june
                }
                return 7; // july
            case 'f':
                return 2; // february
            case 'm':
                if (input.data[from + 2] == 'r') {
                    return 3; // march
                }
                return 5; // may
            case 's':
                return 9; // september
            case 'o':
                return 10; // october
            case 'n':
                return 11; // november
            case 'd':
                return 12; // december
        }
        throw error(from, "Invalid month at " + from);
    }

    /**
     * Parse an subsequence which represent an number, like '1234'
     */
    int parseNum(CharArray input, int from, int to) {
        int num = 0;
        for (int i = from; i < to; i++) {
            num = num * 10 + (input.data[i] - '0');
        }
        return num;
    }

    CharArray buildInput(String str) {
        char[] chars = str.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            char ch = chars[i];
            if (ch >= 'A' && ch <= 'Z') {
                chars[i] = (char) (ch + 32);
            }
        }
        return new CharArray(chars);
    }

    private DateTimeParseException error(int offset) {
        return error(offset, String.format("Text %s cannot parse at %d", input, offset));
    }

    private DateTimeParseException error(int offset, String msg) {
        return new DateTimeParseException(msg, input, offset);
    }

    static class CharArray implements CharSequence {

        char[] data;

        public CharArray(char[] data) {
            this.data = data;
        }

        @Override
        public int length() {
            return data.length;
        }

        @Override
        public char charAt(int index) {
            return data[index];
        }

        @Override
        public CharSequence subSequence(int start, int end) {
            throw new UnsupportedOperationException();
        }
    }

}
