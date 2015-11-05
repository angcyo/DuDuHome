package com.dudu.voice.semantic.chain;

import android.app.Activity;

import com.amap.api.navi.AMapNavi;
import com.dudu.android.launcher.ui.activity.NearbyRepairActivity;
import com.dudu.android.launcher.utils.ActivitiesManager;
import com.dudu.android.launcher.utils.Constants;
import com.dudu.map.Navigation;
import com.dudu.voice.semantic.SemanticConstants;
import com.dudu.voice.semantic.SemanticType;
import com.dudu.voice.semantic.engine.SemanticProcessor;

import de.greenrobot.event.EventBus;

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
            EventBus.getDefault().post(new Navigation(location, Navigation.NAVI_NORMAL, AMapNavi.DrivingDefault));

            SemanticProcessor.getProcessor().switchSemanticType(SemanticType.NORMAL);
        }

        return true;
    }


}
