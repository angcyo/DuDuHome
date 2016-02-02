package com.dudu.aios.ui.fragment;

import android.view.LayoutInflater;
import android.view.View;

import com.dudu.aios.ui.fragment.base.BaseFragment;
import com.dudu.android.launcher.R;

/**
 * Created by Administrator on 2016/2/2.
 */
public class VehicleFragment extends BaseFragment {
    @Override
    public View getChildView() {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_vehicle_inspection, null);
        return view;
    }
}
