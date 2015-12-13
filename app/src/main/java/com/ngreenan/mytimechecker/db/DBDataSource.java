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
import com.ngreenan.mytimechecker.model.PersonDetail;
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

        return getContinentList(cursor);
    }

    private List<Continent> getContinentList(Cursor cursor) {
        List<Continent> continents = new ArrayList<>();

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
        //values.put(DBOpenHelper.COLUMN_PERSONID, person.getCityID()); //this will autoincrement so leave it blank
        values.put(DBOpenHelper.COLUMN_PERSONNAME, person.getPersonName());
        values.put(DBOpenHelper.COLUMN_CITYID, person.getCityID());
        values.put(DBOpenHelper.COLUMN_STARTHOUR, person.getStartHour());
        values.put(DBOpenHelper.COLUMN_STARTMIN, person.getStartMin());
        values.put(DBOpenHelper.COLUMN_ENDHOUR, person.getEndHour());
        values.put(DBOpenHelper.COLUMN_ENDMIN, person.getEndMin());
        values.put(DBOpenHelper.COLUMN_DISPLAYNOTIFICATIONS, person.isDisplayNotifications());
        values.put(DBOpenHelper.COLUMN_ACTIVE, person.isActive());
        values.put(DBOpenHelper.COLUMN_COLORID, person.getColorID());
        values.put(DBOpenHelper.COLUMN_ME, person.isMe());

        long insertID = database.insert(DBOpenHelper.TABLE_PERSONS, null, values);
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

    public List<Person> getMyPersons() {
        //returns all rows from Persons table where Me = 1
        Cursor cursor = database.query(DBOpenHelper.TABLE_PERSONS, DBOpenHelper.TABLE_PERSONS_COLUMNS,
                DBOpenHelper.COLUMN_ME + " = 1", null, null, null, null);

        Log.i(LOGTAG, "Returned " + cursor.getCount() + " persons");
        List<Person> persons = getPersonList(cursor);
        return persons;
    }

    public List<Person> getMyFriendPersons() {
        //returns all rows from Persons table where Me = 0
        Cursor cursor = database.query(DBOpenHelper.TABLE_PERSONS, DBOpenHelper.TABLE_PERSONS_COLUMNS,
                DBOpenHelper.COLUMN_ME + " = 0", null, null, null, null);

        Log.i(LOGTAG, "Returned " + cursor.getCount() + " persons");
        List<Person> persons = getPersonList(cursor);
        return persons;
    }

    public List<Person> getActivePersons() {
        //returns all rows from Persons table where Active = 1
        Cursor cursor = database.query(DBOpenHelper.TABLE_PERSONS, DBOpenHelper.TABLE_PERSONS_COLUMNS,
                DBOpenHelper.COLUMN_ACTIVE + " = 1", null, null, null, null);

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

                if (cursor.getInt(cursor.getColumnIndex(DBOpenHelper.COLUMN_ME)) == 1) {
                    person.setMe(true);
                } else {
                    person.setMe(false);
                }

                //add our object to the list
                persons.add(person);
            }
        }

        //return the populated list!
        return persons;
    }

    //PersonDetails
    public List<PersonDetail> getAllPersonDetails() {
        Cursor cursor = database.query(DBOpenHelper.VIEW_PERSONDETAILS, DBOpenHelper. VIEW_PERSONDETAILS_COLUMNS,
                null, null, null, null, null);

        Log.i(LOGTAG, "Returned " + cursor.getCount() + " personDetails");
        List<PersonDetail> personDetails = getPersonDetailsList(cursor);
        return personDetails;
    }

    public List<PersonDetail> getMyPersonDetails() {
        //returns all rows from Persons table where Me = 1
        Cursor cursor = database.query(DBOpenHelper.VIEW_PERSONDETAILS, DBOpenHelper.VIEW_PERSONDETAILS_COLUMNS,
                DBOpenHelper.COLUMN_ME + " = 1", null, null, null, null);

        Log.i(LOGTAG, "Returned " + cursor.getCount() + " personDetails");
        List<PersonDetail> personDetails = getPersonDetailsList(cursor);
        return personDetails;
    }

    public List<PersonDetail> getMyFriendPersonDetails() {
        //returns all rows from Persons table where Me = 0
        Cursor cursor = database.query(DBOpenHelper.VIEW_PERSONDETAILS, DBOpenHelper.VIEW_PERSONDETAILS_COLUMNS,
                DBOpenHelper.COLUMN_ME + " = 0", null, null, null, null);

        Log.i(LOGTAG, "Returned " + cursor.getCount() + " personDetails");
        List<PersonDetail> personDetails = getPersonDetailsList(cursor);
        return personDetails;
    }



    private List<PersonDetail> getPersonDetailsList(Cursor cursor) {
        List<PersonDetail> personDetails = new ArrayList<>();

        if (cursor.getCount() > 0) {
            //we have some items in our cursor!
            while (cursor.moveToNext()) {
                //loop round cursor, pull out each value and build our new object
                PersonDetail person = new PersonDetail();

                //person
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

                if (cursor.getInt(cursor.getColumnIndex(DBOpenHelper.COLUMN_ME)) == 1) {
                    person.setMe(true);
                } else {
                    person.setMe(false);
                }

                person.setCityName(cursor.getString(cursor.getColumnIndex(DBOpenHelper.COLUMN_CITYNAME)));

                //continent
                person.setContinentID(cursor.getLong(cursor.getColumnIndex(DBOpenHelper.COLUMN_CONTINENTID)));
                person.setContinentName(cursor.getString(cursor.getColumnIndex(DBOpenHelper.COLUMN_CONTINENTNAME)));

                //country
                person.setCountryID(cursor.getLong(cursor.getColumnIndex(DBOpenHelper.COLUMN_COUNTRYID)));
                person.setCountryName(cursor.getString(cursor.getColumnIndex(DBOpenHelper.COLUMN_COUNTRYNAME)));

                if (cursor.getInt(cursor.getColumnIndex(DBOpenHelper.COLUMN_USESREGIONS)) == 1) {
                    person.setUsesRegions(true);
                } else {
                    person.setUsesRegions(false);
                }

                person.setFlagPath(cursor.getString(cursor.getColumnIndex(DBOpenHelper.COLUMN_FLAGPATH)));

                //region
                person.setRegionID(cursor.getLong(cursor.getColumnIndex(DBOpenHelper.COLUMN_REGIONID)));
                person.setRegionName(cursor.getString(cursor.getColumnIndex(DBOpenHelper.COLUMN_REGIONNAME)));

                //timezone
                person.setTimeZoneID(cursor.getLong(cursor.getColumnIndex(DBOpenHelper.COLUMN_TIMEZONEID)));
                person.setTimeZoneName(cursor.getString(cursor.getColumnIndex(DBOpenHelper.COLUMN_TIMEZONENAME)));

                //add our object to the list
                personDetails.add(person);
            }
        }

        //return the populated list!
        return personDetails;
    }



    //create data
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
        Person personMe = new Person();
        personMe.setPersonName("Me");
        personMe.setCityID(215); //london
        personMe.setStartHour(8);
        personMe.setStartMin(0);
        personMe.setEndHour(22);
        personMe.setEndMin(0);
        personMe.setDisplayNotifications(true);
        personMe.setActive(true);
        personMe.setColorID(1);
        personMe.setMe(true);
        create(personMe);

        //them 1
        Person personThem1 = new Person();
        personThem1.setPersonName("Them 1");
        personThem1.setCityID(380); //melbourne
        personThem1.setStartHour(8);
        personThem1.setStartMin(0);
        personThem1.setEndHour(22);
        personThem1.setEndMin(0);
        personThem1.setDisplayNotifications(true);
        personThem1.setActive(true);
        personThem1.setColorID(2);
        personThem1.setMe(false);
        create(personThem1);

        //them 2
        Person personThem2 = new Person();
        personThem2.setPersonName("Them 2");
        personThem2.setCityID(346); //new york
        personThem2.setStartHour(8);
        personThem2.setStartMin(0);
        personThem2.setEndHour(22);
        personThem2.setEndMin(0);
        personThem2.setDisplayNotifications(true);
        personThem2.setActive(false);
        personThem2.setColorID(3);
        personThem2.setMe(false);
        create(personThem2);

        //them 1
        Person personThem3 = new Person();
        personThem3.setPersonName("Them 3");
        personThem3.setCityID(210); //zurich
        personThem3.setStartHour(8);
        personThem3.setStartMin(0);
        personThem3.setEndHour(22);
        personThem3.setEndMin(0);
        personThem3.setDisplayNotifications(true);
        personThem3.setActive(true);
        personThem3.setColorID(4);
        personThem3.setMe(false);
        create(personThem3);
    }

    public City getCityById(long cityID) {
        Cursor cursor = database.query(DBOpenHelper.TABLE_CITIES, DBOpenHelper.TABLE_CITIES_COLUMNS,
                DBOpenHelper.COLUMN_CITYID + " = " + cityID, null, null, null, null);

        City city = new City();

        if (cursor.getCount() != 0) {
            city.setCityID(cursor.getLong(cursor.getColumnIndex(DBOpenHelper.COLUMN_CITYID)));
            city.setCityName(cursor.getString(cursor.getColumnIndex(DBOpenHelper.COLUMN_CITYNAME)));
            city.setContinentID(cursor.getLong(cursor.getColumnIndex(DBOpenHelper.COLUMN_CONTINENTID)));
            city.setCountryID(cursor.getLong(cursor.getColumnIndex(DBOpenHelper.COLUMN_COUNTRYID)));
            city.setRegionID(cursor.getLong(cursor.getColumnIndex(DBOpenHelper.COLUMN_REGIONID)));
            city.setTimeZoneID(cursor.getLong(cursor.getColumnIndex(DBOpenHelper.COLUMN_TIMEZONEID)));
        }

        return city;
    }

    public Country getCountryById(long countryID) {
        Cursor cursor = database.query(DBOpenHelper.TABLE_COUNTRIES, DBOpenHelper.TABLE_COUNTRIES_COLUMNS,
                DBOpenHelper.COLUMN_COUNTRYID + " = " + countryID, null, null, null, null);

        Country country = new Country();

        if (cursor.getCount() != 0) {
            country.setCountryID(cursor.getLong(cursor.getColumnIndex(DBOpenHelper.COLUMN_COUNTRYID)));
            country.setCountryName(cursor.getString(cursor.getColumnIndex(DBOpenHelper.COLUMN_COUNTRYNAME)));

            if (cursor.getInt(cursor.getColumnIndex(DBOpenHelper.COLUMN_USESREGIONS)) == 1) {
                country.setUsesRegions(true);
            } else {
                country.setUsesRegions(false);
            }

            country.setFlagPath(cursor.getString(cursor.getColumnIndex(DBOpenHelper.COLUMN_FLAGPATH)));
        }

        return country;
    }

    public void setActive(long personID, boolean active) {
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.COLUMN_ACTIVE, active);
        database.update(DBOpenHelper.TABLE_PERSONS, values, DBOpenHelper.COLUMN_PERSONID + " = " + String.valueOf(personID), null);
    }


    public void updateName(long personID, String personName) {
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.COLUMN_PERSONNAME, personName);
        database.update(DBOpenHelper.TABLE_PERSONS, values, DBOpenHelper.COLUMN_PERSONID + " = " + String.valueOf(personID), null);
    }

    public List<Country> getCountriesByContinentID(long continentID) {
        Cursor cursor = database.rawQuery("select * from countries where countryID in (select distinct countryID from cities where continentID = " + String.valueOf(continentID) + ") order by countryName", null);

        Log.i(LOGTAG, "Returned " + cursor.getCount() + " countries");
        List<Country> countries = getCountryList(cursor);
        return countries;
    }

    public List<Region> getRegionsByCountryID(long countryID) {
        Cursor cursor = database.rawQuery("select * from regions where regionID in (select distinct regionID from cities where countryID = " + String.valueOf(countryID) + ") order by regionName", null);

        Log.i(LOGTAG, "Returned " + cursor.getCount() + " regions");
        List<Region> regions = getRegionList(cursor);
        return regions;
    }

    public List<City> getCitiesByCountryID(long countryID) {
        Cursor cursor = database.rawQuery("select * from cities where countryID = " + String.valueOf(countryID) + " order by cityName", null);

        Log.i(LOGTAG, "Returned " + cursor.getCount() + " cities");
        List<City> cities = getCityList(cursor);
        return cities;
    }

    public List<City> getCitiesByRegionID(long regionID) {
        Cursor cursor = database.rawQuery("select * from cities where regionID = " + String.valueOf(regionID) + " order by cityName", null);

        Log.i(LOGTAG, "Returned " + cursor.getCount() + " cities");
        List<City> cities = getCityList(cursor);
        return cities;
    }

    public void updatePersonDetails(PersonDetail personDetail) {

        ContentValues values = new ContentValues();
        //name
        values.put(DBOpenHelper.COLUMN_PERSONNAME, personDetail.getPersonName());

        //start time
        values.put(DBOpenHelper.COLUMN_STARTHOUR, personDetail.getStartHour());
        values.put(DBOpenHelper.COLUMN_STARTMIN , personDetail.getStartMin());

        //end time
        values.put(DBOpenHelper.COLUMN_ENDHOUR, personDetail.getEndHour());
        values.put(DBOpenHelper.COLUMN_ENDMIN, personDetail.getEndMin());

        //city
        values.put(DBOpenHelper.COLUMN_CITYID, personDetail.getCityID());

        //active
        values.put(DBOpenHelper.COLUMN_ACTIVE, personDetail.isActive());

        //notify
        values.put(DBOpenHelper.COLUMN_DISPLAYNOTIFICATIONS, personDetail.isDisplayNotifications());

        //color
        values.put(DBOpenHelper.COLUMN_COLORID, personDetail.getColorID());

        database.update(DBOpenHelper.TABLE_PERSONS, values, DBOpenHelper.COLUMN_PERSONID + " = " + String.valueOf(personDetail.getPersonID()), null);
    }


}
