package com.ngreenan.mytimechecker;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.os.Handler;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.ngreenan.mytimechecker.db.DBDataSource;
import com.ngreenan.mytimechecker.model.City;
import com.ngreenan.mytimechecker.model.Continent;
import com.ngreenan.mytimechecker.model.Country;
import com.ngreenan.mytimechecker.model.Person;
import com.ngreenan.mytimechecker.model.Region;
import com.ngreenan.mytimechecker.model.TimeZone;

import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private float rotation = 0F;
    private PieChart pieChart;
    private float totalSeconds = (24F * 60F * 60F);

    public static final int NOTIFICATIONID = 1;

    //constants for XML preferences
    public static final String MYSTARTHOUR = "pref_myStartHour";
    public static final String MYSTARTMIN = "pref_myStartMin";
    public static final String MYENDHOUR = "pref_myEndHour";
    public static final String MYENDMIN = "pref_myEndMin";
    public static final String THEIRSTARTHOUR = "pref_theirStartHour";
    public static final String THEIRSTARTMIN = "pref_theirStartMin";
    public static final String THEIRENDHOUR = "pref_theirEndHour";
    public static final String THEIRENDMIN = "pref_theirEndMin";
    //public static final String MYOFFSET = "pref_myOffset";
    public static final String THEIROFFSET = "pref_theirOffset";
    public static final String SUPPRESSNOTIFICATION = "pref_suppressNotification";
    private SharedPreferences settings;

    //int values representing our start and end times
    private int myStartHour;
    private int myStartMin;
    private int myEndHour;
    private int myEndMin;
    private int theirStartHour;
    private int theirStartMin;
    private int theirEndHour;
    private int theirEndMin;

    private int myOffset;
    private int theirOffset;

    private boolean suppressNotification = false;
    private boolean isCrossOver = false;

    //database
    DBDataSource datasource;
    private List<Continent> continents;
    private List<Country> countries;
    private List<Region> regions;
    private List<City> cities;
    private List<TimeZone> timeZones;
    private List<Person> persons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //load preferences from XML
        loadXMLPreferences();

        //set the layout file to associate with MainActivity
        setContentView(R.layout.activity_main);

        //get a reference to our SquareLinearLayout, create a PieChart and add it!
        SquareLinearLayout squareLinearLayout = (SquareLinearLayout) findViewById(R.id.squareLayout);
        //RelativeLayout linearLayout = (RelativeLayout) findViewById(R.id.layout);
        pieChart = new PieChart(this);
        setTimes();
        squareLinearLayout.addView(pieChart);

        //load data from database
        loadDataFromDatabase();

        //kick off the "timer" to update the rotation of the clock every half second
        timerHandler.postDelayed(timerRunnable, 0);
    }

    private void loadDataFromDatabase() {
        datasource = new DBDataSource(this);
        datasource.open();

        //continents
        continents = datasource.getAllContinents();
        if (continents.size() == 0) {
            datasource.createContinentsData(this);
            continents = datasource.getAllContinents();
        }

        //countries
        countries = datasource.getAllCountries();
        if (countries.size() == 0) {
            datasource.createCountriesData(this);
            countries = datasource.getAllCountries();
        }

        //regions
        regions = datasource.getAllRegions();
        if (regions.size() == 0) {
            datasource.createRegionsData(this);
            regions = datasource.getAllRegions();
        }

        //timezones
        timeZones = datasource.getAllTimeZones();
        if (timeZones.size() == 0) {
            datasource.createTimeZonesData(this);
            timeZones = datasource.getAllTimeZones();
        }

        //cities
        cities = datasource.getAllCities();
        if (cities.size() == 0) {
            datasource.createCitiesData(this);
            cities = datasource.getAllCities();
        }

        //persons
        persons = datasource.getAllPersons();
        if (persons.size() == 0) {
            datasource.createPersonsData();
            persons = datasource.getAllPersons();
        }
    }

    //runs without a timer by reposting this handler at the end of the runnable
    Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {

        //this is what will happen every time the handler runs
        @Override
        public void run() {
            Calendar c = Calendar.getInstance();
            float seconds = (c.get(Calendar.HOUR_OF_DAY) * 60 * 60)
                    + (c.get(Calendar.MINUTE) * 60)
                    + c.get(Calendar.SECOND);

            rotation = ((0 - seconds) / totalSeconds) * 360;

            displayTimes(c);

            pieChart.setRotation(rotation);

            //do we need to send out a notification?
            checkCrossOver(c);

            if (isCrossOver) {
                //we're in a crossover period - have we already seen a notification?
                if (!suppressNotification) {
                    //show a notification
                    displayNotification();
                }
            } else {
                //reset - if a notification is due display it from now on
                suppressNotification = false;
                setXMLPreference(SUPPRESSNOTIFICATION, suppressNotification);
//                Toast.makeText(MainActivity.this, "suppressNotification set to false", Toast.LENGTH_LONG).show();
            }

            //set the time til the next run - in this case 500ms or half a second
            timerHandler.postDelayed(this, 500);
        }
    };

