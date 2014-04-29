// Copyright 2012 Amazon.com, Inc. or its affiliates. All Rights Reserved.
package com.amazon.sample.video.model;

/**
 * <code>MediaState</code> is used to differentiate between the different media playback states.
 *
 * @version $Revision: #2 $, $Date: 2012/09/04 $
 */
public enum MediaState {
    /** Startup state. */
    STARTUP,
    /** Loading state. */
    LOADING,
    /** Media is seeking. */
    SEEKING,
    /** Media is playing. */
    PLAY,
    /** Media is paused. */
    PAUSE,
    /** Media is stopped. */
    STOP
}
