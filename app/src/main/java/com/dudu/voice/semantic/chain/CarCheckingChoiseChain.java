package com.dudu.voice.semantic.chain;

import android.app.Activity;

import com.dudu.android.launcher.LauncherApplication;
import com.dudu.android.launcher.ui.activity.NearbyRepairActivity;
import com.dudu.android.launcher.utils.ActivitiesManager;
import com.dudu.navi.NavigationManager;
import com.dudu.navi.entity.Navigation;
import com.dudu.navi.entity.Point;
import com.dudu.navi.vauleObject.NaviDriveMode;
import com.dudu.navi.vauleObject.NavigationType;
import com.dudu.voice.semantic.SemanticConstants;
import com.dudu.voice.semantic.SemanticType;
import com.dudu.voice.semantic.engine.SemanticProcessor;

/**
 * Created by Administrator on 2015/11/5.
 */
public class CarCheckingChoiseChain extends SemanticChain {

    @Override
    public boolean matchSemantic(String service) {
        return SemanticConstants.SERVICE_CHOISE.equals(service);
    }

    @Override
    public boolean doSemantic(String json) {
        Activity activity = ActivitiesManager
                .getInstance().getTopActivity();
        if (activity != null
                && activity instanceof NearbyRepairActivity) {
            double[] location = {23.156596, 113.30791};

            Navigation navigation = new Navigation(new Point(23.156596,113.30791), NaviDriveMode.SPEEDFIRST, NavigationType.NAVIGATION);
            NavigationManager.getInstance(LauncherApplication.getContext()).startCalculate(navigation);

            SemanticProcessor.getProcessor().switchSemanticType(SemanticType.NORMAL);
        }

        return true;
    }


}
