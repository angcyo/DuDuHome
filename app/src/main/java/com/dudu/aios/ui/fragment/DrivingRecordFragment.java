package com.dudu.aios.ui.fragment;

import android.app.Instrumentation;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;

import com.dudu.aios.ui.fragment.base.BaseFragment;
import com.dudu.aios.ui.utils.contants.FragmentConstants;
import com.dudu.android.launcher.R;

/**
 * Created by Administrator on 2016/2/2.
 */
public class DrivingRecordFragment extends BaseFragment implements View.OnClickListener {
    private ImageButton mCheckVideoButton, mSwitchVideoButton, mTakePhotoButton, mCheckPhotoButton, mBackButton;

    @Override
    public View getChildView() {

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_driving_record, null);

        initFragmentView(view);

        initClickListener();

        return view;
    }

    private void initClickListener() {
        mSwitchVideoButton.setOnClickListener(this);
        mCheckVideoButton.setOnClickListener(this);
        mCheckPhotoButton.setOnClickListener(this);
        mTakePhotoButton.setOnClickListener(this);
        mBackButton.setOnClickListener(this);
    }

    private void initFragmentView(View view) {
        mCheckVideoButton = (ImageButton) view.findViewById(R.id.check_video);
        mSwitchVideoButton = (ImageButton) view.findViewById(R.id.switch_video);
        mCheckPhotoButton = (ImageButton) view.findViewById(R.id.check_photo);
        mTakePhotoButton = (ImageButton) view.findViewById(R.id.take_photo);
        mBackButton = (ImageButton) view.findViewById(R.id.button_back);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.check_video:
                replaceFragment(FragmentConstants.FRAGMENT_VIDEO_LIST);
                break;
            case R.id.take_photo:
                break;
            case R.id.switch_video:
                break;
            case R.id.check_photo:
                replaceFragment(FragmentConstants.FRAGMENT_PHOTO_LIST);
                break;
            case R.id.button_back:
                replaceFragment(FragmentConstants.FRAGMENT_MAIN_PAGE);
                break;

        }

    }
}
