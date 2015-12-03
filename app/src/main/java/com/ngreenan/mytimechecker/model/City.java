package com.ngreenan.mytimechecker.model;

import android.support.annotation.Nullable;

/**
 * Created by Nick on 03/12/2015.
 */
public class City {
    private long cityID;
    private String cityName;
    private long continentID;
    private long countryID;
    private long regionID;
    private long timeZoneID;

    public long getCityID() {
        return cityID;
    }

    public void setCityID(long cityID) {
        this.cityID = cityID;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public long getContinentID() {
        return continentID;
    }

    public void setContinentID(long continentID) {
        this.continentID = continentID;
    }

    public long getCountryID() {
        return countryID;
    }

    public void setCountryID(long countryID) {
        this.countryID = countryID;
    }

    public long getRegionID() {
        return regionID;
    }

    public void setRegionID(long regionID) {
        this.regionID = regionID;
    }

    public long getTimeZoneID() {
        return timeZoneID;
    }

    public void setTimeZoneID(long timeZoneID) {
        this.timeZoneID = timeZoneID;
    }

    @Override
    public String toString() {
        return cityName;
    }
}
