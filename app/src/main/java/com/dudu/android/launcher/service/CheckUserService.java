package com.dudu.android.launcher.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;

import com.dudu.android.launcher.R;
import com.dudu.android.launcher.utils.Constants;
import com.dudu.android.launcher.utils.SharedPreferencesUtils;
import com.dudu.android.launcher.utils.Utils;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by Eaway on 2015/11/29.
 */
public class CheckUserService {

    private Context context;

    private ScheduledExecutorService sendServiceThreadPool = null;

    private static CheckUserService service;

    public static CheckUserService getInstance(Context context) {
        if (service == null) {
            service = new CheckUserService(context);
        }
        return service;
    }

    private CheckUserService(Context context) {
        this.context = context;
        sendServiceThreadPool = Executors.newScheduledThreadPool(1);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            checkUserStateIsActive();
        }
    };

    private Thread sendServiceThread = new Thread() {
        @Override
        public void run() {
            try {
                handler.sendEmptyMessage(0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    public void checkUserStateIsActive() {
        if (Utils.isNetworkConnected(context)) {
            if (!Utils.checkUserStateIsActive(context)) {
//                Toast.makeText(context, context.getString(R.string.user_is_not_active), Toast.LENGTH_SHORT).show();
            } else {
                SharedPreferencesUtils.putBooleanValue(context, Constants.KEY_USER_IS_ACTIVE, true);
                stopSendService();
            }
        } else {
            registerReceiver();
        }
    }

    public void startService() {
        sendServiceThreadPool.scheduleAtFixedRate(sendServiceThread, 1, 10, TimeUnit.SECONDS);
    }

    public void stopSendService() {
        if (sendServiceThreadPool != null && !sendServiceThreadPool.isShutdown()) {
            sendServiceThreadPool.shutdown();
            sendServiceThreadPool = null;
        }
    }

    public void registerReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.VIDEO_PREVIEW_BROADCAST);
        context.registerReceiver(receiver, intentFilter);
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            boolean connected = Utils.isNetworkConnected(context);
            if (connected) {
                checkUserStateIsActive();
            }
        }
    };

}
