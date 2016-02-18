package com.dudu.aios.ui.base;

import android.databinding.ObservableBoolean;

/**
 * Created by lxh on 2016/1/20.
 */
public class CommonObservable {

    public final ObservableBoolean hasTitle = new ObservableBoolean();

    public final ObservableBoolean hasBack = new ObservableBoolean();

    public CommonObservable() {

        this.hasTitle.set(true);
        this.hasBack.set(true);
    }

}
