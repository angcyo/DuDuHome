package com.dudu.aios.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dudu.aios.ui.fragment.base.BaseFragment;
import com.dudu.aios.ui.utils.contants.FragmentConstants;
import com.dudu.android.launcher.R;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SafetyMainFragment extends BaseFragment implements View.OnClickListener {

    private LinearLayout vehicle_guard_btn, vehicle_robbery_btn;

    private ImageButton buttonBack;

    private View view;
    private Logger logger = LoggerFactory.getLogger("SafetyFragment");

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.prevent_rob_layout, container, false);
        initView(view);
        initOnListener();
        return view;
    }

    private void initOnListener() {
        vehicle_guard_btn.setOnClickListener(this);

        vehicle_robbery_btn.setOnClickListener(this);

        buttonBack.setOnClickListener(this);
    }

    private void initView(View view) {
        vehicle_guard_btn = (LinearLayout) view.findViewById(R.id.vehicle_guard_btn);

        vehicle_robbery_btn = (LinearLayout) view.findViewById(R.id.vehicle_robbery_btn);

        buttonBack = (ImageButton) view.findViewById(R.id.vehicle_back_button);
    }

    @Override
    public void onStart() {
        super.onStart();
        actionRobbery();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.vehicle_guard_btn:
                vehicle_guard_btn.setEnabled(false);
                vehicle_robbery_btn.setEnabled(true);
                actionGuard();
                break;
            case R.id.vehicle_robbery_btn:
                vehicle_robbery_btn.setEnabled(false);
                vehicle_guard_btn.setEnabled(true);
                actionRobbery();
                break;
            case R.id.vehicle_back_button:
//                buttonBack.setEnabled(false);
                replaceFragment(FragmentConstants.FRAGMENT_MAIN_PAGE);
                break;
        }
    }

    private void actionRobbery() {
        robbery();
//        getFragmentManager().beginTransaction().replace(R.id.vehicle_right_layout, new RobberyMainFragment()).commit();
    }

    private void actionGuard() {
        guard();
//        getFragmentManager().beginTransaction().replace(R.id.vehicle_right_layout, new GuardFragment()).commit();
    }

    private void robbery() {
        ((ImageView) view.findViewById(R.id.vehicle_guard_icon)).setImageResource(R.drawable.vehicle_guard_normal);
        ((TextView) view.findViewById(R.id.text_vehicle_guard_ch)).setTextColor(getResources().getColor(R.color.unchecked_textColor));
        ((TextView) view.findViewById(R.id.text_vehicle_guard_en)).setTextColor(getResources().getColor(R.color.unchecked_textColor));

        ((ImageView) view.findViewById(R.id.vehicle_robbery_icon)).setImageResource(R.drawable.vehicle_robbery_clicked_icon);
        ((TextView) view.findViewById(R.id.text_vehicle_robbery_ch)).setTextColor(getResources().getColor(R.color.white));
        ((TextView) view.findViewById(R.id.text_vehicle_robbery_en)).setTextColor(getResources().getColor(R.color.white));

    }

    private void guard() {
        ((ImageView) view.findViewById(R.id.vehicle_guard_icon)).setImageResource(R.drawable.vehicle_guard_clicked);
        ((TextView) view.findViewById(R.id.text_vehicle_guard_ch)).setTextColor(getResources().getColor(R.color.white));
        ((TextView) view.findViewById(R.id.text_vehicle_guard_en)).setTextColor(getResources().getColor(R.color.white));

        ((ImageView) view.findViewById(R.id.vehicle_robbery_icon)).setImageResource(R.drawable.vehicle_robbery_normal);
        ((TextView) view.findViewById(R.id.text_vehicle_robbery_ch)).setTextColor(getResources().getColor(R.color.unchecked_textColor));
        ((TextView) view.findViewById(R.id.text_vehicle_robbery_en)).setTextColor(getResources().getColor(R.color.unchecked_textColor));

    }
}
