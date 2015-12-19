package com.ngreenan.mytimechecker;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.ngreenan.mytimechecker.model.Person;
import com.ngreenan.mytimechecker.model.PersonDetail;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nick on 17/10/2015.
 */
public class PieChart extends View {

    private Context context;

    private int percentage;
    private Paint paint = new Paint();
    private Path path = new Path();
    private int landscapeTop = 20;

    //RectFs
    private RectF rectOuter = new RectF();
    private RectF rectClock = new RectF();
    private RectF rectNumbers = new RectF();
    //private RectF rectMe = new RectF();
    //private RectF rectThem = new RectF();
    private List<RectF> rectPeople = new ArrayList<RectF>();

    //RectF sizes
    private float outerThickness = 75F;
    //private float meThickness = 40F;
    //private float themThickness = 110F;
    private float numbersThickness = 200F;
    private float clockThickness = 160F;

    //drawing sizes
    private float ringThickness = 150F;
    private float arcThickness = 60F;
    private float tickThickness = 20F;
    private float textThickness = 50F;

    private float spacerThickness = 0F;
    private float personThickness = 0F;

    //time variables
    //private int myStartTime = 0;
    //private int myEndTime = 0;
    //private int theirStartTime = 0;
    //private int theirEndTime = 0;

    private List<PersonDetail> personDetails = new ArrayList<PersonDetail>();

    private float rotation = 0;
    private int size = 0;

    public PieChart(Context context) {
        this(context, null);
    }

    public PieChart(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        this.context = context;
    }

    public PieChart(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }

    public void setPersonDetails(List<PersonDetail> personDetails) {
        this.personDetails = personDetails;
    }

    public List<PersonDetail> getPersonDetails() {
        return this.personDetails;
    }

    public void setPercentage(int percentage) {
        this.percentage = percentage;
        invalidate();
    }

//    public void setMyStartTime(int hour, int minute) {
//        myStartTime = (hour * 60) + minute;
//        invalidate();
//    }
//
//    public void setMyEndTime(int hour, int minute) {
//        myEndTime = (hour * 60) + minute;
//        invalidate();
//    }
//
//    public void setTheirStartTime(int hour, int minute) {
//        theirStartTime = (hour * 60) + minute;
//        invalidate();
//    }
//
//    public void setTheirEndTime(int hour, int minute) {
//        theirEndTime = (hour * 60) + minute;
//        invalidate();
//    }

    public void setRotation(float rotation) {
        this.rotation = rotation;
        invalidate();
    }

    public int getPercentage() {
        return percentage;
    }

//    public int getMyStartTime() {
//        return myStartTime;
//    }
//
//    public int getMyEndTime() {
//        return myEndTime;
//    }
//
//    public int getTheirStartTime() {
//        return theirStartTime;
//    }
//
//    public int getTheirEndTime() {
//        return theirEndTime;
//    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w < h) {
            size = w;
            setThicknesses();
            rectOuter.set(outerThickness,outerThickness,w-outerThickness,w-outerThickness);
            rectClock.set(clockThickness,clockThickness,w-clockThickness,w-clockThickness);
            rectNumbers.set(numbersThickness,numbersThickness,w-numbersThickness,w-numbersThickness);
            //rectMe.set(meThickness,meThickness,w-meThickness,w-meThickness);
            //rectThem.set(themThickness,themThickness,w-themThickness,w-themThickness);

