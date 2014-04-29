// Copyright 2012 Amazon.com, Inc. or its affiliates. All Rights Reserved.
package com.amazon.sample.video;

import com.amazon.sample.video.view.MediaView;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.WindowManager;

/**
 * A sample application that demonstrates how to implement a custom user interface to
 * <code>MediaPalyer</code> to play streaming video content.
 * <br/>
 * Note:  In order for streaming content to take advantage of hardware accelerated
 * video rendering, the streaming content must meet the following requirements:
 * <br/>
 * <ul>
 * <li>Video encoded with H.264 Baseline Profile between 2-3 Mbps bitrate.</li>
 * <li>For streaming content, ensure that the video encoding is optimized for streaming playback.
 * <br/>
 * Please refer to the <a href="http://developer.android.com/guide/appendix/media-formats.html#recommendations">
 * Video encoding recommendations</a> for additional information.
 * <li>Audio encoded with AAC-LC 2-channel stereo between 128-192 kbps bitrate.</li>
 * <li>MP4 container is recommended.</li>
 * </ul>
 *
 * For more general information, please refer to the
 * <a href="http://developer.android.com/guide/topics/media/mediaplayer.html">Media Playback</a> guide
 * for more information.
 * <br/>
 * Note: This class incorporates the recommended best practice of displaying the activity
 * in full screen mode. For more information, please refer to the SampleScreenLayout sample
 * provided with the Kindle Fire SDK.
 *
 * @version $Revision: #3 $, $Date: 2012/09/04 $
 */
public class SampleCustomVideoActivity extends Activity {
    /** <code>MediaView</code> used to facilitate media playback. */
    private MediaView mMediaView;

    /**
     * Special window flag to minimize the soft key bar. Note that the permission
     * com.amazon.permission.SET_FLAG_NOSOFTKEYS must be declared in the Android
     * Manifest.
     */
    private static final int MINIMIZED_SOFTKEYS = 0x80000000;

    /** Flags to hide the status and soft key bar. */
    private static final int FLAG_MASK = MINIMIZED_SOFTKEYS | WindowManager.LayoutParams.FLAG_FULLSCREEN;

    /** {inheritDoc} */
    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // Hide the status bar and minimize the soft key bar.
       
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                FLAG_MASK);

        mMediaView = (MediaView) findViewById(R.id.mediaView);
        
    }

    /** {inheritDoc} */
    @Override
    public void onResume() {
        super.onResume();

        // Request to lock the orientation to landscape.
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
    }

    /** {inheritDoc} */
    @Override
    public void onPause() {
        super.onPause();

        // Stop content playback upon pausing the activity.
        mMediaView.cleanupMediaPlayer();

        // Reset the orientation lock.
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
    }

    /** {inheritDoc} */
    @Override
    public void onDestroy() {
        super.onDestroy();

        // Stop content playback upon destroying the activity.
        mMediaView.cleanupMediaPlayer();
    }
    
    /** {inheritDoc} */
    @Override
    public boolean dispatchKeyEvent(final KeyEvent event) {
        final int action = event.getAction();
        final int keyCode = event.getKeyCode();
            switch (keyCode) {
            
            // Capture the volume up KeyEvent and return true so the 
            // KeyEvent will not be handled by the device.
            case KeyEvent.KEYCODE_VOLUME_UP:
                if (action == KeyEvent.ACTION_UP) {
                    mMediaView.increaseVolume();
                }
                return true;
                
            // Capture the volume down KeyEvent and return true so the 
            // KeyEvent will not be handled by the device.                
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                if (action == KeyEvent.ACTION_DOWN) {
                    mMediaView.decreaseVolume();
                }
                return true;
            default:
                return super.dispatchKeyEvent(event);
            }
    }

}
