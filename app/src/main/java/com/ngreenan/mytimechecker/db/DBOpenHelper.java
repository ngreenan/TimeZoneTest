package com.ngreenan.mytimechecker.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Nick on 03/12/2015.
 */
public class DBOpenHelper extends SQLiteOpenHelper {

    //constants for table/column names etc

    //logcat
    private static final String LOGTAG = "My Time Checker - DB";

    //database name & version
    private static final String DATABASE_NAME = "mtc.db";
    private static final int DATABASE_VERSION = 1;

    //continent table
    public static final String TABLE_CONTINENTS = "continents";
    public static final String COLUMN_CONTINENTID = "continentID";
    public static final String COLUMN_CONTINENTNAME = "continentName";

    private static final String TABLE_CONTINENTS_CREATE = "CREATE TABLE " + TABLE_CONTINENTS + " ("
            + COLUMN_CONTINENTID + " INTEGER PRIMARY KEY, "
            + COLUMN_CONTINENTNAME + " TEXT)";

    public static final String[] TABLE_CONTINENTS_COLUMNS = {
            COLUMN_CONTINENTID,
            COLUMN_CONTINENTNAME
    };

    //country table
    public static final String TABLE_COUNTRIES = "countries";
    public static final String COLUMN_COUNTRYID = "countryID";
    public static final String COLUMN_COUNTRYNAME = "countryName";
    public static final String COLUMN_USESREGIONS = "usesRegions";
    public static final String COLUMN_FLAGPATH = "flagPath";

    private static final String TABLE_COUNTRIES_CREATE = "CREATE TABLE " + TABLE_COUNTRIES + " ("
            + COLUMN_COUNTRYID + " INTEGER PRIMARY KEY, "
            + COLUMN_COUNTRYNAME + " TEXT, "
            + COLUMN_USESREGIONS + " INTEGER, " //SQLite doesn't have a bit/boolean type, use integer = 0/1
            + COLUMN_FLAGPATH + " TEXT)";

    public static final String[] TABLE_COUNTRIES_COLUMNS = {
            COLUMN_COUNTRYID,
            COLUMN_COUNTRYNAME,
            COLUMN_USESREGIONS,
            COLUMN_FLAGPATH
    };

    //region table
    public static final String TABLE_REGIONS = "regions";
    public static final String COLUMN_REGIONID = "regionID";
    public static final String COLUMN_REGIONNAME = "regionName";

    private static final String TABLE_REGIONS_CREATE = "CREATE TABLE " + TABLE_REGIONS + " ("
            + COLUMN_REGIONID + " INTEGER PRIMARY KEY, "
            + COLUMN_REGIONNAME + " TEXT)";

    public static final String[] TABLE_REGIONS_COLUMNS = {
            COLUMN_REGIONID,
            COLUMN_REGIONNAME
    };

    //timezone table
    public static final String TABLE_TIMEZONES = "timeZones";
    public static final String COLUMN_TIMEZONEID = "timeZoneID";
    public static final String COLUMN_TIMEZONENAME = "timeZoneName";

    private static final String TABLE_TIMEZONES_CREATE = "CREATE TABLE " + TABLE_TIMEZONES + " ("
            + COLUMN_TIMEZONEID + " INTEGER PRIMARY KEY, "
            + COLUMN_TIMEZONENAME + " TEXT)";

    public static final String[] TABLE_TIMEZONES_COLUMNS = {
            COLUMN_TIMEZONEID,
            COLUMN_TIMEZONENAME
    };

    //city table
    public static final String TABLE_CITIES = "cities";
    public static final String COLUMN_CITYID = "cityID";
    public static final String COLUMN_CITYNAME = "cityName";
    //the other four columns in cities are foreign keys that share their name with the source table e.g. continentID
    //so no need to declare them again!

    private static final String TABLE_CITIES_CREATE = "CREATE TABLE " + TABLE_CITIES + " ("
            + COLUMN_CITYID + " INTEGER PRIMARY KEY, "
            + COLUMN_CITYNAME + " TEXT, "
            + COLUMN_CONTINENTID + " INTEGER, "
            + COLUMN_COUNTRYID + " INTEGER, "
            + COLUMN_REGIONID + " INTEGER, "
            + COLUMN_TIMEZONEID + " INTEGER)";

