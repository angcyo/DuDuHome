package com.dudu.aios.ui.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.inputmethodservice.KeyboardView;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.dudu.aios.ui.utils.KeyboardUtil;
import com.dudu.android.launcher.R;
import com.dudu.android.launcher.utils.LogUtils;
import com.dudu.android.launcher.utils.WifiApAdmin;


import java.lang.reflect.Method;

public class FlowPasswordSetDialog extends Dialog implements View.OnClickListener {

    private Button btnOk, btnCancel;

    private EditText txtWifiName, txtWifiPassword;

    private Activity act;

    private KeyboardView keyboardView;

    private OnCancelOnListener listener;

    public FlowPasswordSetDialog(Activity context) {
        super(context, R.style.PasswordSetDialogStyle);
        act = context;
    }

    public void setOnCancelOnListener(OnCancelOnListener listener) {
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.flow_password_set_dialog);
        getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        initView();
        initClickListener();
    }

    private void initClickListener() {
        btnOk.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        initKeyboard();
        txtWifiName.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                new KeyboardUtil(act, txtWifiName, keyboardView).showKeyboard();
                return false;
            }
        });
        txtWifiPassword.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int inputBack = txtWifiPassword.getInputType();
                txtWifiPassword.setInputType(InputType.TYPE_NULL);
                new KeyboardUtil(act, txtWifiPassword, keyboardView).showKeyboard();
                txtWifiPassword.setInputType(inputBack);
                return false;
            }
        });
    }

    private void initKeyboard() {
        if (android.os.Build.VERSION.SDK_INT <= 10) {
            txtWifiName.setInputType(InputType.TYPE_NULL);
        } else {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            try {
                Class<EditText> cls = EditText.class;
                Method setSoftInputOnFocus;
//	setShowSoftInputOnFocus = cls.getMethod("setShowSoftInputOnFocus", boolean.class);
                setSoftInputOnFocus = cls.getMethod("setSoftInputOnFocus", boolean.class);

//4.0的是setShowSoftInputOnFocus,4.2的是setSoftInputOnFocus
                setSoftInputOnFocus.setAccessible(false);
                setSoftInputOnFocus.invoke(txtWifiName, false);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        new KeyboardUtil(act, txtWifiName, keyboardView).showKeyboard();
    }

    private void initView() {
        btnOk = (Button) findViewById(R.id.button_ok);
        btnCancel = (Button) findViewById(R.id.button_cancel);
        txtWifiName = (EditText) findViewById(R.id.txt_wifi_name);
        txtWifiPassword = (EditText) findViewById(R.id.txt_wifi_password);
        keyboardView = (KeyboardView) findViewById(R.id.keyboard_view);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_ok:
                actionOk();
                break;
            case R.id.button_cancel:
                listener.actionCancel();
                dismiss();
                break;
        }
    }

    private void actionOk() {
        String name = txtWifiName.getText().toString();
        String password = txtWifiPassword.getText().toString();
        if (TextUtils.isEmpty(name) | TextUtils.isEmpty(password)) {
            //名字不能为空
            return;
        } else {
            dismiss();
            WifiApAdmin.startWifiAp(act, name, password, null);
            //密码设置成功
            LogUtils.v("flow", "name:" + name + ";  password:" + password);
        }

    }

    public interface OnCancelOnListener {
        void actionCancel();
    }
}
