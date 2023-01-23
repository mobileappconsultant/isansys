package roo.clockanimation;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.isansys.pse_isansysportal.R;

import org.joda.time.DateTime;

import static android.graphics.Paint.ANTI_ALIAS_FLAG;
import static android.graphics.Paint.Cap.ROUND;
import static android.graphics.Paint.Style.FILL;
import static android.graphics.Paint.Style.STROKE;
import static org.joda.time.Minutes.minutesBetween;

// Code originally from https://github.com/evelyne24/ClockDrawableAnimation with mods

public class ClockDrawable extends Drawable implements Animatable {

    private final static int ANIMATION_DURATION = 250;

    private static final int FACE_COLOR = android.R.color.white;
    private static final int RIM_COLOR = android.R.color.black;

    private final Paint facePaint;
    private final Paint rimPaint;
    private final ValueAnimator minAnimator;
    private final ValueAnimator hourAnimator;

    private float rimRadius;
    private float faceRadius;
    private float screwRadius;

    private final Path hourHandPath;
    private final Path minuteHandPath;

    private float remainingHourRotation = 0f;
    private float remainingMinRotation = 0f;

    private float targetHourRotation = 0f;
    private float targetMinRotation = 0f;

    private float currentHourRotation = 0f;
    private float currentMinRotation;

    private boolean hourAnimInterrupted;
    private boolean minAnimInterrupted;

    private DateTime previousTime;

    private boolean animateDays = false;

    private final static String TAG = ClockDrawable.class.getSimpleName();

    public ClockDrawable(Resources resources) {
        facePaint = new Paint(ANTI_ALIAS_FLAG);
        facePaint.setColor(resources.getColor(FACE_COLOR));
        facePaint.setStyle(FILL);

        rimPaint = new Paint(ANTI_ALIAS_FLAG);
        rimPaint.setColor(resources.getColor(RIM_COLOR));
        rimPaint.setStyle(STROKE);
        rimPaint.setStrokeCap(ROUND);
        rimPaint.setStrokeWidth(resources.getDimension(R.dimen.clock_stroke_width));

        hourHandPath = new Path();
        minuteHandPath = new Path();

        hourAnimator = ValueAnimator.ofFloat(0, 0);
        hourAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        hourAnimator.setDuration(ANIMATION_DURATION);
        hourAnimator.addUpdateListener(valueAnimator -> {
            float fraction = (float) valueAnimator.getAnimatedValue();
            remainingHourRotation = targetHourRotation - fraction;
            currentHourRotation = fraction;
            invalidateSelf();
        });
        hourAnimator.addListener(new AnimatorListenerAdapter() {
            @Override public void onAnimationEnd(Animator animation) {
                if (!hourAnimInterrupted) {
                    remainingHourRotation = 0f;
                }
            }
        });


        minAnimator = ValueAnimator.ofFloat(0, 0);
        minAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        minAnimator.setDuration(ANIMATION_DURATION);
        minAnimator.addUpdateListener(valueAnimator -> {
            float fraction = (float) valueAnimator.getAnimatedValue();
            remainingMinRotation = targetMinRotation - fraction;
            currentMinRotation = fraction;
            invalidateSelf();
        });
        minAnimator.addListener(new AnimatorListenerAdapter() {
            @Override public void onAnimationEnd(Animator animation) {
                if (!minAnimInterrupted) {
                    remainingMinRotation = 0f;
                }
            }
        });

        previousTime = DateTime.now().withTime(0, 0, 0, 0);
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);

        rimRadius = Math.min(bounds.width(), bounds.height()) / 2f - rimPaint.getStrokeWidth();
        faceRadius = rimRadius - rimPaint.getStrokeWidth();
        screwRadius = 0;//rimPaint.getStrokeWidth() * 2;
        float hourHandLength = (float) (0.6 * faceRadius);
        float minuteHandLength = (float) (0.9 * faceRadius);
        float top = bounds.centerY() - screwRadius;

