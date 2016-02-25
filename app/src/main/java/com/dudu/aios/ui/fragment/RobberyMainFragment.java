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
import com.dudu.android.libble.BleConnectMain;
import com.dudu.commonlib.repo.ReceiverData;
import com.dudu.workflow.common.CommonParams;
import com.dudu.workflow.common.DataFlowFactory;
import com.dudu.workflow.common.ReceiverDataFlow;
import com.dudu.workflow.common.RequestFactory;
import com.dudu.workflow.obd.CarLock;
import com.dudu.workflow.robbery.RobberyRequest;
import com.dudu.workflow.robbery.RobberyStateModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.greenrobot.event.EventBus;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

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
        initData();

        EventBus.getDefault().unregister(this);
        EventBus.getDefault().register(this);
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
                showRobberModeLayout();
                DataFlowFactory.getSwitchDataFlow().saveRobberyState(false);
                checkCarlock(false);
                requestCheckToUnlock();
                DataFlowFactory.getSwitchDataFlow()
                        .getRobberySwitches()
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(robberySwitches -> {
                            checkHeadLightSwitch(robberySwitches.isHeadlight());
                            checkParkSwitch(robberySwitches.isPark());
                            checkGunSwitch(robberySwitches.isGun());
                        });
                return;
            }
        }
        syncAppRobberyFlow();
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
//                showRobberLockLayout();
                checkHeadLightSwitch(false);
                requestCheckSwitch(CommonParams.HEADLIGHT, false);
                break;
            case R.id.park_vehicle_robbery_off:
