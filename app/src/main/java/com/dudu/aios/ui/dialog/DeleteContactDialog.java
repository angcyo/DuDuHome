package com.dudu.aios.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.dudu.aios.ui.bt.Contact;
import com.dudu.android.launcher.R;

public class DeleteContactDialog extends Dialog implements View.OnClickListener {

    private Button btnOk, btnCancel;

    private OnDialogButtonClickListener listener;

    public DeleteContactDialog(Context context) {
        super(context, R.style.show_contact_dialog_dialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.delete_contact_dialog);
        initView();
        initListener();
    }

    public void setOnDialogButtonClickListener(OnDialogButtonClickListener listener) {
        this.listener = listener;
    }

    private void initListener() {
        btnOk.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
    }

    private void initView() {
        btnOk = (Button) findViewById(R.id.button_ok);
        btnCancel = (Button) findViewById(R.id.button_cancel);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_ok:
                listener.onConfirmClick();
                dismiss();
                break;
            case R.id.button_cancel:
                dismiss();
                break;
        }
    }

    public interface OnDialogButtonClickListener {
        void onConfirmClick();
    }
}
