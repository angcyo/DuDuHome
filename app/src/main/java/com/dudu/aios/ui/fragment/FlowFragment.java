package com.dudu.aios.ui.fragment;

import android.text.TextPaint;
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
import com.dudu.android.launcher.utils.LogUtils;

public class FlowFragment extends BaseFragment implements View.OnClickListener {

    private ImageButton btnBack;

    private FlowCompletedView flowCompletedView;

    private TextView tvFlowPercent;

    private LinearLayout closeFlowContainer, openFlowContainer, passwordSetContainer;

    @Override
    public View getChildView() {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_flow, null);
        initFragmentView(view);
        initClickListener();
        initFlowData();
        return view;
    }

    private void initFlowData() {
        flowCompletedView.setProgress(75);
        TextPaint tp = tvFlowPercent.getPaint();
        tp.setFakeBoldText(true);
        tvFlowPercent.setText("75%");
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
        openFlowContainer.setVisibility(View.GONE);
        closeFlowContainer.setVisibility(View.VISIBLE);
    }

    private void actionCloseFlow() {
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
