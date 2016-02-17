package com.dudu.aios.ui.fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.dudu.aios.ui.fragment.base.BaseVehicleFragment;
import com.dudu.android.launcher.R;

public class RobberyFragment extends BaseVehicleFragment implements View.OnClickListener {

    private View view;

    private ImageView headlight_off_img, headlight_on_img, park_off_img, park_on_img, gun_off_img, gun_on_img;

    @Override
    public View getVehicleChildView() {
        view = LayoutInflater.from(getActivity()).inflate(R.layout.robbery_mode_layout, null);
        initView();
        initListener();
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
                headlight_off_img.setVisibility(View.GONE);
                headlight_on_img.setVisibility(View.VISIBLE);
                break;
            case R.id.headlight_vehicle_robbery_on:
                headlight_on_img.setVisibility(View.GONE);
                headlight_off_img.setVisibility(View.VISIBLE);
                break;
            case R.id.park_vehicle_robbery_off:
                park_off_img.setVisibility(View.GONE);
                park_on_img.setVisibility(View.VISIBLE);
                break;
            case R.id.park_vehicle_robbery_on:
                park_on_img.setVisibility(View.GONE);
                park_off_img.setVisibility(View.VISIBLE);
                break;
            case R.id.gun_vehicle_robbery_off:
                gun_off_img.setVisibility(View.GONE);
                gun_on_img.setVisibility(View.VISIBLE);
                break;
            case R.id.gun_vehicle_robbery_on:
                gun_on_img.setVisibility(View.GONE);
                gun_off_img.setVisibility(View.VISIBLE);
                break;
        }
    }
}
