package com.ngreenan.mytimechecker;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by Nick on 18/12/2015.
 */
public class Util {

    public static float getHourOffsetFromTimeZone(String id) {

        //get TimeZone object from our description e.g. "Europe/London"
        TimeZone timeZone = TimeZone.getTimeZone(id);

        //get Calendar object for our TimeZone
        Calendar calendar = Calendar.getInstance(timeZone);

        //get the zone and dst offsets in millis
        long zoneOffset = calendar.get(Calendar.ZONE_OFFSET);
        long dstOffset = calendar.get(Calendar.DST_OFFSET);

        //get the device's current offset so we can adjust for that
        Calendar defaultCalendar = Calendar.getInstance();
        long zoneOffsetDefault = defaultCalendar.get(Calendar.ZONE_OFFSET);
        long dstOffsetDefault = defaultCalendar.get(Calendar.DST_OFFSET);

        //return the overall offset in hours (i.e. millis / (1000 * 60 * 60)), adjusted for the device's current offset
        return (zoneOffset + dstOffset - zoneOffsetDefault - dstOffsetDefault) / (1000 * 60 * 60);
    }
}