    public static final String[] TABLE_CITIES_COLUMNS = {
            COLUMN_CITYID,
            COLUMN_CITYNAME,
            COLUMN_CONTINENTID,
            COLUMN_COUNTRYID,
            COLUMN_REGIONID,
            COLUMN_TIMEZONEID
    };

    //persons table
    public static final String TABLE_PERSONS = "persons";
    public static final String COLUMN_PERSONID = "personID";
    public static final String COLUMN_PERSONNAME = "personName";
    //public static final long COLUMN_CITYID = "cityID"; //not required as it's a foreign key
    public static final String COLUMN_STARTHOUR = "startHour";
    public static final String COLUMN_STARTMIN = "startMin";
    public static final String COLUMN_ENDHOUR = "endHour";
    public static final String COLUMN_ENDMIN = "endMin";
    public static final String COLUMN_DISPLAYNOTIFICATIONS = "displayNotifications";
    public static final String COLUMN_ACTIVE = "active";
    public static final String COLUMN_COLORID = "colorID";
    public static final String COLUMN_ME = "me";

    private static final String TABLE_PERSONS_CREATE = "CREATE TABLE " + TABLE_PERSONS + " ("
            + COLUMN_PERSONID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_PERSONNAME + " TEXT, "
            + COLUMN_CITYID + " INTEGER, "
            + COLUMN_STARTHOUR + " INTEGER, "
            + COLUMN_STARTMIN + " INTEGER, "
            + COLUMN_ENDHOUR + " INTEGER, "
            + COLUMN_ENDMIN + " INTEGER, "
            + COLUMN_DISPLAYNOTIFICATIONS + " INTEGER, "
            + COLUMN_ACTIVE + " INTEGER, "
            + COLUMN_COLORID + " INTEGER,"
            + COLUMN_ME + " INTEGER)";

    public static final String[] TABLE_PERSONS_COLUMNS = {
            COLUMN_PERSONID,
            COLUMN_PERSONNAME,
            COLUMN_CITYID,
            COLUMN_STARTHOUR,
            COLUMN_STARTMIN,
            COLUMN_ENDHOUR,
            COLUMN_ENDMIN,
            COLUMN_DISPLAYNOTIFICATIONS,
            COLUMN_ACTIVE,
            COLUMN_COLORID,
            COLUMN_ME
    };

    public static final String TABLE_NOTIFICATIONSTATUS = "notificationStatus";
    public static final String COLUMN_NOTIFICATIONSTATUS = "notificationStatus";

    private static final String TABLE_NOTIFICATIONSTATUS_CREATE = "CREATE TABLE " + TABLE_NOTIFICATIONSTATUS + " ("
            + COLUMN_PERSONID + " INTEGER PRIMARY KEY, "
            + COLUMN_NOTIFICATIONSTATUS + " TEXT)";

    public static final String[] TABLE_NOTIFICATIONSTATUS_COLUMNS = {
            COLUMN_PERSONID,
            COLUMN_NOTIFICATIONSTATUS
    };

