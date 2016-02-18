package com.dudu.aios.ui.fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dudu.aios.ui.dialog.PasswordSetDialog;
import com.dudu.aios.ui.fragment.base.BaseFragment;
import com.dudu.aios.ui.utils.contants.FragmentConstants;
import com.dudu.aios.ui.view.FlowCompletedView;
import com.dudu.android.launcher.R;
import com.dudu.android.launcher.utils.Constants;
import com.dudu.android.launcher.utils.SharedPreferencesUtils;
import com.dudu.android.launcher.utils.WifiApAdmin;

import java.text.DecimalFormat;

public class FlowFragment extends BaseFragment implements View.OnClickListener {

    private ImageButton btnBack;

    private FlowCompletedView flowCompletedView;

    private TextView tvFlowPercent, mUsedFlowView, mRemainingFlowView;

    private LinearLayout closeFlowContainer, openFlowContainer, passwordSetContainer;

    private float mTotalFlow = 0;

    private float remainingFlow = 0;

    private DecimalFormat mDecimalFormat = new DecimalFormat("0.00");

    private static final String DEFAULT_FLOW_VALUE = "1024000";

    @Override
    public View getView() {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_flow, null);
        initFragmentView(view);
        initClickListener();
        initFlowData();
        return view;
    }

    private void initFlowData() {
        remainingFlow = Float.parseFloat(SharedPreferencesUtils.getStringValue(getActivity(), Constants.KEY_REMAINING_FLOW, DEFAULT_FLOW_VALUE)) / 1024;

        mTotalFlow = Float.parseFloat(SharedPreferencesUtils.getStringValue(getActivity(), Constants.KEY_MONTH_MAX_VALUE, DEFAULT_FLOW_VALUE)) / 1024;

        float usedFlow = mTotalFlow - remainingFlow;//使用流量改用差值

        mUsedFlowView.setText(getString(R.string.used_flow, mDecimalFormat.format(usedFlow)));

        if (remainingFlow <= 0) {
            mRemainingFlowView.setText(getString(R.string.remaining_flow, 0));
        } else {
            mRemainingFlowView.setText(getString(R.string.remaining_flow, remainingFlow));
        }

        int progress;
        if (usedFlow < 0) {
            progress = 100;
        } else {
            progress = Math.round(((usedFlow) * 100 / mTotalFlow));
        }

        if (progress > 100) {
            flowCompletedView.setProgress(100);
            tvFlowPercent.setText(100 + "%");
        } else {
            if (progress >= 95) {
//                WifiApAdmin.closeWifiAp(mContext);
            }
            flowCompletedView.setProgress(progress);
            tvFlowPercent.setText(progress + "%");
        }
    }

    private void initClickListener() {
        btnBack.setOnClickListener(this);
        closeFlowContainer.setOnClickListener(this);
        openFlowContainer.setOnClickListener(this);
        passwordSetContainer.setOnClickListener(this);
    }

    private void initFragmentView(View view) {
        btnBack = (ImageButton) view.findViewById(R.id.button_back);
        flowCompletedView = (FlowCompletedView) view.findViewById(R.id.flowCompletedView);
        tvFlowPercent = (TextView) view.findViewById(R.id.tv_flow_percent);
        closeFlowContainer = (LinearLayout) view.findViewById(R.id.close_flow_container);
        openFlowContainer = (LinearLayout) view.findViewById(R.id.open_flow_container);
        passwordSetContainer = (LinearLayout) view.findViewById(R.id.passwordSet_container);
        mUsedFlowView = (TextView) view.findViewById(R.id.used_text);
        mRemainingFlowView = (TextView) view.findViewById(R.id.remaining_flow_text);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_back:
                replaceFragment(FragmentConstants.FRAGMENT_MAIN_PAGE);
                break;
            case R.id.close_flow_container:
                actionCloseFlow();
                break;
            case R.id.open_flow_container:
                actionOpenFlow();
                break;
            case R.id.passwordSet_container:
                actionPasswordSet();
                break;
        }
    }

    private void actionOpenFlow() {
        WifiApAdmin.initWifiApState(getActivity());
        openFlowContainer.setVisibility(View.GONE);
        closeFlowContainer.setVisibility(View.VISIBLE);
    }

    private void actionCloseFlow() {
        WifiApAdmin.closeWifiAp(getActivity());
        openFlowContainer.setVisibility(View.VISIBLE);
        closeFlowContainer.setVisibility(View.GONE);
    }

    private void actionPasswordSet() {
        showPasswordSetDialog();
    }

    private void showPasswordSetDialog() {
        PasswordSetDialog dialog = new PasswordSetDialog(getActivity());
        dialog.show();
    }
}
