package com.dudu.android.launcher.ui.activity.bluetooth;


import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.dudu.aios.ui.base.BaseActivity;
import com.dudu.android.launcher.R;
import com.dudu.android.launcher.ui.view.DigitsEditText;
import com.dudu.android.launcher.utils.LogUtils;

public class BtDialActivity extends BaseActivity implements
        View.OnClickListener, TextWatcher {

    private EditText mDigits;

    private Button mDialButton;

    private ImageButton mBackButton, mDialKeyboardButton, mContactsButton, mDeleteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initListener();
    }

    @Override
    protected View getChildView() {
        return LayoutInflater.from(this).inflate(R.layout.activity_blue_tooth_dial, null);
    }

    private void initView() {
        mDigits = (DigitsEditText) findViewById(R.id.dial_digits);
        mDialButton = (Button) findViewById(R.id.button_dial);
        mBackButton = (ImageButton) findViewById(R.id.back_button);
        mDeleteButton = (ImageButton) findViewById(R.id.delete_button);
        mDialKeyboardButton = (ImageButton) findViewById(R.id.button_dial_keyboard);
        mContactsButton = (ImageButton) findViewById(R.id.button_contacts);
    }


    public void initListener() {
        mDigits.setOnClickListener(this);
        mDigits.addTextChangedListener(this);
        mDigits.setCursorVisible(false);
        mDialButton.setOnClickListener(this);
        mBackButton.setOnClickListener(this);
        mDeleteButton.setOnClickListener(this);
        mDialKeyboardButton.setOnClickListener(this);
        mContactsButton.setOnClickListener(this);
    }


    public void onDialButtonClick(View view) {
        if (mDeleteButton.getVisibility() == View.INVISIBLE) {
            mDeleteButton.setVisibility(View.VISIBLE);
        }
        handleDialButtonClick((String) view.getTag());
        LogUtils.v("keyboard", "--" + view.getTag());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_dial:
                doDial();
                break;
            case R.id.back_button:
                finish();
                break;
            case R.id.delete_button:
                removeSelectedDigit();
                break;
            case R.id.button_dial_keyboard:
                startActivity(new Intent(this, BtInCallActivity.class));
                break;
            case R.id.button_contacts:
                startActivity(new Intent(this, BtContactsActivity.class));
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
        // intent.putExtra("dial_number", "18665323029");
        String number = dialString.replace(" ", "");
        intent.putExtra("dial_number", number);
        sendBroadcast(intent);
        startActivity(new Intent(this, BtCallingActivity.class).putExtra("number", dialString));
    }

    public boolean isDigitsEmpty() {
        return mDigits.length() == 0;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (s == null || s.length() == 0) return;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            if (i != 3 && i != 8 && s.charAt(i) == ' ') {
                continue;
            } else {
                sb.append(s.charAt(i));
                if ((sb.length() == 4 || sb.length() == 9) && sb.charAt(sb.length() - 1) != ' ') {
                    sb.insert(sb.length() - 1, ' ');
                }
            }
        }
        if (!sb.toString().equals(s.toString())) {
            int index = start + 1;
            if (sb.charAt(start) == ' ') {
                if (before == 0) {
                    index++;
                } else {
                    index--;
                }
            } else {
                if (before == 1) {
                    index--;
                }
            }
            mDigits.setText(sb.toString());
            mDigits.setSelection(index);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

}
