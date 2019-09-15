package com.github.sisyphsu.dateparser;

import lombok.Data;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
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
    long unixsecond;

    boolean am;
    boolean pm;

    boolean zoneOffsetSetted;
    int zoneOffset;
    TimeZone zone;

    /**
     * Reset this instance, clear all fields to be default value.
     */
    void reset() {
        this.week = 1;
        this.year = 0;
        this.month = 1;
        this.day = 1;
        this.hour = 0;
        this.minute = 0;
        this.second = 0;
        this.ns = 0;
        this.unixsecond = 0;
        this.am = false;
        this.pm = false;
        this.zoneOffsetSetted = false;
        this.zoneOffset = 0;
        this.zone = null;
    }

    /**
     * Convert this DateTime into LocalDateTime
     *
     * @return LocalDateTime
     */
    public LocalDateTime toLocalDateTime() {
        LocalDateTime dateTime = this.buildDateTime();
        // with ZoneOffset
        if (zoneOffsetSetted) {
            ZoneOffset offset = ZoneOffset.ofHoursMinutes(zoneOffset / 60, zoneOffset % 60);
            return dateTime.atOffset(offset).toLocalDateTime();
        }
        // with TimeZone
        if (zone != null) {
            return dateTime.atZone(zone.toZoneId()).toLocalDateTime();
        }
        return dateTime;
    }

    /**
     * Convert this instance into OffsetDateTime
     *
     * @return DateTime with TimeZoneOffset
     */
    public OffsetDateTime toOffsetDateTime() {
        LocalDateTime dateTime = this.buildDateTime();
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
        return dateTime.atZone(ZoneOffset.ofHoursMinutes(0, 0)).toOffsetDateTime();
    }

    /**
     * Build DateTime without TimeZone
     */
    private LocalDateTime buildDateTime() {
        if (unixsecond > 0) {
            return LocalDateTime.ofEpochSecond(unixsecond, ns, ZoneOffset.UTC);
        }
        if (am && hour == 12) {
            this.hour = 0;
        }
        if (pm && hour != 12) {
            this.hour += 12;
        }
        return LocalDateTime.of(year, month, day, hour, minute, second, ns);
    }

}
