package com.github.sisyphsu.dateparser;

import lombok.Data;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.TimeZone;

/**
 * This DateTime used for caching the properties of parser.
 *
 * @author sulin
 * @since 2019-09-12 14:58:15
 */
@Data
public final class DateTime {

    int week;
    int year;
    int month;
    int day;
    int hour;
    int minute;
    int second;
    int ns;

    boolean pm;

    boolean zoneOffsetSetted;
    int zoneOffset;
    TimeZone zone;

    /**
     * Reset this instance, clear all fields to be default value.
     */
    void reset() {
        this.week = 0;
        this.year = 0;
        this.month = 0;
        this.day = 0;
        this.hour = 0;
        this.minute = 0;
        this.second = 0;
        this.ns = 0;
        this.pm = false;
        this.zoneOffsetSetted = false;
        this.zoneOffset = 0;
        this.zone = null;
    }

    /**
     * Convert this instance into DateTime
     *
     * @return DateTime with TimeZoneOffset
     */
    OffsetDateTime toOffsetDateTime() {
        if (pm) {
            this.hour += 12;
        }
        LocalDateTime dateTime = LocalDateTime.of(year, month, day, hour, minute, second, ns);
        // with ZoneOffset
        if (zoneOffsetSetted) {
            ZoneOffset offset = ZoneOffset.ofHoursMinutes(zoneOffset / 60, zoneOffset % 60);
            return dateTime.atOffset(offset);
        }
        // with TimeZone
        if (zone != null) {
            return dateTime.atZone(zone.toZoneId()).toOffsetDateTime();
        }
        // with default
        return dateTime.atZone(ZoneId.systemDefault()).toOffsetDateTime();
    }

}
