package com.dudu.aios.ui.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.dudu.aios.ui.robbery.RobberyConstant;
import com.dudu.aios.ui.view.GestureLockViewGroup;
import com.dudu.android.launcher.R;

/**
 * Created by Administrator on 2016/2/19.
 */
public class GestureFragment extends Fragment implements View.OnClickListener {

    private TextView tvDrawPrompt;

    private Button btPasswordSet;

    private GestureLockViewGroup gestureLockViewGroup;

    private Handler handler = new MyHandle();

    private String category = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_gesture, container, false);
        initView(view);
        initListener();
        initData();
        return view;
    }

    private void initData() {

        Bundle bundle = getArguments();
        if (bundle != null) {
            category = bundle.getString(RobberyConstant.CATEGORY_CONSTANT);

        }
        //设置的手势密码
        gestureLockViewGroup.setAnswer(new int[]{1, 2, 3});
        gestureLockViewGroup.setOnGestureLockViewListener(new GestureLockViewGroup.OnGestureLockViewListener() {
            @Override
            public void onBlockSelected(int cId) {

            }

            @Override
            public void onGestureEvent(boolean matched) {
                // if (matched) {
                //绘制成功
                tvDrawPrompt.setText(getResources().getString(R.string.draw_success));
                tvDrawPrompt.setTextColor(getResources().getColor(R.color.blue));
                Fragment fragment = null;
                if (RobberyConstant.GUARD_CONSTANT.equals(category)) {
                    fragment = new GuardFragment();
                    //防盗
                } else if (RobberyConstant.ROBBERY_CONSTANT.equals(category)) {
                    //防劫
                    fragment = new RobberyMainFragment();
                }
                //正确
                Bundle bundle = new Bundle();
                bundle.putString("pass", "1");
                if (fragment != null) {
                    fragment.setArguments(bundle);
                    getFragmentManager().beginTransaction().replace(R.id.vehicle_right_layout, fragment).commit();
                }
              /*  } else {
                    //绘制失败
                    tvDrawPrompt.setText(getResources().getString(R.string.draw_fault));
                    tvDrawPrompt.setTextColor(getResources().getColor(R.color.red_mistake));
                    handler.sendEmptyMessageDelayed(0, 1000);
                }*/

            }

            @Override
            public void onUnmatchedExceedBoundary() {
                gestureLockViewGroup.setUnMatchExceedBoundary(5);
            }
        });
    }

    private void initListener() {
        btPasswordSet.setOnClickListener(this);

    }

    private void initView(View view) {
        tvDrawPrompt = (TextView) view.findViewById(R.id.text_draw_prompt);
        btPasswordSet = (Button) view.findViewById(R.id.button_passwordSet);
        gestureLockViewGroup = (GestureLockViewGroup) view.findViewById(R.id.id_gestureLockViewGroup);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_passwordSet:
                VehiclePasswordSetFragment fragment = new VehiclePasswordSetFragment();
                Bundle bundle = new Bundle();
                bundle.putString(RobberyConstant.CATEGORY_CONSTANT, category);
                fragment.setArguments(bundle);
                getFragmentManager().beginTransaction().replace(R.id.vehicle_right_layout, fragment).commit();
                break;
        }
    }

    class MyHandle extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            tvDrawPrompt.setText("");
        }
    }
}
