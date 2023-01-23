package com.isansys.pse_isansysportal;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import androidx.core.content.ContextCompat;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

// Code from https://github.com/mmoamenn/ProgressWindow_Android?utm_source=android-arsenal.com&utm_medium=referral&utm_campaign=6496

public class ProgressWindow
{
    private static ProgressWindow instance = null;
    private final Context mContext;

    private WindowManager windowManager;
    private WindowManager.LayoutParams layoutParams;
    private View progressLayout;
    private boolean isAttached;

    /**
     * Private constructor to single-tone class
     *
     * @param context caller context
     */
    private ProgressWindow(Context context)
    {
        mContext = context;
        setupView();
    }

    /**
     * Static method to avoid multi instance from dialog
     *
     * @param context caller context
     * @return dialog reference
     */
    public static ProgressWindow getInstance(Context context)
    {
        synchronized (ProgressWindow.class)
        {
            if (instance == null)
            {
                instance = new ProgressWindow(context);
            }
        }

        return instance;
    }


    /**
     * Function to setup component view
     */
    private void setupView()
    {
        progressLayout = LayoutInflater.from(mContext).inflate(R.layout.view_progress_window, null);

        progressLayout.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener()
        {
            @Override
            public void onViewAttachedToWindow(View v)
            {
                isAttached = true ;
            }

            @Override
            public void onViewDetachedFromWindow(View v)
            {
                isAttached = false ;
            }
        });

        ProgressBar mainProgress = progressLayout.findViewById(R.id.pb_main_progress);
        mainProgress.setIndeterminate(true);
        mainProgress.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(mContext, R.color.blue), android.graphics.PorterDuff.Mode.MULTIPLY);

        LinearLayout mainLayout = progressLayout.findViewById(R.id.ll_main_layout);
        mainLayout.setBackgroundColor(Color.TRANSPARENT);

        DisplayMetrics metrics = new DisplayMetrics();
        windowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(metrics);
        layoutParams = new WindowManager.LayoutParams(
                metrics.widthPixels / 2, metrics.heightPixels / 2,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
        );

        layoutParams.gravity = Gravity.CENTER;
    }


    /**
     * Function to show progress
     */
    public void showProgress()
    {
        if(isAttached == false)
        {
                windowManager.addView(progressLayout, layoutParams);
        }
    }

    /**
     * Function to hide progress
     */
    public void hideProgress()
    {
        if(isAttached)
        {
                windowManager.removeViewImmediate(progressLayout);
        }
    }
}
