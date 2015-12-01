package com.android.agewifitest;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.TextView;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.util.Timer;
import java.util.TimerTask;
import org.slf4j.Logger;

public class MainActivity extends Activity {
    private MainActivity mActivity;
    private Timer timer ;
    private TimerTask timerTask;
    private Handler handler;
    private TextView txtSuccessCount, txtFailCount, txtSuccessProbability;
    private int successCount;
    private int failCount ;
    private File file;
    private Logger log;
    private int log_step;

    private volatile static int hasRun=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = this;
        setContentView(R.layout.activity_main);
        initView();
        log = LoggerFactory.getLogger("init.test.wifi");
        log.debug("[wifi-test][{}]onCreate()", log_step++);
       // hasRun=SharedPreferencesUtil.getHasRunCode(mActivity);
        log.debug("[wifi-test][{}]hadRun:",hasRun);
        if(hasRun==0){
            //SharedPreferencesUtil.putHasRunCode(mActivity,1);
            hasRun=1;
            startTest(null);
        }
    }

    public void startTest(View view) {
        log_step = 0;
        log.debug("[wifi-test][{}]new Test", log_step++);
        log.debug("[wifi-test][{}]确保AP已经关闭", log_step++);
        WifiApAdmin.closeWifiAp(mActivity);
//        WifiApAdmin.initWifiApState(mActivity);

        initData();
    }

    private void initData() {
        handler = new MyHandler();
        timer=new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                if (!WifiApAdmin.isWifiApEnabled(mActivity)) {
                    log.debug("[wifi-test][{}]热点关闭成功", log_step++);
                    log.debug("[wifi-test][{}]准备打开热点", log_step++);
                    WifiApAdmin.startWifiAp(mActivity);
                } else {
                    log.debug("[wifi-test][{}]热点关闭失败", log_step++);
                }
                handler.sendEmptyMessageDelayed(0, 15000);
            }
        };
        timer.schedule(timerTask, 5000, 20000);
    }

    private void initView() {
        txtSuccessCount = (TextView) findViewById(R.id.success_count);
        txtFailCount = (TextView) findViewById(R.id.fail_count);
        txtSuccessProbability = (TextView) findViewById(R.id.success_probability);
    }

    public void stopTest(View view) {
        if(handler!=null&&timer!=null){
            handler.removeMessages(0);
            timer.cancel();
            handler=null;
            timerTask=null;
            successCount=0;
            failCount=0;
            txtSuccessCount.setText("");
            txtFailCount.setText("");
            txtSuccessProbability.setText("");
        }
    }

    public void setting(View view) {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName("com.android.settings",
                "com.android.settings.Settings"));
        startActivity(intent);
    }

    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            checkWifiEnable();
            drawData();
            handler.removeMessages(0);
        }
    }

    private void checkWifiEnable() {
        if (WifiApAdmin.isWifiApEnabled(mActivity)) {
            successCount++;
            log.debug("[wifi-test][{}]热点打开成功：{}次/{}", log_step++, successCount, successCount + failCount);
            log.debug("[wifi-test][{}]准备关闭热点", log_step++);
            WifiApAdmin.closeWifiAp(mActivity);
        } else {
            failCount++;
            log.debug("[wifi-test][{}]热点打开失败：{}次/{}", log_step++, failCount, successCount + failCount);
        }

    }

    private boolean checkPortal() {
        file = new File("/data/local/tmp", "ndsctl.sock");
        if (file.exists()) {
            return true;
        }
        return false;
    }

    private void drawData() {
        initView();
        txtSuccessCount.setText(successCount + "");
        txtFailCount.setText(failCount + "");
        float probability = successCount / (successCount + failCount) * 100;
        txtSuccessProbability.setText(probability + "" + "%");
        if (successCount>=30||failCount>=30) {
            //timer.cancel();
            log.error("成功的次数：{};失败的次数：{}", successCount, failCount);
            //SharedPreferencesUtil.putHasRunCode(mActivity,0);
            hasRun=0;
            reboot();
        }
    }
    public void reboot() {
        set(this, "persist.sys.boot", "reboot");
    }

    public void set(Context context, String key, String val) {
        Intent intent = new Intent("dudu.android.SET_PROP");
        intent.putExtra("prop", key);
        intent.putExtra("val", val);
        try {
            context.sendBroadcast(intent);
        } catch (ActivityNotFoundException exception) {

        }
    }

}
