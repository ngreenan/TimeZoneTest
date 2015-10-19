package com.ngreenan.timezonetest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.os.Handler;
import android.widget.RelativeLayout;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private float rotation = 0F;
    private PieChart pieChart;
    private long startTime = 0;
    private float totalSeconds = (24F * 60F * 60F);

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

        //let's say i work from 9am to 6pm
        pieChart.setMyStartTime(9, 0);
        pieChart.setMyEndTime(18, 0);

        //and the other person works 12am to 12pm
        pieChart.setTheirStartTime(0, 0);
        pieChart.setTheirEndTime(12, 0);

        squareLinearLayout.addView(pieChart);

        timerHandler.postDelayed(timerRunnable, 0);
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
}
