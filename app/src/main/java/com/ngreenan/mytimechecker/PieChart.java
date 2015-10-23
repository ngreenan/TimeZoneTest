package com.ngreenan.mytimechecker;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Nick on 17/10/2015.
 */
public class PieChart extends View {

    private int percentage;
    private Paint paint = new Paint();
    private Path path = new Path();

    //RectFs
    private RectF rectOuter = new RectF();
    private RectF rectClock = new RectF();
    private RectF rectNumbers = new RectF();
    private RectF rectMe = new RectF();
    private RectF rectThem = new RectF();

    //RectF sizes
    private float outerThickness = 75F;
    private float meThickness = 40F;
    private float themThickness = 110F;
    private float numbersThickness = 200F;
    private float clockThickness = 160F;

    //drawing sizes
    private float ringThickness = 150F;
    private float arcThickness = 60F;
    private float tickThickness = 20F;
    private float textThickness = 50F;

    //time variables
    private int myStartTime = 0;
    private int myEndTime = 0;
    private int theirStartTime = 0;
    private int theirEndTime = 0;

    private Boolean crossOver = false;
    private int startCrossOver = 0;
    private int endCrossOver = 0;

    private float rotation = 0;
    private int size = 0;

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

    public void setRotation(float rotation) {
        this.rotation = rotation;
        invalidate();
    }

    public int getPercentage() {
        return percentage;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w < h) {
            size = w;
            setThicknesses();
            rectOuter.set(outerThickness,outerThickness,w-outerThickness,w-outerThickness);
            rectClock.set(clockThickness,clockThickness,w-clockThickness,w-clockThickness);
            rectNumbers.set(numbersThickness,numbersThickness,w-numbersThickness,w-numbersThickness);
            rectMe.set(meThickness,meThickness,w-meThickness,w-meThickness);
            rectThem.set(themThickness,themThickness,w-themThickness,w-themThickness);
        } else {
            size = h;
            setThicknesses();
            rectOuter.set(0,0,h,h);
            rectClock.set(clockThickness,clockThickness,h-clockThickness,h-clockThickness);
            rectNumbers.set(numbersThickness,numbersThickness,h-numbersThickness,h-numbersThickness);
            rectMe.set(meThickness,meThickness,h-meThickness,h-meThickness);
            rectThem.set(themThickness,themThickness,h-themThickness,h-themThickness);
        }
    }

    private void setThicknesses() {
        //rather than hard code our thicknesses, we're going to make them a set percentage of the overall width!
        outerThickness = 0.075F * (float)size;
        meThickness = 0.040F * (float)size;
        themThickness = 0.110F * (float)size;
        numbersThickness = 0.200F * (float)size;
        clockThickness = 0.160F * (float)size;

        ringThickness = 0.150F * (float)size;
        arcThickness = 0.060F * (float)size;
        tickThickness = 0.020F * (float)size;
        textThickness = 0.050F * (float)size;
    }

    private void getCrossOver() {
        //we need to work out the crossover between our two time periods
        //if indeed we have one
        //six different scenarios that i can think of...

        //CASE 1: overlap, my time first
        if (myStartTime <= theirStartTime && myEndTime <= theirEndTime && myEndTime >= theirStartTime) {
            crossOver = true;
            startCrossOver = theirStartTime;
            endCrossOver = myEndTime;
        }
        //CASE 2: overlap, their time first
        else if (theirStartTime <= myStartTime && theirEndTime <= myEndTime && theirEndTime >= myStartTime) {
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

        //setup all our values for start/finish angles
        float myTimeStart = (myStartTime * 360 / (24F * 60F));
        float myTimeEnd = (myEndTime * 360 / (24F * 60F));
        float theirTimeStart = (theirStartTime * 360 / (24F * 60F));
        float theirTimeEnd = (theirEndTime * 360 / (24F * 60F));
        float crossOverStart = (startCrossOver * 360F) / (24F * 60F);
        float crossOverEnd = (endCrossOver * 360F) / (24F * 60F);

        //rotate the whole canvas depending on the time
        canvas.rotate(rotation, (float) size / 2, (float) size / 2);

        //base ring
        //paint.setColor(Color.parseColor("#90CAF9"));
        paint.setColor(getResources().getColor(R.color.colorRing));
        paint.setStrokeWidth(ringThickness);
        paint.setStyle(Paint.Style.STROKE);
        path.reset();
        path.addArc(rectOuter, 0, 360);
        canvas.drawPath(path, paint);

        //cross over
//        if (crossOver) {
//            paint.setColor(Color.parseColor("#9C27B0"));
//            paint.setStrokeWidth(150);
//            paint.setStyle(Paint.Style.FILL_AND_STROKE);
//            path.reset();
//            path.addArc(rectOuter, crossOverStart - 90, crossOverEnd - crossOverStart);
//            canvas.drawPath(path, paint);
//        }

        //my time arc - blue
        //paint.setColor(Color.parseColor("#0D47A1"));
        paint.setColor(getResources().getColor(R.color.colorMe));
        paint.setStrokeWidth(arcThickness);
        paint.setStyle(Paint.Style.STROKE);
        path.reset();
        path.addArc(rectMe, myTimeStart - 90, myTimeEnd - myTimeStart);
        canvas.drawPath(path, paint);

        //their time arc - red
        //paint.setColor(Color.parseColor("#F44336"));
        paint.setColor(getResources().getColor(R.color.colorThem));
        paint.setStrokeWidth(arcThickness);
        paint.setStyle(Paint.Style.STROKE);
        path.reset();
        path.addArc(rectThem, theirTimeStart - 90, theirTimeEnd - theirTimeStart);
        canvas.drawPath(path, paint);

        //clock tick marks
        paint.setColor(Color.DKGRAY);
        paint.setStrokeWidth(tickThickness);
        paint.setStyle(Paint.Style.STROKE);
        path.reset();
        for (int x = 0; x < 360; x += 15) {
            path.addArc(rectClock,x-0.5F, 1);
        }
        canvas.drawPath(path, paint);

        //numbers on each tick mark
        paint.setColor(Color.DKGRAY);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(textThickness);
        paint.setStrokeWidth(1);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        for (int x = 0; x < 24; x++) {
            path.reset();
            path.addArc(rectNumbers, (x * 15) - 5F - 90F, 10F);
            canvas.drawTextOnPath(String.valueOf(x),path,0,20,paint);
        }
    }
}
