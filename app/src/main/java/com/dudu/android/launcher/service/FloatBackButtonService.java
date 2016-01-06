package com.dudu.android.launcher.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.dudu.android.launcher.R;
import com.dudu.android.launcher.ui.activity.MainActivity;
import com.dudu.android.launcher.utils.ActivitiesManager;
import com.dudu.navi.event.NaviEvent;

import org.scf4a.Event;

import de.greenrobot.event.EventBus;


/**
 * Created by lxh on 2015/11/24.
 */
public class FloatBackButtonService extends Service{

    // 悬浮窗View的参数
    private WindowManager.LayoutParams windowParams;

    // 用于控制在屏幕上添加或移除悬浮窗
    private WindowManager windowManager;

    private View floatButton;

    private boolean isShow = false;

    private Handler mHandler;
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        EventBus.getDefault().register(this);

        initButton();

        mHandler = new Handler();
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    private void initButton(){

        floatButton = new Button(this);
        floatButton.setBackgroundResource(R.drawable.back_button_selector);
        if (windowParams == null) {
            windowParams = new WindowManager.LayoutParams();
            windowParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
            windowParams.format = PixelFormat.RGBA_8888;
            windowParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                    | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
            windowParams.width = 90;
            windowParams.height = 73 ;

            windowParams.x = -getWmWidth()/2;
            windowParams.y = 0;
            windowParams.alpha = 1.0f;
        }
        floatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
    }
    private int getWmWidth() {
        return getWindowManager().getDefaultDisplay().getWidth();// 屏幕宽度
    }

    private synchronized WindowManager getWindowManager() {
        if (windowManager == null) {
            windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        }
        return windowManager;
    }

    public void onEventMainThread(NaviEvent.FloatButtonEvent event){

        switch (event){
            case SHOW:
                if(windowManager!=null&&floatButton!=null){
                    isShow = true;
                    windowManager.addView(floatButton,windowParams);
                }

                break;
            case HIDE:
                if(windowManager!=null&&floatButton!=null&&isShow){
                    isShow = false;
                    windowManager.removeView(floatButton);
                }
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(!ActivitiesManager.getInstance().isTopActivity(FloatBackButtonService.this, "com.dudu.android.launcher")){
                            EventBus.getDefault().post(NaviEvent.FloatButtonEvent.SHOW);
                        }
                    }
                }, 3000);
                break;
        }
    }
}
