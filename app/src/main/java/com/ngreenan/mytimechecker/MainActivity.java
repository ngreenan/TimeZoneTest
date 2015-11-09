package com.ngreenan.mytimechecker;

import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.os.Handler;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private float rotation = 0F;
    private PieChart pieChart;
    private float totalSeconds = (24F * 60F * 60F);

    //constants for XML preferences
    public static final String MYSTARTHOUR = "pref_myStartHour";
    public static final String MYSTARTMIN = "pref_myStartMin";
    public static final String MYENDHOUR = "pref_myEndHour";
    public static final String MYENDMIN = "pref_myEndMin";
    public static final String THEIRSTARTHOUR = "pref_theirStartHour";
    public static final String THEIRSTARTMIN = "pref_theirStartMin";
    public static final String THEIRENDHOUR = "pref_theirEndHour";
    public static final String THEIRENDMIN = "pref_theirEndMin";
    public static final String MYOFFSET = "pref_myOffset";
    public static final String THEIROFFSET = "pref_theirOffset";
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

            pieChart.setRotation(rotation);

            //set the time til the next run - in this case 500ms or half a second
            timerHandler.postDelayed(this, 500);
        }
    };

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

        //kick off the "timer" to update the rotation of the clock every half second
        timerHandler.postDelayed(timerRunnable, 0);
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

        myOffset = settings.getInt(MYOFFSET,0);
        theirOffset = settings.getInt(THEIROFFSET, 0);
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

        TextView textView = (TextView) findViewById(R.id.myTimeZone);
        textView.setText(deriveTimeZone(myOffset));

        textView = (TextView) findViewById(R.id.theirTimeZone);
        textView.setText(deriveTimeZone(theirOffset));
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

    public void changeOffset(View view) {
        //which button did we press?
        switch (view.getId()) {
            case R.id.myPlus:
                myOffset++;
                setXMLPreference(MYOFFSET, myOffset);
                break;
            case R.id.myMinus:
                myOffset--;
                setXMLPreference(MYOFFSET, myOffset);
                break;
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
        button = (Button) findViewById(R.id.myPlus);
        button.setEnabled(myOffset < 14);

        //myMinus
        button = (Button) findViewById(R.id.myMinus);
        button.setEnabled(myOffset > -12);

        //theirPlus
        button = (Button) findViewById(R.id.theirPlus);
        button.setEnabled(theirOffset < 14);

        //theirMinus
        button = (Button) findViewById(R.id.theirMinus);
        button.setEnabled(theirOffset > -12);

        setTimes();
    }
}
