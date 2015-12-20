package com.ngreenan.mytimechecker;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.os.Handler;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.ngreenan.mytimechecker.db.DBDataSource;
import com.ngreenan.mytimechecker.model.PersonDetail;

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
    private List<PersonDetail> personDetails;
    boolean loadingData = false;
    boolean reloadData = false;

    ListView detailsListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //load preferences from XML
        loadXMLPreferences();

        //set the layout file to associate with MainActivity
        setContentView(R.layout.activity_main);
        detailsListView = (ListView) findViewById(R.id.detailsListView);

        //load and populate data - moved to separate method so it can be called onResume as well as onCreate
        datasource = new DBDataSource(this);
        loadAndPopulateData();
    }

    private void loadAndPopulateData() {
        //load data from database
        loadDataFromDatabase();

        //get a reference to our SquareLinearLayout, create a PieChart and add it!
        SquareLinearLayout squareLinearLayout = (SquareLinearLayout) findViewById(R.id.squareLayout);
        pieChart = new PieChart(this);

        //remove all views - if we have any
        squareLinearLayout.removeAllViews();

        //pass over objects, do other stuff
        setTimes();

        //add our new view
        squareLinearLayout.addView(pieChart);

        //kick off the "timer" to update the rotation of the clock every half second
        timerHandler.postDelayed(timerRunnable, 0);
    }

    @Override
    protected void onResume() {
        super.onResume();

        //reload the data, and pass it over to PieChart again
        loadAndPopulateData();
    }

    private void loadDataFromDatabase() {

        if (!datasource.isOpen()) {
            datasource.open();
        }

        personDetails = datasource.getActivePersonDetails();
        if (personDetails.size() == 0 && !loadingData) {

            //initialize our database in the background
            loadingData = true;

            //show a ProgressDialog so it's not just a blank screen
            final ProgressDialog dialog = new ProgressDialog(this);
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setMessage("Initializing database - please wait...");
            dialog.setIndeterminate(true);
            dialog.setCancelable(false);
            dialog.show();

            //setup a new thread to do our initialization in
            Thread thread = new Thread() {
                @Override
                public void run() {
                    //generate default data
                    datasource.createContinentsData(MainActivity.this);
                    datasource.createCountriesData(MainActivity.this);
                    datasource.createRegionsData(MainActivity.this);
                    datasource.createTimeZonesData(MainActivity.this);
                    datasource.createCitiesData(MainActivity.this);
                    datasource.createPersonsData();

                    //and now load the newly set up persons
                    personDetails = datasource.getActivePersonDetails();

                    loadingData = false;
                    reloadData = true;

                    //dismiss the ProgressDialog now that we're done
                    dialog.dismiss();
                }
            };

            //start the thread
            thread.start();
        }
    }

    //runs without a timer by reposting this handler at the end of the runnable
    Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {

        //this is what will happen every time the handler runs
        @Override
        public void run() {

            //do we need to reload our data?
            if (reloadData) {
                loadAndPopulateData();
                reloadData = false;
            }

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
            }

            ((BaseAdapter) detailsListView.getAdapter()).notifyDataSetChanged();

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
        //get my details and put into ListView
        ArrayAdapter<PersonDetail> myArrayAdapter = new MainArrayAdapter(this, R.layout.main_list_item, personDetails);
        detailsListView.setAdapter(myArrayAdapter);

        pieChart.setPersonDetails(personDetails);

        //displayTimes(Calendar.getInstance());
    }

    private void displayTimes(Calendar c) {
//        TextView textView;
//
//        int myHour = (c.get(Calendar.HOUR_OF_DAY) + myOffset) % 24;
//        int theirHour = (c.get(Calendar.HOUR_OF_DAY) + theirOffset) % 24;
//
//        if (myHour < 0) {
//            myHour += 24;
//        }
//
//        if (theirHour < 0) {
//            theirHour += 24;
//        }
//
//        //display my current time
//        textView = (TextView) findViewById(R.id.myActualTime);
//        textView.setText(String.format("%02d", myHour) + ":" + String.format("%02d", c.get(Calendar.MINUTE)));
//
//        //display their current time
//        textView = (TextView) findViewById(R.id.theirActualTime);
//        textView.setText(String.format("%02d", theirHour) + ":" + String.format("%02d", c.get(Calendar.MINUTE)));
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
//            case R.id.theirPlus:
//                theirOffset++;
//                setXMLPreference(THEIROFFSET, theirOffset);
//                break;
//            case R.id.theirMinus:
//                theirOffset--;
//                setXMLPreference(THEIROFFSET, theirOffset);
//                break;
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
//        button = (Button) findViewById(R.id.theirPlus);
//        button.setEnabled(theirOffset < 14);

        //theirMinus
//        button = (Button) findViewById(R.id.theirMinus);
//        button.setEnabled(theirOffset > -12);

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

    public void viewDetail(View view) {
        //this will open a new view - not going to pass anything over, just a simple "load new view" intent
        Intent detailIntent = new Intent(this, DetailActivity.class);
        startActivity(detailIntent);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.setup:
                viewDetail(null);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
