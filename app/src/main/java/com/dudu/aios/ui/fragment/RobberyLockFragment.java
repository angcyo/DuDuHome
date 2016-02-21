package com.dudu.aios.ui.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dudu.aios.ui.robbery.RobberyConstant;
import com.dudu.android.launcher.R;
import com.dudu.workflow.common.DataFlowFactory;
import com.dudu.workflow.common.ObservableFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class RobberyLockFragment extends Fragment implements View.OnClickListener {

    private View guard_unlock_layout, guard_locked_layout;

    private TextView tvTitleCh, tvTitleEn;


    private Logger logger = LoggerFactory.getLogger("RobberyLockFragment");

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.vehicle_guard_layout, container, false);
        initView(view);
        initListener();
        initData();
        syncAppRobberyFlow();
        return view;
    }

    private void initData() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            String pass = bundle.getString("pass");
            if (pass.equals("1")) {
            }
        }
    }

    private void initListener() {
        guard_locked_layout.setOnClickListener(this);
        guard_unlock_layout.setOnClickListener(this);
    }

    private void initView(View view) {
        guard_unlock_layout = view.findViewById(R.id.vehicle_unlock_layout);
        guard_locked_layout = view.findViewById(R.id.vehicle_locked_layout);
        tvTitleCh = (TextView) view.findViewById(R.id.text_title_ch);
        tvTitleCh.setText(getResources().getString(R.string.vehicle_robbery_ch));
        tvTitleEn = (TextView) view.findViewById(R.id.text_title_en);
        tvTitleEn.setText(getResources().getString(R.string.vehicle_robbery_en));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.vehicle_unlock_layout:
                lock();
                break;
            case R.id.vehicle_locked_layout:
                unlock();
                transferParameters();
                break;
        }
    }

    private void transferParameters() {
        VehiclePasswordSetFragment vehiclePasswordSetFragment = new VehiclePasswordSetFragment();
        Bundle bundle = new Bundle();
        bundle.putString(RobberyConstant.CATEGORY_CONSTANT, RobberyConstant.ROBBERY_CONSTANT);
        vehiclePasswordSetFragment.setArguments(bundle);
        getFragmentManager().beginTransaction().replace(R.id.vehicle_right_layout, vehiclePasswordSetFragment).commit();
    }

    private void unlock() {
        guard_locked_layout.setVisibility(View.VISIBLE);
        guard_unlock_layout.setVisibility(View.GONE);
    }

    private void lock() {

        guard_locked_layout.setVisibility(View.GONE);
        guard_unlock_layout.setVisibility(View.VISIBLE);
    }

    public void syncAppRobberyFlow() {
        ObservableFactory.syncAppRobberyFlow()
                .subscribe(receiverData -> {
                    if (!receiverData.getSwitch0Value().equals("1")) {
                        DataFlowFactory.getSwitchDataFlow().saveRobberyState(true);
                        getFragmentManager().beginTransaction().replace(R.id.vehicle_right_layout, new RobberyFragment()).commit();
                    }
                });
    }
}
