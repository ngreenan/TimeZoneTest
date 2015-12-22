package com.ngreenan.mytimechecker;

import android.app.DialogFragment;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.os.Handler;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.ngreenan.mytimechecker.db.DBDataSource;
import com.ngreenan.mytimechecker.model.NotificationStatus;
import com.ngreenan.mytimechecker.model.PersonDetail;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity implements AboutDialogFragment.AboutDialogListener {

    private float rotation = 0F;
    private PieChart pieChart;
    private SquareLinearLayout squareLinearLayout;
    private AboutDialogFragment dialog;
    private float totalSeconds = (24F * 60F * 60F);

    public static final int NOTIFICATIONID = 1;

    //constants for XML preferences
    private SharedPreferences settings;

    //database
    DBDataSource datasource;
    private List<PersonDetail> personDetails;
    boolean loadingData = false;
    boolean reloadData = false;
    boolean timerStarted = false;

    ListView detailsListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //load preferences from XML
        //loadXMLPreferences();

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
        squareLinearLayout = (SquareLinearLayout) findViewById(R.id.squareLayout);
        pieChart = new PieChart(this);

        //remove all views - if we have any
        squareLinearLayout.removeAllViews();

        //pass over objects, do other stuff
        setTimes();

        //add our new view
        squareLinearLayout.addView(pieChart);

        //remove the runnable first, then add again
        //this means we won't get multiple timers running
        //but we'll always get one kicking off when we move back to MainActivity
        timerHandler.removeCallbacks(timerRunnable);
        timerHandler.postDelayed(timerRunnable, 0);
        timerStarted = true;
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

                    //start up the timer again
                    timerHandler.removeCallbacks(timerRunnable);
                    timerHandler.postDelayed(timerRunnable, 0);
                    timerStarted = true;

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

            if (loadingData) {
                //we're initializing our data - don't bother with any of this timer stuff yet!
                return;
            }

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

            pieChart.setRotation(rotation);

//            //do we need to send out a notification?
//            checkCrossOver(c);
//
//            if (isCrossOver) {
//                //we're in a crossover period - have we already seen a notification?
//                if (!suppressNotification) {
//                    //show a notification
//                    displayNotification();
//                }
//            } else {
//                //reset - if a notification is due display it from now on
//                suppressNotification = false;
//                setXMLPreference(SUPPRESSNOTIFICATION, suppressNotification);
//            }

            ((BaseAdapter) detailsListView.getAdapter()).notifyDataSetChanged();

            checkForNotifications();

            //set the time til the next run - in this case 1000ms or a second
            timerHandler.removeCallbacks(timerRunnable);
            timerHandler.removeCallbacks(timerRunnable);
            timerHandler.removeCallbacks(timerRunnable);
            timerHandler.removeCallbacks(timerRunnable);
            timerHandler.removeCallbacks(timerRunnable);
            timerHandler.removeCallbacks(timerRunnable);
            timerHandler.removeCallbacks(timerRunnable);
            timerHandler.postDelayed(this, 1000);
        }
    };

    private void checkForNotifications() {

        //step 0 - load the data and check that we actually have some
        //on the very initial startup, we won't!
        List<PersonDetail> details = datasource.getMyPersonDetails();
        if (details.size() == 0) {
            return;
        }

        //step 1 - get the start and end time in millis for me
        PersonDetail me = details.get(0);

        if (currentlyInPeriod(me)) {
            //we're in our period - is anyone else?
            //loop round all the PersonDetails where
            //a) we're active
            //b) we're displaying notifications
            //c) we have a city
            List<PersonDetail> notificationPersons = datasource.getNotificationPersonDetails();
            ArrayList<String> array = new ArrayList<>();
            int colorID = 0;

            for (PersonDetail person : notificationPersons) {

                NotificationStatus notificationStatus = datasource.getNotificationStatusById(person.getPersonID());

                //are they in an active period?
                if (currentlyInPeriod(person)) {
                    //are they due a notification?
                    if (notificationStatus.getNotificationStatus() == 0) {
                        //yes - display one, mark it as done
                        array.add(person.getPersonName());

                        colorID = getResources().getIdentifier("color" + person.getColorID(), "color", getPackageName());

                        notificationStatus.setNotificationStatus(1);
                        datasource.insertUpdateNotificationStatus(notificationStatus);
                    } else {
                        //no - they've already had one for this period
                        //do nothing
                    }
                } else {
                    //not in notification period - do we need to reset notificationStatus?
                    if (notificationStatus.getNotificationStatus() == 1) {
                        //yes - reset it
                        notificationStatus.setNotificationStatus(0);
                        datasource.insertUpdateNotificationStatus(notificationStatus);
                    } else {
                        //no - already marked as due for next period
                    }
                }
            }

            if (array.size() > 0) {
                //we have some people to notify about!
                String message = "";

                switch (array.size()) {
                    case 1:
                        message = array.get(0) + " is now contactable!";
                        break;
                    case 2:
                        message = array.get(0) + " and " + array.get(1) + " are now contactable!";
                        break;
                    default:
                        for (int i = 0; i < array.size() - 1; i++) {
                            message += array.get(i) + ", ";
                        }
                        message = message.substring(0, message.length() - 2);
                        message += " and " + array.get(array.size() -1) + " are now contactable";
                        break;
                }

                displayPersonNotification(message, colorID);
            }
        } else {
            //we're not in our period - reset notificationStatus table
            datasource.resetNotificationStatus();
        }
    }

    private void displayPersonNotification(String message, int color){

        //build the notification itself
        Notification.Builder mBuilder =
                new Notification.Builder(this)
                        .setSmallIcon(R.drawable.ic_stat_notification)
                        .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.mipmap.ic_launcher))
                        .setContentTitle("My Time Checker")
                        .setContentText(message)
                        .setPriority(Notification.PRIORITY_DEFAULT)
                        .setAutoCancel(true);

        //build the intent
        Intent resultIntent = new Intent(this, MainActivity.class);
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

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
    }

    private boolean currentlyInPeriod(PersonDetail person) {
        TimeZone myTimeZone = TimeZone.getTimeZone(person.getTimeZoneName());

        Calendar myCalendar = Calendar.getInstance(myTimeZone);

        //start
        myCalendar.set(Calendar.HOUR_OF_DAY, person.getStartHour());
        myCalendar.set(Calendar.MINUTE, person.getStartMin());
        myCalendar.set(Calendar.SECOND, 0);
        myCalendar.set(Calendar.MILLISECOND, 0);

        long myStartMillis = myCalendar.getTimeInMillis();

        //end
        myCalendar.set(Calendar.HOUR_OF_DAY, person.getEndHour());
        myCalendar.set(Calendar.MINUTE, person.getEndMin());

        long myEndMillis = myCalendar.getTimeInMillis();

        if (myStartMillis > myEndMillis) {
            //move end to the following day
            myEndMillis += (24 * 60 * 60 * 1000);
        }

        if (myStartMillis <= Calendar.getInstance().getTimeInMillis()
                && myEndMillis >= Calendar.getInstance().getTimeInMillis()) {
            return true;
        } else {
            return false;
        }
    }

    private void loadXMLPreferences() {
        //load values from XML preferences
        //initialise our SharedPreferences object
        settings = getPreferences(MODE_PRIVATE);

        //suppressNotification = settings.getBoolean(SUPPRESSNOTIFICATION, false);
    }

    private void setTimes() {
        //get my details and put into ListView
        ArrayAdapter<PersonDetail> myArrayAdapter = new MainArrayAdapter(this, R.layout.main_list_item, personDetails);
        detailsListView.setAdapter(myArrayAdapter);

        pieChart.setPersonDetails(personDetails);
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


    public void viewDetail(View view) {
        //this will open a new view - not going to pass anything over, just a simple "load new view" intent
        Intent detailIntent = new Intent(this, DetailActivity.class);
        startActivity(detailIntent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
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
            case R.id.about:
                showAbout();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showAbout() {
        dialog = new AboutDialogFragment();
        dialog.show(getFragmentManager(),"blah");
    }

    @Override
    //this is what we'll do when we click on OK on the AboutDialogFragment
    //uses the interface we declared to pass the responsibility to handle the click events over
    //to the parent activity
    public void onDialogPositiveClick(DialogFragment dialog) {
        dialog.dismiss();
    }

    public void clickURL(View view) {
        TextView textView = (TextView) view;
        String urlToLaunch = textView.getText().toString();
        Uri webPage = Uri.parse(urlToLaunch);
        Intent webContent = new Intent(Intent.ACTION_VIEW, webPage);
        startActivity(webContent);
    }
}
