package com.dudu.aios.ui.fragment;

import android.app.ActionBar;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.dudu.aios.ui.dialog.VehiclePasswordSetDialog;
import com.dudu.aios.ui.fragment.base.BaseVehicleFragment;
import com.dudu.android.launcher.R;

/**
 * Created by Administrator on 2016/2/16.
 */
public class VehiclePasswordSetFragment extends BaseVehicleFragment implements View.OnClickListener {

    private Button btnPasswordSet, btnGesture;

    private Button btnZero, btnOne, btnTwo, btnThree, btnFour, btnFive, btnSix, btnSeven, btnEight, btnNine, btnWell, btnRice;

    private int passwordDigit = 0;

    private String password = "";

    private LinearLayout dynamicPasswordContainer;

    private Handler handler = new MyHandle();


    @Override
    public View getVehicleChildView() {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.guard_password_set_layout, null);
        initView(view);
        initListener();
        initData();
        return view;
    }

    private void initData() {
        stePassword(0);
    }

    private void initView(View view) {
        btnGesture = (Button) view.findViewById(R.id.button_passwordSet);
        btnPasswordSet = (Button) view.findViewById(R.id.button_gesture_unlock);
        dynamicPasswordContainer = (LinearLayout) view.findViewById(R.id.dynamic_password_container);

        btnZero = (Button) view.findViewById(R.id.button_zero);
        btnOne = (Button) view.findViewById(R.id.button_one);
        btnTwo = (Button) view.findViewById(R.id.button_two);
        btnThree = (Button) view.findViewById(R.id.button_three);
        btnFour = (Button) view.findViewById(R.id.button_four);
        btnFive = (Button) view.findViewById(R.id.button_five);
        btnSix = (Button) view.findViewById(R.id.button_six);
        btnSeven = (Button) view.findViewById(R.id.button_seven);
        btnEight = (Button) view.findViewById(R.id.button_eight);
        btnNine = (Button) view.findViewById(R.id.button_nine);
        btnWell = (Button) view.findViewById(R.id.button_well);
        btnRice = (Button) view.findViewById(R.id.button_rice);
    }

    private void initListener() {
        btnGesture.setOnClickListener(this);
        btnPasswordSet.setOnClickListener(this);

        btnZero.setOnClickListener(this);
        btnOne.setOnClickListener(this);
        btnTwo.setOnClickListener(this);
        btnThree.setOnClickListener(this);
        btnFour.setOnClickListener(this);
        btnFive.setOnClickListener(this);
        btnSix.setOnClickListener(this);
        btnSeven.setOnClickListener(this);
        btnEight.setOnClickListener(this);
        btnNine.setOnClickListener(this);
        btnWell.setOnClickListener(this);
        btnRice.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_one:
                break;
            case R.id.button_two:
                break;
            case R.id.button_three:
                break;
            case R.id.button_four:
                break;
            case R.id.button_five:
                break;
            case R.id.button_six:
                break;
            case R.id.button_seven:
                break;
            case R.id.button_eight:
                break;
            case R.id.button_nine:
                break;
            case R.id.button_zero:
                break;
            case R.id.button_well:
                break;
            case R.id.button_rice:
                break;
            case R.id.button_passwordSet:
                actionPasswordSet();
                return;
            case R.id.button_gesture_unlock:
                getFragmentManager().beginTransaction().replace(R.id.container, new GestureFragment()).commit();
                return;
        }
        handleDialButtonClick(v);
    }

    private void actionPasswordSet() {
        showPasswordSetDialog();
    }

    private void showPasswordSetDialog() {
        VehiclePasswordSetDialog dialog = new VehiclePasswordSetDialog(getActivity());
        dialog.show();
    }

    private void handleDialButtonClick(View v) {
        password += v.getTag();
        passwordDigit++;
        stePassword(passwordDigit);
        if (passwordDigit == 4) {
            handler.sendEmptyMessageDelayed(0, 1000);
            passwordDigit = 0;
            if (password.equals("1234")) {
                //正确
            } else {
                //错误
            }
            password = "";
        }
    }

    private void stePassword(int step) {
        dynamicPasswordContainer.removeAllViews();
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        for (int i = 0; i < 4; i++) {
            ImageView imageView = new ImageView(getActivity());
            if (step > i) {
                imageView.setImageResource(R.drawable.password_unlock_point_full);
            } else {
                imageView.setImageResource(R.drawable.password_unlock_point_null);
            }
            layoutParams.setMargins(7, 7, 7, 7);
            imageView.setLayoutParams(layoutParams);
            dynamicPasswordContainer.addView(imageView);
        }
    }

    class MyHandle extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            stePassword(0);
        }
    }
}
