package com.dudu.aios.ui.activity;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.Window;

import com.dudu.aios.ui.fragment.MainFragment;
import com.dudu.android.launcher.R;

public class MainRecordActivity extends Activity {
    private FragmentTransaction ft;
    private FragmentManager fm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_record);
        initData();
    }

    private void initData() {
        fm = this.getFragmentManager();
        ft = fm.beginTransaction();
        ft.replace(R.id.container, new MainFragment());
        ft.commit();
    }
}
