package com.android.agebluetest;


import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;
import java.util.Timer;
import java.util.TimerTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainActivity extends Activity {
    private MainActivity mActivity;
    private Timer timer ;
    private TimerTask timerTask;
    private Handler handler;
    private TextView txtSuccessCount, txtFailCount, txtSuccessProbability;
    private int successCount ;
    private int failCount ;
    private Logger log;
    private int log_step;
    private volatile static int hasRun=0;
    BluetoothManager bluetoothManager;
    BluetoothAdapter bluetoothAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity=this;
        setContentView(R.layout.activity_main);
        initView();

        log = LoggerFactory.getLogger("init.test.blue");
        log.debug("[blue-test][{}]onCreate()", hasRun);
        if(hasRun==0){
            log.debug("[blue-test]before hasRun[{}]", hasRun);
            hasRun=1;
            log.debug("[blue-test]after hasRun[{}]", hasRun);
            log_step = 0;
            startTest(null);
        }
    }

    private void initData() {
       bluetoothManager = (BluetoothManager) mActivity.getSystemService(
                Context.BLUETOOTH_SERVICE);
       bluetoothAdapter = bluetoothManager.getAdapter();
        handler = new MyHandler();
        timer=new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                if (!isBlueEnable()) {
                    log.debug("[blue-test][{}]蓝牙关闭成功", log_step++);
                    log.debug("[blue-test][{}]准备打开蓝牙", log_step++);
                    openBlueTooth();
                } else {
                    log.debug("[" +
                            "blue-test][{}]蓝牙关闭失败", log_step++);
                }
                handler.sendEmptyMessageDelayed(0, 10000);
            }
        };
        timer.schedule(timerTask, 5000, 15000);
    }


    private void checkBlueEnable() {
        if (isBlueEnable()) {
            successCount++;
            log.debug("[blue-test][{}]蓝牙打开成功：{}次/{}", log_step++, successCount, successCount + failCount);
            log.debug("[blue-test][{}]准备关闭蓝牙", log_step++);
            bluetoothAdapter.disable();
        } else {
            failCount++;
            log.debug("[blue-test][{}]蓝牙打开失败：{}次/{}", log_step++, failCount, successCount + failCount);
        }
    }

    private void openBlueTooth() {
        if (bluetoothAdapter != null) {
            if (!bluetoothAdapter.isEnabled()) {
                bluetoothAdapter.enable();
            }
        }
    }
    private boolean isBlueEnable(){

        if(bluetoothAdapter!=null){
        if(bluetoothAdapter.isEnabled()){
            return true;
        }else {
            return false;
        }
        }else {
            return false;
        }

    }
    private void drawData(){
        initView();
        txtSuccessCount.setText(successCount + "");
        txtFailCount.setText(failCount + "");
        float probability = successCount / (successCount + failCount) * 100;
        txtSuccessProbability.setText(probability + "" + "%");
        if (successCount>=30||failCount >= 30) {
            //timer.cancel();
            log.error("成功的次数：{};失败的次数：{}", successCount, failCount);
            hasRun=0;
            reboot();
        }
    }
    public void reboot() {
        set(this, "persist.sys.boot", "reboot");
    }
    public void startTest(View view) {
        log_step = 0;
        log.debug("[blue-test][{}]new Test", log_step++);
        log.debug("[blue-test][{}]确保蓝牙已经关闭", log_step++);
        openBlueTooth();
        initData();
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
            checkBlueEnable();
            drawData();
            handler.removeMessages(0);
        }
    }

   private void initView() {
        txtSuccessCount = (TextView) findViewById(R.id.success_count);
        txtFailCount = (TextView) findViewById(R.id.fail_count);
        txtSuccessProbability = (TextView) findViewById(R.id.success_probability);
    }
}
