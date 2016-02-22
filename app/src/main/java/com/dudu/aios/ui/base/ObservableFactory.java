package com.dudu.aios.ui.base;

import com.dudu.android.launcher.databinding.ActivityLayoutCommonBinding;
import com.dudu.drivevideo.event.FrontCameraReadyPreview;

/**
 * Created by lxh on 2016/1/20.
 */
public class ObservableFactory {

    private static ObservableFactory observableFactory;

    private TitleBarObservable titleBarObservable;

    private CommonObservable commonObservable;


    public ObservableFactory() {

    }

    public static ObservableFactory getInstance() {

        if (observableFactory == null) {
            observableFactory = new ObservableFactory();
        }
        return observableFactory;
    }

    public TitleBarObservable getTitleObservable() {

        if (titleBarObservable == null) {
            titleBarObservable = new TitleBarObservable();
            titleBarObservable.init();
        }
        return titleBarObservable;
    }

    public CommonObservable getCommonObservable(ActivityLayoutCommonBinding activityLayoutCommonBinding) {

        if (commonObservable == null) {
            commonObservable = new CommonObservable(activityLayoutCommonBinding);
        }
        return commonObservable;
    }

    public CommonObservable getCommonObservable(){
        return commonObservable;
    }
}
