package com.dudu.aios.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.dudu.aios.ui.bt.Contact;
import com.dudu.android.launcher.R;

public class ShowContactDetailDialog extends Dialog implements View.OnClickListener {

    private Button btnOk, btnCancel;

    private EditText txtName, txtNumber;

    private OnAddContactListener listener;

    public ShowContactDetailDialog(Context context) {
        super(context, R.style.show_contact_dialog_dialog);
    }

    public void setOnAddContactListener(OnAddContactListener listener) {
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_contact_detail_dialog);
        initView();
        initListener();
    }

    private void initListener() {
        btnOk.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
    }

    private void initView() {
        btnOk = (Button) findViewById(R.id.button_ok);
        btnCancel = (Button) findViewById(R.id.button_cancel);
        txtName = (EditText) findViewById(R.id.txt_name);
        txtNumber = (EditText) findViewById(R.id.txt_number);
        setCanceledOnTouchOutside(false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_ok:
                actionSave();
                break;
            case R.id.button_cancel:
                dismiss();
                break;
        }
    }

    private void actionSave() {
        String name = txtName.getText().toString();
        String number = txtNumber.getText().toString();
        if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(number)) {
            if (number.length() == 11) {
                Contact contact = new Contact();
                contact.setName(name);
                contact.setNumber(number);
                listener.saveContact(contact);
                dismiss();
            }
        }
    }

    public interface OnAddContactListener {
        void saveContact(Contact contact);
    }
}