//    private void displayNotification() {
//        //TODO: replace this bit with a notification instead of this toast
//        Toast.makeText(MainActivity.this, "Now entering cross over period!!!", Toast.LENGTH_LONG).show();
//
//        //suppress future notifications for this crossover period
//        suppressNotification = true;
//    }

    private void displayNotification(){

        //build the notification itself
        Notification.Builder mBuilder =
                new Notification.Builder(this)
                .setSmallIcon(R.drawable.ic_stat_notification)
                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.mipmap.ic_launcher))
                .setContentTitle("My Time Checker")
                .setContentText("Your friend is now contactable!")
                .setPriority(Notification.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        //build the intent
        Intent resultIntent = new Intent(this, MainActivity.class);

        //the stack builder object will contain an artificial back stack for the started activity
        //this ensures that navigating backwards works properly
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

        //adds the back stack for the Intent
        stackBuilder.addParentStack(MainActivity.class);

        //adds the Intent
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent pendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(pendingIntent);

        //get the NotificationManager and add notification to it
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(NOTIFICATIONID, mBuilder.build());

        suppressNotification = true;
        setXMLPreference(SUPPRESSNOTIFICATION, suppressNotification);
        //Toast.makeText(MainActivity.this, "suppressNotification set to true", Toast.LENGTH_LONG).show();
    }

    private void loadXMLPreferences() {
        //load values from XML preferences
        //initialise our SharedPreferences object
        settings = getPreferences(MODE_PRIVATE);

        myStartHour = settings.getInt(MYSTARTHOUR, 8);
        myStartMin = settings.getInt(MYSTARTMIN, 0);
        myEndHour = settings.getInt(MYENDHOUR, 22);
        myEndMin = settings.getInt(MYENDMIN, 0);
        theirStartHour = settings.getInt(THEIRSTARTHOUR, 8);
        theirStartMin = settings.getInt(THEIRSTARTMIN, 0);
        theirEndHour = settings.getInt(THEIRENDHOUR, 22);
        theirEndMin = settings.getInt(THEIRENDMIN, 0);

        //myOffset = settings.getInt(MYOFFSET,0);
        myOffset = 0;
        theirOffset = settings.getInt(THEIROFFSET, 0);

        suppressNotification = settings.getBoolean(SUPPRESSNOTIFICATION, false);
        //Toast.makeText(MainActivity.this, "suppressNotification set to " + String.valueOf(suppressNotification), Toast.LENGTH_LONG).show();
    }

    private void setTimes() {
        //set my times
        pieChart.setMyStartTime(myStartHour - myOffset, myStartMin);
        pieChart.setMyEndTime(myEndHour - myOffset, myEndMin);

        //set their times
        pieChart.setTheirStartTime(theirStartHour - theirOffset, theirStartMin);
        pieChart.setTheirEndTime(theirEndHour - theirOffset, theirEndMin);

        //set button text
        Button button = (Button) findViewById(R.id.myStartButton);
        button.setText(String.format("%02d", myStartHour) + ":" + String.format("%02d", myStartMin));

        button = (Button) findViewById(R.id.myEndButton);
        button.setText(String.format("%02d", myEndHour) + ":" + String.format("%02d", myEndMin));

        button = (Button) findViewById(R.id.theirStartButton);
        button.setText(String.format("%02d", theirStartHour) + ":" + String.format("%02d", theirStartMin));

        button = (Button) findViewById(R.id.theirEndButton);
        button.setText(String.format("%02d", theirEndHour) + ":" + String.format("%02d", theirEndMin));

        TextView textView;// = (TextView) findViewById(R.id.myTimeZone);
        //textView.setText(deriveTimeZone(myOffset));

        textView = (TextView) findViewById(R.id.theirTimeZone);
        textView.setText(deriveTimeZone(theirOffset));

        displayTimes(Calendar.getInstance());
    }

    private void displayTimes(Calendar c) {
        TextView textView;

        int myHour = (c.get(Calendar.HOUR_OF_DAY) + myOffset) % 24;
        int theirHour = (c.get(Calendar.HOUR_OF_DAY) + theirOffset) % 24;

        if (myHour < 0) {
            myHour += 24;
        }

        if (theirHour < 0) {
            theirHour += 24;
        }

        //display my current time
        textView = (TextView) findViewById(R.id.myActualTime);
        textView.setText(String.format("%02d", myHour) + ":" + String.format("%02d", c.get(Calendar.MINUTE)));

        //display their current time
        textView = (TextView) findViewById(R.id.theirActualTime);
        textView.setText(String.format("%02d", theirHour) + ":" + String.format("%02d", c.get(Calendar.MINUTE)));
    }

    private String deriveTimeZone(int offset) {
        if (offset == 0) {
            return "0";
        } else if (offset > 0) {
            return "+" + String.valueOf(offset);
        } else {
            return String.valueOf(offset);
        }
    }

    public void rotateCanvas(View view) {
        if (rotation == 360F) {
            rotation = 10F;
        }
        else {
            rotation += 10F;
        }

        pieChart.setRotation(rotation);
    }

    public void getTime(final View view) {
        //load a TimePickerDialog and ask the user for the new time
        int hour = 0;
        int minute = 0;

        //work out which time we're changing from the id of the Button pressed
        switch (view.getId()) {
            case R.id.myStartButton:
                hour = myStartHour;
                minute = myStartMin;
                break;
            case R.id.myEndButton:
                hour = myEndHour;
                minute = myEndMin;
                break;
            case R.id.theirStartButton:
                hour = theirStartHour;
                minute = theirStartMin;
                break;
            case R.id.theirEndButton:
                hour = theirEndHour;
                minute = theirEndMin;
                break;
        }

        //launch our TimePickerDialog
        TimePickerDialog timePickerDialog;
        //we need to set a context, an OnTimeSetListener, a start hour, a start minute and whether it's 24 hours or not
        timePickerDialog = new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                //this gets called when we've picked a time - which values do we want to set??
                //for each we will set the variables to the new value
                //but also update the XML preferences so that when we reload the app, it'll remember what times we chose
                switch (view.getId()) {
                    case R.id.myStartButton:
                        //variables
                        myStartHour = selectedHour;
                        myStartMin = selectedMinute;
                        //XML preferences
                        setXMLPreference(MYSTARTHOUR, selectedHour);
                        setXMLPreference(MYSTARTMIN, selectedMinute);
                        break;
                    case R.id.myEndButton:
                        //variables
                        myEndHour = selectedHour;
                        myEndMin = selectedMinute;
                        //XML preferences
                        setXMLPreference(MYENDHOUR, selectedHour);
                        setXMLPreference(MYENDMIN, selectedMinute);
                        break;
                    case R.id.theirStartButton:
                        //variables
                        theirStartHour = selectedHour;
                        theirStartMin = selectedMinute;
                        //XML preferences
                        setXMLPreference(THEIRSTARTHOUR, selectedHour);
                        setXMLPreference(THEIRSTARTMIN, selectedMinute);
                        break;
                    case R.id.theirEndButton:
                        //variables
                        theirEndHour = selectedHour;
                        theirEndMin = selectedMinute;
                        //XML preferences
                        setXMLPreference(THEIRENDHOUR, selectedHour);
                        setXMLPreference(THEIRENDMIN, selectedMinute);
                        break;
                }

                setTimes();
            }
        }, hour, minute, true);
        timePickerDialog.setTitle("Select Time");
        timePickerDialog.show();
    }

    private void setXMLPreference(String key, int value) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    private void setXMLPreference(String key, boolean value) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public void changeOffset(View view) {
        //which button did we press?
        switch (view.getId()) {
//            case R.id.myPlus:
//                myOffset++;
//                setXMLPreference(MYOFFSET, myOffset);
//                break;
//            case R.id.myMinus:
//                myOffset--;
//                setXMLPreference(MYOFFSET, myOffset);
//                break;
            case R.id.theirPlus:
                theirOffset++;
                setXMLPreference(THEIROFFSET, theirOffset);
                break;
            case R.id.theirMinus:
                theirOffset--;
                setXMLPreference(THEIROFFSET, theirOffset);
                break;
        }

        //do we need to disable anything?
        Button button;

        //myPlus
        //button = (Button) findViewById(R.id.myPlus);
        //button.setEnabled(myOffset < 14);

        //myMinus
        //button = (Button) findViewById(R.id.myMinus);
        //button.setEnabled(myOffset > -12);

        //theirPlus
        button = (Button) findViewById(R.id.theirPlus);
        button.setEnabled(theirOffset < 14);

        //theirMinus
        button = (Button) findViewById(R.id.theirMinus);
        button.setEnabled(theirOffset > -12);

        setTimes();
    }

    private void checkCrossOver(Calendar c) {
        //we need to know if the current time is after both start times and before both end times

        //get the current minutes
        int timeNow = (c.get(Calendar.HOUR_OF_DAY) * 60)
                + c.get(Calendar.MINUTE);

        //get my start/end and their start/end in minutes, allowing for time differences
        int myStart = (myStartHour - myOffset) * 60 + myStartMin;
        int myEnd = (myEndHour - myOffset) * 60 + myEndMin;
        int theirStart = (theirStartHour - theirOffset) * 60 + theirStartMin;
        int theirEnd = (theirEndHour - theirOffset) * 60 + theirEndMin;

        //Toast.makeText(MainActivity.this, myStart + "," + myEnd + "," + theirStart + "," + theirEnd + "," + timeNow, Toast.LENGTH_LONG).show();

        int minutes = 24 * 60;

        //if start times go into previous day, add a day's worth of minutes to allow for this
        //otherwise the comparison won't work properly
        if (myStart < 0) {
            myStart += minutes;
            myEnd += minutes;
        }

        if (theirStart < 0) {
            theirStart += minutes;
            theirEnd += minutes;
        }

        //now check if we're in a cross over period
        //return true if we are, false if we're not
        if (myStart <= timeNow
                && theirStart <= timeNow % minutes
                && myEnd >= timeNow
                && theirEnd >= timeNow) {
            isCrossOver = true;
        } else {
            isCrossOver = false;
        }
    }
}
