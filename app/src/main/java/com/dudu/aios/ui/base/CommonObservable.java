package com.dudu.aios.ui.base;

import android.databinding.ObservableBoolean;

import com.dudu.android.launcher.databinding.ActivityLayoutCommonBinding;
import com.dudu.drivevideo.DriveVideo;
import com.dudu.drivevideo.event.FrontCameraReadyPreview;

import de.greenrobot.event.EventBus;

/**
 * Created by lxh on 2016/1/20.
 */
public class CommonObservable {

    public final ObservableBoolean hasTitle = new ObservableBoolean();

    public final ObservableBoolean hasBack = new ObservableBoolean();

    public final ObservableBoolean hasBackground = new ObservableBoolean();

    public ActivityLayoutCommonBinding activityLayoutCommonBinding;

    public CommonObservable(ActivityLayoutCommonBinding activityLayoutCommonBinding) {
        this.activityLayoutCommonBinding = activityLayoutCommonBinding;
        this.hasTitle.set(true);
        this.hasBack.set(true);
        this.hasBackground.set(true);

        EventBus.getDefault().unregister(this);
        EventBus.getDefault().register(this);
    }


    public void onEventMainThread(FrontCameraReadyPreview frontCameraReadyPreview) {
        activityLayoutCommonBinding.preview.addView(DriveVideo.getInstance().getFrontCameraDriveVideo().getCameraPreview());
    }
}
