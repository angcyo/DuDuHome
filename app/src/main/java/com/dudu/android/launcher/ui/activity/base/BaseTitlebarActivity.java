package com.dudu.android.launcher.ui.activity.base;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

import com.dudu.android.launcher.R;
import com.dudu.android.launcher.utils.NetworkUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseTitlebarActivity extends BaseActivity {
    private Logger log;
    private BroadcastReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        super.onCreate(savedInstanceState);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
                R.layout.activity_custom_title);
        log = LoggerFactory.getLogger("net.conn");

        initTitleBar();

        receiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                initTitleBar();
            }
        };

        registerReceiver(receiver, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(receiver);
        super.onDestroy();
    }

    private void initTitleBar() {
        TextView textView = (TextView) getWindow().findViewById(
                R.id.signal_textview);
        String type = NetworkUtils.getCurrentNetworkType(mContext);
        log.debug("Net.Conn.change: {}", type);
        textView.setText(type);
    }

}
