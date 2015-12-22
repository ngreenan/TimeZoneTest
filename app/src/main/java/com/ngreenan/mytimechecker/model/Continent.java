package com.ngreenan.mytimechecker.model;

/**
 * Created by Nick on 03/12/2015.
 */
public class Continent {

    private long continentID;
    private String continentName;

    public long getContinentID() {
        return continentID;
    }

    public void setContinentID(long continentID) {
        this.continentID = continentID;
    }

    public String getContinentName() {
        return continentName;
    }

    public void setContinentName(String continentName) {
        this.continentName = continentName;
    }

    @Override
    public String toString() {
        return continentName;
    }
}
