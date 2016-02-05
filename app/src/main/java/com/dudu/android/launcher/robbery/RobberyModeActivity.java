package com.dudu.android.launcher.robbery;

import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.dudu.android.launcher.R;
import com.dudu.android.launcher.ui.activity.base.BaseNoTitlebarAcitivity;

/**
 * Created by Administrator on 2016/2/5.
 */
public class RobberyModeActivity extends BaseNoTitlebarAcitivity {


    private CheckBox aCheckBox;
    private CheckBox bCheckBox;
    private CheckBox cCheckBox;

    @Override
    public int initContentView() {
        return R.layout.robbery_layout;
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        aCheckBox = (CheckBox) findViewById(R.id.a_checkbox);
        bCheckBox = (CheckBox) findViewById(R.id.a_checkbox);
        cCheckBox = (CheckBox) findViewById(R.id.a_checkbox);
    }

    @Override
    public void initListener() {
        aCheckBox.setOnCheckedChangeListener(onCheckedChangeListener);
    }

    @Override
    public void initDatas() {

    }

    CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            switch (buttonView.getId()) {
                case R.id.a_checkbox:
                    RobberyCheckRequest.getInstance().requeset("18520339890", 0, isChecked ? 0 : 1);
                    break;
                case R.id.b_checkbox:
                    RobberyCheckRequest.getInstance().requeset("18520339890", 1, isChecked ? 0 : 1);
                    break;
                case R.id.c_checkbox:
                    RobberyCheckRequest.getInstance().requeset("18520339890", 2, isChecked ? 0 : 1);
                    break;
            }
        }
    };
}
