// Copyright 2012 Amazon.com, Inc. or its affiliates. All Rights Reserved.
package com.amazon.sample.video.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * <code>StatusBarView</code> is a view that extends a custom <code>LinearLayout</code>
 * that is the top status bar that is displayed to the user for the <code>MediaView</code>.
 * It allows for the user to input a custom URI for a streaming video file of the user's choice,
 * and provides status messaging during media playback.
 *
 * @version $Revision: #3 $, $Date: 2012/09/04 $
 * @see MediaView
 */
public class StatusBarView extends LinearLayout {
    /** File extension for MP4 video file containers. */
    private static final String FILE_EXTENSION_MP4 = ".mp4";

    /** File extension for 3GP video file containers. */
    private static final String FILE_EXTENSION_3GP = ".3gp";

    /** File extension for 3GP video file containers. */
    private static final String FILE_EXTENSION_3GPP = ".3gpp";

    /** TextView to display status messages. */
    private TextView mStatusTextView;

    /** URL input from the user. */
    private EditText mUriEditText;

    /**
     * Creates a new <code>StatusBarView</code> object.
     *
     * @param context the application environment information.
     * @param attrs the layout attributes.
     */
    public StatusBarView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Creates a new <code>StatusBarView</code> object.
     *
     * @param context the application environment information.
     */
    public StatusBarView(final Context context) {
        super(context);
    }

    /** {inheritDoc} */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        LayoutInflater.from(getContext()).inflate(com.amazon.sample.video.R.layout.status_bar, this);

        mStatusTextView = (TextView) findViewById(com.amazon.sample.video.R.id.textViewStatus);
        mUriEditText = (EditText) findViewById(com.amazon.sample.video.R.id.editTextURL);
    }

    /**
     * Sets the text to display for the status <code>TextView</code> widget.
     *
     * @param status the text to display.
     */
    public void setStatus(final String status) {
        mStatusTextView.setText(status);
    }

    /**
     * Sets the enabled state for the URI <code>EditText</code> widget.
     *
     * @param state <code>true</code> if URI EditText widget is enabled.
     *              <code>false</code> if URI EditText widget is disabled.
     */
    public void setInputEnabled(final boolean state) {
        mUriEditText.setEnabled(state);
    }

    /**
     * Gets the enabled state for the URI <code>EditText</code> widget.
     *
     * @return <code>true</code> if URI EditText widget is enabled.
     *         <code>false</code> if URI EditText widget is disabled.
     */
    public boolean isInputEnabled() {
        return mUriEditText.isEnabled();
    }

    /**
     * Gets the path to the video content from the URI <code>EditText</code> widget.
     *
     * @return the URI for the video content.
     * @throws IllegalArgumentException if the URI is invalid.
     */
    public String getInput() throws IllegalArgumentException {
        final String uri = mUriEditText.getText().toString().trim();

        if (!isUriFileExtensionValid(uri)) {
            throw new IllegalArgumentException(getContext()
                    .getString(com.amazon.sample.video.R.string.text_status_invalid_url));
        }

        return uri;
    }

    /**
     * Checks if the file extension of the given URI is valid.
     *
     * @param uri the path of the media content.
     * @return <code>true</code> if the URI ends with a valid file extension for a video file.
     *         <code>false</code> if the URI is null, empty, or does not end with a valid file extension.
     * @see StatusBarView#FILE_EXTENSION_MP4
     * @see StatusBarView#FILE_EXTENSION_3GP
     */
    private boolean isUriFileExtensionValid(final String uri) {
        if (uri == null || uri.length() <= 0) {
            return false;
        }

        final String lowerCaseUri = uri.toLowerCase();
        if (lowerCaseUri.endsWith(FILE_EXTENSION_MP4) || lowerCaseUri.endsWith(FILE_EXTENSION_3GP)
                || lowerCaseUri.endsWith(FILE_EXTENSION_3GPP)) {
            return true;
        }

        return false;
    }
}
