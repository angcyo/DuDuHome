package com.dudu.android.launcher.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.dudu.android.launcher.R;

/**
 * Created by lxh on 2015/11/27.
 */
public class WaitingDialog extends Dialog {

    private TextView tv_msg;

    private String message;

    public WaitingDialog(Context context, int theme) {
        super(context, theme);
    }

    public WaitingDialog(Context context,String message) {
        this(context, R.style.RouteSearchPoiDialogStyle);
        this.message = message;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.waiting_layout);
        tv_msg = (TextView)findViewById(R.id.waiting_text);
        tv_msg.setText(message);
    }

    public void setMessage(String message){
        if(tv_msg!=null)
            tv_msg.setText(message);
    }
}
