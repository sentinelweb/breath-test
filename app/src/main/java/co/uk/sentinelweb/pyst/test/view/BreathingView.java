package co.uk.sentinelweb.pyst.test.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.View;


import java.util.ArrayList;

import sentinelweb.uk.co.pyst.test.R;

/**
 * A view that displays breathing patterns based on rise/fall times
 * All times are ms
 * Created by robert on 15/04/2015.
 */
public class BreathingView extends View {
    public static final int MS = 1000;

    public static final long DEFAULT_HOROZONTAL_TIME = 20*MS;
    public static final long DEFAULT_RISE_TIME = 3*MS;
    public static final long DEFAULT_FALL_TIME = 5*MS;
    /** Screen width in time */
    private long mHorizontalTime = DEFAULT_HOROZONTAL_TIME;
    /** the rise time */
    private long mRiseTime = DEFAULT_RISE_TIME;
    /** the fall time */
    private long mFallTime = DEFAULT_FALL_TIME;
    /** the axis color */
    private int mAxisColor;
    /** the curve color */
    private int mCurveColor;
    /** the dot color */
    private int mDotColor;
    /** the breath dot color */
    private int mBreathColor;

    // timing
    boolean update = true;
    int updateTime = 20;
    /** ui timing handler */
    private Handler mHandler;

    /** drawing stuff */
    private Paint mAxisPaint;
    /** the curve */
    private Paint mCurvePaint;
    /** the rising / falling dot */
    private Paint mDotPaint;
    /** draw breath points */
    private Paint mBreathPaint;

    /** the curve height (px) */
    private int mCurveAmplitude;
    /** Path object to draw the curve*/
    Path curvePath;
    float density = 1;
//    private float mCubicOffsetPx;
//    private int mCubicOffset;
    //PathInterpolator mDotInterploator;

    /**
     * Represents a breath change (inflexion)
     */
    private static class Breath {
        /** time */
        private Long time;
        /** is a rise i.e. going up next */
        private Boolean rise;

        public Breath(Boolean rise, Long time) {
            this.rise = rise;
            this.time = time;
        }

        public Boolean isRise() {return rise;}

        public Long getTime() {return time;}

        public void setTime(Long time) {
            this.time = time;
        }

    }
    /** list of current points a time and rise(true) / fall(false) change are stored*/
    ArrayList<Breath> mBreathChanges = new ArrayList<>();

    /** Constructor */
    public BreathingView(final Context context) {
        super(context);
        init(context, null, 0, 0);
    }

    public BreathingView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0, 0);
    }

    public BreathingView(final Context context, final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr, 0);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public BreathingView(final Context context, final AttributeSet attrs, final int defStyleAttr, final int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr, defStyleRes);

    }

    /**
     * Initialise the view.
     *
     * @param context
     * @param attrs
     * @param defStyleAttr
     * @param defStyleRes
     */
    private void init(final Context context, final AttributeSet attrs, final int defStyleAttr, final int defStyleRes) {
//        LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        layoutInflater.inflate(R.layout.view_user_header, this);
        density = getResources().getDisplayMetrics().density;
//        setCubicOffsets();
        mCurveColor = getResources().getColor(R.color.blue_900);
        mAxisColor = getResources().getColor(R.color.grey_300);
        mDotColor = getResources().getColor(R.color.orange_700);
        mBreathColor = getResources().getColor(R.color.red_600);
        mHandler=new Handler();
        mAxisPaint = new Paint();
        mAxisPaint.setStrokeWidth(1*density);
        mAxisPaint.setColor(mAxisColor);
        mAxisPaint.setStyle(Paint.Style.STROKE);
        mCurvePaint = new Paint(mAxisPaint);
        mCurvePaint.setColor(mCurveColor);
        mBreathPaint = new Paint(mAxisPaint);
        mBreathPaint.setColor(mBreathColor);
        mBreathPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mDotPaint = new Paint(mAxisPaint);
        mDotPaint.setColor(mDotColor);
        mDotPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        curvePath = new Path();
        //mDotInterploator = new PathInterpolator(curvePath);
    }

