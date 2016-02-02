package com.dudu.android.launcher.ui.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.dudu.android.launcher.R;
import com.dudu.android.launcher.ui.activity.NearbyRepairActivity;

/**
 * Created by Administrator on 2015/11/5.
 */
public class CarCheckingDialog extends Dialog {

    private Activity mActivity;

    private Button mChangeButton;

    public CarCheckingDialog(Activity context) {
        super(context, R.style.WifiSettingDialogStyle);
        mActivity = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.warning_dialog);
        mChangeButton = (Button) findViewById(R.id.btn_change);
        mChangeButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mActivity, NearbyRepairActivity.class);
                mActivity.startActivity(intent);
                mActivity.finish();
            }
        });
    }
}
