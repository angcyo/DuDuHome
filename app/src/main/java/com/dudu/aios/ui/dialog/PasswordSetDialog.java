package com.dudu.aios.ui.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.dudu.aios.ui.utils.KeyboardUtil;
import com.dudu.android.launcher.R;
import com.dudu.android.launcher.utils.LogUtils;

public class PasswordSetDialog extends Dialog implements View.OnClickListener {

    private Button btnOk, btnCancel;

    private EditText txtWifiName, txtWifiPassword;

    private Context ctx;

    private Activity act;

    public PasswordSetDialog(Context context) {
        super(context, R.style.PasswordSetDialogStyle);
        ctx = context;
        act = (Activity) context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.password_set_dialog);
        initView();
        initClickListener();
    }

    private void initClickListener() {
        btnOk.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        txtWifiName.setInputType(InputType.TYPE_NULL);
        txtWifiName.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                LogUtils.v("flow", "name");
                new KeyboardUtil(act, ctx, txtWifiName).showKeyboard();
                return false;
            }
        });
        txtWifiPassword.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                LogUtils.v("flow", "password");
                int inputBack = txtWifiPassword.getInputType();
                txtWifiPassword.setInputType(InputType.TYPE_NULL);
                new KeyboardUtil(act, ctx, txtWifiPassword).showKeyboard();
                txtWifiPassword.setInputType(inputBack);
                return false;
            }
        });
    }

    private void initView() {
        btnOk = (Button) findViewById(R.id.button_ok);
        btnCancel = (Button) findViewById(R.id.button_cancel);
        txtWifiName = (EditText) findViewById(R.id.txt_wifi_name);
        txtWifiPassword = (EditText) findViewById(R.id.txt_wifi_password);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_ok:
                break;
            case R.id.button_cancel:
                dismiss();
                break;
        }
    }
}
