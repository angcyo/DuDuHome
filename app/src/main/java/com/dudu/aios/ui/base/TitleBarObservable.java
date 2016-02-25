package com.dudu.aios.ui.base;

import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.graphics.drawable.Drawable;
import android.location.GpsSatellite;
import android.location.GpsStatus;

import com.dudu.android.launcher.LauncherApplication;
import com.dudu.android.launcher.R;
import com.dudu.android.launcher.utils.DialogUtils;
import com.dudu.android.launcher.utils.StatusBarManager;
import org.scf4a.BleStateChange;
import com.dudu.event.DeviceEvent;
import com.dudu.monitor.Monitor;

import java.util.Iterator;

import de.greenrobot.event.EventBus;

/**
 * Created by lxh on 2016/1/20.
 */
public class TitleBarObservable {

    public final ObservableBoolean bluetooth = new ObservableBoolean();

    public final ObservableBoolean gps = new ObservableBoolean();

    public final ObservableBoolean video = new ObservableBoolean();

    public final ObservableField<Drawable> single_drawable = new ObservableField();

    public final ObservableField<String> single = new ObservableField<>();

    public final ObservableBoolean showSingle = new ObservableBoolean();

    public TitleBarObservable() {
        setDefaultState();
    }


    private int mSatellite = 0;

    private int disConnectedCount = 0;

    private static final int SIM_SIGNAL_IMAGE_IDS[] = {
            R.drawable.signal_0,
            R.drawable.signal_1,
            R.drawable.signal_2,
            R.drawable.signal_3,
            R.drawable.signal_4,
            R.drawable.signal_5
    };

    private void setDefaultState() {
        this.bluetooth.set(false);
        this.gps.set(false);
        this.video.set(false);
        this.showSingle.set(true);
        this.single_drawable.set(LauncherApplication.getContext().getResources().getDrawable(R.drawable.signal_5));
        this.single.set("4G");
    }

    public void init() {
        EventBus.getDefault().register(this);
        setSimLevel(StatusBarManager.getInstance().getSignalLevel() + 1);
    }

    private void setSimLevel(int level) {
        if (level < 0 || level >= SIM_SIGNAL_IMAGE_IDS.length) {
            return;
        }
        single_drawable.set(LauncherApplication.getContext().getResources().getDrawable(SIM_SIGNAL_IMAGE_IDS[level]));

    }

    public void onEventMainThread(GpsStatus gpsStatus) {
        int maxSatellites = gpsStatus.getMaxSatellites();
        Iterator<GpsSatellite> iterator = gpsStatus.getSatellites()
                .iterator();
        mSatellite = 0;
        while (iterator.hasNext() && mSatellite <= maxSatellites) {
            mSatellite++;
        }
        if (mSatellite > 0 && (!Monitor.getInstance(LauncherApplication.getContext()).getCurrentLocation().getProvider().equals("lbs"))) {
            gps.set(true);
        } else {
            gps.set(false);
        }

    }


    public void onEventMainThread(DeviceEvent.Video event) {
        StatusBarManager.getInstance().setRecording(event.getState());
        video.set(event.getState() == DeviceEvent.ON);
    }

    public void onEventMainThread(BleStateChange event) {
        StatusBarManager.getInstance().setBleConnState(event.getConnState());
        switch (event.getConnState()) {
            case BleStateChange.BLEDISCONNECTED:
                bluetooth.set(false);
                disConnectedCount++;
                if (disConnectedCount >= 30)
                    DialogUtils.showOBDErrorDialog(LauncherApplication.getContext());
                break;
            case BleStateChange.BLECONNECTED:
                disConnectedCount = 0;
                DialogUtils.dismissOBDErrorDialog(LauncherApplication.getContext());
                bluetooth.set(true);
                break;
        }

    }

    public void onEventMainThread(DeviceEvent.SimLevel simLevel) {
        setSimLevel(simLevel.getSimLevel() + 1);
    }

    public void onEventMainThread(DeviceEvent.SimType simType) {
        single.set(simType.getSimType());
        if (simType.getSimType().equals("2G") || simType.getSimType().equals("3G") || simType.getSimType().equals("4G")) {
            showSingle.set(true);
        } else {
            showSingle.set(false);
        }
    }
}
