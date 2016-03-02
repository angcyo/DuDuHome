package com.dudu.aios.ui.fragment.base;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dudu.aios.ui.activity.MainRecordActivity;
import com.dudu.aios.ui.utils.contants.FragmentConstants;
import com.dudu.aios.ui.voice.VoiceEvent;
import com.dudu.android.launcher.LauncherApplication;
import com.dudu.android.launcher.utils.ActivitiesManager;

import de.greenrobot.event.EventBus;

public abstract class BaseFragment extends BaseManagerFragment
{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        EventBus.getDefault().unregister(this);
        EventBus.getDefault().register(this);

        return getView();
    }


    public void replaceFragment(String name) {
        MainRecordActivity activity = (MainRecordActivity) getActivity();
        if (activity != null)
            activity.replaceFragment(name);
    }

    public void onEventMainThread(VoiceEvent event) {
        switch (event) {
            case SHOW_ANIM:
                replaceFragment(FragmentConstants.VOICE_FRAGMENT);

                break;
            case DISMISS_WINDOW:
                Log.d("lxh", "voice  DISMISS_WINDOW BaseFragment  " + LauncherApplication.lastFragment);
                if (ActivitiesManager.getInstance().getTopActivity() instanceof MainRecordActivity) {
                    replaceFragment(LauncherApplication.lastFragment);
                }
                break;
        }

    }

}
