package com.dudu.aios.ui.fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.dudu.aios.ui.fragment.base.BaseVehicleFragment;
import com.dudu.android.launcher.R;
import com.dudu.android.launcher.utils.ToastUtils;
import com.dudu.workflow.common.CommonParams;
import com.dudu.workflow.common.DataFlowFactory;
import com.dudu.workflow.common.ObservableFactory;
import com.dudu.workflow.common.RequestFactory;
import com.dudu.workflow.robbery.RobberyRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import rx.Subscription;

public class RobberyFragment extends BaseVehicleFragment implements View.OnClickListener {

    private View view;

    private ImageView headlight_off_img, headlight_on_img, park_off_img, park_on_img, gun_off_img, gun_on_img;

    private Logger logger = LoggerFactory.getLogger("RobberyFragment");

    @Override
    public View getVehicleChildView() {
        view = LayoutInflater.from(getActivity()).inflate(R.layout.robbery_mode_layout, null);
        initView();
        initListener();
        syncAppRobberyFlow();
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
                checkHeadLightSwitch(true);
                requestCheckSwitch(CommonParams.HEADLIGHT, true);
                break;
            case R.id.headlight_vehicle_robbery_on:
                checkHeadLightSwitch(false);
                requestCheckSwitch(CommonParams.HEADLIGHT, false);
                break;
            case R.id.park_vehicle_robbery_off:
                checkParkSwitch(true);
                requestCheckSwitch(CommonParams.PARK, true);
                break;
            case R.id.park_vehicle_robbery_on:
                checkParkSwitch(false);
                requestCheckSwitch(CommonParams.PARK, false);
                break;
            case R.id.gun_vehicle_robbery_off:
                checkGunSwitch(true);
                requestCheckSwitch(CommonParams.GUN, true);
                break;
            case R.id.gun_vehicle_robbery_on:
                checkGunSwitch(false);
                requestCheckSwitch(CommonParams.GUN, false);
                break;
        }
    }

    public void requestCheckSwitch(int type, boolean on_off) {
        RequestFactory.getRobberyRequest()
                .settingAntiRobberyMode(type, on_off ? 1 : 0, new RobberyRequest.SwitchCallback() {
                    @Override
                    public void switchSuccess(boolean success) {
                        logger.debug("打开开关" + type + (success ? "成功" : "失败"));
                        if (!success) {
                            DataFlowFactory.getSwitchDataFlow()
                                    .saveRobberySwitch(type, !on_off);
                            switch (type) {
                                case CommonParams.HEADLIGHT:
                                    checkHeadLightSwitch(!on_off);
                                    break;

                                case CommonParams.PARK:
                                    checkParkSwitch(!on_off);
                                    break;

                                case CommonParams.GUN:
                                    checkGunSwitch(!on_off);
                                    break;
                            }
                        }
                    }

                    @Override
                    public void requestError(String error) {
                        DataFlowFactory.getSwitchDataFlow()
                                .saveRobberySwitch(type, !on_off);
                        logger.debug("打开开关" + type + "失败" + error);
                    }
                });
    }

    private void checkHeadLightSwitch(boolean on_off) {
        headlight_on_img.setVisibility(on_off ? View.VISIBLE : View.GONE);
        headlight_off_img.setVisibility(on_off ? View.GONE : View.VISIBLE);

    }

    private void checkParkSwitch(boolean on_off) {
        park_on_img.setVisibility(on_off ? View.VISIBLE : View.GONE);
        park_off_img.setVisibility(on_off ? View.GONE : View.VISIBLE);
    }

    private void checkGunSwitch(boolean on_off) {
        Subscription sub1 = null;
        if (on_off) {
            try {
                sub1 = ObservableFactory.gun3Toggle()
                        .subscribe(aBoolean -> {
                                }
                                , throwable -> {
                                    if (!(throwable instanceof TimeoutException)) {
                                        logger.debug("Gun toggle fail, try again");
                                        checkGunSwitch(true);
                                    }
                                }
                                , () -> {
                                    logger.debug("Gun toggle robbery, sync to app");
                                    requestCheckSwitch(0, true);
                                });
            } catch (IOException e) {
                logger.error("gun3Toggle exception", e);
                //TODO show error
                return;
            }
        } else {
            if (sub1 != null && sub1.isUnsubscribed()) sub1.unsubscribe();
        }
        gun_on_img.setVisibility(on_off ? View.VISIBLE : View.GONE);
        gun_off_img.setVisibility(on_off ? View.GONE : View.VISIBLE);
    }

    public void syncAppRobberyFlow() {
        DataFlowFactory.getSwitchDataFlow()
                .getRobberyState()
                .subscribe(robbed -> {
                    if (robbed) {
                        getFragmentManager().beginTransaction().replace(R.id.container, new RobberyLockFragment()).commit();
                    }
                });
        DataFlowFactory.getSwitchDataFlow()
                .getRobberySwitches()
                .subscribe(robberySwitches -> {
                    checkHeadLightSwitch(robberySwitches.isHeadlight());
                    checkParkSwitch(robberySwitches.isPark());
                    checkGunSwitch(robberySwitches.isGun());
                });

        ObservableFactory.syncAppRobberyFlow()
                .subscribe(receiverData -> {
                    checkHeadLightSwitch(receiverData.getSwitch1Value().equals("1"));
                    checkParkSwitch(receiverData.getSwitch2Value().equals("1"));
                    checkGunSwitch(receiverData.getSwitch3Value().equals("1"));
                    if (receiverData.getSwitch0Value().equals("1")) {
                        DataFlowFactory.getSwitchDataFlow().saveRobberyState(true);
                        getFragmentManager().beginTransaction().replace(R.id.container, new RobberyLockFragment()).commit();
                    }
                });
        RequestFactory.getRobberyRequest()
                .isCarRobbed(new RobberyRequest.CarRobberdCallback() {
                    @Override
                    public void hasRobbed(boolean robbed) {
                        if (robbed) {
                            ToastUtils.showToast("打开防劫控制中心");
                        }
                    }

                    @Override
                    public void requestError(String error) {
                        logger.error(error);
                    }
                });
        RequestFactory.getRobberyRequest()
                .getRobberyState(new RobberyRequest.RobberStateCallback() {
                    @Override
                    public void switchsState(boolean flashRateTimes, boolean emergencyCutoff, boolean stepOnTheGas) {
                        checkHeadLightSwitch(flashRateTimes);
                        checkParkSwitch(emergencyCutoff);
                        checkGunSwitch(stepOnTheGas);
                    }

                    @Override
                    public void requestError(String error) {
                        logger.equals(error);
                    }
                });
    }
}
