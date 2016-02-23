package com.dudu.android.launcher.ui.activity.bluetooth;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.dudu.aios.ui.base.BaseActivity;
import com.dudu.android.launcher.R;

public class BtContactsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected View getChildView() {
        return LayoutInflater.from(this).inflate(R.layout.activity_bt_contacts, null);
    }
}