    //views
    public static final String VIEW_PERSONDETAILS = "personDetails";
    private static final String VIEW_PERSONDETAILS_CREATE =
            "CREATE VIEW " + VIEW_PERSONDETAILS + " AS SELECT p." + COLUMN_PERSONID
                    + ", p." + COLUMN_PERSONNAME
                    + ", p." + COLUMN_STARTHOUR
                    + ", p." + COLUMN_STARTMIN
                    + ", p." + COLUMN_ENDHOUR
                    + ", p." + COLUMN_ENDMIN
                    + ", p." + COLUMN_DISPLAYNOTIFICATIONS
                    + ", p." + COLUMN_ACTIVE
                    + ", p." + COLUMN_COLORID
                    + ", p." + COLUMN_ME
                    + ", cn." + COLUMN_CONTINENTID
                    + ", co." + COLUMN_COUNTRYID
                    + ", ct." + COLUMN_CITYID
                    + ", r." + COLUMN_REGIONID
                    + ", tz." + COLUMN_TIMEZONEID
                    + ", cn." + COLUMN_CONTINENTNAME
                    + ", co." + COLUMN_COUNTRYNAME
                    + ", co." + COLUMN_USESREGIONS
                    + ", co." + COLUMN_FLAGPATH
                    + ", r." + COLUMN_REGIONNAME
                    + ", tz." + COLUMN_TIMEZONENAME
                    + ", ct." + COLUMN_CITYNAME
                    + " from " + TABLE_PERSONS
                    + " p left join " + TABLE_CITIES
                    + " ct on p." + COLUMN_CITYID + " = ct." + COLUMN_CITYID
                    + " left join " + TABLE_CONTINENTS
                    + " cn on ct." + COLUMN_CONTINENTID + " = cn." + COLUMN_CONTINENTID
                    + " left join " + TABLE_COUNTRIES
                    + " co on ct." + COLUMN_COUNTRYID + " = co." + COLUMN_COUNTRYID
                    + " left join " + TABLE_REGIONS
                    + " r on ct." + COLUMN_REGIONID + " = r." + COLUMN_REGIONID
                    + " left join " + TABLE_TIMEZONES
                    + " tz on ct." + COLUMN_TIMEZONEID + " = tz." + COLUMN_TIMEZONEID + ";";
    public static final String[] VIEW_PERSONDETAILS_COLUMNS = {
            COLUMN_PERSONID,
                    COLUMN_PERSONNAME,
                    COLUMN_STARTHOUR,
                    COLUMN_STARTMIN,
                    COLUMN_ENDHOUR,
                    COLUMN_ENDMIN,
                    COLUMN_DISPLAYNOTIFICATIONS,
                    COLUMN_ACTIVE,
                    COLUMN_COLORID,
                    COLUMN_ME,
                    COLUMN_CONTINENTID,
                    COLUMN_COUNTRYID,
                    COLUMN_CITYID,
                    COLUMN_REGIONID,
                    COLUMN_TIMEZONEID,
                    COLUMN_CONTINENTNAME,
                    COLUMN_COUNTRYNAME,
                    COLUMN_USESREGIONS,
                    COLUMN_FLAGPATH,
                    COLUMN_REGIONNAME,
                    COLUMN_TIMEZONENAME,
                    COLUMN_CITYNAME
    };

    //default constructor
    public DBOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        //create tables
        db.execSQL(TABLE_CONTINENTS_CREATE);
        Log.i(LOGTAG, "Created continents table");
        db.execSQL(TABLE_COUNTRIES_CREATE);
        Log.i(LOGTAG, "Created countries table");
        db.execSQL(TABLE_REGIONS_CREATE);
        Log.i(LOGTAG, "Created regions table");
        db.execSQL(TABLE_TIMEZONES_CREATE);
        Log.i(LOGTAG, "Created timezones table");
        db.execSQL(TABLE_CITIES_CREATE);
        Log.i(LOGTAG, "Created cities table");
        db.execSQL(TABLE_PERSONS_CREATE);
        Log.i(LOGTAG, "Created persons table");
        db.execSQL(TABLE_NOTIFICATIONSTATUS_CREATE);
        Log.i(LOGTAG, "Created notificationStatus table");

        //create views
        db.execSQL(VIEW_PERSONDETAILS_CREATE);
        Log.i(LOGTAG, "Created PersonDetails view");

        Log.i(LOGTAG, "Database created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //for now, drop all and recreate
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTINENTS);
        Log.i(LOGTAG, "Dropped continents table");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COUNTRIES);
        Log.i(LOGTAG, "Dropped countries table");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_REGIONS);
        Log.i(LOGTAG, "Dropped regions table");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TIMEZONES);
        Log.i(LOGTAG, "Dropped timezones table");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CITIES);
        Log.i(LOGTAG, "Dropped cities table");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PERSONS);
        Log.i(LOGTAG, "Dropped persons table");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTIFICATIONSTATUS);
        Log.i(LOGTAG, "Dropped notificationStatus table");
        db.execSQL("DROP VIEW IF EXISTS " + VIEW_PERSONDETAILS);
        Log.i(LOGTAG, "Dropped personDetails view");

        onCreate(db);
        Log.i(LOGTAG, "Database has been upgraded from " + oldVersion + " to " + newVersion);
    }
}
