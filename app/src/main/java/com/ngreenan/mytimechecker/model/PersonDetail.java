package com.ngreenan.mytimechecker.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Nick on 06/12/2015.
 */
public class PersonDetail implements Parcelable {
    private long personID;
    private String personName;
    private long cityID;
    private int startHour;
    private int startMin;
    private int endHour;
    private int endMin;
    private boolean displayNotifications;
    private boolean active;
    private int colorID;
    private boolean me;

    private String cityName;
    private long continentID;
    private long countryID;
    private long regionID;
    private long timeZoneID;

    private String continentName;

    private String countryName;
    private boolean usesRegions;
    private String flagPath;

    private String regionName;

    private String timeZoneName;

    public long getPersonID() {
        return personID;
    }

    public void setPersonID(long personID) {
        this.personID = personID;
    }

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    public long getCityID() {
        return cityID;
    }

    public void setCityID(long cityID) {
        this.cityID = cityID;
    }

    public int getStartHour() {
        return startHour;
    }

    public void setStartHour(int startHour) {
        this.startHour = startHour;
    }

    public int getStartMin() {
        return startMin;
    }

    public void setStartMin(int startMin) {
        this.startMin = startMin;
    }

    public int getEndHour() {
        return endHour;
    }

    public void setEndHour(int endHour) {
        this.endHour = endHour;
    }

    public int getEndMin() {
        return endMin;
    }

    public void setEndMin(int endMin) {
        this.endMin = endMin;
    }

    public boolean isDisplayNotifications() {
        return displayNotifications;
    }

    public void setDisplayNotifications(boolean displayNotifications) {
        this.displayNotifications = displayNotifications;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public int getColorID() {
        return colorID;
    }

    public void setColorID(int colorID) {
        this.colorID = colorID;
    }

    public boolean isMe() {
        return me;
    }

    public void setMe(boolean me) {
        this.me = me;
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

    public String getContinentName() {
        return continentName;
    }

    public void setContinentName(String continentName) {
        this.continentName = continentName;
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

    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }

    public String getTimeZoneName() {
        return timeZoneName;
    }

    public void setTimeZoneName(String timeZoneName) {
        this.timeZoneName = timeZoneName;
    }

    @Override
    public String toString() {
        return personName;
    }

    //default constructor
    public PersonDetail() {
    }

    //parcel constructor
    public PersonDetail(Parcel in) {
        personID = in.readLong();
        personName = in.readString();
        cityID = in.readLong();
        startHour = in.readInt();
        startMin = in.readInt();
        endHour = in.readInt();
        endMin = in.readInt();
        displayNotifications = in.readByte() != 0;
        active = in.readByte() != 0;
        colorID = in.readInt();
        me = in.readByte() != 0;

        cityName = in.readString();
        continentID = in.readLong();
        countryID = in.readLong();
        regionID = in.readLong();
        timeZoneID = in.readLong();

        continentName = in.readString();

        countryName = in.readString();
        usesRegions = in.readByte() != 0;
        flagPath = in.readString();

        regionName = in.readString();

        timeZoneName = in.readString();
    }

    //implement Parcelable
    @Override
    public int describeContents() {
        //not a complex item - don't need to describe anything
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(personID);
        dest.writeString(personName);
        dest.writeLong(cityID);
        dest.writeInt(startHour);
        dest.writeInt(startMin);
        dest.writeInt(endHour);
        dest.writeInt(endMin);
        dest.writeByte((byte) (displayNotifications ? 1 : 0));
        dest.writeByte((byte) (active ? 1 : 0));
        dest.writeInt(colorID);
        dest.writeByte((byte) (me ? 1 : 0));

        dest.writeString(cityName);
        dest.writeLong(continentID);
        dest.writeLong(countryID);
        dest.writeLong(regionID);
        dest.writeLong(timeZoneID);

        dest.writeString(continentName);

        dest.writeString(countryName);
        dest.writeByte((byte) (usesRegions ? 1 : 0));
        dest.writeString(flagPath);

        dest.writeString(regionName);

        dest.writeString(timeZoneName);
    }

    public static final Parcelable.Creator<PersonDetail> CREATOR = new Parcelable.Creator<PersonDetail>() {

        @Override
        public PersonDetail createFromParcel(Parcel source) {
            return new PersonDetail(source);
        }

        @Override
        public PersonDetail[] newArray(int size) {
            return new PersonDetail[size];
        }
    };
}
