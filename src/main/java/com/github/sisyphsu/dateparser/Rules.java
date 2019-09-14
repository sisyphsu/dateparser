package com.github.sisyphsu.dateparser;

import java.util.*;

/**
 * @author sulin
 * @since 2019-09-12 14:34:29
 */
public final class Rules {

    static final String[] months = {
            "jan", "feb", "mar", "apr", "may", "jun", "jul", "aug", "sep", "oct", "nov", "dec",
            "january",
            "february",
            "march",
            "april",
            "may",
            "june",
            "july",
            "august",
            "september",
            "october",
            "november",
            "december",
    };
    static final String[] weeks = {
            "mon", "tue", "wed", "thu", "fri", "sat", "sun",
            "monday",
            "tuesday",
            "wednesday",
            "thursday",
            "friday",
            "saturday",
            "sunday",
    };

    static final List<String> STANDARD_RULES = new ArrayList<>();
    static final Map<String, RuleHandler> RULE_MAP = new HashMap<>();

    static {
        // support day of week, like 'Mon' or 'Monday,'
        for (String week : weeks) {
            register(String.format("(?<week>%s)\\W+", week));
        }

        for (String month : months) {
            // month-word at first, like 'may. 8th, 2009,' or 'may. 8th, 09'
            register(String.format("(?<month>%s)\\W+(?<day>\\d{1,2})(?>th)?\\W+(?<year>\\d{2,4})\\b", month));

            // month-word at middle, like '8th, may, 2009,' or '8th-may-09'
            register(String.format("(?<day>\\d{1,2})(?>th)?\\W+(?<month>%s)\\W+(?<year>\\d{2,4})\\b", month));

            // month-word at middle, like '2009-may-8th'
            register(String.format("(?<year>\\d{4})\\W+(?<month>%s)\\W+(?<day>\\d{1,2})(?>th)?\\W*", month));
        }

        // yyyy-MM-dd, yyyy/MM/dd...
        register("(?<year>\\d{4})\\W{1}(?<month>\\d{1,2})\\W{1}(?<day>\\d{1,2})[^\\d]?");

        // yyyy-MM, yyyy/MM...
        register("^(?<year>\\d{4})\\W{1}(?<month>\\d{1,2})$");

        // MM/dd/yyyy, dd/MM/yyyy
        register("(?<dayOrMonth>\\d{1,2}\\W{1}\\d{1,2})\\W{1}(?<year>\\d{4})[^\\d]?");

        // dd/MM/yy, MM/dd/yy
        register("(?<dayOrMonth>\\d{1,2}\\W{1}\\d{1,2})\\W{1}(?<year>\\d{2})[^\\d]?");

        // yyyy
        register("^(?<year>\\d{4})$");
        // yyyyMM
        register("^(?<year>\\d{4})(?<month>\\d{2})$");
        // yyyyMMdd
        register("^(?<year>\\d{4})(?<month>\\d{2})(?<day>\\d{2})$");
        // yyyyMMddhhmmss
        register("^(?<year>\\d{4})(?<month>\\d{2})(?<day>\\d{2})(?<hour>\\d{2})(?<minute>\\d{2})(?<second>\\d{2})$");

        // at hh:mm:ss.SSSSZ
        register("(?>at )?(?<hour>\\d{1,2}):(?<minute>\\d{1,2})(?>:(?<second>\\d{1,2}))?(?>[.,](?<ns>\\d+))?(?<zero>Z)?");

        // am, pm
        register(" ?(?<m>am|pm)");

        // +08:00
        register(" ?(?<zoneOffset>[-+]\\d{2}(?>:?\\d{2})?)");

        // (CEST) (GMT Daylight Time)
        register(" \\((?<zoneName>\\w+(?> \\w+))\\)");

        // support all languages' default TimeZone
        for (String zoneId : TimeZone.getAvailableIDs()) {
            final TimeZone zone = TimeZone.getTimeZone(zoneId);
            final RuleHandler handler = (cs, from, to, dt) -> dt.zone = zone;

            register(String.format(" \\Q%s\\E", zone.getID().toLowerCase()), handler);
        }

        // support others no-standard 'timezone'
        register(" pdt", (cs, from, to, dt) -> dt.zone = TimeZone.getTimeZone("America/Los_Angeles"));
        register(" cest", (cs, from, to, dt) -> dt.zone = TimeZone.getTimeZone("CET"));

        // MSK m=+0.000000001
        register(" msk m=[+-]\\d\\.\\d+");
    }

    public static synchronized void register(String re) {
        STANDARD_RULES.add(re);
    }

    public static synchronized void register(String re, RuleHandler handler) {
        RULE_MAP.put(re, handler);
    }

}
