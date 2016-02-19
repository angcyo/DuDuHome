package com.dudu.aios.ui.robbery;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dudu.aios.ui.fragment.base.BaseFragment;
import com.dudu.aios.ui.utils.contants.FragmentConstants;
import com.dudu.android.launcher.R;


/**
 * Created by lxh on 16/2/11.
 */
public class VehicleFragment extends BaseFragment implements View.OnClickListener {

    private RelativeLayout view_Right;

    private View vehicle_guard_View, vehicle_robbery_view, preventView;

    private ImageButton backButton;

    private ImageView guard_unlock_img, guard_locked_img;

    private View guard_unlock_layout, guard_locked_layout;

    private View vehicle_guard_btn, vehicle_robbery_btn;

    private ImageView headlight_off_img, headlight_on_img, park_off_img, park_on_img, gun_off_img, gun_on_img;


    @Override
    public View getView() {
        preventView = LayoutInflater.from(getActivity()).inflate(R.layout.prevent_rob_layout, null);
        initView();
        initListener();
        initData();
        return preventView;
    }

    private void initView() {

        view_Right = (RelativeLayout) preventView.findViewById(R.id.vehicle_right_layout);

        ViewGroup.LayoutParams rightParms = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        vehicle_guard_View = LayoutInflater.from(getActivity()).inflate(R.layout.vehicle_guard_layout, null);
        view_Right.addView(vehicle_guard_View, rightParms);

        vehicle_robbery_view = LayoutInflater.from(getActivity()).inflate(R.layout.robbery_mode_layout, null);
        view_Right.addView(vehicle_robbery_view, rightParms);

        vehicle_guard_btn = preventView.findViewById(R.id.vehicle_guard_btn);
        vehicle_robbery_btn = preventView.findViewById(R.id.vehicle_robbery_btn);

        guard_unlock_img = (ImageView) vehicle_guard_View.findViewById(R.id.vehicle_unlock_image);
        guard_unlock_layout = vehicle_guard_View.findViewById(R.id.vehicle_unlock_layout);

        guard_locked_img = (ImageView) vehicle_guard_View.findViewById(R.id.vehicle_locked_image);
        guard_locked_layout = vehicle_guard_View.findViewById(R.id.vehicle_locked_layout);

        headlight_off_img = (ImageView) vehicle_robbery_view.findViewById(R.id.headlight_vehicle_robbery_off);
        headlight_on_img = (ImageView) vehicle_robbery_view.findViewById(R.id.headlight_vehicle_robbery_on);
        park_off_img = (ImageView) vehicle_robbery_view.findViewById(R.id.park_vehicle_robbery_off);
        park_on_img = (ImageView) vehicle_robbery_view.findViewById(R.id.park_vehicle_robbery_on);
        gun_off_img = (ImageView) vehicle_robbery_view.findViewById(R.id.gun_vehicle_robbery_off);
        gun_on_img = (ImageView) vehicle_robbery_view.findViewById(R.id.gun_vehicle_robbery_on);

        backButton = (ImageButton) preventView.findViewById(R.id.back_button);

    }

    private void initData() {

        robbery();
        guard_unlock();
    }

    private void initListener() {

        backButton.setOnClickListener(this);
        vehicle_robbery_btn.setOnClickListener(this);
        vehicle_guard_btn.setOnClickListener(this);
        guard_locked_img.setOnClickListener(this);
        guard_unlock_img.setOnClickListener(this);

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
            case R.id.back_button:
                replaceFragment(FragmentConstants.FRAGMENT_MAIN_PAGE);
                break;
            case R.id.vehicle_guard_btn:
                guard();
                break;
            case R.id.vehicle_robbery_btn:
                robbery();
                break;
            case R.id.vehicle_unlock_image:
                guard_lock();
                break;
            case R.id.vehicle_locked_image:
                guard_unlock();
                break;
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


    private void guard() {

        vehicle_robbery_view.setVisibility(View.GONE);
        vehicle_guard_View.setVisibility(View.VISIBLE);
        ((ImageView) preventView.findViewById(R.id.vehicle_guard_icon)).setImageResource(R.drawable.vehicle_guard_clicked);
        ((TextView) preventView.findViewById(R.id.text_vehicle_guard_ch)).setTextColor(getResources().getColor(R.color.white));
        ((TextView) preventView.findViewById(R.id.text_vehicle_guard_en)).setTextColor(getResources().getColor(R.color.white));
        ((ImageView) preventView.findViewById(R.id.vehicle_robbery_icon)).setImageResource(R.drawable.vehicle_robbery_normal);
        ((TextView) preventView.findViewById(R.id.text_vehicle_robbery_ch)).setTextColor(getResources().getColor(R.color.unchecked_textColor));
        ((TextView) preventView.findViewById(R.id.text_vehicle_robbery_en)).setTextColor(getResources().getColor(R.color.unchecked_textColor));

    }

    private void robbery() {
        vehicle_robbery_view.setVisibility(View.VISIBLE);
        vehicle_guard_View.setVisibility(View.GONE);
        ((ImageView) preventView.findViewById(R.id.vehicle_guard_icon)).setImageResource(R.drawable.vehicle_guard_normal);
        ((TextView) preventView.findViewById(R.id.text_vehicle_guard_ch)).setTextColor(getResources().getColor(R.color.unchecked_textColor));
        ((TextView) preventView.findViewById(R.id.text_vehicle_guard_en)).setTextColor(getResources().getColor(R.color.unchecked_textColor));
        ((ImageView) preventView.findViewById(R.id.vehicle_robbery_icon)).setImageResource(R.drawable.vehicle_robbery_clicked_icon);
        ((TextView) preventView.findViewById(R.id.text_vehicle_robbery_ch)).setTextColor(getResources().getColor(R.color.white));
        ((TextView) preventView.findViewById(R.id.text_vehicle_robbery_en)).setTextColor(getResources().getColor(R.color.white));

    }

    private void guard_unlock() {


        guard_locked_layout.setVisibility(View.GONE);
        guard_unlock_layout.setVisibility(View.VISIBLE);

    }

    private void guard_lock() {

        guard_locked_layout.setVisibility(View.VISIBLE);
        guard_unlock_layout.setVisibility(View.GONE);

    }
}


