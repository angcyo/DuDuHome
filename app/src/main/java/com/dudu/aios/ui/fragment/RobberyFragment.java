package com.dudu.aios.ui.fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.dudu.aios.ui.fragment.base.BaseVehicleFragment;
import com.dudu.android.launcher.R;
import com.dudu.commonlib.repo.ReceiverData;
import com.dudu.workflow.common.RequestFactory;
import com.dudu.workflow.robbery.RobberyFlow;
import com.dudu.workflow.robbery.RobberyRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import rx.functions.Action1;

public class RobberyFragment extends BaseVehicleFragment implements View.OnClickListener {

    private static final int HEADLIGHT = 1;
    private static final int PARK = 2;
    private static final int GUN = 3;

    private View view;

    private ImageView headlight_off_img, headlight_on_img, park_off_img, park_on_img, gun_off_img, gun_on_img;

    private Logger logger = LoggerFactory.getLogger("RobberyFragment");

    @Override
    public View getVehicleChildView() {
        view = LayoutInflater.from(getActivity()).inflate(R.layout.robbery_mode_layout, null);
        initView();
        initListener();
        changeSwitchFlow();
        return view;
    }

    private void initView() {
        headlight_off_img = (ImageView) view.findViewById(R.id.headlight_vehicle_robbery_off);
        headlight_on_img = (ImageView) view.findViewById(R.id.headlight_vehicle_robbery_on);
        park_off_img = (ImageView) view.findViewById(R.id.park_vehicle_robbery_off);
        park_on_img = (ImageView) view.findViewById(R.id.park_vehicle_robbery_on);
        gun_off_img = (ImageView) view.findViewById(R.id.gun_vehicle_robbery_off);
        gun_on_img = (ImageView) view.findViewById(R.id.gun_vehicle_robbery_on);
    }

    private void initListener() {
        headlight_off_img.setOnClickListener(this);
        headlight_on_img.setOnClickListener(this);
        park_off_img.setOnClickListener(this);
        park_on_img.setOnClickListener(this);
        gun_off_img.setOnClickListener(this);
        gun_on_img.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.headlight_vehicle_robbery_off:
                checkHeadLightSwitch(false);
                requestCheckSwitch(HEADLIGHT,false);
                break;
            case R.id.headlight_vehicle_robbery_on:
                checkHeadLightSwitch(true);
                requestCheckSwitch(HEADLIGHT,true);
                break;
            case R.id.park_vehicle_robbery_off:
                checkParkSwitch(false);
                requestCheckSwitch(PARK,false);
                break;
            case R.id.park_vehicle_robbery_on:
                checkParkSwitch(true);
                requestCheckSwitch(PARK,true);
                break;
            case R.id.gun_vehicle_robbery_off:
                checkGunSwitch(false);
                requestCheckSwitch(GUN,false);
                break;
            case R.id.gun_vehicle_robbery_on:
                checkGunSwitch(true);
                requestCheckSwitch(GUN,true);
                break;
        }
    }

    public void requestCheckSwitch(int type, boolean on_off){
        RequestFactory.getRobberyRequest()
                .settingAntiRobberyMode(type, on_off?1:0, new RobberyRequest.SwitchCallback() {
                    @Override
                    public void switchSuccess(boolean success) {
                        logger.debug("打开开关"+type+(success?"成功":"失败"));
                    }

                    @Override
                    public void requestError(String error) {
                        logger.debug("打开开关"+type+"失败"+error.toString());
                    }
                });
    }

    private void checkHeadLightSwitch(boolean opened){
        headlight_off_img.setVisibility(opened?View.VISIBLE:View.GONE);
        headlight_on_img.setVisibility(opened?View.GONE:View.VISIBLE);
    }

    private void checkParkSwitch(boolean opened){
        park_off_img.setVisibility(opened?View.VISIBLE:View.GONE);
        park_on_img.setVisibility(opened?View.GONE:View.VISIBLE);
    }

    private void checkGunSwitch(boolean opened){
        gun_off_img.setVisibility(opened?View.VISIBLE:View.GONE);
        gun_on_img.setVisibility(opened?View.GONE:View.VISIBLE);
    }

    public void changeSwitchFlow(){
        RobberyFlow.getInstance().getRobberyFlow()
                .subscribe(new Action1<ReceiverData>() {
                    @Override
                    public void call(ReceiverData receiverData) {
                        checkHeadLightSwitch(receiverData.getSwitch1Value().equals("1"));
                        checkParkSwitch(receiverData.getSwitch2Value().equals("1"));
                        checkGunSwitch(receiverData.getSwitch3Value().equals("1"));
                    }
                });
    }
}
