// Copyright 2012 Amazon.com, Inc. or its affiliates. All Rights Reserved.
package com.amazon.sample.video.view;

import java.io.IOException;
import java.util.TimerTask;

import com.amazon.sample.video.R;
import com.amazon.sample.video.model.MediaModel;
import com.amazon.sample.video.model.MediaState;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnInfoListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnVideoSizeChangedListener;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.SurfaceHolder.Callback;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

/**
 * <code>MediaView</code> is a custom view that extends <code>RelativeLayout</code>.
 * It provides an example of how to implement a fully customized user interface
 * in order to provide a unique media playback experience within an application
 * and also serves as an alternative to implementing <code>VideoView</code>.
 *
 * @version $Revision: #6 $, $Date: 2012/09/04 $
 * @see MediaPlayer
 * @see ControlBarView
 * @see StatusBarView
 * @see RelativeLayout
 */
public class MediaView extends RelativeLayout {
    /** Logging tag to be used for logging output. */
    private static final String LOG_TAG = MediaView.class.getSimpleName();

    /** Milliseconds to pause between <code>SeekBar</code> duration updates. */
    private static final long DURATION_UPDATE_EPOCH = 500L;

    /** Milliseconds to delay before hiding the controls upon media playback. */
    private static final long HIDE_CONTROL_EPOCH = 5000L;

    /** WiFi Lock identifier. */
    public static final String WIFI_LOCK_NAME = "com.amazon.sample.video.wifi.lock";

    /** View that facilitates user input and status messaging. */
    private StatusBarView mStatusBar;

    /** View that controls media playback. */
    private ControlBarView mControlBar;

    /** View that controls audio volume. */
    private VolumeControlView mVolumeControl;

    /** Surface to render video content to. */
    private SurfaceView mSurface;

    /** Surface holder for MediaPlayer to use. */
    private SurfaceHolder mSurfaceHolder;

    /**
     * Reference to the <a href="http://developer.android.com/reference/android/media/MediaPlayer.html">MediaPlayer</a>
     * object used to play the streaming video content.
     */
    private MediaPlayer mMediaPlayer = null;

    /** Lock used to keep the Wifi enabled while streaming video content. */
    private WifiLock mWifiLock;

    /** Stores associated state information for content playback. */
    private MediaModel mMediaModel;

    /** The thread that queries the media playback duration. */
    private Thread mDurationTrackingThread;

    /** The timer used to hide the controls after a set duration upon media playback. */
    private Handler mHideControlTimer;

