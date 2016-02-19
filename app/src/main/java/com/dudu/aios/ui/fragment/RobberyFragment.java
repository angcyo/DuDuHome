package com.dudu.aios.ui.fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.dudu.aios.ui.fragment.base.BaseVehicleFragment;
import com.dudu.android.launcher.R;
import com.dudu.commonlib.repo.ReceiverData;
import com.dudu.workflow.robbery.RobberyFlow;

import rx.functions.Action1;

public class RobberyFragment extends BaseVehicleFragment implements View.OnClickListener {

    private View view;

    private ImageView headlight_off_img, headlight_on_img, park_off_img, park_on_img, gun_off_img, gun_on_img;

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
                headlight_off_img.setVisibility(View.GONE);
                headlight_on_img.setVisibility(View.VISIBLE);
                break;
            case R.id.headlight_vehicle_robbery_on:
                headlight_on_img.setVisibility(View.GONE);
                headlight_off_img.setVisibility(View.VISIBLE);
                break;
            case R.id.park_vehicle_robbery_off:
                park_off_img.setVisibility(View.GONE);
                park_on_img.setVisibility(View.VISIBLE);
                break;
            case R.id.park_vehicle_robbery_on:
                park_on_img.setVisibility(View.GONE);
                park_off_img.setVisibility(View.VISIBLE);
                break;
            case R.id.gun_vehicle_robbery_off:
                gun_off_img.setVisibility(View.GONE);
                gun_on_img.setVisibility(View.VISIBLE);
                break;
            case R.id.gun_vehicle_robbery_on:
                gun_on_img.setVisibility(View.GONE);
                gun_off_img.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void checkHeadLightSwitch(boolean opened){
        headlight_off_img.setVisibility(opened?View.GONE:View.VISIBLE);
        headlight_on_img.setVisibility(opened?View.VISIBLE:View.GONE);
    }

    private void checkParkSwitch(boolean opened){
        park_off_img.setVisibility(opened?View.GONE:View.VISIBLE);
        park_on_img.setVisibility(opened?View.VISIBLE:View.GONE);
    }

    private void checkGasSwitch(boolean opened){
        gun_off_img.setVisibility(opened?View.GONE:View.VISIBLE);
        gun_on_img.setVisibility(opened?View.VISIBLE:View.GONE);
    }

    public void changeSwitchFlow(){
        RobberyFlow.getInstance().getRobberyFlow()
                .subscribe(new Action1<ReceiverData>() {
                    @Override
                    public void call(ReceiverData receiverData) {
                        checkHeadLightSwitch(receiverData.getSwitch1Value().equals("1"));
                        checkParkSwitch(receiverData.getSwitch2Value().equals("1"));
                        checkGasSwitch(receiverData.getSwitch3Value().equals("1"));
                    }
                });
    }
}
