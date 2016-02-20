package com.dudu.aios.ui.fragment.base;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dudu.aios.ui.fragment.GuardFragment;
import com.dudu.aios.ui.fragment.MainFragment;
import com.dudu.aios.ui.fragment.RobberyFragment;
import com.dudu.aios.ui.utils.contants.FragmentConstants;
import com.dudu.android.launcher.R;

/**
 * Created by Administrator on 2016/2/16.back_button
 */
public abstract class BaseVehicleFragment extends BaseFragment {

    private RelativeLayout childVehicleViewContainer;

    private LinearLayout vehicle_guard_btn, vehicle_robbery_btn;

    protected View view;

    private ImageButton buttonBack;


    @Override

    public View getView() {
        view = LayoutInflater.from(getActivity()).inflate(R.layout.prevent_rob_layout, null);
        initVehicleView(view);
        initOnListener();
        return view;
    }

    private void initOnListener() {

        MyListener listener = new MyListener();

        vehicle_guard_btn.setOnClickListener(listener);

        vehicle_robbery_btn.setOnClickListener(listener);

        buttonBack.setOnClickListener(listener);

    }

    private void initVehicleView(View view) {

        childVehicleViewContainer = (RelativeLayout) view.findViewById(R.id.vehicle_right_layout);

        ViewGroup.LayoutParams rightParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        childVehicleViewContainer.addView(getVehicleChildView(), rightParams);

        vehicle_guard_btn = (LinearLayout) view.findViewById(R.id.vehicle_guard_btn);

        vehicle_robbery_btn = (LinearLayout) view.findViewById(R.id.vehicle_robbery_btn);

        buttonBack = (ImageButton) view.findViewById(R.id.vehicle_back_button);

    }

    private void robbery() {
        ((ImageView) view.findViewById(R.id.vehicle_guard_icon)).setImageResource(R.drawable.vehicle_guard_normal);
        ((TextView) view.findViewById(R.id.text_vehicle_guard_ch)).setTextColor(getResources().getColor(R.color.unchecked_textColor));
        ((TextView) view.findViewById(R.id.text_vehicle_guard_en)).setTextColor(getResources().getColor(R.color.unchecked_textColor));

        ((ImageView) view.findViewById(R.id.vehicle_robbery_icon)).setImageResource(R.drawable.vehicle_robbery_clicked_icon);
        ((TextView) view.findViewById(R.id.text_vehicle_robbery_ch)).setTextColor(getResources().getColor(R.color.white));
        ((TextView) view.findViewById(R.id.text_vehicle_robbery_en)).setTextColor(getResources().getColor(R.color.white));

        getFragmentManager().beginTransaction().replace(R.id.container, new RobberyFragment()).commit();

    }

    private void guard() {
        ((ImageView) view.findViewById(R.id.vehicle_guard_icon)).setImageResource(R.drawable.vehicle_guard_clicked);
        ((TextView) view.findViewById(R.id.text_vehicle_guard_ch)).setTextColor(getResources().getColor(R.color.white));
        ((TextView) view.findViewById(R.id.text_vehicle_guard_en)).setTextColor(getResources().getColor(R.color.white));

        ((ImageView) view.findViewById(R.id.vehicle_robbery_icon)).setImageResource(R.drawable.vehicle_robbery_normal);
        ((TextView) view.findViewById(R.id.text_vehicle_robbery_ch)).setTextColor(getResources().getColor(R.color.unchecked_textColor));
        ((TextView) view.findViewById(R.id.text_vehicle_robbery_en)).setTextColor(getResources().getColor(R.color.unchecked_textColor));

        getFragmentManager().beginTransaction().replace(R.id.container, new GuardFragment()).commit();
    }

    public abstract View getVehicleChildView();

    private class MyListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.vehicle_guard_btn:
                    guard();
                    break;
                case R.id.vehicle_robbery_btn:
                    robbery();
                    break;
                case R.id.vehicle_back_button:
                    replaceFragment(FragmentConstants.FRAGMENT_MAIN_PAGE);
                    break;
            }
        }
    }
}