        hourHandPath.reset();
        hourHandPath.moveTo(bounds.centerX(), bounds.centerY());
        hourHandPath.addRect(bounds.centerX(), top, bounds.centerX(), top - hourHandLength, Direction.CCW);
        hourHandPath.close();

        minuteHandPath.reset();
        minuteHandPath.moveTo(bounds.centerX(), bounds.centerY());
        minuteHandPath.addRect(bounds.centerX(), top, bounds.centerX(), top - minuteHandLength, Direction.CCW);
        minuteHandPath.close();
    }

    @Override public void draw(Canvas canvas) {
        Rect bounds = getBounds();

        // draw the outer rim of the clock
        canvas.drawCircle(bounds.centerX(), bounds.centerY(), rimRadius, rimPaint);
        // draw the face of the clock
        canvas.drawCircle(bounds.centerX(), bounds.centerY(), faceRadius, facePaint);
        // draw the little rim in the middle of the clock
        canvas.drawCircle(bounds.centerX(), bounds.centerY(), screwRadius, rimPaint);

        int saveCount = canvas.save();
        canvas.rotate(currentHourRotation, bounds.centerX(), bounds.centerY());
        // draw hour hand
        canvas.drawPath(hourHandPath, rimPaint);
        canvas.restoreToCount(saveCount);

        saveCount = canvas.save();
        canvas.rotate(currentMinRotation, bounds.centerX(), bounds.centerY());
        // draw minute hand
        canvas.drawPath(minuteHandPath, rimPaint);
        canvas.restoreToCount(saveCount);
    }

    @Override public void setAlpha(int alpha) {
        rimPaint.setAlpha(alpha);
        facePaint.setAlpha(alpha);
        invalidateSelf();
    }

    @Override public void setColorFilter(ColorFilter colorFilter) {
        rimPaint.setColorFilter(colorFilter);
        invalidateSelf();
    }

    @Override public int getOpacity() {
        return PixelFormat.OPAQUE;
    }

    @Override public void start() {
        hourAnimInterrupted = false;
        minAnimInterrupted = false;
        hourAnimator.start();
        minAnimator.start();
    }

    public void setAnimateDays(boolean animateDays) {
        this.animateDays = animateDays;
    }

    public void start(DateTime newTime) {

        Log.e(TAG, newTime.toString() + " : previousTime = " + previousTime.getMinuteOfDay() + " : newTime " + newTime.getMinuteOfDay());

        int minDiff = getMinsBetween(previousTime, newTime);

        // Isansys change. No point redrawing the clock if it hasnt changed minute
        if (minDiff != 0)
        {
            Log.e(TAG, "new minute detected : minDiff = " + minDiff);

            // 60min ... 360grade
            // minDif .. minDelta
            float minDeltaRotation = ((float) minDiff * 360f) / 60f;
            // 720min ... 360grade = 12h ... 360grade
            // minDif ... hourDelta
            float hourDeltaRotation = ((float) minDiff * 360f) / 720f;

            remainingMinRotation += minDeltaRotation;
            remainingHourRotation += hourDeltaRotation;

            if (isRunning()) {
                stop();
            }

            targetHourRotation = currentHourRotation + remainingHourRotation;
            hourAnimator.setFloatValues(currentHourRotation, targetHourRotation);

            targetMinRotation = currentMinRotation + remainingMinRotation;
            minAnimator.setFloatValues(currentMinRotation, targetMinRotation);

            start();

            previousTime = newTime;
        }
    }

    @Override public void stop() {
        hourAnimInterrupted = true;
        minAnimInterrupted = true;
        hourAnimator.cancel();
        minAnimator.cancel();
    }

    @Override public boolean isRunning() {
        return hourAnimator.isRunning() || minAnimator.isRunning();
    }

    private int getMinsBetween(DateTime t1, DateTime t2) {
        if(animateDays) {
            return minutesBetween(t1, t2).getMinutes();
        }
        return minutesBetween(t1, t2.withDate(t1.getYear(), t1.getMonthOfYear(), t1.getDayOfMonth())).getMinutes();
    }
}
