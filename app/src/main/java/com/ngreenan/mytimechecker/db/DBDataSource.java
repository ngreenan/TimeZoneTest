package com.ngreenan.mytimechecker.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.ngreenan.mytimechecker.MainActivity;
import com.ngreenan.mytimechecker.model.City;
import com.ngreenan.mytimechecker.model.Continent;
import com.ngreenan.mytimechecker.model.Country;
import com.ngreenan.mytimechecker.model.Person;
import com.ngreenan.mytimechecker.model.Region;
import com.ngreenan.mytimechecker.model.TimeZone;
import com.ngreenan.mytimechecker.xml.CitiesJDOMParser;
import com.ngreenan.mytimechecker.xml.ContinentsJDOMParser;
import com.ngreenan.mytimechecker.xml.CountriesJDOMParser;
import com.ngreenan.mytimechecker.xml.RegionsJDOMParser;
import com.ngreenan.mytimechecker.xml.TimeZonesJDOMParser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nick on 03/12/2015.
 */
public class DBDataSource {

    //logcat
    private static final String LOGTAG = "My Time Checker - DB";

    SQLiteOpenHelper dbhelper;
    SQLiteDatabase database;

    //constructor
    public DBDataSource(Context context) {
        dbhelper = new DBOpenHelper(context);
    }

    //database level methods
    public void open() {
        database = dbhelper.getWritableDatabase();
        Log.i(LOGTAG, "Database opened");
    }

    public void close() {
        dbhelper.close();
        Log.i(LOGTAG, "Database closed");
    }

