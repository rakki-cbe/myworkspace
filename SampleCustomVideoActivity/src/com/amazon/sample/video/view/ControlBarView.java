// Copyright 2012 Amazon.com, Inc. or its affiliates. All Rights Reserved.
package com.amazon.sample.video.view;

import com.amazon.sample.video.R;
import com.amazon.sample.video.model.MediaState;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

/**
 * <code>ControlBarView</code> is a view that extends a custom <code>LinearLayout</code>
 * that is the bottom control bar that is displayed to the user for the <code>MediaView</code>.
 * It allows for the user to control video playback supporting:
 * <br/>
 * <ul>
 * <li>Play</li>
 * <li>Pause</li>
 * <li>Stop</li>
 * <li>Seek forwards or backwards</li>
 * </ul>
 *
 * @version $Revision: #3 $, $Date: 2012/09/04 $
 * @see MediaView
 */
public class ControlBarView extends LinearLayout {
    /** Used to play and pause the content. */
    private ImageButton mPlayButton;

    /** SeekBar to track and control content play back progress. */
    private SeekBar mSeekBar;

    /**
     * Creates a new <code>ControlBarView</code> object.
     *
     * @param context the application environment information.
     * @param attrs the layout attributes.
     */
    public ControlBarView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Creates a new <code>ControlBarView</code> object.
     *
     * @param context the application environment information.
     */
    public ControlBarView(final Context context) {
        super(context);
    }

    /** {inheritDoc} */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        LayoutInflater.from(getContext()).inflate(com.amazon.sample.video.R.layout.control_bar, this);

        mPlayButton = (ImageButton) findViewById(R.id.playImageButton);

        mSeekBar = (SeekBar) findViewById(R.id.seekBar);
        mSeekBar.setProgress(0);
    }

    /**
     * Sets the <code>OnClickListener</code> for the play button.
     *
     * @param listener the listener to handle {@link OnClickListener#onClick(android.view.View)} events.
     */
    public void setPlayButtonOnClickListener(final OnClickListener listener) {
        mPlayButton.setOnClickListener(listener);
    }

    /**
     * Sets the <code>OnSeekBarChangeListener</code> for the seek bar.
     *
     * @param listener the listener to handle <code>OnSeekBarChangeListener</code> events.
     * @see OnSeekBarChangeListener
     */
    public void setOnSeekBarChangeListener(final OnSeekBarChangeListener listener) {
        mSeekBar.setOnSeekBarChangeListener(listener);
    }

    /**
     * Resets the <code>SeekBar</code> progress to 0 and sets the maximum value
     * to the specified value.
     *
     * @param max the maximum value to set the <code>SeekBar</code> to.
     */
    public void resetSeekBar(final int max) {
        mSeekBar.setProgress(0);
        mSeekBar.setMax(max);
    }

    /**
     * Sets the <code>SeekBar</code> progress to the specified value.
     *
     * @param progress the <code>SeekBar</code> progress.
     */
    public void setSeekBarProgress(final int progress) {
        mSeekBar.setProgress(progress);
    }

    /**
     * Update the control bar widgets based on predefined activity states.
     *
     * @param state the UI state.
     * @see MediaState
     */
    public void setUiState(final MediaState state) {
        switch (state) {
        case STARTUP:
            mPlayButton.setEnabled(false);
            mPlayButton.setImageResource(R.drawable.button_play_images);
            mSeekBar.setEnabled(false);
            break;
        case LOADING:
            mPlayButton.setEnabled(true);
            mPlayButton.setImageResource(R.drawable.button_play_images);
            mSeekBar.setEnabled(true);
            break;
        case SEEKING:
            // Maintain the previous state.
            break;
        case PLAY:
            mPlayButton.setEnabled(true);
            mPlayButton.setImageResource(R.drawable.button_pause_images);
            mSeekBar.setEnabled(true);
            break;
        case PAUSE:
            mPlayButton.setEnabled(true);
            mPlayButton.setImageResource(R.drawable.button_play_images);
            mSeekBar.setEnabled(true);
            break;
        case STOP: /* Fall through. */
        default:
            mPlayButton.setEnabled(true);
            mPlayButton.setImageResource(R.drawable.button_play_images);
            mSeekBar.setEnabled(false);
            break;
        }
    }
}
