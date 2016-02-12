package com.dudu.aios.ui.fragment.base;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.provider.Settings;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import com.dudu.android.launcher.utils.DensityUtil;

import org.scf4a.Event;

/**
 * Created by dengjun on 2016/1/23.
 * Description :
 */
public class VolBrightnessSetting implements GestureDetector.OnGestureListener, View.OnTouchListener {
    private Activity activity;

    private static final int GESTURE_MODIFY_VOLUME = 1;
    private static final int GESTURE_MODIFY_BRIGHTNESS = 2;
    private int GESTURE_FLAG = 0;// 1，调节音量 2，调节亮度

    private AudioManager audiomanager;
    private int maxVolume, currentVolume;
    private static final float STEP_VOLUME = 2f;// 协调音量滑动时的步长，避免每次滑动都改变，导致改变过快

    public static int staticBrightness = 255;
    private GestureDetector gestureDetector;
    private int maxBrightness, currentBrightness;
    private static final float STEP_BRIGHTNESS = 2f;// 协调亮度滑动时的步长，避免每次滑动都改变，导致改变过快
    int width;


    public VolBrightnessSetting(Activity activity, View view) {
        this.activity = activity;

        audiomanager = (AudioManager) activity.getSystemService(Context.AUDIO_SERVICE);
        maxVolume = audiomanager.getStreamMaxVolume(AudioManager.STREAM_MUSIC); // 获取系统最大音量
        currentVolume = audiomanager.getStreamVolume(AudioManager.STREAM_MUSIC); // 获取当前值

        gestureDetector = new GestureDetector(activity, this);
        gestureDetector.setIsLongpressEnabled(true);
        view.setOnTouchListener(this);

        maxBrightness = 255;
        currentBrightness = Settings.System.getInt(activity.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, 255);//取得当前亮度

        if (currentBrightness < 80) {
            currentBrightness = 80;
        }


        WindowManager wm = activity.getWindowManager();
        width = wm.getDefaultDisplay().getWidth();


//        Log.i("lcc", "staticBrightness =" + staticBrightness);
//        synchroBrightness(staticBrightness);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }
    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        switch (GESTURE_FLAG) {  // 如果每次触摸屏幕后第一次scroll是调节音量，那之后的scroll事件都处理音量调节，直到离开屏幕执行下一次操作
            case GESTURE_MODIFY_VOLUME:
                setVolume(distanceX, distanceY);    //设置音量
                break;
            case GESTURE_MODIFY_BRIGHTNESS: //设置亮度
                setBrightness(distanceX, distanceY);
                break;
        }
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        float touchX = event.getX();
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (touchX > (float) width / 2) {
                GESTURE_FLAG = GESTURE_MODIFY_VOLUME;
            } else {
                GESTURE_FLAG = GESTURE_MODIFY_BRIGHTNESS;
            }
        }
        if (event.getAction() == MotionEvent.ACTION_UP) {
            GESTURE_FLAG = 0;// 手指离开屏幕后，重置调节音量或进度的标志
        }
        return gestureDetector.onTouchEvent(event);
    }

    private void setVolume(float distanceX, float distanceY) {
        currentVolume = audiomanager.getStreamVolume(AudioManager.STREAM_MUSIC); // 获取当前值
        if (Math.abs(distanceY) > Math.abs(distanceX)) {
            if ((distanceY >= DensityUtil.dip2px(activity, STEP_VOLUME)) && (currentVolume < maxVolume)) {// 音量调大   纵向移动大于横向移动
                currentVolume++;  // 音量调大,注意横屏时的坐标体系,尽管左上角是原点，但横向向上滑动时distanceY为正；为避免调节过快，distanceY应大于一个设定值
            } else if ((distanceY <= -DensityUtil.dip2px(activity, STEP_VOLUME)) && (currentVolume > 0)) {
                currentVolume--;// 音量调小
            }
            Log.i("lcc", "currentVolume =" + currentVolume+ "  maxVolume = "+ maxVolume);
            audiomanager.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume, AudioManager.FLAG_SHOW_UI);
        }
    }

    private void setBrightness(float distanceX, float distanceY) {
        currentBrightness = Settings.System.getInt(activity.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, 255);//取得当前亮度
        if (Math.abs(distanceY) > Math.abs(distanceX)) {// 亮度调大
            if ((distanceY >= DensityUtil.dip2px(activity, STEP_BRIGHTNESS)) && (currentBrightness < maxBrightness)) {// 纵向移动大于横向移动
                currentBrightness = currentBrightness + 17;// 亮度调大,注意横屏时的坐标体系,尽管左上角是原点，但横向向上滑动时distanceY为正 // 为避免调节过快，distanceY应大于一个设定值
                if (currentBrightness > maxBrightness) {
                    currentBrightness = maxBrightness;
                }
            } else if (distanceY <= -DensityUtil.dip2px(activity, STEP_BRIGHTNESS)) {// 亮度调小
                if (currentBrightness >= 17) {
                    currentBrightness = currentBrightness - 17;
                    if (currentBrightness < 17) {
                        currentBrightness = 0;
                    }
                }
            }
            synchroBrightness(currentBrightness);
        }
    }

    public void synchroBrightness(int brightness){
        Settings.System.putInt(activity.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, brightness);
        currentBrightness = Settings.System.getInt(activity.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, -1);

//        Log.i("lcc", "brightness =" + brightness+"  currentBrightness = "+ currentBrightness);

        staticBrightness = currentBrightness;

        WindowManager.LayoutParams wl = activity.getWindow().getAttributes();
        float tmpFloat = (float) brightness / 255;
        if (tmpFloat > 0 && tmpFloat <= 1) {
            wl.screenBrightness = tmpFloat;
        }
        activity.getWindow().setAttributes(wl);
    }


    public boolean getOnTouchEventReturnFlag(MotionEvent motionEvent){
        return gestureDetector.onTouchEvent(motionEvent);
    }
}
