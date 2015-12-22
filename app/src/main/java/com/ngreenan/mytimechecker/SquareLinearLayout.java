package com.ngreenan.mytimechecker;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * Created by Nick on 17/10/2015.
 */
public class SquareLinearLayout extends LinearLayout {

    public SquareLinearLayout(Context context) {
        super(context);
    }

    public SquareLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
//
//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        int width = getMeasuredWidth();
//        setMeasuredDimension(width, width);
//    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int w = getMeasuredWidth();
        int h = getMeasuredHeight();

        if (w < h) {
            //portrait
            //what if it's a really square shaped phone?
            //cap it at specified height

            float width = Float.parseFloat(String.valueOf(w));
            float height = Float.parseFloat(String.valueOf(h));
            int cappedHeightPercentage = getResources().getInteger(R.integer.heightCap);
            float cappedHeight = ((float)cappedHeightPercentage / 100F) * height;

            if (width > cappedHeight) {
                setMeasuredDimension((int) cappedHeight,(int) cappedHeight);
            } else {
                setMeasuredDimension(w, w);
            }
        } else {
            setMeasuredDimension(w, w);
        }
    }
}
