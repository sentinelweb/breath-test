package co.uk.sentinelweb.pyst.test.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import sentinelweb.uk.co.pyst.test.R;

/**
 * Created by robert on 15/04/2015.
 */
public class ControlsView extends RelativeLayout {
    BreathingView mTargetView;
    SeekBar mTimeSeekBar;
    SeekBar mRiseTimeSeekBar;
    SeekBar mFallTimeSeekBar;
    TextView mTimeSeekText;
    TextView mRiseTimeSeekText;
    TextView mFallTimeSeekText;
    public ControlsView(final Context context) {
        super(context);
        init(context, null, 0, 0);
    }

    public ControlsView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0, 0);
    }

    public ControlsView(final Context context, final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr, 0);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ControlsView(final Context context, final AttributeSet attrs, final int defStyleAttr, final int defStyleRes) {
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
        LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layoutInflater.inflate(R.layout.view_controls, this);
        mTimeSeekBar = (SeekBar)findViewById(R.id.view_controls_time);
        mTimeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int value, boolean fromUser) {
                if (fromUser && mTargetView!=null) {
                    mTargetView.setHorizontalTime(value*1000);
                    mTimeSeekText.setText(Integer.toString(value));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        mRiseTimeSeekBar = (SeekBar)findViewById(R.id.view_controls_rise_time);
        mRiseTimeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int value, boolean fromUser) {
                if (fromUser && mTargetView!=null) {
                    mTargetView.setRiseTime(value * 100);
                    mRiseTimeSeekText.setText(Float.toString(value/10f));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        mFallTimeSeekBar = (SeekBar)findViewById(R.id.view_controls_fall_time);
        mFallTimeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int value, boolean fromUser) {
                if (fromUser && mTargetView!=null) {
                    mTargetView.setFallTime(value * 100);
                    mFallTimeSeekText.setText(Float.toString(value/10f));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        mTimeSeekText = (TextView)findViewById(R.id.view_controls_time_text);
        mRiseTimeSeekText = (TextView)findViewById(R.id.view_controls_rise_time_text);
        mFallTimeSeekText = (TextView)findViewById(R.id.view_controls_fall_time_text);
    }


    /**
     * Update the sliders + text from the value in the breathView (mTargetView)
     */
    public void updateView() {
        final int time = (int) mTargetView.getHorizontalTime() / 1000;
        mTimeSeekText.setText(Integer.toString(time));
        mTimeSeekBar.setProgress(time);

        final int rise = (int) mTargetView.getRiseTime() / 1000;
        mRiseTimeSeekBar.setProgress(rise * 10);
        mRiseTimeSeekText.setText(Integer.toString(rise));

        final int fall = (int) mTargetView.getFallTime() / 1000;
        mFallTimeSeekBar.setProgress(fall*10);
        mFallTimeSeekText.setText(Integer.toString(fall));
    }

    public BreathingView getTargetView() {
        return mTargetView;
    }

    public void setTargetView(BreathingView targetView) {
        mTargetView = targetView;
        updateView();
    }
}