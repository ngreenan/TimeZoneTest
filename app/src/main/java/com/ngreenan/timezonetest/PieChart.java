package com.ngreenan.timezonetest;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by Nick on 17/10/2015.
 */
public class PieChart extends View {

    private int percentage;
    private Paint paint = new Paint();
    private RectF rectOuter = new RectF();
    private RectF rectInner = new RectF();
    private RectF rectClock = new RectF();
    private float ringThickness = 170F;
    private float clockThickness = 150F;

    private int myStartTime = 0;
    private int myEndTime = 0;
    private int theirStartTime = 0;
    private int theirEndTime = 0;

    private Boolean crossOver = false;
    private int startCrossOver = 0;
    private int endCrossOver = 0;


    public PieChart(Context context) {
        this(context, null);
    }

    public PieChart(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
    }

    public PieChart(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setPercentage(int percentage) {
        this.percentage = percentage;
        invalidate();
    }

    public void setMyStartTime(int hour, int minute) {
        myStartTime = (hour * 60) + minute;
        invalidate();
    }

    public void setMyEndTime(int hour, int minute) {
        myEndTime = (hour * 60) + minute;
        invalidate();
    }

    public void setTheirStartTime(int hour, int minute) {
        theirStartTime = (hour * 60) + minute;
        invalidate();
    }

    public void setTheirEndTime(int hour, int minute) {
        theirEndTime = (hour * 60) + minute;
        invalidate();
    }

    public int getPercentage() {
        return percentage;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w < h) {
            rectOuter.set(0,0,w,w);
            rectClock.set(clockThickness,clockThickness,w-clockThickness,w-clockThickness);
            rectInner.set(ringThickness,ringThickness,w-ringThickness,w-ringThickness);
        } else {
            rectOuter.set(0,0,h,h);
            rectClock.set(clockThickness,clockThickness,h-clockThickness,h-clockThickness);
            rectInner.set(ringThickness,ringThickness,h-ringThickness,h-ringThickness);
        }
    }

    private void getCrossOver()
    {
        //we need to work out the crossover between our two time periods
        //if indeed we have one
        //six different scenarios that i can think of...

        //CASE 1: overlap, my time first
        if (myStartTime <= theirStartTime && myEndTime <= theirEndTime) {
            crossOver = true;
            startCrossOver = theirStartTime;
            endCrossOver = myEndTime;
        }
        //CASE 2: overlap, their time first
        else if (theirStartTime <= myStartTime && theirEndTime <= myEndTime) {
            crossOver = true;
            startCrossOver = myStartTime;
            endCrossOver = theirEndTime;
        }
        //CASE 3: their time completely within my time
        else if (theirStartTime >= myStartTime && theirEndTime <= myEndTime) {
            crossOver = true;
            startCrossOver = theirStartTime;
            endCrossOver = theirEndTime;
        }
        //CASE 4: my time completely within their time
        else if (myStartTime >= theirStartTime && myEndTime <= theirEndTime) {
            crossOver = true;
            startCrossOver = myStartTime;
            endCrossOver = myEndTime;
        }
        //CASE 5: no crossover, my time earlier
        //CASE 6: no crossover, their time earlier
        else {
            crossOver = false;
            startCrossOver = 0;
            endCrossOver = 0;
        }
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //calculate the cross over period
        getCrossOver();

        float myTimeStart = (myStartTime * 360 / (24F * 60F));
        float myTimeEnd = (myEndTime * 360 / (24F * 60F));
        float theirTimeStart = (theirStartTime * 360 / (24F * 60F));
        float theirTimeEnd = (theirEndTime * 360 / (24F * 60F));

        float crossOverStart = (startCrossOver * 360F) / (24F * 60F);
        float crossOverEnd = (endCrossOver * 360F) / (24F * 60F);

        //canvas.drawColor(Color.WHITE);

        //base ring
        paint.setColor(Color.parseColor("#2196F3"));
        canvas.drawArc(rectOuter, 0, 360, true, paint);

        //my time arc - blue
        paint.setColor(Color.parseColor("#0D47A1"));
        canvas.drawArc(rectOuter, myTimeStart - 90, myTimeEnd - myTimeStart, true, paint);

        //their time arc - red
        paint.setColor(Color.parseColor("#F44336"));
        canvas.drawArc(rectOuter, theirTimeStart - 90, theirTimeEnd - theirTimeStart, true, paint);

        //cross over
        if (crossOver) {
            paint.setColor(Color.parseColor("#9C27B0"));
            canvas.drawArc(rectOuter, crossOverStart - 90, crossOverEnd - crossOverStart, true, paint);
        }

        //inner circle to make it a ring
        paint.setColor(Color.parseColor("#E3F2FD"));
        canvas.drawArc(rectClock, 0, 360, true, paint);

        paint.setColor(Color.BLACK);
        for (int x = 0; x <= 360; x += 15) {
            canvas.drawArc(rectClock, x-0.5F, 1, true, paint);
        }

        paint.setColor(Color.parseColor("#E3F2FD"));
        canvas.drawArc(rectInner, 0, 360, true, paint);

    }
}
