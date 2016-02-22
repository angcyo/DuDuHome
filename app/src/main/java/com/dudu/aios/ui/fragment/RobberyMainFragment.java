package com.dudu.aios.ui.fragment;

import android.app.Fragment;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dudu.aios.ui.robbery.RobberyConstant;
import com.dudu.android.launcher.R;
import com.dudu.commonlib.repo.ReceiverData;
import com.dudu.commonlib.utils.RxBus;
import com.dudu.workflow.common.CommonParams;
import com.dudu.workflow.common.DataFlowFactory;
import com.dudu.workflow.common.ObservableFactory;
import com.dudu.workflow.common.ReceiverDataFlow;
import com.dudu.workflow.common.RequestFactory;
import com.dudu.workflow.robbery.RobberyRequest;
import com.dudu.workflow.robbery.RobberyStateModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import rx.Subscription;

public class RobberyMainFragment extends Fragment implements View.OnClickListener {

    private View view;

    private View robberyModeLayout, robberyLockLayout;

    private ImageView headlight_off_img, headlight_on_img, park_off_img, park_on_img, gun_off_img, gun_on_img;

    private View guard_unlock_layout, guard_locked_layout;

    private TextView tvTitleCh, tvTitleEn;

    private Logger logger = LoggerFactory.getLogger("RobberyMainFragment");


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_robbery_main, container, false);
        initView();
        initListener();
        //initData();
        syncAppRobberyFlow();
        return view;
    }

    private void initListener() {
        headlight_off_img.setOnClickListener(this);
        headlight_on_img.setOnClickListener(this);
        park_off_img.setOnClickListener(this);
        park_on_img.setOnClickListener(this);
        gun_off_img.setOnClickListener(this);
        gun_on_img.setOnClickListener(this);

        guard_locked_layout.setOnClickListener(this);
        guard_unlock_layout.setOnClickListener(this);
    }


    private void initData() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            String pass = bundle.getString("pass");
            if (pass.equals("1")) {
                requestCheckToUnlock();
                return;
            }
        }
    }

    private void initView() {
        robberyModeLayout = view.findViewById(R.id.layout_robbery);
        robberyLockLayout = view.findViewById(R.id.layout_robbery_lock);

        headlight_off_img = (ImageView) view.findViewById(R.id.headlight_vehicle_robbery_off);
        headlight_on_img = (ImageView) view.findViewById(R.id.headlight_vehicle_robbery_on);
        park_off_img = (ImageView) view.findViewById(R.id.park_vehicle_robbery_off);
        park_on_img = (ImageView) view.findViewById(R.id.park_vehicle_robbery_on);
        gun_off_img = (ImageView) view.findViewById(R.id.gun_vehicle_robbery_off);
        gun_on_img = (ImageView) view.findViewById(R.id.gun_vehicle_robbery_on);

        guard_unlock_layout = view.findViewById(R.id.vehicle_unlock_layout);
        guard_locked_layout = view.findViewById(R.id.vehicle_locked_layout);
        tvTitleCh = (TextView) view.findViewById(R.id.text_title_ch);
        tvTitleCh.setText(getResources().getString(R.string.vehicle_robbery_ch));
        tvTitleEn = (TextView) view.findViewById(R.id.text_title_en);
        tvTitleEn.setText(getResources().getString(R.string.vehicle_robbery_en));

        showRobberModeLayout();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.headlight_vehicle_robbery_off:
               checkHeadLightSwitch(true);
                requestCheckSwitch(CommonParams.HEADLIGHT, true);
                break;
            case R.id.headlight_vehicle_robbery_on:
                showRobberLockLayout();
                checkHeadLightSwitch(false);
                requestCheckSwitch(CommonParams.HEADLIGHT, false);
                break;
            case R.id.park_vehicle_robbery_off:
                showRobberLockLayout();
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

            case R.id.vehicle_unlock_layout:
                showRobberModeLayout();
//                lock();
                break;
            case R.id.vehicle_locked_layout:
                showRobberModeLayout();
                transferParameters();
                break;

        }
    }

    public void requestCheckToUnlock() {
        showUnlockedView();
        DataFlowFactory.getSwitchDataFlow()
                .saveRobberyState(false);
        RequestFactory.getRobberyRequest()
                .closeAntiRobberyMode(new RobberyRequest.CloseRobberyModeCallback() {
                    @Override
                    public void closeSuccess(boolean success) {
                        if (!success) {
                            requestCheckToUnlock();
                        }
                    }

                    @Override
                    public void requestError(String error) {
                        logger.debug(error);
                        requestCheckToUnlock();
                    }
                });
    }

    private void transferParameters() {
        VehiclePasswordSetFragment vehiclePasswordSetFragment = new VehiclePasswordSetFragment();
        Bundle bundle = new Bundle();
        bundle.putString(RobberyConstant.CATEGORY_CONSTANT, RobberyConstant.ROBBERY_CONSTANT);
        vehiclePasswordSetFragment.setArguments(bundle);
        getFragmentManager().beginTransaction().replace(R.id.vehicle_right_layout, vehiclePasswordSetFragment).commit();
    }

    public void requestCheckSwitch(int type, boolean on_off) {
        DataFlowFactory.getSwitchDataFlow().saveRobberySwitch(type, on_off);
        RequestFactory.getRobberyRequest()
                .settingAntiRobberyMode(type, on_off ? 1 : 0, new RobberyRequest.SwitchCallback() {
                    @Override
                    public void switchSuccess(boolean success) {
                        logger.debug("打开开关" + type + (success ? "成功" : "失败"));
                        if (!success) {
                            requestCheckSwitch(type, on_off);
                        }
                    }

                    @Override
                    public void requestError(String error) {
                        requestCheckSwitch(type, on_off);
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

    public void syncAppRobberyFlow() {
        DataFlowFactory.getSwitchDataFlow()
                .getRobberyState()
                .subscribe(robbed ->{
                    if(robbed) {
                        showRobberLockLayout();
                        showLockedView();
                    }else {
                        showUnlockedView();
                        showRobberModeLayout();
                    }
                });
        DataFlowFactory.getSwitchDataFlow()
                .getRobberySwitches()
                .subscribe(robberySwitches -> {
                    checkHeadLightSwitch(robberySwitches.isHeadlight());
                    checkParkSwitch(robberySwitches.isPark());
                    checkGunSwitch(robberySwitches.isGun());
                });

//        ObservableFactory.syncAppRobberyFlow()
//                .subscribe(receiverData -> {
//                    checkHeadLightSwitch(receiverData.getSwitch1Value().equals("1"));
//                    checkParkSwitch(receiverData.getSwitch2Value().equals("1"));
//                    checkGunSwitch(receiverData.getSwitch3Value().equals("1"));
//                });
        RequestFactory.getRobberyRequest()
                .isCarRobbed(new RobberyRequest.CarRobberdCallback() {
                    @Override
                    public void hasRobbed(boolean robbed) {
                        if(robbed) {
                            showRobberLockLayout();
                            showLockedView();
                        }else {
                            showUnlockedView();
                            showRobberModeLayout();
                        }
                    }

                    @Override
                    public void requestError(String error) {

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

    public void onEventMainThread(ReceiverData receiverData) {
        if(ReceiverDataFlow.getRobberyReceiveData(receiverData)){
            checkHeadLightSwitch(receiverData.getSwitch1Value().equals("1"));
            checkParkSwitch(receiverData.getSwitch2Value().equals("1"));
            checkGunSwitch(receiverData.getSwitch3Value().equals("1"));
            if(receiverData.getSwitch0Value().equals("1")){
                showRobberLockLayout();
                showLockedView();
            }else {
                showUnlockedView();
                showRobberModeLayout();
            }
        }
    }

    private void checkGunSwitch(boolean on_off) {
        gun_on_img.setVisibility(on_off ? View.VISIBLE : View.GONE);
        gun_off_img.setVisibility(on_off ? View.GONE : View.VISIBLE);
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
                            requestCheckSwitch(CommonParams.ROBBERYSTATE, true);
                            DataFlowFactory.getSwitchDataFlow().saveRobberyState(true);
                            RxBus.getInstance().send(new RobberyStateModel(true));
                        });
            } catch (IOException e) {
                logger.error("gun3Toggle exception", e);
                return;
            }
        } else {
            if (sub1 != null && sub1.isUnsubscribed()) sub1.unsubscribe();
        }
    }

    private void showLockedView() {
        guard_locked_layout.setVisibility(View.VISIBLE);
        guard_unlock_layout.setVisibility(View.GONE);
    }

    private void showUnlockedView() {
        guard_locked_layout.setVisibility(View.GONE);
        guard_unlock_layout.setVisibility(View.VISIBLE);
    }

    //显示防劫模式的界面
    private void showRobberModeLayout() {
        robberyModeLayout.setVisibility(View.VISIBLE);
        robberyLockLayout.setVisibility(View.GONE);
    }

    //显示防劫上锁的页面
    private void showRobberLockLayout() {
        robberyLockLayout.setVisibility(View.VISIBLE);
        robberyModeLayout.setVisibility(View.GONE);
    }
}
