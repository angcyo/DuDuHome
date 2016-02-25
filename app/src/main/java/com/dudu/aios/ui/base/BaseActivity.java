package com.dudu.aios.ui.base;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import com.dudu.android.launcher.R;
import com.dudu.android.launcher.databinding.ActivityLayoutCommonBinding;
import com.dudu.android.launcher.utils.ActivitiesManager;

import org.wysaid.view.CameraRecordGLSurfaceView;

public abstract class BaseActivity extends Activity {
    public static final String effectConfigs[] = {
            "",
            "@beautify bilateral 10 4 1 @style haze -0.5 -0.5 1 1 1 @curve RGB(0, 0)(94, 20)(160, 168)(255, 255) @curve R(0, 0)(129, 119)(255, 255)B(0, 0)(135, 151)(255, 255)RGB(0, 0)(146, 116)(255, 255)",
            "#unpack @blur lerp 0.5", //可调节模糊强度
            "@blur lerp 1", //可调节混合强度
            "#unpack @dynamic wave 1", //可调节速度
            "@dynamic wave 0.5",       //可调节混合
    };

    protected ActivityLayoutCommonBinding baseBinding;

    protected ObservableFactory observableFactory;

    protected View childView;

    protected Handler mHandler;

    protected  VolBrightnessSetting volBrightnessSetting;

    protected   CameraRecordGLSurfaceView cameraView;

//    protected ImageView rearCameraPreview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        baseBinding = DataBindingUtil.setContentView(this, R.layout.activity_layout_common);

        childView = getChildView();
        baseBinding.mainContainer.addView(childView);

        observableFactory = ObservableFactory.getInstance();

        baseBinding.setTitle(observableFactory.getTitleObservable());

        baseBinding.setCommon(observableFactory.getCommonObservable(baseBinding));

        mHandler = new Handler();

        volBrightnessSetting = new VolBrightnessSetting(this, baseBinding.baseView);

//        rearCameraPreview = baseBinding.rearCameraPreview;
    }

    protected void initPreview(){
        cameraView = baseBinding.myGLSurfaceView;
        cameraView.setVisibility(View.VISIBLE);
        cameraView.presetCameraForward(false);
        cameraView.presetRecordingSize(1920, 480);
//        cameraView.setZOrderOnTop(false);
//        cameraView.setZOrderMediaOverlay(true);

        cameraView.setFilterWithConfig(effectConfigs[2]);

        cameraView.setOnCreateCallback(new CameraRecordGLSurfaceView.OnCreateCallback() {
            @Override
            public void createOver(boolean success) {
                if (success) {
                    Log.i("drivevideo", "view 创建成功");
                    cameraView.setFilterWithConfig(effectConfigs[2]);
                } else {
                    Log.e("drivevideo", "view 创建失败!");
                }
            }
        });
    }

    public void setNoBlur(){
        cameraView.setFilterWithConfig(effectConfigs[0]);
    }

    public void setBlur(){
        cameraView.setFilterWithConfig(effectConfigs[2]);
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
