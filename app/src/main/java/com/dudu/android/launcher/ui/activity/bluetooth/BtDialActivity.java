package com.dudu.android.launcher.ui.activity.bluetooth;


import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.dudu.android.launcher.R;
import com.dudu.android.launcher.ui.activity.base.BaseTitlebarActivity;
import com.dudu.android.launcher.ui.view.DigitsEditText;

public class BtDialActivity extends BaseTitlebarActivity implements
        View.OnClickListener, TextWatcher {

    private EditText mDigits;

    private Button mDialButton;

    private Button mBackButton;

    private Button mDeleteButton;

    @Override
    public int initContentView() {
        return R.layout.activity_blue_tooth_dial;
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        mDigits = (DigitsEditText) findViewById(R.id.dial_digits);
        mDialButton = (Button) findViewById(R.id.dial_button);
        mBackButton = (Button) findViewById(R.id.back_button);
        mDeleteButton = (Button) findViewById(R.id.delete_button);
    }

    @Override
    public void initListener() {
        mDigits.setOnClickListener(this);
        mDigits.addTextChangedListener(this);
        mDigits.setCursorVisible(false);
        mDialButton.setOnClickListener(this);
        mBackButton.setOnClickListener(this);
        mDeleteButton.setOnClickListener(this);
    }

    @Override
    public void initDatas() {

    }

    public void onDialButtonClick(View view) {
        if (mDeleteButton.getVisibility() == View.INVISIBLE) {
            mDeleteButton.setVisibility(View.VISIBLE);
        }

        switch (view.getId()) {
            case R.id.number_button01:
                handleDialButtonClick("1");
                break;
            case R.id.number_button02:
                handleDialButtonClick("2");
                break;
            case R.id.number_button03:
                handleDialButtonClick("3");
                break;
            case R.id.well_button:
                handleDialButtonClick("#");
                break;
            case R.id.number_button04:
                handleDialButtonClick("4");
                break;
            case R.id.number_button05:
                handleDialButtonClick("5");
                break;
            case R.id.number_button06:
                handleDialButtonClick("6");
                break;
            case R.id.number_button0:
                handleDialButtonClick("0");
                break;
            case R.id.number_button07:
                handleDialButtonClick("7");
                break;
            case R.id.number_button08:
                handleDialButtonClick("8");
                break;
            case R.id.number_button09:
                handleDialButtonClick("9");
                break;
            case R.id.rice_button:
                handleDialButtonClick("*");
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dial_button:
                doDial();
                break;
            case R.id.back_button:
                finish();
                break;
            case R.id.delete_button:
                removeSelectedDigit();
                break;
        }
    }

    private void handleDialButtonClick(String digit) {
        final int length = mDigits.length();
        final int start = mDigits.getSelectionStart();
        final int end = mDigits.getSelectionEnd();
        if (length == start && length == end) {
            mDigits.setCursorVisible(false);
        }

        if (start < end) {
            mDigits.getEditableText().replace(start, end, digit);
        } else {
            mDigits.getEditableText().insert(mDigits.getSelectionEnd(), digit);
        }
    }

    private void removeSelectedDigit() {
        final int length = mDigits.length();
        final int start = mDigits.getSelectionStart();
        final int end = mDigits.getSelectionEnd();
        if (start < end) {
            mDigits.getEditableText().replace(start, end, "");
        } else {
            if (mDigits.isCursorVisible()) {
                if (end > 0) {
                    mDigits.getEditableText().replace(end - 1, end, "");
                }
            } else {
                if (length > 1) {
                    mDigits.getEditableText().replace(length - 1, length, "");
                } else {
                    mDigits.getEditableText().clear();
                }
            }
        }

        if (isDigitsEmpty()) {
            mDeleteButton.setVisibility(View.INVISIBLE);
        }
    }

    private void doDial() {
        String dialString = mDigits.getText().toString();
        if (TextUtils.isEmpty(dialString)) {
            return;
        }

        Intent intent = new Intent("wld.btphone.bluetooth.DIAL");
        intent.putExtra("dial_number", dialString);
        sendBroadcast(intent);
    }

    public boolean isDigitsEmpty() {
        return mDigits.length() == 0;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
