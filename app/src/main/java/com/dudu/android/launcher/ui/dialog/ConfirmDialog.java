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
public class ConfirmDialog extends Dialog implements View.OnClickListener {

    private Context mContext;

    private Button mConfirmView;

    private String mConfirmText;

    private OnConfirmClickListener mListener;

    public interface OnConfirmClickListener {

        void onConfirmClick();
    }

    public ConfirmDialog(Context context) {
        super(context, R.style.GeneralDialogStyle);
        mContext = context;
        mConfirmText = context.getString(R.string.ok);
    }

    public ConfirmDialog(Context context, String confirmText, String cancelText) {
        super(context, R.style.GeneralDialogStyle);
        mContext = context;

        if (TextUtils.isEmpty(confirmText)) {
            mConfirmText = context.getString(R.string.ok);
        } else {
            mConfirmText = confirmText;
        }
    }

    public void setOnConfirmClickListener(OnConfirmClickListener l) {
        mListener = l;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_confirm, null);
        setContentView(view);

        mConfirmView = (Button) view.findViewById(R.id.ok_view);
        mConfirmView.setText(mConfirmText);
        mConfirmView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        dismiss();

        if (mListener != null) {
            mListener.onConfirmClick();
        }
    }

}
