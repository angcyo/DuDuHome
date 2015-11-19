package com.dudu.android.launcher.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.dudu.android.launcher.R;

/**
 * Created by ZACK on 2015/11/16.
 * 没有蓝牙是弹出的dialog
 */
public class BluetoothAlertDialog extends Dialog implements View.OnClickListener{

    public BluetoothAlertDialog(Context context) {
        super(context, R.style.GeneralDialogStyle);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.blue_teeth_error_prompt);
        LinearLayout linearLayout=(LinearLayout)findViewById(R.id.layout_blue_teeth);
        setCanceledOnTouchOutside(false);
       linearLayout.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.layout_blue_teeth:
               // dismiss();
                break;
        }
    }

}
