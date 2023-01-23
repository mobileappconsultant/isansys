package com.isansys.pse_isansysportal;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

/**
 * Created by Neal on 16/08/2018.
 */

// This class extends the ScrollView of FragmentPatientVitalsDisplay to disable vertical scrolling when scrolling/scaling horizontally

// https://stackoverflow.com/questions/18893198/how-to-disable-and-enable-the-scrolling-on-android-scrollview

public class CustomScrollView extends ScrollView
{
    private boolean scrolling_enabled = true;


    public void setScrollingEnabled(boolean enableScrolling)
    {
        this.scrolling_enabled = enableScrolling;
    }


    public CustomScrollView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }


    public CustomScrollView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }


    public CustomScrollView(Context context)
    {
        super(context);
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev)
    {
        if (scrolling_enabled)
        {
            return super.onInterceptTouchEvent(ev);
        }
        else
        {
            return false;
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent ev)
    {
        if (scrolling_enabled)
        {
            return super.onTouchEvent(ev);
        }
        else
        {
            return false;
        }
    }
}

