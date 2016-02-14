package com.dudu.aios.ui.map;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

import com.dudu.android.launcher.R;

/**
 * Created by lxh on 2016/2/14.
 */
public class NavigationActivity extends Activity{


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.gaode_navigation_layout);
    }
}
