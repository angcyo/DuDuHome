package com.dudu.aios.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.dudu.aios.ui.fragment.base.BaseVehicleFragment;
import com.dudu.aios.ui.robbery.RobberyConstant;
import com.dudu.android.launcher.R;
import com.dudu.workflow.common.DataFlowFactory;
import com.dudu.workflow.common.ObservableFactory;
import com.dudu.workflow.common.RequestFactory;
import com.dudu.workflow.guard.GuardRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Administrator on 2016/2/17.
 */
public class GuardFragment extends BaseVehicleFragment implements View.OnClickListener {

    private View guard_unlock_layout, guard_locked_layout;

    private TextView tvTitleCh, tvTitleEn;
    private Logger logger = LoggerFactory.getLogger("GuardFragment");

    @Override
    public View getVehicleChildView() {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.vehicle_guard_layout, null);
        initView(view);
        initListener();
        reflashViews();
        return view;
    }

    private void initListener() {
        guard_locked_layout.setOnClickListener(this);
        guard_unlock_layout.setOnClickListener(this);
    }

    private void initView(View view) {
        guard_unlock_layout = view.findViewById(R.id.vehicle_unlock_layout);
        guard_locked_layout = view.findViewById(R.id.vehicle_locked_layout);
        tvTitleCh = (TextView) view.findViewById(R.id.text_title_ch);
        tvTitleCh.setText(getResources().getString(R.string.vehicle_guard_ch));
        tvTitleEn = (TextView) view.findViewById(R.id.text_title_en);
        tvTitleEn.setText(getResources().getString(R.string.vehicle_guard_en));
        guardLockViews();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.vehicle_unlock_layout:
                guardUnlockViews();                
                VehiclePasswordSetFragment vehiclePasswordSetFragment = new VehiclePasswordSetFragment();
                transferParameters();
                break;
            case R.id.vehicle_locked_layout:
                guardLockViews();
                lockGuard();
                break;
        }
    }

    private void transferParameters() {
        VehiclePasswordSetFragment vehiclePasswordSetFragment = new VehiclePasswordSetFragment();
        Bundle bundle = new Bundle();
        bundle.putString(RobberyConstant.CATEGORY_CONSTANT, RobberyConstant.GUARD_CONSTANT);
        vehiclePasswordSetFragment.setArguments(bundle);
        getFragmentManager().beginTransaction().replace(R.id.container, vehiclePasswordSetFragment).commit();
	}

    private void lockGuard() {
        DataFlowFactory.getSwitchDataFlow()
                .saveGuardSwitch(true);
        RequestFactory.getGuardRequest()
                .lockCar(new GuardRequest.LockStateCallBack() {
                    @Override
                    public void hasLocked(boolean locked) {
                        DataFlowFactory.getSwitchDataFlow()
                                .saveGuardSwitch(locked);
                    }

                    @Override
                    public void requestError(String error) {
                        DataFlowFactory.getSwitchDataFlow()
                                .saveGuardSwitch(false);
                        logger.error(error);
                    }
                });
    }

    private void guardUnlockViews() {
        guard_locked_layout.setVisibility(View.VISIBLE);
        guard_unlock_layout.setVisibility(View.GONE);
    }

    private void guardLockViews() {
        guard_locked_layout.setVisibility(View.GONE);
        guard_unlock_layout.setVisibility(View.VISIBLE);
    }

    private void checkGuardSwitch(boolean locked) {
        if (locked) {
            guardLockViews();
        } else {
            guardUnlockViews();
        }
    }

    public void reflashViews() {
        DataFlowFactory.getSwitchDataFlow()
                .getGuardSwitch()
                .subscribe(locked -> {
                    checkGuardSwitch(locked);
                });
        ObservableFactory.getGuardReceiveObservable()
                .subscribe(locked -> {
                    checkGuardSwitch(locked);
                    DataFlowFactory.getSwitchDataFlow()
                            .saveGuardSwitch(locked);
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
}
