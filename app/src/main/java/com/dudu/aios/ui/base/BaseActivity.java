package com.dudu.aios.ui.base;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;

import com.dudu.android.launcher.R;
import com.dudu.android.launcher.databinding.ActivityLayoutCommonBinding;
import com.dudu.android.launcher.utils.ActivitiesManager;

public abstract class BaseActivity extends Activity {

    protected ActivityLayoutCommonBinding baseBinding;

    protected ObservableFactory observableFactory;

    protected View childView;

    protected Handler mHandler;

    protected  VolBrightnessSetting volBrightnessSetting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        baseBinding = DataBindingUtil.setContentView(this, R.layout.activity_layout_common);

        childView = getChildView();
        baseBinding.mainContainer.addView(childView);

        observableFactory = ObservableFactory.getInstance();

        baseBinding.setTitle(observableFactory.getTitleObservable());

        baseBinding.setCommon(observableFactory.getCommonObservable());

        mHandler = new Handler();

        volBrightnessSetting = new VolBrightnessSetting(this, baseBinding.baseView);

    }

    protected abstract View getChildView();



    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivitiesManager.getInstance().removeActivity(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ActivitiesManager.getInstance().addActivity(this);

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return volBrightnessSetting.getOnTouchEventReturnFlag(event);
    }
}