//    private void setCubicOffsets() {
//        mCubicOffset = getMeasuredWidth()*mHorizontalTime;
//        mCubicOffsetPx = mCubicOffset * density;
//    }

    /**
     * Checking for the breath changes array
     * Remove old item
     * adds new item as needed to display in the current time window
     * remove items after the first that are past the rhs of the screen
     */
    private void checkBreaths() {
        // now
        final long now = SystemClock.uptimeMillis();
        //lowest time to display
        final long lowestTime = now - mHorizontalTime/2;
        //highest time to display
        final long highestTime = lowestTime+mHorizontalTime;
        //we can clear Points we need to keep on point past the lowest that can be displayed.
        int lowestDisplayed = -1;
        int highestDisplayed = -1;
        for (int i = 0;i< mBreathChanges.size();i++) {
            Breath breathChange = mBreathChanges.get(i);
            if (lowestDisplayed==-1 && breathChange.time>lowestTime) {
                lowestDisplayed = i;
            }
        }
        // remove all items below lowest displayed
        while (lowestDisplayed>2) {
            mBreathChanges.remove(0);
            lowestDisplayed--;
        }
        // if the array is uninitialised we add a breath now and the rest will fill in
        if (mBreathChanges.size()==0) {
            mBreathChanges.add(new Breath(true,now));
        }
        // add more record as required
        Breath highest = mBreathChanges.get(mBreathChanges.size()-1);
        while (highest.time<highestTime) {
            if (highest.isRise()){
                // add fall
                highest = new Breath(false, highest.time+mFallTime);
            } else {
                //add rise
                highest = new Breath(true, highest.time+mRiseTime);
            }
            mBreathChanges.add(highest);
        }
        // at this point highest is the first highest point off screen (on right)
        // we remove entries that are in front of this as they arent needed
        int index = mBreathChanges.indexOf(highest);
        while (index+1<mBreathChanges.size()) {
            mBreathChanges.remove(index+1);
        }
    }

    /**
     * Used to remove future points from the breathChanges array after a rise/fall time change
     * new values will be created when the array is checked
     */
    private void clearFutureBreaths() {
        final long now = SystemClock.uptimeMillis();
        for (int i=mBreathChanges.size()-1;i>=0;i--) {
            if (mBreathChanges.get(i).getTime()>now) {
                mBreathChanges.remove(i);
            }else {
                break;
            }
        }
    }

    @Override
    protected void onSizeChanged(final int w, final int h, final int oldw, final int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        // set the curve height
        mCurveAmplitude = h/3;
        //setCubicOffsets();
    }

    @Override
    protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    //re-used points and breaths
    final PointF mCurrentPoint = new PointF();
    final PointF mNowPoint = new PointF();
    final PointF mLastPoint = new PointF();
    // the scale ms/px (x) and the time offset(y)
    final PointF mScaleAndOffset = new PointF();
    final PointF mDotPoint = new PointF();
    /** reused control points */
    final PointF c1 = new PointF();
    final PointF c2 = new PointF();
    /** Special breath set at now used to calc the dot position */
    final Breath nowBreath = new Breath(null,SystemClock.uptimeMillis());
    @Override
    protected void onDraw(final Canvas canvas) {
        checkBreaths();
        super.onDraw(canvas);// draws bg

        // draw axis.
        canvas.drawLine(getMeasuredWidth()/2,0,getMeasuredWidth()/2,getMeasuredHeight(), mAxisPaint);
        canvas.drawLine(0, getMeasuredHeight() / 2, getMeasuredWidth(), getMeasuredHeight() / 2, mAxisPaint);
        // make the path to plot
        makePath(null);
        canvas.drawPath(curvePath, mCurvePaint);

        // get the window scaling and offset
        getScaleAndOffset(mScaleAndOffset);

        // draw breath inflexion points
        for (Breath b : mBreathChanges) {
            getBreathPointOnScreen(mScaleAndOffset,mCurrentPoint,b);
            canvas.drawCircle(mCurrentPoint.x,mCurrentPoint.y,2*density,mBreathPaint);
        }
        // get the next and last breath to plot the dot
        Breath lastBreath = null;
        Breath nextBreath = null;
        boolean lower=true;
        final long now = SystemClock.uptimeMillis();
        for (int i=1;i<mBreathChanges.size();i++) {
            if (lower && mBreathChanges.get(i).getTime()>now) {
                lastBreath = mBreathChanges.get(i-1);
                nextBreath = mBreathChanges.get(i);
                break;
            }
        }
        // plot the dot
        if (lastBreath!=null && nextBreath!=null) {
            // set now, next and last points from nowBreath, nextbreath and lastBreath respectively
            getBreathPointOnScreen(mScaleAndOffset,mCurrentPoint,nextBreath);
            getBreathPointOnScreen(mScaleAndOffset, mLastPoint, lastBreath);
            nowBreath.setTime(now);
            getBreathPointOnScreen(mScaleAndOffset, mNowPoint, nowBreath);
            // calculate the ration by liner interpolation of x coordinates
            float ratio = (mNowPoint.x-mLastPoint.x)/(mCurrentPoint.x-mLastPoint.x);
            setControlPoints(mCurrentPoint, mLastPoint);
            // calculate the dot point
            calculateBezierPoint(ratio, mLastPoint, c1, c2, mCurrentPoint, mDotPoint);
            // now the point is in mDotPoint we just need y
            //draw next and last breath outlines
            mDotPaint.setStyle(Paint.Style.STROKE);
            canvas.drawCircle(mCurrentPoint.x, mCurrentPoint.y, 10 * density, mDotPaint);
            canvas.drawCircle(mLastPoint.x, mLastPoint.y, 10*density, mDotPaint);
            // paint the dot
            mDotPaint.setStyle(Paint.Style.FILL_AND_STROKE);
            canvas.drawCircle(getMeasuredWidth()/2, mDotPoint.y, 20*density, mDotPaint);
        }
        // check to post an update.
        if (update) {
            mHandler.postDelayed(new Runnable() {@Override public void run() { invalidate(); } }, updateTime);
        }
    }

    /**
     * Makes the path to plot.
     * @param canvas used for testing (plot beizer comtrol points)
     */
    private void makePath(final Canvas canvas) {
        curvePath.reset();

        for (int i=0;i< mBreathChanges.size();i++) {
            final Breath b = mBreathChanges.get(i);
            getBreathPointOnScreen(mScaleAndOffset, mCurrentPoint, b);
            //Log.d("path", i + ". " + mCurrentPoint.x + " x " + mCurrentPoint.y + " ms:" + scaleAndOffsetMs.y + " rise:" + b.isRise());
            if (i==0) {
                curvePath.moveTo(mCurrentPoint.x, mCurrentPoint.y);
            } else {
                setControlPoints(mCurrentPoint, mLastPoint);
                curvePath.cubicTo( c1.x, c1.y, c2.x, c2.y, mCurrentPoint.x, mCurrentPoint.y );//,
                if (canvas!=null) {
                    canvas.drawCircle(c1.x, c1.y, 2 * density, mAxisPaint);
                    canvas.drawLine(mLastPoint.x, mLastPoint.y, c1.x, c1.y, mAxisPaint);
                    canvas.drawCircle(c2.x, c2.y, 2 * density, mDotPaint);
                    canvas.drawLine(mCurrentPoint.x, mCurrentPoint.y, c2.x, c2.y, mDotPaint);
                }

            }
            mLastPoint.set(mCurrentPoint);
        }
    }

    private void setControlPoints(PointF mCurrentPoint, PointF mLastPoint) {
        final float cubicOffset = (mCurrentPoint.x - mLastPoint.x) /2;
        final float c1x = mLastPoint.x + cubicOffset/*mCubicOffsetPx*/;
        final float c1y = mLastPoint.y;
        final float c2x = mCurrentPoint.x - cubicOffset;
        final float c2y = mCurrentPoint.y;
        c1.set(c1x,c1y);
        c2.set(c2x,c2y);
    }

    /**
     * Calculates time scale and offset - the point is used to store data but it is NOT a coordinate
     * TODO reuse point
     * @return scale(x) , offsetMs (y)
     */
    private PointF getScaleAndOffset(PointF scaleAndOffsetMs) {
        final long now = SystemClock.uptimeMillis();
        scaleAndOffsetMs.x = getMeasuredWidth()/(float)mHorizontalTime;// px/ms
        scaleAndOffsetMs.y = now - mHorizontalTime/2;
        //Log.d("path", "start: sc:" + scaleAndOffsetMs.x + ":" + scaleAndOffsetMs.y);
        return scaleAndOffsetMs;
    }

    /**
     * Get the point on the screen of the supplied breath
     * since y is arbitrary if rise is not supplied y coord is not calculated.
     * @param scaleAndOffsetMs
     * @param p
     * @param b
     */
    private void getBreathPointOnScreen(final PointF scaleAndOffsetMs, final PointF p,  final Breath b) {
        final float msFromLeft = b.getTime() - scaleAndOffsetMs.y;
        p.x = msFromLeft * scaleAndOffsetMs.x /**/;// ms * px/ms = px
        if (b.isRise()!=null) p.y = getMeasuredHeight()/2+(b.isRise() ? -mCurveAmplitude : mCurveAmplitude);
    }

    /**
     * from: http://stackoverflow.com/questions/9494167/move-an-object-on-on-a-b%C3%A9zier-curve-path
     * calculates the beizer point at interval t [0..1]
     *
     * t is time(value of 0.0f-1.0f; 0 is the start 1 is the end)
     */
    PointF calculateBezierPoint(float t, PointF s, PointF c1, PointF c2, PointF e, PointF target) {
        final float u = 1 - t;
        final float tt = t*t;
        final float uu = u*u;
        final float uuu = uu * u;
        final float ttt = tt * t;

        target.set((int) (s.x * uuu), (int) (s.y * uuu));
        target.x += 3 * uu * t * c1.x;
        target.y += 3 * uu * t * c1.y;
        target.x += 3 * u * tt * c2.x;
        target.y += 3 * u * tt * c2.y;
        target.x += ttt * e.x;
        target.y += ttt * e.y;

        return target;
    }

    public int getAxisColor() {
        return mAxisColor;
    }

    public void setAxisColor(int axisColor) {
        mAxisColor = axisColor;
    }

    public int getCurveColor() {
        return mCurveColor;
    }

    public void setCurveColor(int curveColor) {
        mCurveColor = curveColor;
    }

    public int getCurveAmplitude() {
        return mCurveAmplitude;
    }

    public void setCurveAmplitude(int curveAmplitude) {
        mCurveAmplitude = curveAmplitude;
    }

    public int getDotColor() {
        return mDotColor;
    }

    public void setDotColor(int dotColor) {
        mDotColor = dotColor;
    }

    public long getFallTime() {
        return mFallTime;
    }

    public void setFallTime(long fallTime) {
        mFallTime = Math.max(fallTime,100);
        clearFutureBreaths();
    }

    public long getRiseTime() {
        return mRiseTime;
    }

    public void setRiseTime(long riseTime) {
        mRiseTime = Math.max(riseTime,100);;
        clearFutureBreaths();
    }

    public long getHorizontalTime() {
        return mHorizontalTime;
    }

    public void setHorizontalTime(long horizontalTime) {
        mHorizontalTime = horizontalTime;
    }

    public boolean isUpdate() {
        return update;
    }

    public void setUpdate(boolean update) {
        this.update = update;
    }

    public int getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(int updateTime) {
        this.updateTime = updateTime;
    }
}