package com.dudu.aios.ui.map.observable;

import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;

import com.dudu.navi.vauleObject.NaviDriveMode;

/**
 * Created by lxh on 2016/2/15.
 */
public class RouteStrategyObservable {

    public final ObservableField<String> driveModeStr = new ObservableField<>();

    public final ObservableField<String> number = new ObservableField<>();

    public final ObservableField<NaviDriveMode> driveMode = new ObservableField<>();


    public RouteStrategyObservable(NaviDriveMode driveMode, String number) {

        driveModeStr.set(driveMode.getName());
        this.number.set(number);
        this.driveMode.set(driveMode);
    }
}
