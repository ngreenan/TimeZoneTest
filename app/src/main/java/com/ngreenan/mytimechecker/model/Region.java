package com.ngreenan.mytimechecker.model;

/**
 * Created by Nick on 03/12/2015.
 */
public class Region {
    private long regionID;
    private String regionName;

    public long getRegionID() {
        return regionID;
    }

    public void setRegionID(long regionID) {
        this.regionID = regionID;
    }

    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }

    @Override
    public String toString() {
        return regionName;
    }
}
