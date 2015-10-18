package com.ngreenan.timezonetest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.LinearLayout;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SquareLinearLayout squareLinearLayout = (SquareLinearLayout) findViewById(R.id.squareLayout);
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.layout);

        PieChart pieChart = new PieChart(this);

        //let's say i work from 9am to 6pm
        pieChart.setMyStartTime(9, 0);
        pieChart.setMyEndTime(18, 0);

        //and the other person works 12am to 12pm
        pieChart.setTheirStartTime(0, 0);
        pieChart.setTheirEndTime(12, 0);

        squareLinearLayout.addView(pieChart);
    }
}
