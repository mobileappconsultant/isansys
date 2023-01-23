package com.ajit.customseekbar;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.SeekBar;

public class CustomSeekBarVertical extends SeekBar
{
    private ArrayList<ProgressItem> threshold_items_list;

    private Paint paint;
    private Rect rectangle;
    
    
    private void initSeekBar()
    {
        // Hide the Thumb by making it invisible
        this.getThumb().mutate().setAlpha(0);

        // Create the Paint objects used to draw the blocks on the screen
        paint = new Paint();
        rectangle = new Rect();
    }
    
    
    public CustomSeekBarVertical(Context context)
    {
        super(context);
        initSeekBar();
    }

    public CustomSeekBarVertical(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        initSeekBar();
    }

    public CustomSeekBarVertical(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        initSeekBar();
    }

    public void initData(ArrayList<ProgressItem> threshold_items_list)
    {
        this.threshold_items_list = threshold_items_list;
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }


    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        if (threshold_items_list != null)
        {
            if (threshold_items_list.size() > 0)
            {
                int threshold_display_height = getHeight();
                
                int threshold_item_start_point = 0;
                int threshold_item_stop_point;

                int left = 0;
                int right = getWidth();

                int top;
                int bottom;

                int list_size = threshold_items_list.size();

                for (int i = 0; i < list_size; i++)
                {
                    ProgressItem threshold_item = threshold_items_list.get(i);
                    paint.setColor(threshold_item.color);

                    threshold_item_stop_point = (int) ((threshold_item.progressItemPercentage * threshold_display_height) / 100);

                    // Catch the "Leftover 1000%" catch all value and replace it with a sensible one
                    if (i == threshold_items_list.size() - 1 && threshold_item_stop_point != threshold_display_height)
                    {
                        threshold_item_stop_point = threshold_display_height;
                    }

                    top = threshold_item_start_point;
                    bottom = threshold_item_stop_point;
                    
                    rectangle.set(left, top, right, bottom);
                    
                    //Log.e(TAG, "Rect : " + String.valueOf(rectangle.bottom) + " " + String.valueOf(rectangle.left) + " " + String.valueOf(rectangle.right) + " " + String.valueOf(rectangle.top));
                    
                    canvas.drawRect(rectangle, paint);
                    
                    threshold_item_start_point = threshold_item_stop_point;
                }
            }
        }
    }
}
