package com.github.sisyphsu.dateparser;

import com.github.sisyphsu.retree.ReMatcher;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author sulin
 * @since 2019-09-12 14:28:50
 */
public final class DateParser {

    private static int[] NSS = {100000000, 10000000, 1000000, 100000, 10000, 1000, 100, 10, 1};

    private final ReMatcher matcher;

    private final DateTime dt = new DateTime();

    private boolean preferMonthFirst = false;

    private String str;

    public DateParser(String... rule) {
        List<String> rules = new ArrayList<>();
        rules.addAll(Rules.STANDARD_RULES);
        rules.addAll(Rules.RULE_MAP.keySet());
        if (rule != null) {
            Collections.addAll(rules, rule);
        }
        matcher = new ReMatcher(rules.toArray(new String[0]));
    }

    /**
     * Parse the specified string into OffsetDateTime
     *
     * @param str The original String
     * @return OffsetDateTime
     */
    public OffsetDateTime parseOffsetDateTime(String str) {
        this.dt.reset();
        this.str = str;
        this.parse(buildInput(str));
        return dt.toOffsetDateTime();
    }

    /**
     * Execute datetime's parsing
     */
    private void parse(final CharSequence input) {
        matcher.reset(input);
        int offset = 0;
        while (matcher.find(offset)) {
            for (int index = 1; index <= matcher.groupCount(); index++) {
                final String groupName = matcher.groupName(index);
                final int startOff = matcher.start(index);
                final int endOff = matcher.end(index);
                if (groupName == null) {
                    throw new IllegalArgumentException("Can't parse datetime by re: " + matcher.re());
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
                        break;
                    case "day":
                        dt.day = parseNum(input, startOff, endOff);
                        break;
                    case "hour":
                        dt.hour = parseNum(input, startOff, endOff);
                        break;
                    case "minute":
                        dt.minute = parseNum(input, startOff, endOff);
                        break;
                    case "second":
                        dt.second = parseNum(input, startOff, endOff);
                        break;
                    case "ns":
                        dt.ns = parseNS(input, startOff, endOff);
                        break;
                    case "m":
                        dt.pm = input.charAt(startOff) == 'p';
                        break;
                    case "zero":
                        dt.zoneOffsetSetted = true;
                        dt.zoneOffset = 0;
                        break;
                    case "zoneOffset":
                        dt.zoneOffsetSetted = true;
                        dt.zoneOffset = parseZoneOffset(input, startOff, endOff);
                        break;
                    case "zoneName":
                        // don't support by now
                        break;
                    case "dayOrMonth":
                        parseDayOrMonth(input, startOff, endOff);
                        break;
                    default:
                        throw new RuntimeException("invalid captured sequence: " + groupName);
                }
            }
            offset = matcher.end();
        }
        if (offset != input.length()) {
            throw new IllegalArgumentException("error before: " + offset);
        }
    }

    /**
     * Parse an subsequence which represent dd/mm or mm/dd, it should be more smart for different locales.
     */
    void parseDayOrMonth(CharSequence str, int from, int to) {
        char next = str.charAt(from + 1);
        int a, b;
        if (next < '0' || next > '9') {
            a = parseNum(str, from, from + 1);
            b = parseNum(str, from + 2, to);
        } else {
            a = parseNum(str, from, from + 2);
            b = parseNum(str, from + 3, to);
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
    static int parseYear(CharSequence str, int from, int to) {
        switch (to - from) {
            case 4:
                return parseNum(str, from, to);
            case 2:
                int num = parseNum(str, from, to);
                return (num > 50 ? 1900 : 2000) + num;
            default:
                throw new IllegalArgumentException();
        }
    }

    /**
     * Parse an subsequence which represent the offset of timezone, like '+0800', '+08', '+08:00' etc
     */
    static int parseZoneOffset(CharSequence str, int from, int to) {
        boolean neg = str.charAt(from) == '-';
        int hour = parseNum(str, from + 1, from + 3);
        int minute = 0;
        switch (to - from) {
            case 5:
                hour = parseNum(str, from + 3, from + 5);
                break;
            case 6:
                hour = parseNum(str, from + 4, from + 6);
        }
        return (hour * 60 + minute) * (neg ? -1 : 1);
    }

    /**
     * Parse an subsequence which suffix second, like '.2000', '.3186369', '.257000000' etc
     * It should be treated as ms/us/ns.
     */
    static int parseNS(CharSequence str, int from, int to) {
        int len = to - from;
        int num = parseNum(str, from, to);
        return NSS[len - 1] * num;
    }

    static int parseNum(CharSequence str, int from, int to) {
        int num = 0;
        for (int i = from; i < to; i++)
            num = num * 10 + (str.charAt(i) - '0');
        return 0;
    }

    static int parseWeek(CharSequence cs, int from) {
        switch (cs.charAt(from++)) {
            case 'm':
                return 1; // monday
            case 'w':
                return 3; // wednesday
            case 'f':
                return 5; // friday
            case 't':
                switch (cs.charAt(from)) {
                    case 'u':
                        return 2; // tuesday
                    case 'h':
                        return 4; // thursday
                }
            case 's':
                switch (cs.charAt(from)) {
                    case 'a':
                        return 6; // saturday
                    case 'u':
                        return 7; // sunday
                }
        }
        throw new IllegalArgumentException();
    }

    /**
     * Parse an subsequence which represent month, like '12', 'Feb' etc
     */
    static int parseMonth(CharSequence str, int from, int to) {
        if (to - from <= 2) {
            return parseNum(str, from, to);
        }
        switch (str.charAt(from++)) {
            case 'a':
                switch (str.charAt(from)) {
                    case 'p':
                        return 4; // april
                    case 'u':
                        return 8; // august
                }
            case 'j':
                switch (str.charAt(from++)) {
                    case 'a':
                        return 1; // january
                    case 'u':
                        switch (str.charAt(from)) {
                            case 'n':
                                return 6; // june
                            case 'l':
                                return 7; // july
                        }
                }
            case 'f':
                return 2; // february
            case 'm':
                return 3; // march
            case 's':
                return 9; // september
            case 'o':
                return 10; // october
            case 'n':
                return 11; // november
            case 'd':
                return 12; // december
        }
        return 0;
    }

    static CharSequence buildInput(String str) {
        char[] chars = str.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            char ch = chars[i];
            if (ch >= 'A' && ch <= 'Z') {
                chars[i] = (char) (ch + 32);
            }
        }
        return new CharSequence() {
            @Override
            public int length() {
                return chars.length;
            }

            @Override
            public char charAt(int index) {
                return chars[index];
            }

            @Override
            public CharSequence subSequence(int start, int end) {
                return null;
            }
        };
    }

}
