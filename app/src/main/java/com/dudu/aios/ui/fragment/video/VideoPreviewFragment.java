package com.dudu.aios.ui.fragment.video;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dudu.aios.ui.fragment.base.BaseFragment;
import com.dudu.android.launcher.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class VideoPreviewFragment extends BaseFragment {


    public VideoPreviewFragment() {
        // Required empty public constructor
    }


    @Override
    public View getChildView() {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_video_preview, null);
        return view;
    }


}
