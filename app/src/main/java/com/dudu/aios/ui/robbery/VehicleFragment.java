package com.dudu.aios.ui.robbery;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.dudu.aios.ui.fragment.base.BaseFragment;
import com.dudu.aios.ui.utils.contants.FragmentConstants;
import com.dudu.android.launcher.R;


/**
 * Created by lxh on 16/2/11.
 */
public class VehicleFragment extends BaseFragment implements
        View.OnClickListener {


    private RelativeLayout view_Right;
    private View vehicle_guard_View, vehicle_robbery_view, preventView;
    private Button backButton;
    private ImageView guard_unlock_img, guard_locked_img;
    private View guard_unlock_layout, guard_locked_layout;

    private View vehicle_guard_btn, vehicle_robbery_btn;


    @Override
    public View getChildView() {
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

        backButton = (Button) preventView.findViewById(R.id.back_button);

        vehicle_guard_btn = preventView.findViewById(R.id.vehicle_guard_btn);
        vehicle_robbery_btn = preventView.findViewById(R.id.vehicle_robbery_btn);

        guard_unlock_img = (ImageView) vehicle_guard_View.findViewById(R.id.vehicle_unlock_image);
        guard_unlock_layout = vehicle_guard_View.findViewById(R.id.vehicle_unlock_layout);

        guard_locked_img = (ImageView) vehicle_guard_View.findViewById(R.id.vehicle_locked_image);
        guard_locked_layout = vehicle_guard_View.findViewById(R.id.vehicle_locked_layout);


    }

    private void initData() {

        guard();
        guard_unlock();
    }

    private void initListener() {

        backButton.setOnClickListener(this);
        vehicle_robbery_btn.setOnClickListener(this);
        vehicle_guard_btn.setOnClickListener(this);
        guard_locked_img.setOnClickListener(this);
        guard_unlock_img.setOnClickListener(this);
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
        }

    }


    private void guard() {

        vehicle_robbery_view.setVisibility(View.GONE);
        vehicle_guard_View.setVisibility(View.VISIBLE);
        ((ImageView) preventView.findViewById(R.id.vehicle_guard_icon)).setImageResource(R.drawable.vehicle_guard_selected);
        ((ImageView) preventView.findViewById(R.id.vehicle_robbery_icon)).setImageResource(R.drawable.vehicle_robbery_normal);

    }

    private void robbery() {
        vehicle_robbery_view.setVisibility(View.VISIBLE);
        vehicle_guard_View.setVisibility(View.GONE);
        ((ImageView) preventView.findViewById(R.id.vehicle_guard_icon)).setImageResource(R.drawable.vehicle_guard_normal);
        ((ImageView) preventView.findViewById(R.id.vehicle_robbery_icon)).setImageResource(R.drawable.vehicle_robbery_selected_icon);


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


