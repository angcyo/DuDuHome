package com.dudu.android.launcher.service.video;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.dudu.android.launcher.R;
import com.dudu.android.launcher.ui.activity.MainActivity;

public class WindowService extends Service implements View.OnClickListener {
    private WindowManager wManager;
    private WindowManager.LayoutParams mParams;
    private Button button;
    private boolean flag = true;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {

        wManager = (WindowManager) getApplicationContext().getSystemService(
                Context.WINDOW_SERVICE);
        mParams = new WindowManager.LayoutParams();
        mParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;// 系统提示window
        mParams.format = PixelFormat.TRANSLUCENT;// 支持透明
        //mParams.format = PixelFormat.RGBA_8888;
        mParams.flags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;// 焦点
        mParams.width = 100;//窗口的宽和高
        mParams.height = 100;
        mParams.x = 0;//窗口位置的偏移量
        mParams.y = 0;
        //mParams.alpha = 0.1f;//窗口的透明度
        button = new Button(this);
        button.setBackgroundResource(R.drawable.back_button_selector);
        button.setOnClickListener(this);
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (flag) {
            flag = false;
            wManager.addView(button, mParams);//添加窗口
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        if (button.getParent() != null)
            wManager.removeView(button);//移除窗口
        Log.v("ji..", "onDestroy");
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        if (v.equals(button)) {
            flag = true;
            if (button.getParent() != null) {
                Intent intent = new Intent(WindowService.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                stopSelf();
            }
        }
    }
}
