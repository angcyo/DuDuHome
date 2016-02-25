package com.dudu.aios.ui.fragment;

import android.app.Fragment;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dudu.aios.ui.robbery.RobberyConstant;
import com.dudu.aios.ui.view.RobberyAnimView;
import com.dudu.android.launcher.R;
import com.dudu.android.libble.BleConnectMain;
import com.dudu.commonlib.repo.ReceiverData;
import com.dudu.workflow.common.DataFlowFactory;
import com.dudu.workflow.common.ReceiverDataFlow;
import com.dudu.workflow.common.RequestFactory;
import com.dudu.workflow.guard.GuardRequest;
import com.dudu.workflow.obd.CarLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.greenrobot.event.EventBus;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subjects.BehaviorSubject;

public class GuardFragment extends Fragment implements View.OnClickListener {

    private View guard_unlock_layout, guard_locked_layout;

    private TextView tvTitleCh, tvTitleEn;
    private Logger logger = LoggerFactory.getLogger("GuardFragment");

    private RelativeLayout animContainer;

    private RobberyAnimView animView;

    private boolean stopAnim = false;

    private Handler handler = new AnimHandler();

    private LinearLayout viewContainer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.vehicle_guard_layout, container, false);
        initView(view);
        initListener();
        initData();
        EventBus.getDefault().unregister(this);
        EventBus.getDefault().register(this);
        return view;
    }

    private void initListener() {
        guard_locked_layout.setOnClickListener(this);
        guard_unlock_layout.setOnClickListener(this);
    }

    private void initView(View view) {
        viewContainer = (LinearLayout) view.findViewById(R.id.view_container);
        guard_unlock_layout = view.findViewById(R.id.vehicle_unlock_layout);
        guard_locked_layout = view.findViewById(R.id.vehicle_locked_layout);
        animContainer = (RelativeLayout) view.findViewById(R.id.anim_container);
        tvTitleCh = (TextView) view.findViewById(R.id.text_title_ch);
        tvTitleCh.setText(getResources().getString(R.string.vehicle_guard_ch));
        tvTitleEn = (TextView) view.findViewById(R.id.text_title_en);
        tvTitleEn.setText(getResources().getString(R.string.vehicle_guard_en));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.vehicle_unlock_layout:
                //上锁
                actionLock();
                break;
            case R.id.vehicle_locked_layout:
                //解锁动作
                actionUnlock();
                break;
        }
    }

    private void actionLock() {
        logger.debug("actionLock");
        viewContainer.setVisibility(View.GONE);
        //播放动画
        toggleAnim();
        checkCarlock(true);
        showLockView();
        DataFlowFactory.getSwitchDataFlow()
                .saveGuardSwitch(true);
        //请求网络
        lockGuard();
    }

    private void actionUnlock() {
        logger.debug("actionUnlock");
        transferParameters();
    }

    private void initData() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            String pass = bundle.getString("pass");
            if (pass.equals("1")) {
                logger.debug("initData");
                checkCarlock(false);
                showUnlockView();
                DataFlowFactory.getSwitchDataFlow()
                        .saveGuardSwitch(false);
                unlockGuard();
                return;
            }
        }
        reflashViews();
    }

    private void transferParameters() {
        GestureFragment gestureFragment = new GestureFragment();
        Bundle bundle = new Bundle();
        bundle.putString(RobberyConstant.CATEGORY_CONSTANT, RobberyConstant.GUARD_CONSTANT);
        gestureFragment.setArguments(bundle);
        getFragmentManager().beginTransaction().replace(R.id.vehicle_right_layout, gestureFragment).commit();
    }


    private void lockGuard() {
        logger.debug("lockGuard");
        stopAnim = false;
        RequestFactory.getGuardRequest()
                .lockCar(new GuardRequest.LockStateCallBack() {
                    @Override
                    public void hasLocked(boolean locked) {
                        if (!locked) {
                            logger.debug("加锁失败");
                        } else {
                            logger.debug("加锁成功");
                        }
                        stopAnim = true;
                    }


                    @Override
                    public void requestError(String error) {
                        logger.error(error);
                        stopAnim = true;
                    }
                });
    }

    private void unlockGuard() {
        logger.debug("unlockGuard");
        RequestFactory.getGuardRequest()
                .unlockCar(new GuardRequest.UnlockCallBack() {

                    @Override
                    public void unlocked(boolean locked) {
                        if (!locked) {
                            //解锁成功
                            logger.debug("解锁成功");
                        } else {
                            //解锁失败
                            logger.debug("解锁失败");
                        }
                    }

                    @Override
                    public void requestError(String error) {
                        logger.error(error);
                        logger.debug("解锁失败");
                    }
                });
    }

    private void showUnlockView() {
        logger.debug("unlock");
        guard_locked_layout.setVisibility(View.GONE);
        guard_unlock_layout.setVisibility(View.VISIBLE);
    }

    private void showLockView() {
        logger.debug("lock");
        guard_locked_layout.setVisibility(View.VISIBLE);
        guard_unlock_layout.setVisibility(View.GONE);
    }

    private void clearAnim() {
        if (animContainer != null && animContainer.getChildCount() != 0) {
            animContainer.removeAllViews();
            if (animView != null) {
                animView.stopAnim();
                viewContainer.setVisibility(View.VISIBLE);
            }
        }
    }

    BehaviorSubject subject = BehaviorSubject.create();

    private void toggleAnim() {
        animView = new RobberyAnimView(getActivity());
        animView.setZOrderOnTop(true);
        animView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
        animContainer.addView(animView);
        animView.setOnAnimPlayListener(new RobberyAnimView.OnAnimPlayListener() {
            @Override
            public boolean play() {
                logger.debug("stopAnim:" + stopAnim);
                if (stopAnim) {
                    subject.onNext(stopAnim);

                }
                return stopAnim;
            }
        });
        subject.subscribe(new Action1<Boolean>() {
            @Override
            public void call(Boolean o) {
                if (o) {
                    handler.sendEmptyMessage(0);
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (animView != null) {
            animView.stopAnim();
            viewContainer.setVisibility(View.VISIBLE);
        }
    }

    private void checkGuardSwitch(boolean locked) {
        logger.debug("checkGuardSwitch:locked:" + locked);
        if (locked) {
            showLockView();
        } else {
            showUnlockView();
        }
    }

    public void reflashViews() {
        DataFlowFactory.getSwitchDataFlow()
                .getGuardSwitch()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(locked -> {
                    checkGuardSwitch(locked);
                });
        RequestFactory.getGuardRequest()
                .isAntiTheftOpened(new GuardRequest.LockStateCallBack() {
                    @Override
                    public void hasLocked(boolean locked) {
//                        checkGuardSwitch(locked);
//                        DataFlowFactory.getSwitchDataFlow()
//                                .saveGuardSwitch(locked);
                        DataFlowFactory.getSwitchDataFlow()
                                .getGuardSwitch()
                                .subscribeOn(Schedulers.newThread())
                                .subscribe(lock->{
                                    if(lock){
                                        lockGuard();
                                    }else{
                                        unlockGuard();
                                    }
                                });
                    }

                    @Override
                    public void requestError(String error) {
                        logger.error(error);
                    }
                });
    }

    public void onEventMainThread(ReceiverData event) {
        if (ReceiverDataFlow.getGuardReceiveData(event)) {
            logger.debug("onEventMainThread:" + event.getSwitchValue());
            boolean lock = event.getSwitchValue().equals("1");
            checkGuardSwitch(lock);
            ReceiverDataFlow.saveGuardReceiveData(event);
            checkCarlock(lock);
        }
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

    private class AnimHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            clearAnim();
        }
    }
}
