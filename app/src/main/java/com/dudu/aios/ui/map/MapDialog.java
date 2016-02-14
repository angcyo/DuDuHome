package com.dudu.aios.ui.map;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.dudu.android.launcher.R;

/**
 * Created by Administrator on 2016/2/14.
 */
public class MapDialog extends Dialog {
    private TextView tv_msg;

    private String message;

    private Button cancelBtn;

    private View.OnClickListener cancelListener;

    public MapDialog(Context context, int theme) {
        super(context, theme);
    }

    public MapDialog(Context context, String message, View.OnClickListener listener) {
        this(context, R.style.RouteSearchPoiDialogStyle);
        this.message = message;
        this.cancelListener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_dialog_layout);
        tv_msg = (TextView) findViewById(R.id.map_dialog_text);
        if (TextUtils.isEmpty(message)) {
            tv_msg.setText(message);
        }
        cancelBtn = (Button) findViewById(R.id.map_dialog_cancle);
        cancelBtn.setOnClickListener(this.cancelListener);

    }

    public void setMessage(String message) {
        if (tv_msg != null)
            tv_msg.setText(message);
    }

}
