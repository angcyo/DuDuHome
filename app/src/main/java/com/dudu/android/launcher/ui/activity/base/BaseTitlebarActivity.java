package com.dudu.android.launcher.ui.activity.base;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.os.Bundle;
import android.telephony.CellLocation;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.dudu.android.launcher.R;
import com.dudu.android.launcher.utils.LocationUtils;
import com.dudu.android.launcher.utils.LogUtils;
import com.dudu.android.launcher.utils.NetworkUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;

public abstract class BaseTitlebarActivity extends BaseActivity {

    public static final String ACTION_CONNECTIVITY_CHANGE = "android.net.conn.CONNECTIVITY_CHANGE";

    private SignalReceiver mGsmReceiver;

    private ImageView gpsImg;

    private int satellite = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        super.onCreate(savedInstanceState);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
                R.layout.activity_custom_title);

        mGsmReceiver = new SignalReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_CONNECTIVITY_CHANGE);
        registerReceiver(mGsmReceiver, intentFilter);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mGsmReceiver);
        super.onDestroy();
    }

    private void initTitleBar() {
        TextView textView = (TextView) getWindow().findViewById(
                R.id.signal_textview);
        String type = NetworkUtils.getCurrentNetworkType(mContext);
        textView.setText(type);

        gpsImg = (ImageView)findViewById(R.id.gps_img);
    }

    private class SignalReceiver extends  BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            initTitleBar();
        }
    }


    public void onEventMainThread(GpsStatus gpsStatus){
        int maxSatellites = gpsStatus.getMaxSatellites();
        Iterator<GpsSatellite> iters = gpsStatus.getSatellites()
                .iterator();
        satellite = 0;
        while (iters.hasNext() && satellite <= maxSatellites) {
            satellite++;
        }
        if(satellite > 0 &&(!LocationUtils.getInstance(this).getLocProvider().equals("lbs"))){
            gpsImg.setBackgroundResource(R.drawable.gps_nromal);
        }else {
            gpsImg.setBackgroundResource(R.drawable.gps_abnormal);
        }

    }

}
