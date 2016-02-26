package com.dudu.aios.ui.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dudu.aios.ui.robbery.RobberyConstant;
import com.dudu.android.launcher.R;
import com.dudu.commonlib.repo.ReceiverData;
import com.dudu.workflow.common.DataFlowFactory;
import com.dudu.workflow.common.ReceiverDataFlow;
import com.dudu.workflow.common.RequestFactory;
import com.dudu.workflow.robbery.RobberyRequest;

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
                requestCheckToUnlock();
                return;
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
//                lock();
                break;
            case R.id.vehicle_locked_layout:
                transferParameters();
                break;
        }
    }

    private void transferParameters() {
        GestureFragment gestureFragment = new GestureFragment();
        Bundle bundle = new Bundle();
        bundle.putString(RobberyConstant.CATEGORY_CONSTANT, RobberyConstant.ROBBERY_CONSTANT);
        gestureFragment.setArguments(bundle);
        getFragmentManager().beginTransaction().replace(R.id.vehicle_right_layout, gestureFragment).commit();
    }

    private void showLockedView() {
        guard_locked_layout.setVisibility(View.VISIBLE);
        guard_unlock_layout.setVisibility(View.GONE);
    }

    private void showUnlockedView() {
        guard_locked_layout.setVisibility(View.GONE);
        guard_unlock_layout.setVisibility(View.VISIBLE);
    }

    public void requestCheckToUnlock() {
        showUnlockedView();
        DataFlowFactory.getSwitchDataFlow()
                .saveRobberyState(false);
        RequestFactory.getRobberyRequest()
                .closeAntiRobberyMode(new RobberyRequest.CloseRobberyModeCallback() {
                    @Override
                    public void closeSuccess(boolean success) {
                        if (!success) {
                            requestCheckToUnlock();
                        }
                    }

                    @Override
                    public void requestError(String error) {
                        logger.debug(error);
                        requestCheckToUnlock();
                    }
                });
    }

    public void syncAppRobberyFlow() {
        DataFlowFactory.getSwitchDataFlow()
                .getRobberyState()
                .subscribe(locked -> {
                    if (locked) {
                        showLockedView();
                    } else {
                        showUnlockedView();
                    }
                },(error)->{
                    logger.error("syncAppRobberyFlow", error);
                });
    }

    public void onEventMainThread(ReceiverData event) {
        if (ReceiverDataFlow.getRobberyReceiveData(event)) {
            getFragmentManager().beginTransaction().replace(R.id.vehicle_right_layout, new RobberyFragment()).commit();
            ReceiverDataFlow.saveRobberyReceiveData(event);
        }
    }
}
