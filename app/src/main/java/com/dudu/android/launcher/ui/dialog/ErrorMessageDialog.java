package com.dudu.android.launcher.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dudu.android.launcher.R;

/**
 * Created by Administrator on 2015/11/24.
 */
public class ErrorMessageDialog extends Dialog {

    private ImageView mErrorImage;

    private TextView mErrorTextView;

    private int mErrorMessageId;

    private int mImageId;

    public ErrorMessageDialog(Context context, int errorMessageId, int imageId) {
        super(context, R.style.NearbyPoiActivityStyle);
        mErrorMessageId = errorMessageId;
        mImageId = imageId;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.error_message_layout);
        mErrorImage = (ImageView) findViewById(R.id.error_message_image);
        mErrorImage.setImageResource(mImageId);
        mErrorTextView = (TextView) findViewById(R.id.error_message_tv);
        mErrorTextView.setText(mErrorMessageId);
    }

}
