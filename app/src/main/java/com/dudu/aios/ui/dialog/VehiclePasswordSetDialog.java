package com.dudu.aios.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.dudu.android.launcher.R;


public class VehiclePasswordSetDialog extends Dialog implements View.OnClickListener {

    private Button btnCancel, btnOk, btnSend;

    private Button btZero, btOne, btTwo, btThree, btFour, btFive, btSix, btSeven, btEight, btNine, btWell, btRice;

    private EditText txtVerificationCode, txtNewPassword;

    public VehiclePasswordSetDialog(Context context) {
        super(context, R.style.PasswordSetDialogStyle);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vehicle_password_set_dialog);
        initView();
        initListener();
    }

    private void initListener() {
        btnOk.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        btnSend.setOnClickListener(this);
        btZero.setOnClickListener(this);
        btOne.setOnClickListener(this);
        btTwo.setOnClickListener(this);
        btThree.setOnClickListener(this);
        btFour.setOnClickListener(this);
        btFive.setOnClickListener(this);
        btSix.setOnClickListener(this);
        btSeven.setOnClickListener(this);
        btEight.setOnClickListener(this);
        btNine.setOnClickListener(this);
        btWell.setOnClickListener(this);
        btRice.setOnClickListener(this);
    }

    private void initView() {
        btnOk = (Button) findViewById(R.id.button_ok);
        btnCancel = (Button) findViewById(R.id.button_cancel);
        btnSend = (Button) findViewById(R.id.button_send);
        txtVerificationCode = (EditText) findViewById(R.id.txt_verification_code);
        txtNewPassword = (EditText) findViewById(R.id.txt_new_password);

        btZero = (Button) findViewById(R.id.button_zero);
        btOne = (Button) findViewById(R.id.button_one);
        btTwo = (Button) findViewById(R.id.button_two);
        btThree = (Button) findViewById(R.id.button_three);
        btFour = (Button) findViewById(R.id.button_four);
        btFive = (Button) findViewById(R.id.button_five);
        btSix = (Button) findViewById(R.id.button_six);
        btSeven = (Button) findViewById(R.id.button_seven);
        btEight = (Button) findViewById(R.id.button_eight);
        btNine = (Button) findViewById(R.id.button_nine);
        btWell = (Button) findViewById(R.id.button_well);
        btRice = (Button) findViewById(R.id.button_rice);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_ok:
                break;
            case R.id.button_send:
                sendSMS();
                break;
            case R.id.button_cancel:
                dismiss();
                break;
            case R.id.button_one:
                handleDialButtonClick("1");
                break;
            case R.id.button_two:
                handleDialButtonClick("2");
                break;
            case R.id.button_three:
                handleDialButtonClick("3");
                break;
            case R.id.button_four:
                handleDialButtonClick("4");
                break;
            case R.id.button_five:
                handleDialButtonClick("5");
                break;
            case R.id.button_six:
                handleDialButtonClick("6");
                break;
            case R.id.button_seven:
                handleDialButtonClick("7");
                break;
            case R.id.button_eight:
                handleDialButtonClick("8");
                break;
            case R.id.button_nine:
                handleDialButtonClick("9");
                break;
            case R.id.button_zero:
                handleDialButtonClick("0");
                break;
            case R.id.button_well:
                handleDialButtonClick("#");
                break;
            case R.id.button_rice:
                handleDialButtonClick("*");
                break;
        }
    }

    private void handleDialButtonClick(String digit) {
        final int length = txtNewPassword.length();
        final int start = txtNewPassword.getSelectionStart();
        final int end = txtNewPassword.getSelectionEnd();
        if (length == start && length == end) {
            txtNewPassword.setCursorVisible(false);
        }

        if (start < end) {
            txtNewPassword.getEditableText().replace(start, end, digit);
        } else {
            txtNewPassword.getEditableText().insert(txtNewPassword.getSelectionEnd(), digit);
        }
    }

    private void sendSMS() {
        txtVerificationCode.setText("123456");
    }
}
