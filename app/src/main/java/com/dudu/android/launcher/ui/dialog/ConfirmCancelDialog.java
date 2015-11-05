package com.dudu.android.launcher.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import com.dudu.android.launcher.R;

/**
 * Created by 赵圣琪 on 2015/11/3.
 */
public class ConfirmCancelDialog extends Dialog implements View.OnClickListener {

    private Context mContext;

    private Button mConfirmView;

    private Button mCancelView;

    private String mConfirmText;

    private String mCancelText;

    private OnDialogButttonClickListener mListener;

    public interface OnDialogButttonClickListener {

        void onConfirmClick();

        void onCancelClick();
    }

    public ConfirmCancelDialog(Context context) {
        super(context, R.style.GeneralDialogStyle);
        mContext = context;
        mConfirmText = context.getString(R.string.ok);
        mCancelText = context.getString(R.string.cancel);
    }

    public ConfirmCancelDialog(Context context, String confirmText, String cancelText) {
        super(context, R.style.GeneralDialogStyle);
        mContext = context;

        if (TextUtils.isEmpty(confirmText)) {
            mConfirmText = context.getString(R.string.ok);
        } else {
            mConfirmText = confirmText;
        }

        if (TextUtils.isEmpty(cancelText)) {
            mCancelText = cancelText;
        } else {
            mCancelText = cancelText;
        }
    }

    public void setOnButtonClicked(OnDialogButttonClickListener l) {
        mListener = l;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_confirm_cancel, null);
        setContentView(view);

        mConfirmView = (Button) view.findViewById(R.id.ok_view);
        mConfirmView.setText(mConfirmText);
        mConfirmView.setOnClickListener(this);
        mCancelView = (Button) view.findViewById(R.id.cancel_view);
        mCancelView.setText(mCancelText);
        mCancelView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        dismiss();
        switch (v.getId()) {
            case R.id.ok_view:
                if (mListener != null) {
                    mListener.onConfirmClick();
                }

                break;
            case R.id.cancel_view:
                if (mListener != null) {
                    mListener.onCancelClick();
                }

                break;
        }
    }

}
