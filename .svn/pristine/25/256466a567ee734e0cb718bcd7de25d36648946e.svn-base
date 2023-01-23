package com.ajit.customseekbar;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.SeekBar;

public class CustomSeekBar extends SeekBar
{
    private ArrayList<ProgressItem> mProgressItemsList;

    private Paint textPaint;
    private int text_size_in_pixels;
    
    private Paint progressPaint;
    private Rect progressRect;
    
    
    private void initSeekBar()
    {
        // Hide the Thumb by making it invisible
        this.getThumb().mutate().setAlpha(0);

        
        // Create the Paint object used to draw text on screen
        textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        
        int text_size_in_density_pixels = 25; //50dp
        text_size_in_pixels = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, text_size_in_density_pixels, getResources().getDisplayMetrics());
        textPaint.setTextSize(text_size_in_pixels);
        
        // Create the Paint objects used to draw the blocks on the screen
        progressPaint = new Paint();
        progressRect = new Rect();
    }
    
    
    public CustomSeekBar(Context context)
    {
        super(context);
        initSeekBar();
    }

    public CustomSeekBar(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        initSeekBar();
    }

    public CustomSeekBar(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        initSeekBar();
    }

    public void initData(ArrayList<ProgressItem> progressItemsList)
    {
        this.mProgressItemsList = progressItemsList;
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }


    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        if (mProgressItemsList != null)
        {
            if (mProgressItemsList.size() > 0)
            {
                int progressBarWidth = getWidth();
                int progressBarHeight = getHeight();
    
                int lastProgressX = 0;
                int progressItemRight;
                int list_size = mProgressItemsList.size();

                for (int i = 0; i < list_size; i++)
                {
                    ProgressItem progressItem = mProgressItemsList.get(i);
                    
                    progressPaint.setColor(progressItem.color);
    
                    progressItemRight = (int) ((progressItem.progressItemPercentage * progressBarWidth) / 100);

                    // for last item give right to progress item to the width
                    if (i == mProgressItemsList.size() - 1 && progressItemRight != progressBarWidth)
                    {
                        progressItemRight = progressBarWidth;
                    }
                    
                    progressRect.set(lastProgressX, 0, progressItemRight, progressBarHeight - text_size_in_pixels);
                    canvas.drawRect(progressRect, progressPaint);
                    lastProgressX = progressItemRight;
                    
                    if (progressItem.label != null)
                    {
                        canvas.drawText(progressItem.label, lastProgressX, progressBarHeight - 10, textPaint);
                    }
                }
            }
        }
    }

}
