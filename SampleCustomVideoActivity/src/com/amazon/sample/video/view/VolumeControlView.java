// Copyright 2012 Amazon.com, Inc. or its affiliates. All Rights Reserved.
package com.amazon.sample.video.view;

import android.content.Context;
import android.media.AudioManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

/**
 * <code>VolumeControlView</code> is a custom view that extends a custom <code>LinearLayout</code>
 * for controlling the volume of media playback through via <code>AudioManager</code>.
 * <br/>
 * For more information, please refer to the
 *  <a href="http://developer.android.com/reference/android/media/AudioManager.html">AudioManager</a> documentation.
 *
 * @version $Revision: #5 $, $Date: 2012/09/04 $
 * @see MediaView
 */
public class VolumeControlView extends LinearLayout {
    /** Audio stream type. */
    private static final int AUDIO_STREAM_TYPE = AudioManager.STREAM_MUSIC;

    /** Flags to pass to the <code>AudioManager</code> upon changing the volume. */
    private static final int AUDIO_MANAGER_VOLUME_FLAGS = 0;

    /** Percentage of the volume to adjust by. */
    private static final float VOLUME_ADJUSTMENT_AMOUNT = 0.1f;

    /** Used to control the volume. */
    private SeekBar mSeekBar;

    /** Used to decrease the volume. */
    private ImageButton mMuteButton;

    /** Used to increase the volume. */
    private ImageButton mMaxButton;

    /** Used to change the volume. */
    private AudioManager mAudioManager;

    /**
     * Creates a new <code>VolumeControlView</code> object.
     *
     * @param context the application environment context.
     * @param attrs the layout attributes.
     */
    public VolumeControlView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    /** {inheritDoc} */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        LayoutInflater.from(getContext()).inflate(com.amazon.sample.video.R.layout.volume_control, this);

        mAudioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);

        mSeekBar = (SeekBar) findViewById(com.amazon.sample.video.R.id.volumeSeekBar);
        mSeekBar.setOnSeekBarChangeListener(mSeekBarChangeListener);
        mSeekBar.setMax(mAudioManager.getStreamMaxVolume(AUDIO_STREAM_TYPE));
        mSeekBar.setProgress(mAudioManager.getStreamVolume(AUDIO_STREAM_TYPE));

        mMuteButton = (ImageButton) findViewById(com.amazon.sample.video.R.id.imageButtonMute);
        mMuteButton.setOnTouchListener(mVolumeMuteListener);

        mMaxButton = (ImageButton) findViewById(com.amazon.sample.video.R.id.imageButtonLoud);
        mMaxButton.setOnTouchListener(mVolumeMaxListener);
    }
    
    /**
     * Increase the volume through the <code>AudioManager</code> and update the
     * status of the <code>SeekBar</code>.
     */
    public void increaseVolume() {
    	mAudioManager.adjustVolume(AudioManager.ADJUST_RAISE, 0); 
    	mSeekBar.setProgress(mAudioManager.getStreamVolume(AUDIO_STREAM_TYPE));
    }
    
    /**
     * Decrease the volume through the <code>AudioManager</code> and update the
     * status of the <code>SeekBar</code>. 
     */
    public void decreaseVolume() {
    	mAudioManager.adjustVolume(AudioManager.ADJUST_LOWER, 0);
    	mSeekBar.setProgress(mAudioManager.getStreamVolume(AUDIO_STREAM_TYPE));
    }
    
    /** Listens for SeekBar changes to control the volume. */
    private final OnSeekBarChangeListener mSeekBarChangeListener = new OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(final SeekBar seekBar, final int progress, boolean fromUser) {
            // Update the volume based on user selection.
            mAudioManager.setStreamVolume(AUDIO_STREAM_TYPE, progress, AUDIO_MANAGER_VOLUME_FLAGS);
        }

        @Override
        public void onStartTrackingTouch(final SeekBar seekBar) {
            /* Do nothing. */
        }

        @Override
        public void onStopTrackingTouch(final SeekBar seekBar) {
            /* Do nothing. */
        }
    };

    /** Handles the user pressing the decrease volume button. */
    private final OnTouchListener mVolumeMuteListener = new OnTouchListener() {
        @Override
        public boolean onTouch(final View view, final MotionEvent event) {
            // Decrease the volume by the volume adjustment amount percentage.
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                int newVolume = (int) (mSeekBar.getProgress() - (VOLUME_ADJUSTMENT_AMOUNT * mSeekBar.getMax()));
                if (newVolume < 0) {
                    newVolume = 0;
                }

                mSeekBar.setProgress(newVolume);
            }

            // Return false so that the MotionEvent continues processing through the event chain.
            return false;
        }
    };

    /** Handles the user pressing the increase volume button. */
    private final OnTouchListener mVolumeMaxListener = new OnTouchListener() {
        @Override
        public boolean onTouch(final View view, final MotionEvent event) {
            // Increase the volume by the volume adjustment amount percentage.
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                int newVolume = (int) (mSeekBar.getProgress() + (VOLUME_ADJUSTMENT_AMOUNT * mSeekBar.getMax()));
                if (newVolume > mSeekBar.getMax()) {
                    newVolume = mSeekBar.getMax();
                }

                mSeekBar.setProgress(newVolume);
            }

            // Return false so that the MotionEvent continues processing through the event chain.
            return false;
        }
    };
}
