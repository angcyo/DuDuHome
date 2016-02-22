package com.dudu.aios.ui.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dudu.aios.ui.robbery.RobberyConstant;
import com.dudu.aios.ui.view.RobberyAnimView;
import com.dudu.android.launcher.R;
import com.dudu.commonlib.repo.ReceiverData;
import com.dudu.workflow.common.DataFlowFactory;
import com.dudu.workflow.common.ReceiverDataFlow;
import com.dudu.workflow.common.RequestFactory;
import com.dudu.workflow.guard.GuardRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import rx.functions.Action1;
import rx.subjects.BehaviorSubject;

public class GuardFragment extends Fragment implements View.OnClickListener {

    private View guard_unlock_layout, guard_locked_layout;

    private TextView tvTitleCh, tvTitleEn;
    private Logger logger = LoggerFactory.getLogger("GuardFragment");

    private RelativeLayout animContainer;

    private RobberyAnimView animView;

    private boolean stopAnim = false;

    private Handler handler = new AnimHandler();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.vehicle_guard_layout, container, false);
        initView(view);
        initListener();
        initData();
        return view;
    }

    private void initListener() {
        guard_locked_layout.setOnClickListener(this);
        guard_unlock_layout.setOnClickListener(this);
    }

    private void initView(View view) {
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
        //播放动画
        toggleAnim();
        //请求网络
        lockGuard();
    }

    private void actionUnlock() {
        transferParameters();
    }

    private void initData() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            String pass = bundle.getString("pass");
            if (pass.equals("1")) {
                unlockGuard();
                return;
            }
        }
        reflashViews();
    }

    private void transferParameters() {
        VehiclePasswordSetFragment vehiclePasswordSetFragment = new VehiclePasswordSetFragment();
        Bundle bundle = new Bundle();
        bundle.putString(RobberyConstant.CATEGORY_CONSTANT, RobberyConstant.GUARD_CONSTANT);
        vehiclePasswordSetFragment.setArguments(bundle);
        getFragmentManager().beginTransaction().replace(R.id.vehicle_right_layout, vehiclePasswordSetFragment).commit();
    }


    private void lockGuard() {
        lock();
        stopAnim = false;
        DataFlowFactory.getSwitchDataFlow()
                .saveGuardSwitch(true);
        RequestFactory.getGuardRequest()
                .lockCar(new GuardRequest.LockStateCallBack() {
                    @Override
                    public void hasLocked(boolean locked) {
                        if(!locked){
                            lockGuard();
                        }
                        stopAnim = true;
                    }


                    @Override
                    public void requestError(String error) {
                        logger.error(error);
                        stopAnim = true;
                        lockGuard();
                    }
                });
    }

    private void unlockGuard() {
        unlock();
        DataFlowFactory.getSwitchDataFlow()
                .saveGuardSwitch(false);
        RequestFactory.getGuardRequest()
                .lockCar(new GuardRequest.LockStateCallBack() {
                    @Override
                    public void hasLocked(boolean locked) {
                        if(!locked){
                            unlockGuard();
                            //解锁成功
                            logger.debug("解锁成功");
                        }else{
                            //解锁失败
                            logger.debug("解锁失败");
                        }
                    }


                    @Override
                    public void requestError(String error) {
                        logger.error(error);
                        logger.debug("解锁失败");
                        unlockGuard();
                    }
                });
    }

    private void unlock() {
        guard_locked_layout.setVisibility(View.GONE);
        guard_unlock_layout.setVisibility(View.VISIBLE);
    }

    private void lock() {
        guard_locked_layout.setVisibility(View.VISIBLE);
        guard_unlock_layout.setVisibility(View.GONE);
    }

    private void clearAnim() {
        if (animContainer != null && animContainer.getChildCount() != 0) {
            animContainer.removeAllViews();
            if (animView != null) {
                animView.stopAnim();
            }
        }
    }

    BehaviorSubject subject = BehaviorSubject.create();

    private void toggleAnim() {
        animView = new RobberyAnimView(getActivity());
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
        }
    }

    private void checkGuardSwitch(boolean locked) {
        if (locked) {
            lock();
        } else {
            unlock();
        }
    }

    public void reflashViews() {
        DataFlowFactory.getSwitchDataFlow()
                .getGuardSwitch()
                .subscribe(locked -> {
                    checkGuardSwitch(locked);
                });
        RequestFactory.getGuardRequest()
                .isAntiTheftOpened(new GuardRequest.LockStateCallBack() {
                    @Override
                    public void hasLocked(boolean locked) {
                        checkGuardSwitch(locked);
                        DataFlowFactory.getSwitchDataFlow()
                                .saveGuardSwitch(locked);
                    }

                    @Override
                    public void requestError(String error) {
                        logger.error(error);
                    }
                });
    }

    public void onEventMainThread(ReceiverData event) {
        if(ReceiverDataFlow.getGuardReceiveData(event)){
            checkGuardSwitch(event.getSwitchValue().equals("1"));
            ReceiverDataFlow.saveGuardReceiveData(event);
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
