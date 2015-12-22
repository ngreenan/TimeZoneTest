package com.ngreenan.mytimechecker.model;

/**
 * Created by Nick on 03/12/2015.
 */
public class TimeZone {
    private long timeZoneID;
    private String timeZoneName;

    public long getTimeZoneID() {
        return timeZoneID;
    }

    public void setTimeZoneID(long timeZoneID) {
        this.timeZoneID = timeZoneID;
    }

    public String getTimeZoneName() {
        return timeZoneName;
    }

    public void setTimeZoneName(String timeZoneName) {
        this.timeZoneName = timeZoneName;
    }

    @Override
    public String toString() {
        return timeZoneName;
    }
}
