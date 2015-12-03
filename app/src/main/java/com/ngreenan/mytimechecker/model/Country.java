package com.ngreenan.mytimechecker.model;

/**
 * Created by Nick on 03/12/2015.
 */
public class Country {
    private long countryID;
    private String countryName;
    private boolean usesRegions;
    private String flagPath;

    public long getCountryID() {
        return countryID;
    }

    public void setCountryID(long countryID) {
        this.countryID = countryID;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public boolean isUsesRegions() {
        return usesRegions;
    }

    public void setUsesRegions(boolean usesRegions) {
        this.usesRegions = usesRegions;
    }

    public String getFlagPath() {
        return flagPath;
    }

    public void setFlagPath(String flagPath) {
        this.flagPath = flagPath;
    }

    @Override
    public String toString() {
        return countryName;
    }
}
