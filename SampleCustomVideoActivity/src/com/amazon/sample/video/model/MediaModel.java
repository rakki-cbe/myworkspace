// Copyright 2012 Amazon.com, Inc. or its affiliates. All Rights Reserved.
package com.amazon.sample.video.model;

/**
 * <code>MediaModel</code> is used to store media playback state information for the
 * <code>MediaView</code> object.
 *
 * @version $Revision: #3 $, $Date: 2012/09/04 $
 * @see MediaView
 */
public class MediaModel {
    /** Video width. */
    private int mVideoWidth = 0;

    /** Video height. */
    private int mVideoHeight = 0;

    /** Surface width. */
    private int mSurfaceWidth = 0;

    /** Surface height. */
    private int mSurfaceHeight = 0;

    /**
     * The surface width that is rescaled from the video width while maintaining the
     * video's aspect ratio.
     */
    private int mScaledWidth = 0;

    /**
     * The surface height that is rescaled from the video height while maintaining the
     * video's aspect ratio.
     */
    private int mScaledHeight = 0;

    /** The <code>MediaPlayer</code> prepared state. */
    private boolean mMediaPrepared = false;

    /** The media playback state. */
    private MediaState mState;

    /** Path for the streaming content. */
    private String mUri;

    /** Resets the <code>ContentModel</code> to the default initialized values. */
    public void reset() {
        mVideoWidth = 0;
        mVideoHeight = 0;
        mSurfaceWidth = 0;
        mSurfaceHeight = 0;
        mMediaPrepared = false;
        mUri = null;
        mState = MediaState.STOP;
    }

    /**
     * Sets the video width and height.
     *
     * @param width the width of the video in pixels.
     * @param height the height of the video in pixels.
     */
    public void setVideoDimensions(final int width, final int height) {
        mVideoWidth = width;
        mVideoHeight = height;
        scaleSurfaceDimensions();
    }

    /**
     * Gets the video width.
     *
     * @return the width of the video in pixels.
     */
    public int getVideoWidth() {
        return mVideoWidth;
    }

    /**
     * Gets the video height.
     *
     * @return the height of the video in pixels.
     */
    public int getVideoHeight() {
        return mVideoHeight;
    }

    /**
     * Sets the surface width and height.
     *
     * @param width the width of the surface to render the video content in pixels.
     * @param height the height of the surface to runder the video content in pixels.
     */
    public void setSurfaceDimensions(final int width, final int height) {
        mSurfaceWidth = width;
        mSurfaceHeight = height;
        scaleSurfaceDimensions();
    }

    /**
     * Gets the surface width.
     *
     * @return the width of the surface in pixels.
     */
    public int getSurfaceWidth() {
        return mSurfaceWidth;
    }

    /**
     * Gets the surface height.
     *
     * @return the height of the surface in pixels.
     */
    public int getSurfaceHeight() {
        return mSurfaceHeight;
    }

    /**
     * Gets the scaled width of the surface that accounts for aspect ratio
     * of the video content aspect ratio.
     *
     * @return the width that is scaled to the available surface width.
     */
    public int getScaledWidth() {
        return mScaledWidth;
    }

    /**
     * Gets the scaled height of the surface that accounts for the aspect ratio
     * of the video content.
     *
     * @return the height that is scaled to the available surface height.
     */
    public int getScaledHeight() {
        return mScaledHeight;
    }

    /**
     * Sets the state of the content that is prepared by the <code>MediaPlayer</code>.
     *
     * @param prepared <code>true</code> if the <code>MediaPlayer</code> has prepared the content.
     *                 <code>false</code> if the <code>MediaPlayer</code> has not prepared the content.
     */
    public void setMediaPrepared(final boolean prepared) {
        mMediaPrepared = prepared;
    }

    /**
     * Gets the state of the content media that is prepared by the <code>MediaPlayer</code>.
     *
     * @return <code>true</code> if the <code>MediaPlayer</code> has prepared the content.
     *         <code>false</code> if the <code>MediaPlayer</code> has not prepared the content.
     */
    public boolean isMediaPrepared() {
        return mMediaPrepared;
    }

    /**
     * Sets the path of the streaming media content.
     *
     * @param uri the Universal Resource Identifier.
     */
    public void setUri(final String uri) {
        mUri = uri;
    }

    /**
     * Gets the path of the streaming media content.
     *
     * @return the Universal Resource Identifier.
     */
    public String getUri() {
        return mUri;
    }

    /**
     * Sets the media playback state.
     *
     * @param state the Media playback state.
     */
    public void setMediaState(final MediaState state) {
        mState = state;
    }

    /**
     * Gets the media playback state.
     *
     * @return the Media playback state.
     */
    public MediaState getMediaState() {
        return mState;
    }

    /**
     * Determines if the video content size is known.
     *
     * @return <code>true</code> if the video dimensions are known.
     *         <code>false</code> if the video dimensions are unknown.
     */
    public boolean isVideoSizeKnown() {
        if (mVideoWidth > 0 && mVideoHeight > 0 ) {
            return true;
        }

        return false;
    }

    /**
     * Determines if the scaled size is known.
     *
     * @return <code>true</code> if the scaled dimensions have been computed.
     *         <code>false</code> if the scaled dimensions are unknown.
     */
    public boolean isScaledSizeKnown() {
        if (mScaledWidth > 0 && mScaledHeight > 0) {
            return true;
        }

        return false;
    }

    /**
     * Determines if the surface size is known.
     *
     * @return <code>true</code> if the surface dimensions are known.
     *         <code>false</code> if the surface dimensions are unknown.
     */
    private boolean isSurfaceSizeKnown() {
        if (mSurfaceWidth > 0 && mSurfaceHeight > 0) {
            return true;
        }

        return false;
    }

    /** Scales the video size to the surface size while maintaining the video's aspect ratio. */
    private void scaleSurfaceDimensions() {
        if (isVideoSizeKnown() && isSurfaceSizeKnown()) {
            final double widthRatio = (double) mSurfaceWidth / (double) mVideoWidth;
            final double heightRatio = (double) mSurfaceHeight / (double) mVideoHeight;

            if (widthRatio > heightRatio) {
                mScaledWidth = (int) (mSurfaceHeight * mVideoWidth / mVideoHeight);
                mScaledHeight = mSurfaceHeight;
            } else if (widthRatio < heightRatio) {
                mScaledWidth = mSurfaceWidth;
                mScaledHeight = (int) (mSurfaceWidth * mVideoHeight / mVideoWidth);
            } else {
                mScaledWidth = mSurfaceWidth;
                mScaledHeight = mSurfaceHeight;
            }
        }
    }
}
