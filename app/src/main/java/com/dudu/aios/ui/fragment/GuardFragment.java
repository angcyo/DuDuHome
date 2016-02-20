package com.dudu.aios.ui.fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.dudu.aios.ui.fragment.base.BaseVehicleFragment;
import com.dudu.android.launcher.R;
import com.dudu.android.launcher.utils.LogUtils;

/**
 * Created by Administrator on 2016/2/17.
 */
public class GuardFragment extends BaseVehicleFragment implements View.OnClickListener {

    private View guard_unlock_layout, guard_locked_layout;

    private TextView tvTitleCh, tvTitleEn;

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
        tvTitleCh = (TextView) view.findViewById(R.id.text_title_ch);
        tvTitleCh.setText(getResources().getString(R.string.vehicle_guard_ch));
        tvTitleEn = (TextView) view.findViewById(R.id.text_title_en);
        tvTitleEn.setText(getResources().getString(R.string.vehicle_guard_en));
        lock();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.vehicle_unlock_layout:
                unlock();
                getFragmentManager().beginTransaction().replace(R.id.container, new VehiclePasswordSetFragment()).commit();
                break;
            case R.id.vehicle_locked_layout:
                lock();
                break;
        }
    }

    private void unlock() {
        guard_locked_layout.setVisibility(View.VISIBLE);
        guard_unlock_layout.setVisibility(View.GONE);
    }

    private void lock() {
        guard_locked_layout.setVisibility(View.GONE);
        guard_unlock_layout.setVisibility(View.VISIBLE);
    }
}
