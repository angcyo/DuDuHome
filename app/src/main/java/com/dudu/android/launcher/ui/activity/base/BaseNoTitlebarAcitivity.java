package com.dudu.android.launcher.ui.activity.base;

import android.os.Bundle;
import android.view.Window;

public abstract class BaseNoTitlebarAcitivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	requestWindowFeature(Window.FEATURE_NO_TITLE);
    	super.onCreate(savedInstanceState);
    }

}
