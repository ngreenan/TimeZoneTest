package com.ngreenan.mytimechecker;

import android.app.TimePickerDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.os.Handler;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TimePicker;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private float rotation = 0F;
    private PieChart pieChart;
    private long startTime = 0;
    private float totalSeconds = (24F * 60F * 60F);

    private int myStartHour = 9;
    private int myStartMin = 0;
    private int myEndHour = 18;
    private int myEndMin = 0;
    private int theirStartHour = 0;
    private int theirStartMin = 0;
    private int theirEndHour = 12;
    private int theirEndMin = 0;

    //runs without a timer by reposting this handler at the end of the runnable
    Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {

        @Override
        public void run() {
            Calendar c = Calendar.getInstance();
            float seconds = (c.get(Calendar.HOUR_OF_DAY) * 60 * 60)
                    + (c.get(Calendar.MINUTE) * 60)
                    + c.get(Calendar.SECOND);

            rotation = ((0 - seconds) / totalSeconds) * 360;

            pieChart.setRotation(rotation);

            timerHandler.postDelayed(this, 500);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SquareLinearLayout squareLinearLayout = (SquareLinearLayout) findViewById(R.id.squareLayout);
        RelativeLayout linearLayout = (RelativeLayout) findViewById(R.id.layout);

        pieChart = new PieChart(this);
        setTimes();

        squareLinearLayout.addView(pieChart);

        timerHandler.postDelayed(timerRunnable, 0);
    }

    private void setTimes() {
        //set my times
        pieChart.setMyStartTime(myStartHour, myStartMin);
        pieChart.setMyEndTime(myEndHour, myEndMin);

        //set their times
        pieChart.setTheirStartTime(theirStartHour, theirStartMin);
        pieChart.setTheirEndTime(theirEndHour, theirEndMin);

        //set button text
        Button button = (Button) findViewById(R.id.myStartButton);
        button.setText(String.format("%02d", myStartHour) + ":" + String.format("%02d", myStartMin));

        button = (Button) findViewById(R.id.myEndButton);
        button.setText(String.format("%02d", myEndHour) + ":" + String.format("%02d", myEndMin));

        button = (Button) findViewById(R.id.theirStartButton);
        button.setText(String.format("%02d", theirStartHour) + ":" + String.format("%02d", theirStartMin));

        button = (Button) findViewById(R.id.theirEndButton);
        button.setText(String.format("%02d", theirEndHour) + ":" + String.format("%02d", theirEndMin));
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
        //Calendar currentDate = Calendar.getInstance();
        int hour = 0;
        int minute = 0;

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

        TimePickerDialog timePickerDialog;
        timePickerDialog = new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                switch (view.getId()) {
                    case R.id.myStartButton:
                        myStartHour = selectedHour;
                        myStartMin = selectedMinute;
                        break;
                    case R.id.myEndButton:
                        myEndHour = selectedHour;
                        myEndMin = selectedMinute;
                        break;
                    case R.id.theirStartButton:
                        theirStartHour = selectedHour;
                        theirStartMin = selectedMinute;
                        break;
                    case R.id.theirEndButton:
                        theirEndHour = selectedHour;
                        theirEndMin = selectedMinute;
                        break;
                }

                setTimes();
            }
        }, hour, minute, true);
        timePickerDialog.setTitle("Select Time");
        timePickerDialog.show();
    }
}