    /**
     * Creates a new <code>MediaView</code> object.
     *
     * @param context the application environment.
     * @param attrs the <code>View</code> attributes.
     */
    public MediaView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        //String path="http://www.youtube.com/v/oyB5QWcPSRo?version=3&f=user_uploads&app=youtube_gdata";
        //prepareContent(path);
        
    }

    /**
     * Creates a new <code>MediaView</code> object.
     *
     * @param context the application environment.
     */
    public MediaView(final Context context) {
        super(context);
        //String path="http://www.youtube.com/v/oyB5QWcPSRo?version=3&f=user_uploads&app=youtube_gdata";
        //prepareContent(Uri.parse(path));
        //prepareContent(path);
        
    }

    /** {inheritDoc} */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        final Context context = getContext();
        LayoutInflater.from(context).inflate(com.amazon.sample.video.R.layout.media_view, this);

        // Obtain the reference to the Wifi lock.
        mWifiLock = ((WifiManager) context.getSystemService(Context.WIFI_SERVICE))
                .createWifiLock(WifiManager.WIFI_MODE_FULL, WIFI_LOCK_NAME);

        mSurface = (SurfaceView) findViewById(R.id.surfaceView);
        mSurface.setOnTouchListener(mSurfaceTouchListener);
        mSurfaceHolder = mSurface.getHolder();
        mSurfaceHolder.addCallback(mCallback);
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        mStatusBar = (StatusBarView) findViewById(R.id.statusBar);
        mControlBar = (ControlBarView) findViewById(R.id.controlBar);
        mControlBar.setPlayButtonOnClickListener(mOnClickPlayListener);
        mControlBar.setOnSeekBarChangeListener(mSeekBarListener);

        mVolumeControl = (VolumeControlView) findViewById(R.id.volumeControlView);
        mVolumeControl.setOnTouchListener(mVolumeTouchListener);

        mMediaModel = new MediaModel();
        mHideControlTimer = new Handler();

        // Set the UI state.
        setMediaState(MediaState.STARTUP);
    }

    /**
     * Loads the content based on user provided path for the media content.
     *
     * @param uri the path for the media content.
     */
    private void prepareContent(final String uri) {
        try {
            // Prepare the video content for playback
        	System.out.println("comes correctly 2"+uri);
            loadContent(uri);
        } catch (final IllegalStateException e) {
            mStatusBar.setStatus(getContext().getString(R.string.text_status_invalid_content));
            Log.e(LOG_TAG, e.getLocalizedMessage(), e);
        } catch (final IllegalArgumentException e) {
            mStatusBar.setStatus(getContext().getString(R.string.text_status_invalid_url));
            Log.e(LOG_TAG, e.getLocalizedMessage(), e);
        } catch (final IOException e) {
            mStatusBar.setStatus(getContext().getString(R.string.text_status_invalid_content));
            Log.e(LOG_TAG, e.getLocalizedMessage(), e);
        }
    }

    /**
     * Instantiates a new <code>MediaPlayer</code> object to play the streaming
     * content.
     * <br/>
     * Please refer to the
     * <a href="http://developer.android.com/reference/android/media/MediaPlayer.html">MediaPlayer</a>
     * for more information.
     *
     * @param uri the path to the streaming content.
     * @throws IllegalArgumentException if the <code>MediaPlayer</code> is in an invalid state.
     * @throws IllegalStateException if the URL is malformed.
     * @throws IOException if unable to read the video content from the specified path.
     */
    private void loadContent(final String uri) throws IllegalStateException, IllegalArgumentException, IOException {
        if (uri == null || uri.length() == 0 || mSurfaceHolder == null) {
            Log.d(LOG_TAG, getContext().getString(R.string.log_prepare_error));
            return;
        }

        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setDisplay(mSurfaceHolder);
        mMediaPlayer.setDataSource(uri);
        mMediaPlayer.setOnPreparedListener(mPreparedListener);
        mMediaPlayer.setOnCompletionListener(mCompletionListener);
        mMediaPlayer.setOnBufferingUpdateListener(mBufferingListener);
        mMediaPlayer.setOnVideoSizeChangedListener(mVideoSizeChangedListener);
        mMediaPlayer.setOnInfoListener(mInfoListener);
        mMediaPlayer.setOnErrorListener(mErrorListener);
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        // Ensures that the screen remains on during content playback.
        mMediaPlayer.setScreenOnWhilePlaying(true);

        // Obtain the Wifi lock.
        mWifiLock.acquire();

        // Prepare the stream without blocking the current thread.
        setMediaState(MediaState.LOADING);
        mMediaPlayer.prepareAsync();

        mMediaModel.setUri(uri);
    }

    /** Starts playing the content. */
    private void startContent() {
        // Get the scaled dimensions of the video content.
        final int scaledWidth = mMediaModel.getScaledWidth();
        final int scaledHeight = mMediaModel.getScaledHeight();

        Log.d(LOG_TAG, String.format(getContext().getString(R.string.log_scaled_size), scaledWidth, scaledHeight));

        // Resize the visible surface used to render the video content.
        final ViewGroup.LayoutParams surfaceLayoutParams = mSurface.getLayoutParams();
        surfaceLayoutParams.width = scaledWidth;
        surfaceLayoutParams.height = scaledHeight;

        // Ensure that the surface holder is also resized to match the scaled video content size.
        mSurfaceHolder.setFixedSize(scaledWidth, scaledHeight);

        // Reset the seek bar.
        mControlBar.resetSeekBar(mMediaPlayer.getDuration());

        // Start media play back.
        mMediaPlayer.start();
        startDurationTracker();

        // Update the UI state.
        setMediaState(MediaState.PLAY);

        // Hide the controls after a set duration.
        hideControls();
    }

    /** Releases and cleans up the <code>MediaPlayer</code> and wifi lock. */
    public void cleanupMediaPlayer() {
        setMediaState(MediaState.STOP);

        // Clean up the duration tracking thread.
        stopDurationTracker();
        mControlBar.resetSeekBar(0);

        // Clean up the MediaPlayer's allocated resources.
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }

        // Release the Wifi lock.
        if (mWifiLock != null && mWifiLock.isHeld()) {
            mWifiLock.release();
        }

        // Reset the model.
        if (mMediaModel != null) {
            mMediaModel.reset();
        }
    }

    /** Pauses or resumes media playback and updates the UI state. */
    private void togglePausePlayback() {
        if (mMediaPlayer != null) {
            if (mMediaPlayer.isPlaying()) {
                // Pause playback
                setMediaState(MediaState.PAUSE);
                stopDurationTracker();
                mMediaPlayer.pause();

            } else {
                // Resume playback if the MediaPlayer is already in a paused state.
                setMediaState(MediaState.PLAY);
                mMediaPlayer.start();
                startDurationTracker();

                // Auto hide the controls.
                hideControls();
            }
        }
    }

    /** Stops media playback and resets the UI state. */
    private void stopContent() {
        if (mMediaPlayer != null) {
            // Stop media playback and release the allocated resources.
            mMediaPlayer.stop();
            cleanupMediaPlayer();
        }
    }

    /**
     * Creates a new thread to periodically query the media content and
     * update the seek bar duration during playback.
     */
    private void startDurationTracker() {
        mDurationTrackingThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (mMediaPlayer != null && mMediaModel.getMediaState() == MediaState.PLAY) {
                    mControlBar.setSeekBarProgress(mMediaPlayer.getCurrentPosition());

                    // Sleep for a set epoch between updates.
                    try {
                        Thread.sleep(DURATION_UPDATE_EPOCH);
                    } catch (final InterruptedException e) {
                        // Ensure that this thread is interrupted.
                        Thread.currentThread().interrupt();
                    }
                }
            }

        });

        mDurationTrackingThread.start();
    }

    /** Stops the duration tracking thread from running. */
    private void stopDurationTracker() {
        // Interrupt the thread in case the tracker is sleeping.
        if (mDurationTrackingThread != null && mDurationTrackingThread.isAlive()) {
            mDurationTrackingThread.interrupt();
        }
    }

    /** Toggles the visibility of the top and bottom control bars. */
    private void toggleControlVisibility() {
        if (mStatusBar.getVisibility() == ViewGroup.VISIBLE &&
                mControlBar.getVisibility() == ViewGroup.VISIBLE) {
            setControlVisible(false);
        } else {
            setControlVisible(true);
        }
    }

    /**
     * Hides the controls based on a delay.
     *
     * @see MediaView#HIDE_CONTROL_EPOCH
     */
    private void hideControls() {
        mHideControlTimer.removeCallbacks(mHideControlTimeTask);
        mHideControlTimer.postDelayed(mHideControlTimeTask, HIDE_CONTROL_EPOCH);
    }

    /** Sets the visibility of the top and bottom control bars. */
    private void setControlVisible(final boolean visible) {
        final Context context = getContext();

        if (visible) {
            if (mStatusBar.getVisibility() != ViewGroup.VISIBLE &&
                    mControlBar.getVisibility() != ViewGroup.VISIBLE) {
                final Animation fadeIn = AnimationUtils.loadAnimation(context, android.R.anim.fade_in);

                // Create transparent regions on the surface view to provide a smoother transition of
                // visibility of the ViewGroups.
                mStatusBar.getParent().requestTransparentRegion(mSurface);
                mStatusBar.startAnimation(fadeIn);
                mStatusBar.setVisibility(ViewGroup.VISIBLE);

                mControlBar.getParent().requestTransparentRegion(mSurface);
                mControlBar.startAnimation(fadeIn);
                mControlBar.setVisibility(ViewGroup.VISIBLE);

                mVolumeControl.getParent().requestTransparentRegion(mSurface);
                mVolumeControl.startAnimation(fadeIn);
                mVolumeControl.setVisibility(ViewGroup.VISIBLE);
            }
        } else {
            if (mStatusBar.getVisibility() != ViewGroup.INVISIBLE &&
                    mControlBar.getVisibility() != ViewGroup.INVISIBLE) {
                final Animation fadeOut = AnimationUtils.loadAnimation(context, android.R.anim.fade_out);

                mStatusBar.getParent().requestTransparentRegion(mSurface);
                mStatusBar.startAnimation(fadeOut);
                mStatusBar.setVisibility(ViewGroup.INVISIBLE);

                mControlBar.getParent().requestTransparentRegion(mSurface);
                mControlBar.startAnimation(fadeOut);
                mControlBar.setVisibility(ViewGroup.INVISIBLE);

                mVolumeControl.getParent().requestTransparentRegion(mSurface);
                mVolumeControl.startAnimation(fadeOut);
                mVolumeControl.setVisibility(ViewGroup.INVISIBLE);
            }
        }
    }

    /**
     * Update the UI controls based on predefined activity states.
     *
     * @param state the media state.
     * @see MediaState
     */
    private void setMediaState(final MediaState state) {
        mMediaModel.setMediaState(state);
        switch (state) {
        case LOADING:
            mStatusBar.setStatus(getContext().getString(R.string.text_status_preparing));
            /* Fall through. */
        case SEEKING: /* Fall through. */
        case PLAY:
            mStatusBar.setInputEnabled(false);
            break;
        case PAUSE: /* Fall through. */
        case STARTUP: /* Fall through. */
        case STOP: /* Fall through. */
        default:
            mStatusBar.setInputEnabled(true);
            break;
        }

        mControlBar.setUiState(state);
    }

    /** Show the controls and increase the volume. Then place a request to hide the controls. */
    public void increaseVolume() {
    	setControlVisible(true);
    	mVolumeControl.increaseVolume();
    	hideControls();
    }
    
    /** Show the controls and decrease the volume. Then place a request to hide the controls. */
    public void decreaseVolume() {
    	setControlVisible(true);
    	mVolumeControl.decreaseVolume();
    	hideControls();
    }
    
    /** The <code>TimerTask</code> that auto hides the UI controls upon playing media content. */
    private final TimerTask mHideControlTimeTask = new TimerTask() {
        @Override
        public void run() {
            if (mMediaModel.getMediaState() == MediaState.PLAY) {
                setControlVisible(false);
            }
        }
    };

    /** <code>OnClickListener</code> for the play button. */
    private final OnClickListener mOnClickPlayListener = new OnClickListener() {
        @Override
        public void onClick(final View view) {
            Log.d(LOG_TAG, getContext().getString(R.string.log_play_button_clicked));

            final Context context = getContext();

            switch (mMediaModel.getMediaState()) {
            case PLAY: // Currently playing, so pause the content.
                /* Fall through. */
            case PAUSE: // Currently paused
                String inputUri = null;

                try {
                    inputUri = mStatusBar.getInput();
                } catch (final IllegalArgumentException e) {
                    mStatusBar.setStatus(context.getString(R.string.text_status_invalid_url));
                }

                if (inputUri != null) {
                    if (inputUri.equalsIgnoreCase(mMediaModel.getUri())) {
                        // Resume playback.
                        togglePausePlayback();
                    } else {
                        // New URI entered by the user.  Stop the content and prepare the new URI.
                        if (mMediaPlayer != null) {
                            stopContent();
                            mStatusBar.setStatus(context.getString(R.string.text_status_stoped));

                            // Prepare to play the new streaming content.
                            prepareContent(inputUri);
                        } else {
                            Log.e(LOG_TAG, context.getString(R.string.log_stop_error));
                            mStatusBar.setStatus(context.getString(R.string.text_stop_error));
                        }
                    }
                }

                break;
            case STOP: // Currently not playing any content.
                // Prepare the video content based on the path provided by the user for playback.
                try {
                    final String uri = mStatusBar.getInput();
                    prepareContent(uri);
                } catch (final IllegalArgumentException e) {
                    mStatusBar.setStatus(context.getString(R.string.text_status_invalid_url));
                }

                break;
            default:
                /* Do nothing. */
                break;
            }
        }
    };

    /** Handles the surface callbacks. */
    private final Callback mCallback = new Callback() {
        @Override
        public void surfaceChanged(final SurfaceHolder holder, final int format, final int width, final int height) {
            Log.d(LOG_TAG, String.format(getContext().getString(R.string.log_surface_changed), format, width, height));
            mMediaModel.setSurfaceDimensions(width, height);
        }

        @Override
        public void surfaceCreated(final SurfaceHolder holder) {
            Log.d(LOG_TAG, getContext().getString(R.string.log_surface_created));
            setMediaState(MediaState.STOP);
        }

        @Override
        public void surfaceDestroyed(final SurfaceHolder holder) {
            Log.d(LOG_TAG, getContext().getString(R.string.log_surface_destroyed));
        }
    };

    /** Handles buffering updates from the <code>MediaPlayer</code>. */
    private final OnBufferingUpdateListener mBufferingListener = new OnBufferingUpdateListener() {
        @Override
        public void onBufferingUpdate(final MediaPlayer mediaPlayer, final int percent) {
            mStatusBar.setStatus(String.format(getContext().getString(R.string.text_buffering), percent));
        }
    };

    /** Starts playing the content once the <code>MediaPlayer</code> has finished preparing. */
    private final OnPreparedListener mPreparedListener = new OnPreparedListener() {
        @Override
        public void onPrepared(final MediaPlayer mediaPlayer) {
            Log.d(LOG_TAG, getContext().getString(R.string.log_video_prepared));
            mMediaModel.setMediaPrepared(true);
            if (mMediaModel.isScaledSizeKnown()) {
                startContent();
            }
        }
    };

    /** Resets the activity state upon completion of content playback. */
    private final OnCompletionListener mCompletionListener = new OnCompletionListener() {
        @Override
        public void onCompletion(final MediaPlayer mediaPlayer) {
            final Context context = getContext();

            Log.d(LOG_TAG, context.getString(R.string.log_video_completed));

            // Update the status and reset the SeekBar position.
            mStatusBar.setStatus(context.getString(R.string.text_status_completed));
            mControlBar.resetSeekBar(0);

            setControlVisible(true);

            cleanupMediaPlayer();
        }
    };

    /** Handles changes in the video content size. */
    private final OnVideoSizeChangedListener mVideoSizeChangedListener = new OnVideoSizeChangedListener() {
        @Override
        public void onVideoSizeChanged(final MediaPlayer mediaPlayer, final int width, final int height) {
            Log.d(LOG_TAG, String.format(getContext().getString(R.string.log_video_size_changed), width, height));
            mMediaModel.setVideoDimensions(width, height);

            if (mMediaModel.isMediaPrepared() && mMediaModel.isScaledSizeKnown()) {
                startContent();
            }
        }
    };

    /** Handles information updates from the <code>MediaPlayer</code>. */
    private final OnInfoListener mInfoListener = new OnInfoListener() {
        @Override
        public boolean onInfo(final MediaPlayer mediaPlayer, final int what, final int extra) {
            final Context context = getContext();
            String whatString;

            switch (what) {
            case MediaPlayer.MEDIA_INFO_BAD_INTERLEAVING:
                whatString = context.getString(R.string.log_video_info_bad_interleaving);
                break;
            case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                whatString = context.getString(R.string.log_video_info_buffering_end);
                break;
            case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                whatString = context.getString(R.string.log_video_info_buffering_start);
                break;
            case MediaPlayer.MEDIA_INFO_METADATA_UPDATE:
                whatString = context.getString(R.string.log_video_info_metadata_update);
                break;
            case MediaPlayer.MEDIA_INFO_NOT_SEEKABLE:
                whatString = context.getString(R.string.log_video_info_not_seekable);
                break;
            case MediaPlayer.MEDIA_INFO_VIDEO_TRACK_LAGGING:
                whatString = context.getString(R.string.log_video_info_video_track_lagging);
                break;
            case MediaPlayer.MEDIA_INFO_UNKNOWN: /* Fall through. */
            default:
                whatString = context.getString(R.string.log_video_info_unkown);
                break;
            }

            Log.i(LOG_TAG, String.format(context.getString(R.string.log_video_info), whatString, extra));

            setControlVisible(true);
            mStatusBar.setStatus(whatString);

            return false;
        }
    };

    /** Handles error notifications from the <code>MediaPlayer</code>. */
    private final OnErrorListener mErrorListener = new OnErrorListener() {
        @Override
        public boolean onError(final MediaPlayer mediaPlayer, final int what, final int extra) {
            final Context context = getContext();
            Log.e(LOG_TAG, String.format(context.getString(R.string.log_video_error), what, extra));
            mStatusBar.setStatus(context.getString(R.string.text_status_error));

            cleanupMediaPlayer();

            return true;
        }
    };

    /** Handles notifications from the <code>SeekBar</code>. */
    private final OnSeekBarChangeListener mSeekBarListener = new OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(final SeekBar seekBar, final int progress, final boolean fromUser) {
            if (fromUser && mMediaPlayer != null) {
                mMediaPlayer.seekTo(progress);
                mControlBar.setSeekBarProgress(progress);
            }
        }

        @Override
        public void onStartTrackingTouch(final SeekBar seekBar) {
            setMediaState(MediaState.SEEKING);

            // Cancel auto hiding of the controls.
            mHideControlTimer.removeCallbacks(mHideControlTimeTask);
            stopDurationTracker();
        }

        @Override
        public void onStopTrackingTouch(final SeekBar seekBar) {
            setMediaState(MediaState.PLAY);
            startDurationTracker();
            hideControls();
        }
    };

    /** Toggles the visibility of the controls upon the user tapping the surface. */
    private final OnTouchListener mSurfaceTouchListener = new OnTouchListener() {
        @Override
        public boolean onTouch(final View view, final MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_UP && mMediaPlayer != null && mMediaPlayer.isPlaying()) {
                // Prevent the hiding of controls in case the TimeTask is set to run.
                mHideControlTimer.removeCallbacks(mHideControlTimeTask);
                toggleControlVisibility();
            }

            return true;
        }
    };

    /** Resets the hide delay upon the user adjusting the volume. */
    private final OnTouchListener mVolumeTouchListener = new OnTouchListener() {
        @Override
        public boolean onTouch(final View view, final MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                // Prevent the hiding of controls in case the TimeTask is set to run.
                mHideControlTimer.removeCallbacks(mHideControlTimeTask);
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                hideControls();
            }

            return true;
        }
    };
}
