/*
    Copyright 2015 erz05

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
*/

package com.erz.timepicker_library;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class TimePicker extends View {

    private static final String STATE_PARENT = "parent";
    private static final String STATE_ANGLE = "angle";
    private static final String STATE_TEXT_COLOR = "textColor";
    private static final String STATE_CLOCK_COLOR = "clockColor";
    private static final String STATE_DIAL_COLOR = "dialColor";
    private static final String STATE_DISABLE_TOUCH = "disableTouch";

    private Paint paint;
    private RectF rectF;

    private float min;
    private float padding;
    private float radius;
    private float dialRadius;
    private float offset;
    private float slopX;
    private float slopY;
    private float dialX;
    private float dialY;
    private final static float secAngle = 360 / 12;

    private int hour;
    private int previousActualHour;
    private int minutes;
    private int tmp;
    private int previousHour;
    private int textColor = Color.BLACK;
    private int clockColor = Color.BLACK;
    private int dialColor = Color.BLACK;

    private double angle;
    private double degrees;

    private boolean isMoving;
    private boolean amPm;
    private boolean twentyFour;
    private boolean disableTouch;

    private boolean useDate;
    private boolean showDate;
    private boolean snapToNearestHour;

    private OnTimeChangedListener timeChangedListener;
    private CheckTimeWithinRangeListener checkTimeWithinRangeListener;

    private final Calendar calendar = Calendar.getInstance(TimeZone.getDefault());

    public interface OnTimeChangedListener {
        void timeChanged(Date date);
    }

    public interface CheckTimeWithinRangeListener {
        boolean timeInValidRange(Date date);
    }

    public TimePicker(Context context) {
        super(context);
        init(context, null);
    }

    public TimePicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        angle = (-Math.PI / 2) + .001;

        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setTextAlign(Paint.Align.CENTER);

        rectF = new RectF();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        float width = MeasureSpec.getSize(widthMeasureSpec);
        float height = MeasureSpec.getSize(heightMeasureSpec);

        min = Math.min(width, height);
        setMeasuredDimension((int) min, (int) min);

        offset = min * 0.5f;
        //padding = min / 20;
        padding = 35;           // MK changed to use EVERY available pixel
        radius = min / 2 - (padding * 2);
        //dialRadius = radius/5;
        dialRadius = radius/8;  // MK changed to make smaller - so dial can be bigger
        rectF.set(-radius, -radius, radius, radius);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.translate(offset, offset);

        paint.setStrokeWidth(1);
        paint.setStyle(Paint.Style.FILL);
        paint.setAlpha(255);

        // Save the Date/Time here in case an invalid time is set.
        Date previousTime = getTime();

        //Rad to Deg
        degrees = (Math.toDegrees(angle) + 90) % 360;
        degrees = (degrees + 360) % 360;

        if (snapToNearestHour) {
            int degrees_int = (int) degrees;
            degrees_int = ((degrees_int + 15) / 30) * 30;
            degrees = degrees_int;
            angle = Math.toRadians(degrees - 90);
            calculatePointerPosition(angle);
        }

        //get Hour
        hour = ((int) degrees / 30) % 12;
        if (hour == 0) hour = 12;

        //get Minutes
        minutes = ((int) (degrees * 2)) % 60;
        String mStr = (minutes < 10) ? "0" + minutes : minutes + "";

        //get AM/PM
        if ((hour == 12 && previousHour == 11) || (hour == 11 && previousHour == 12)) amPm = !amPm;
        String amPmStr = amPm ? "AM" : "PM";

        previousHour = hour;
        int actualHour = hour;
        if (amPm) {
            if (actualHour == 12) {
                actualHour = 0;
            }
        }
        else {
            if (actualHour < 12) {
                actualHour += 12;
            }
        }

        if (useDate) {
            if (actualHour == 0 && previousActualHour == 23) {
                calendar.add(Calendar.HOUR_OF_DAY, 24);
            }

            if (actualHour == 23 && previousActualHour == 0) {
                calendar.add(Calendar.HOUR_OF_DAY, -24);
            }
        }

        previousActualHour = actualHour;

        if (checkTimeWithinRangeListener.timeInValidRange(getTime()) == false) {
            setTime(previousTime);

            minutes = ((int)(degrees * 2)) % 60;
            mStr = (minutes < 10) ? "0" + minutes : minutes + "";
        }

        paint.setColor(textColor);
        paint.setTextSize(min / 5);
        String hStr;
        if (twentyFour) {
            tmp = hour;
            if (!amPm) {
                if (tmp < 12) tmp += 12;
            } else {
                if (tmp == 12) tmp = 0;
            }
            hStr = (tmp < 10) ? "0" + tmp : tmp + "";

            if (showDate) {
                canvas.drawText(hStr + ":" + mStr, 0, paint.getTextSize() / 4, paint);
                paint.setTextSize(min / 20);

                Date date = getTime();
                String dayOfWeek = DateFormat.format("EEEE", date).toString();
                String dayOfMonth = DateFormat.format("dd", date).toString();
                String month = DateFormat.format("MMMM", date).toString();
                String dateString = dayOfWeek + " " + dayOfMonth + " " + month;

                canvas.drawText(dateString, 0, paint.getTextSize() * 2, paint);
            }
            else {
                canvas.drawText(hStr + ":" + mStr, 0, paint.getTextSize() / 3, paint);
            }
        } else {
            hStr = (hour < 10) ? "0" + hour : hour + "";
            canvas.drawText(hStr + ":" + mStr, 0, paint.getTextSize() / 4, paint);
            paint.setTextSize(min / 10);
            canvas.drawText(amPmStr, 0, paint.getTextSize() * 2, paint);
        }

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(min / 30);
        paint.setColor(clockColor);
        canvas.drawOval(rectF, paint);

        int startAngle = 0;
        for (tmp = 0; tmp < 12; tmp++) {
            canvas.save();
            canvas.rotate(startAngle, 0, 0);
            canvas.drawLine(0, radius, 0, radius - padding, paint);
            canvas.restore();
            startAngle += secAngle;
        }

        if (!disableTouch) {
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(dialColor);
            //paint.setAlpha(100);

            calculatePointerPosition(angle);
            canvas.drawCircle(dialX, dialY, dialRadius, paint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event == null || disableTouch) return false;
        getParent().requestDisallowInterceptTouchEvent(true);

        float posX = event.getX() - offset;
        float posY = event.getY() - offset;

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                calculatePointerPosition(angle);
                if (posX >= (dialX - dialRadius) && posX <= (dialX + dialRadius)
                        && posY >= (dialY - dialRadius) && posY <= (dialY + dialRadius)) {

                    slopX = posX - dialX;
                    slopY = posY - dialY;
                    isMoving = true;
                    invalidate();
                } else {
                    getParent().requestDisallowInterceptTouchEvent(false);
                    return false;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (isMoving) {
                    angle = (float) Math.atan2(posY - slopY, posX - slopX);
                    if (timeChangedListener != null) {
                        timeChangedListener.timeChanged(getTime());
                    }
                    invalidate();
                } else {
                    getParent().requestDisallowInterceptTouchEvent(false);
                    return false;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                isMoving = false;
                invalidate();
                break;
        }
        return true;
    }

    private void calculatePointerPosition(double angle) {
        dialX = (float) (radius * Math.cos(angle));
        dialY = (float) (radius * Math.sin(angle));
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        Bundle state = new Bundle();

        state.putParcelable(STATE_PARENT, superState);
        state.putDouble(STATE_ANGLE, angle);
        state.putInt(STATE_CLOCK_COLOR, clockColor);
        state.putInt(STATE_DIAL_COLOR, dialColor);
        state.putInt(STATE_TEXT_COLOR, textColor);
        state.putBoolean(STATE_DISABLE_TOUCH, disableTouch);

        return state;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        Bundle savedState = (Bundle) state;
        Parcelable superState = savedState.getParcelable(STATE_PARENT);
        super.onRestoreInstanceState(superState);

        angle = savedState.getDouble(STATE_ANGLE);
        clockColor = savedState.getInt(STATE_CLOCK_COLOR);
        dialColor = savedState.getInt(STATE_DIAL_COLOR);
        textColor = savedState.getInt(STATE_TEXT_COLOR);
        disableTouch = savedState.getBoolean(STATE_DISABLE_TOUCH);
    }

    public void setColor(int color) {
        this.textColor = color;
        this.clockColor = color;
        this.dialColor = color;
        invalidate();
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
        invalidate();
    }

    public void setClockColor(int clockColor) {
        this.clockColor = clockColor;
        invalidate();
    }

    public void setDialColor(int dialColor) {
        this.dialColor = dialColor;
        invalidate();
    }

    public void enableTwentyFourHour(boolean twentyFour) {
        this.twentyFour = twentyFour;
        invalidate();
    }

    public void disableTouch(boolean disableTouch) {
        this.disableTouch = disableTouch;
    }

    public void useDate(boolean useDate) {
        this.useDate = useDate;
    }

    public void showDate(boolean showDate) {
        this.showDate = showDate;
    }

    public void snapToNearestHour(boolean snapToNearestHour) {
        this.snapToNearestHour = snapToNearestHour;
    }

    private Date getTime() {
        tmp = hour;
        if (!amPm) {
            if (tmp < 12) tmp += 12;
        } else {
            if (tmp == 12) tmp = 0;
        }

        calendar.set(Calendar.HOUR_OF_DAY, tmp);
        calendar.set(Calendar.MINUTE, minutes);

        // Isansys added
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        // End Isansys added

        return calendar.getTime();
    }

    public void setTime(Date date) {
        calendar.setTime(date);
        hour = calendar.get(Calendar.HOUR);
        minutes = calendar.get(Calendar.MINUTE);
        amPm = calendar.get(Calendar.AM_PM) == Calendar.AM;
        degrees = ((hour * 30) + 270) % 360;
        angle = Math.toRadians(degrees);
        degrees = ((double) minutes / 2);
        angle += Math.toRadians(degrees) + .001;
        invalidate();
    }

    public void setTimeChangedListener(OnTimeChangedListener timeChangedListener) {
        this.timeChangedListener = timeChangedListener;
    }

    public void setCheckTimeWithinRangeListener(CheckTimeWithinRangeListener checkTimeWithinRangeListener ) {
        this.checkTimeWithinRangeListener = checkTimeWithinRangeListener;
    }
}
