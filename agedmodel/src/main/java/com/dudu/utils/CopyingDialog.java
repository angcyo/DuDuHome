package com.dudu.utils;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

import com.dudu.agedmodel.R;


/**
 * Created by Administrator on 2015/12/23.
 */
public class CopyingDialog extends Dialog {
    private TextView tvMessage;

    private String message;

    public CopyingDialog(Context context, String message) {
        super(context, R.style.NearbyPoiActivityStyle);
        this.message = message;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.copy_message_layout);
        tvMessage = (TextView) findViewById(R.id.tv_message);
        tvMessage.setText(message);
    }
}
