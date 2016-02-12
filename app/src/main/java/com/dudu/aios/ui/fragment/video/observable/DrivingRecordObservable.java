package com.dudu.aios.ui.fragment.video.observable;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.dudu.aios.ui.fragment.video.DrivingRecordFragment;
import com.dudu.aios.ui.utils.contants.FragmentConstants;
import com.dudu.android.launcher.databinding.FragmentDrivingRecordBinding;
import com.dudu.commonlib.CommonLib;
import com.dudu.network.utils.DuduLog;

/**
 * Created by dengjun on 2016/2/12.
 * Description :
 */
public class DrivingRecordObservable {
    private Context mContext;
    private FragmentDrivingRecordBinding fragmentDrivingRecordBinding;
    private DrivingRecordFragment drivingRecordFragment;



    public DrivingRecordObservable(DrivingRecordFragment drivingRecordFragment, FragmentDrivingRecordBinding fragmentDrivingRecordBinding) {
        mContext = CommonLib.getInstance().getContext();
        this.fragmentDrivingRecordBinding = fragmentDrivingRecordBinding;
        this.drivingRecordFragment = drivingRecordFragment;
    }

    public void changeFrontAndRearPreview(View view){

    }


    public void onButtonBack(View view){
        DuduLog.d("数据绑定");
        drivingRecordFragment.replaceFragment(FragmentConstants.FRAGMENT_MAIN_PAGE);
    }
}