    //table level methods
    //continent
    public void create(Continent continent){
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.COLUMN_CONTINENTID, continent.getContinentID());
        values.put(DBOpenHelper.COLUMN_CONTINENTNAME, continent.getContinentName());
        database.insert(DBOpenHelper.TABLE_CONTINENTS, null, values);
    }

    public List<Continent> getAllContinents() {
        Cursor cursor = database.query(DBOpenHelper.TABLE_CONTINENTS, DBOpenHelper.TABLE_CONTINENTS_COLUMNS,
                null, null, null, null, null);

        Log.i(LOGTAG, "Returned " + cursor.getCount() + " continents");
        List<Continent> continents = getContinentList(cursor);
        return continents;
    }

    private List<Continent> getContinentList(Cursor cursor) {
        List<Continent> continents = new ArrayList<Continent>();

        if (cursor.getCount() > 0) {
            //we have some items in our cursor!
            while (cursor.moveToNext()) {
                //loop round cursor, pull out each value and build our new object
                Continent continent = new Continent();
                continent.setContinentID(cursor.getLong(cursor.getColumnIndex(DBOpenHelper.COLUMN_CONTINENTID)));
                continent.setContinentName(cursor.getString(cursor.getColumnIndex(DBOpenHelper.COLUMN_CONTINENTNAME)));
                //add our object to the list
                continents.add(continent);
            }
        }

        //return the populated list!
        return continents;
    }

    //country
    public void create(Country country){
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.COLUMN_COUNTRYID, country.getCountryID());
        values.put(DBOpenHelper.COLUMN_COUNTRYNAME, country.getCountryName());
        values.put(DBOpenHelper.COLUMN_USESREGIONS, country.isUsesRegions());
        values.put(DBOpenHelper.COLUMN_FLAGPATH, country.getFlagPath());
        database.insert(DBOpenHelper.TABLE_COUNTRIES, null, values);
    }

    public List<Country> getAllCountries() {
        Cursor cursor = database.query(DBOpenHelper.TABLE_COUNTRIES, DBOpenHelper.TABLE_COUNTRIES_COLUMNS,
                null, null, null, null, null);

        Log.i(LOGTAG, "Returned " + cursor.getCount() + " countries");
        List<Country> countries = getCountryList(cursor);
        return countries;
    }

    private List<Country> getCountryList(Cursor cursor) {
        List<Country> countries = new ArrayList<Country>();

        if (cursor.getCount() > 0) {
            //we have some items in our cursor!
            while (cursor.moveToNext()) {
                //loop round cursor, pull out each value and build our new object
                Country country = new Country();
                country.setCountryID(cursor.getLong(cursor.getColumnIndex(DBOpenHelper.COLUMN_COUNTRYID)));
                country.setCountryName(cursor.getString(cursor.getColumnIndex(DBOpenHelper.COLUMN_COUNTRYNAME)));

                if (cursor.getInt(cursor.getColumnIndex(DBOpenHelper.COLUMN_USESREGIONS)) == 1) {
                    country.setUsesRegions(true);
                } else {
                    country.setUsesRegions(false);
                }

                country.setFlagPath(cursor.getString(cursor.getColumnIndex(DBOpenHelper.COLUMN_FLAGPATH)));

                //add our object to the list
                countries.add(country);
            }
        }

        //return the populated list!
        return countries;
    }

    //region
    public void create(Region region){
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.COLUMN_REGIONID, region.getRegionID());
        values.put(DBOpenHelper.COLUMN_REGIONNAME, region.getRegionName());
        database.insert(DBOpenHelper.TABLE_REGIONS, null, values);
    }

    public List<Region> getAllRegions() {
        Cursor cursor = database.query(DBOpenHelper.TABLE_REGIONS, DBOpenHelper.TABLE_REGIONS_COLUMNS,
                null, null, null, null, null);

        Log.i(LOGTAG, "Returned " + cursor.getCount() + " regions");
        List<Region> regions = getRegionList(cursor);
        return regions;
    }

    private List<Region> getRegionList(Cursor cursor) {
        List<Region> regions = new ArrayList<Region>();

        if (cursor.getCount() > 0) {
            //we have some items in our cursor!
            while (cursor.moveToNext()) {
                //loop round cursor, pull out each value and build our new object
                Region region = new Region();
                region.setRegionID(cursor.getLong(cursor.getColumnIndex(DBOpenHelper.COLUMN_REGIONID)));
                region.setRegionName(cursor.getString(cursor.getColumnIndex(DBOpenHelper.COLUMN_REGIONNAME)));
                //add our object to the list
                regions.add(region);
            }
        }

        //return the populated list!
        return regions;
    }

    //timezone
    public void create(TimeZone timeZone){
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.COLUMN_TIMEZONEID, timeZone.getTimeZoneID());
        values.put(DBOpenHelper.COLUMN_TIMEZONENAME, timeZone.getTimeZoneName());
        database.insert(DBOpenHelper.TABLE_TIMEZONES, null, values);
    }


    public List<TimeZone> getAllTimeZones() {
        Cursor cursor = database.query(DBOpenHelper.TABLE_TIMEZONES, DBOpenHelper.TABLE_TIMEZONES_COLUMNS,
                null, null, null, null, null);

        Log.i(LOGTAG, "Returned " + cursor.getCount() + " timezones");
        List<TimeZone> timeZones = getTimeZoneList(cursor);
        return timeZones;
    }

    private List<TimeZone> getTimeZoneList(Cursor cursor) {
        List<TimeZone> timeZones = new ArrayList<TimeZone>();

        if (cursor.getCount() > 0) {
            //we have some items in our cursor!
            while (cursor.moveToNext()) {
                //loop round cursor, pull out each value and build our new object
                TimeZone timeZone = new TimeZone();
                timeZone.setTimeZoneID(cursor.getLong(cursor.getColumnIndex(DBOpenHelper.COLUMN_TIMEZONEID)));
                timeZone.setTimeZoneName(cursor.getString(cursor.getColumnIndex(DBOpenHelper.COLUMN_TIMEZONENAME)));
                //add our object to the list
                timeZones.add(timeZone);
            }
        }

        //return the populated list!
        return timeZones;
    }

    //city
    public void create(City city){
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.COLUMN_CITYID, city.getCityID());
        values.put(DBOpenHelper.COLUMN_CITYNAME, city.getCityName());
        values.put(DBOpenHelper.COLUMN_CONTINENTID, city.getContinentID());
        values.put(DBOpenHelper.COLUMN_COUNTRYID, city.getCountryID());
        values.put(DBOpenHelper.COLUMN_REGIONID, city.getRegionID());
        values.put(DBOpenHelper.COLUMN_TIMEZONEID, city.getTimeZoneID());

        database.insert(DBOpenHelper.TABLE_CITIES, null, values);
    }

    public List<City> getAllCities() {
        Cursor cursor = database.query(DBOpenHelper.TABLE_CITIES, DBOpenHelper.TABLE_CITIES_COLUMNS,
                null, null, null, null, null);

        Log.i(LOGTAG, "Returned " + cursor.getCount() + " cities");
        List<City> cities = getCityList(cursor);
        return cities;
    }

    private List<City> getCityList(Cursor cursor) {
        List<City> cities = new ArrayList<City>();

        if (cursor.getCount() > 0) {
            //we have some items in our cursor!
            while (cursor.moveToNext()) {
                //loop round cursor, pull out each value and build our new object
                City city = new City();
                city.setCityID(cursor.getLong(cursor.getColumnIndex(DBOpenHelper.COLUMN_CITYID)));
                city.setCityName(cursor.getString(cursor.getColumnIndex(DBOpenHelper.COLUMN_CITYNAME)));
                city.setContinentID(cursor.getLong(cursor.getColumnIndex(DBOpenHelper.COLUMN_CONTINENTID)));
                city.setCountryID(cursor.getLong(cursor.getColumnIndex(DBOpenHelper.COLUMN_COUNTRYID)));
                city.setRegionID(cursor.getLong(cursor.getColumnIndex(DBOpenHelper.COLUMN_REGIONID)));
                city.setTimeZoneID(cursor.getLong(cursor.getColumnIndex(DBOpenHelper.COLUMN_TIMEZONEID)));

                //add our object to the list
                cities.add(city);
            }
        }

        //return the populated list!
        return cities;
    }

    //person
    public Person create(Person person){
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.COLUMN_PERSONID, person.getCityID());
        values.put(DBOpenHelper.COLUMN_PERSONNAME, person.getPersonName());
        values.put(DBOpenHelper.COLUMN_CITYID, person.getCityID());
        values.put(DBOpenHelper.COLUMN_STARTHOUR, person.getStartHour());
        values.put(DBOpenHelper.COLUMN_STARTMIN, person.getStartMin());
        values.put(DBOpenHelper.COLUMN_ENDHOUR, person.getEndHour());
        values.put(DBOpenHelper.COLUMN_ENDMIN, person.getEndMin());
        values.put(DBOpenHelper.COLUMN_DISPLAYNOTIFICATIONS, person.isDisplayNotifications());
        values.put(DBOpenHelper.COLUMN_ACTIVE, person.isActive());
        values.put(DBOpenHelper.COLUMN_COLORID, person.getColorID());

        long insertID = database.insert(DBOpenHelper.TABLE_CITIES, null, values);
        person.setPersonID(insertID);
        return person;
    }

    public List<Person> getAllPersons() {
        Cursor cursor = database.query(DBOpenHelper.TABLE_PERSONS, DBOpenHelper.TABLE_PERSONS_COLUMNS,
                null, null, null, null, null);

        Log.i(LOGTAG, "Returned " + cursor.getCount() + " persons");
        List<Person> persons = getPersonList(cursor);
        return persons;
    }

    private List<Person> getPersonList(Cursor cursor) {
        List<Person> persons = new ArrayList<Person>();

        if (cursor.getCount() > 0) {
            //we have some items in our cursor!
            while (cursor.moveToNext()) {
                //loop round cursor, pull out each value and build our new object
                Person person = new Person();
                person.setPersonID(cursor.getLong(cursor.getColumnIndex(DBOpenHelper.COLUMN_PERSONID)));
                person.setPersonName(cursor.getString(cursor.getColumnIndex(DBOpenHelper.COLUMN_PERSONNAME)));
                person.setCityID(cursor.getLong(cursor.getColumnIndex(DBOpenHelper.COLUMN_CITYID)));
                person.setStartHour(cursor.getInt(cursor.getColumnIndex(DBOpenHelper.COLUMN_STARTHOUR)));
                person.setStartMin(cursor.getInt(cursor.getColumnIndex(DBOpenHelper.COLUMN_STARTMIN)));
                person.setEndHour(cursor.getInt(cursor.getColumnIndex(DBOpenHelper.COLUMN_ENDHOUR)));
                person.setEndMin(cursor.getInt(cursor.getColumnIndex(DBOpenHelper.COLUMN_ENDMIN)));

                if (cursor.getInt(cursor.getColumnIndex(DBOpenHelper.COLUMN_DISPLAYNOTIFICATIONS)) == 1) {
                    person.setDisplayNotifications(true);
                } else {
                    person.setDisplayNotifications(false);
                }

                if (cursor.getInt(cursor.getColumnIndex(DBOpenHelper.COLUMN_ACTIVE)) == 1) {
                    person.setActive(true);
                } else {
                    person.setActive(false);
                }

                person.setColorID(cursor.getInt(cursor.getColumnIndex(DBOpenHelper.COLUMN_COLORID)));

                //add our object to the list
                persons.add(person);
            }
        }

        //return the populated list!
        return persons;
    }

    public void createContinentsData(Context context) {
        ContinentsJDOMParser parser = new ContinentsJDOMParser();
        List<Continent> continents = parser.parseXML(context);

        for (Continent continent : continents) {
            create(continent);
        }
    }

    public void createCountriesData(Context context) {
        CountriesJDOMParser parser = new CountriesJDOMParser();
        List<Country> countries = parser.parseXML(context);

        for (Country country : countries) {
            create(country);
        }
    }

    public void createRegionsData(Context context) {
        RegionsJDOMParser parser = new RegionsJDOMParser();
        List<Region> regions = parser.parseXML(context);

        for (Region region : regions) {
            create(region);
        }
    }

    public void createTimeZonesData(Context context) {
        TimeZonesJDOMParser parser = new TimeZonesJDOMParser();
        List<TimeZone> timeZones = parser.parseXML(context);

        for (TimeZone timeZone : timeZones) {
            create(timeZone);
        }
    }

    public void createCitiesData(Context context) {
        CitiesJDOMParser parser = new CitiesJDOMParser();
        List<City> cities = parser.parseXML(context);

        for (City city : cities) {
            create(city);
        }
    }

    public void createPersonsData() {
        //this one is different as we're not seeding from XML

        //me
        Person person = new Person();
        person.setPersonName("Me");
        person.setCityID(215);
        person.setStartHour(8);
        person.setStartMin(0);
        person.setEndHour(22);
        person.setEndMin(0);
        person.setDisplayNotifications(true);
        person.setActive(true);
        person.setColorID(1);
        create(person);

        //them
        person = new Person();
        person.setPersonName("Them");
        person.setCityID(215);
        person.setStartHour(8);
        person.setStartMin(0);
        person.setEndHour(22);
        person.setEndMin(0);
        person.setDisplayNotifications(true);
        person.setActive(true);
        person.setColorID(2);
        create(person);
    }
}
