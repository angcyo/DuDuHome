package com.dudu.aios.ui.voice;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.dudu.aios.ui.activity.MainRecordActivity;
import com.dudu.aios.ui.fragment.base.BaseFragment;
import com.dudu.aios.ui.utils.contants.FragmentConstants;
import com.dudu.android.launcher.LauncherApplication;
import com.dudu.android.launcher.R;
import com.dudu.android.launcher.utils.ActivitiesManager;

import de.greenrobot.event.EventBus;

/**
 * Created by lxh on 2016/2/13.
 */
public class VoiceFragment extends BaseFragment {

    @Override
    public View getView() {

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.voice_layout, null);

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


}
