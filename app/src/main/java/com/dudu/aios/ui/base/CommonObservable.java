package com.dudu.aios.ui.base;

import android.databinding.ObservableBoolean;
import android.view.View;

import com.dudu.android.launcher.R;
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

    public final ObservableBoolean hasRearCameraPreview= new ObservableBoolean();

    public ActivityLayoutCommonBinding activityLayoutCommonBinding;

    public CommonObservable(ActivityLayoutCommonBinding activityLayoutCommonBinding) {
        this.activityLayoutCommonBinding = activityLayoutCommonBinding;
        this.hasTitle.set(true);
        this.hasBack.set(true);
        this.hasBackground.set(true);
        this.hasRearCameraPreview.set(false);

        EventBus.getDefault().unregister(this);
        EventBus.getDefault().register(this);
    }


    public void onEventMainThread(FrontCameraReadyPreview frontCameraReadyPreview) {
        activityLayoutCommonBinding.preview.addView(DriveVideo.getInstance().getFrontCameraDriveVideo().getCameraPreview());
    }

    public void startRearPreview(){
        activityLayoutCommonBinding.rearCameraPreview.setVisibility(View.VISIBLE);
        DriveVideo.getInstance().getRearCameraDriveVideo().setImageView(activityLayoutCommonBinding.rearCameraPreview);
        DriveVideo.getInstance().getRearCameraDriveVideo().startPreview();
    }

    public void stopRearPreview(){
        DriveVideo.getInstance().getRearCameraDriveVideo().stopPreview();
        activityLayoutCommonBinding.rearCameraPreview.setVisibility(View.INVISIBLE);
    }
}
