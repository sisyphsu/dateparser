package com.github.sisyphsu;

import org.junit.jupiter.api.Test;

import java.time.ZoneId;
import java.util.TimeZone;

/**
 * @author sulin
 * @since 2019-09-14 13:31:30
 */
public class ZoneTest {

    @Test
    public void test() {
        for (String availableZoneId : ZoneId.getAvailableZoneIds()) {
            System.out.println(availableZoneId);
        }
    }

    @Test
    public void zoneId() {
        ZoneId id = ZoneId.systemDefault();
        System.out.println(id.getId());
        System.out.println(id);
    }

    @Test
    public void timeZone() {
        for (String availableID : TimeZone.getAvailableIDs()) {
            System.out.println(availableID + "\t" + TimeZone.getTimeZone(availableID).getDisplayName());
        }
    }

    @Test
    public void timezone2() {
        TimeZone zone = TimeZone.getTimeZone("MST");

        System.out.println(zone.getRawOffset());
        System.out.println(zone.getDisplayName());
    }

}