            //now calculate the person RectFs
            for (int i = 1; i <= personDetails.size(); i++) {
                RectF rect = rectPeople.get(i - 1);
                float thickness = getThickness(i);
                rect.set(thickness, thickness, w-thickness, w-thickness);
            }

        } else {
            size = h;
            setThicknesses();

            float sidePadding = ((float)w - (float)h) / 2;

//            rectOuter.set(outerThickness + sidePadding,outerThickness,w-outerThickness - sidePadding,h-outerThickness);
//            rectClock.set(clockThickness + sidePadding,clockThickness,w-clockThickness- sidePadding,h-clockThickness);
//            rectNumbers.set(numbersThickness + sidePadding,numbersThickness,w-numbersThickness- sidePadding,h-numbersThickness);
//            rectMe.set(meThickness + sidePadding,meThickness,w-meThickness - sidePadding,h-meThickness);
//            rectThem.set(themThickness + sidePadding,themThickness,w-themThickness - sidePadding,h-themThickness);

            rectOuter.set(outerThickness,outerThickness,h-outerThickness,h-outerThickness);
            rectClock.set(clockThickness,clockThickness,h-clockThickness,h-clockThickness);
            rectNumbers.set(numbersThickness,numbersThickness,h-numbersThickness,h-numbersThickness);
            //rectMe.set(meThickness,meThickness,h-meThickness,h-meThickness);
            //rectThem.set(themThickness,themThickness,h-themThickness,h-themThickness);

            //now calculate the person RectFs
            for (int i = 1; i <= personDetails.size(); i++) {
                RectF rect = rectPeople.get(i - 1);
                float thickness = getThickness(i);
                rect.set(thickness, thickness, h-thickness, h-thickness);
            }
        }
    }

    private float getThickness(int i) {
        //return ((i+1) * 0.5F * spacerThickness) + ((i - 1) * personThickness) + (0.5F * personThickness);
        return (i * spacerThickness) + ((i - 0.5F) * personThickness);
    }

    private void setThicknesses() {
        //rather than hard code our thicknesses, we're going to make them a set percentage of the overall width!

        //v1 - we also don't have a fixed number of people - we'll declare a list of RectFs instead
        for (int i = 0; i < personDetails.size(); i++) {
            rectPeople.add(new RectF());
        }

        outerThickness = 0.075F * (float)size;
        //meThickness = 0.040F * (float)size;
        //themThickness = 0.110F * (float)size;
        numbersThickness = 0.200F * (float)size;
        clockThickness = 0.160F * (float)size;

        ringThickness = 0.150F * (float)size;
        arcThickness = 0.060F * (float)size;
        tickThickness = 0.020F * (float)size;
        textThickness = 0.050F * (float)size;

        spacerThickness = (ringThickness * 0.2F) / (personDetails.size() + 1);
        personThickness = (ringThickness * 0.8F) / (personDetails.size());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int canvasWidth = canvas.getWidth();
        int canvasHeight = canvas.getHeight();

        if (canvasWidth > canvasHeight) {
            float shift = ((float)canvasWidth - (float)canvasHeight) / 2;
            canvas.translate(shift, 0F);
        }

        //rotate the whole canvas depending on the time
        canvas.rotate(rotation, (float) size / 2, (float) size / 2);

        //base ring
        //paint.setColor(Color.parseColor("#90CAF9"));
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(getResources().getColor(R.color.colorRing));
        paint.setStrokeWidth(ringThickness);
        paint.setStyle(Paint.Style.STROKE);
        path.reset();
        path.addArc(rectOuter, 0, 360);
        canvas.drawPath(path, paint);

//        //my time arc - blue
//        paint.setColor(getResources().getColor(R.color.colorMe));
//        paint.setStrokeWidth(arcThickness);
//        paint.setStyle(Paint.Style.STROKE);
//        path.reset();
//
//        if (myTimeEnd >= myTimeStart) {
//            path.addArc(rectMe, myTimeStart - 90, myTimeEnd - myTimeStart);
//        } else {
//            path.addArc(rectMe, myTimeStart - 90, 360F + myTimeEnd - myTimeStart);
//        }
//
//        canvas.drawPath(path, paint);
//
//        //their time arc - red
//        paint.setColor(getResources().getColor(R.color.colorThem));
//        paint.setStrokeWidth(arcThickness);
//        paint.setStyle(Paint.Style.STROKE);
//        path.reset();
//
//        if (theirTimeEnd >= theirTimeStart) {
//            path.addArc(rectThem, theirTimeStart - 90, theirTimeEnd - theirTimeStart);
//        } else {
//            path.addArc(rectThem, theirTimeStart - 90, 360F + theirTimeEnd - theirTimeStart);
//        }
//
//        canvas.drawPath(path, paint);

        //plot arc for each person
        for (int i = 0; i < personDetails.size(); i++) {
            PersonDetail person = personDetails.get(i);

            //check that we have a CityID - if not we don't want to draw anything
            if (person.getCityID() == 0) {
                continue;
            }

            //get offset for this timezone
            float offset = Util.getHourOffsetFromTimeZone(person.getTimeZoneName());
            float offsetRotation = offset / 24;

            //setup all our values for start/finish angles
            float timeStart = ((((person.getStartHour() - offset) * 60) + person.getStartMin()) * 360 / (24F * 60F));
            float timeEnd = ((((person.getEndHour() - offset) * 60) + person.getEndMin()) * 360 / (24F * 60F));

            int colorID = context.getResources().getIdentifier("color" + person.getColorID(), "color", context.getPackageName());

            if (colorID != 0) {
                paint.setColor(context.getResources().getColor(colorID));
            } else {
                paint.setColor(getResources().getColor(R.color.color1));
            }

            paint.setStrokeWidth(personThickness);
            paint.setStyle(Paint.Style.STROKE);
            path.reset();

            RectF rect = rectPeople.get(i);
            if (timeEnd >= timeStart) {
                path.addArc(rect, timeStart - 90, timeEnd - timeStart);
            } else {
                path.addArc(rect, timeStart - 90, 360F + timeEnd - timeStart);
            }

            //draw path
            canvas.drawPath(path, paint);
        }

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
