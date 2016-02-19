package com.dudu.aios.ui.fragment;

import android.view.LayoutInflater;
import android.view.View;

import com.dudu.aios.ui.fragment.base.BaseVehicleFragment;
import com.dudu.android.launcher.R;
import com.dudu.android.launcher.utils.LogUtils;

/**
 * Created by Administrator on 2016/2/17.
 */
public class GuardFragment extends BaseVehicleFragment implements View.OnClickListener {

    private View guard_unlock_layout, guard_locked_layout;

    @Override
    public View getVehicleChildView() {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.vehicle_guard_layout, null);
        initView(view);
        initListener();
        return view;
    }

    private void initListener() {
        guard_locked_layout.setOnClickListener(this);
        guard_unlock_layout.setOnClickListener(this);
    }

    private void initView(View view) {
        guard_unlock_layout = view.findViewById(R.id.vehicle_unlock_layout);
        guard_locked_layout = view.findViewById(R.id.vehicle_locked_layout);
        guard_lock();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.vehicle_unlock_layout:
                guard_unlock();
                getFragmentManager().beginTransaction().replace(R.id.container, new VehiclePasswordSetFragment()).commit();
                break;
            case R.id.vehicle_locked_layout:
                guard_lock();
                break;
        }
    }

    private void guard_unlock() {
        guard_locked_layout.setVisibility(View.VISIBLE);
        guard_unlock_layout.setVisibility(View.GONE);
    }

    private void guard_lock() {
        guard_locked_layout.setVisibility(View.GONE);
        guard_unlock_layout.setVisibility(View.VISIBLE);
    }
}