//                showRobberLockLayout();
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
//                showRobberModeLayout();
                transferParameters();
                break;

        }
    }

    public void requestCheckToUnlock() {
        showUnlockedView();
        DataFlowFactory.getSwitchDataFlow()
                .saveRobberyState(false);
        requestUnlock();
    }

    public void requestUnlock() {
        RequestFactory.getRobberyRequest()
                .closeAntiRobberyMode(new RobberyRequest.CloseRobberyModeCallback() {
                    @Override
                    public void closeSuccess(boolean success) {
                        if (success) {
                            logger.debug("关闭防劫模式成功");
                        } else {
                            logger.debug("关闭防劫模式失败");
                        }
//                        syncAppRobberyFlow();
                    }

                    @Override
                    public void requestError(String error) {
                        logger.debug(error);
                    }
                });
    }

    private void transferParameters() {
        GestureFragment gestureFragment = new GestureFragment();
        Bundle bundle = new Bundle();
        bundle.putString(RobberyConstant.CATEGORY_CONSTANT, RobberyConstant.ROBBERY_CONSTANT);
        gestureFragment.setArguments(bundle);
        getFragmentManager().beginTransaction().replace(R.id.vehicle_right_layout, gestureFragment).commit();
    }

    public void requestCheckSwitch(int type, boolean on_off) {
        if(type == CommonParams.ROBBERYSTATE) {
            DataFlowFactory.getSwitchDataFlow().saveRobberyState(on_off);
        } else {
            DataFlowFactory.getSwitchDataFlow().saveRobberySwitch(type, on_off);
        }
        RequestFactory.getRobberyRequest()
                .settingAntiRobberyMode(type, on_off ? 1 : 0, new RobberyRequest.SwitchCallback() {
                    @Override
                    public void switchSuccess(boolean success) {
                        logger.debug("打开开关" + type + (success ? "成功" : "失败"));
                    }

                    @Override
                    public void requestError(String error) {
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
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(robbed -> {
                    if (robbed) {
                        showRobberLockLayout();
                        showLockedView();
                    } else {
                        showUnlockedView();
                        showRobberModeLayout();
                    }
                });
        DataFlowFactory.getSwitchDataFlow()
                .getRobberySwitches()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(robberySwitches -> {
                    checkHeadLightSwitch(robberySwitches.isHeadlight());
                    checkParkSwitch(robberySwitches.isPark());
                    checkGunSwitch(robberySwitches.isGun());
                });

        RequestFactory.getRobberyRequest()
                .isCarRobbed(new RobberyRequest.CarRobberdCallback() {
                    @Override
                    public void hasRobbed(boolean robbed) {
                        DataFlowFactory.getSwitchDataFlow()
                                .getRobberyState()
                                .subscribeOn(Schedulers.newThread())
                                .subscribe(hasRobbed -> {
                                    if (hasRobbed != robbed) {
                                        if(!hasRobbed) {
                                            requestUnlock();
                                        }else{
                                            requestCheckSwitch(CommonParams.ROBBERYSTATE,true);
                                        }
                                    }
                                });

                    }

                    @Override
                    public void requestError(String error) {
                        logger.debug(error);
                    }
                });
        RequestFactory.getRobberyRequest()
                .getRobberyState(new RobberyRequest.RobberStateCallback() {
                    @Override
                    public void switchsState(boolean flashRateTimes, boolean emergencyCutoff, boolean stepOnTheGas) {
                        DataFlowFactory.getSwitchDataFlow()
                                .getRobberySwitch(CommonParams.HEADLIGHT)
                                .subscribeOn(Schedulers.newThread())
                                .subscribe(headlight_on -> {
                                    if (headlight_on != flashRateTimes) {
                                        requestCheckSwitch(CommonParams.HEADLIGHT, headlight_on);
                                    }
                                });
                        DataFlowFactory.getSwitchDataFlow()
                                .getRobberySwitch(CommonParams.PARK)
                                .subscribeOn(Schedulers.newThread())
                                .subscribe(park_on -> {
                                    if (park_on != emergencyCutoff) {
                                        requestCheckSwitch(CommonParams.PARK, park_on);
                                    }
                                });
                        DataFlowFactory.getSwitchDataFlow()
                                .getRobberySwitch(CommonParams.GUN)
                                .subscribeOn(Schedulers.newThread())
                                .subscribe(gun_on -> {
                                    if (gun_on != stepOnTheGas) {
                                        requestCheckSwitch(CommonParams.GUN, gun_on);
                                    }
                                });

                    }

                    @Override
                    public void requestError(String error) {
                        logger.equals(error);
                    }
                });
    }

    public void onEventMainThread(ReceiverData receiverData) {
        if (ReceiverDataFlow.getRobberyReceiveData(receiverData)) {
            checkHeadLightSwitch(receiverData.getSwitch1Value().equals("1"));
            checkParkSwitch(receiverData.getSwitch2Value().equals("1"));
            checkGunSwitch(receiverData.getSwitch3Value().equals("1"));

            boolean lockCar = receiverData.getSwitch0Value().equals("1");
            if (lockCar) {
                showRobberLockLayout();
                showLockedView();
            } else {
                showUnlockedView();
                showRobberModeLayout();
            }
            DataFlowFactory.getSwitchDataFlow().saveRobberyState(lockCar);
            checkCarlock(lockCar);
        }
    }

    public void onEventMainThread(RobberyStateModel event) {
        logger.debug("收到防劫模式触发事件:"+event.getRobberyState());
        DataFlowFactory.getSwitchDataFlow().getRobberySwitch(CommonParams.GUN)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(switchIsOn -> {
                    logger.debug("查询本地是否打开了防3次踩油门:"+switchIsOn);
                    if(switchIsOn){
                        DataFlowFactory.getSwitchDataFlow().saveRobberyState(true);
                        checkCarlock(event.getRobberyState());
                        requestCheckSwitch(CommonParams.ROBBERYSTATE,event.getRobberyState());
                        if (event.getRobberyState()) {
                            showRobberLockLayout();
                            showLockedView();
                        } else {
                            showUnlockedView();
                            showRobberModeLayout();
                        }
                    }
                });
    }

    public void checkCarlock(boolean lock){
        if(BleConnectMain.getInstance().isBleConnected()){
            if(lock) {
                CarLock.lockCar();
            } else {
                CarLock.unlockCar();
            }
        }
    }

    private void checkGunSwitch(boolean on_off) {
        gun_on_img.setVisibility(on_off ? View.VISIBLE : View.GONE);
        gun_off_img.setVisibility(on_off ? View.GONE : View.VISIBLE);
        DataFlowFactory.getSwitchDataFlow()
                .saveRobberySwitch(CommonParams.GUN,on_off);
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
