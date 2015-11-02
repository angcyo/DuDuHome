package com.dudu.voice.semantic.chain;

import android.app.Activity;

import com.dudu.android.launcher.ui.activity.LocationActivity;
import com.dudu.android.launcher.utils.ActivitiesManager;
import com.dudu.android.launcher.utils.ChoiseUtil;
import com.dudu.android.launcher.utils.JsonUtils;
import com.dudu.map.MapManager;
import com.dudu.voice.semantic.SemanticConstants;

/**
 * Created by pc on 2015/10/30.
 */
public class ChoiseChain extends SemanticChain{

    private int choiseSize = 0;



    @Override
    public boolean matchSemantic(String service) {
        return service.equalsIgnoreCase(SemanticConstants.SERVICE_CHOISE);
    }


    @Override
    public boolean doSemantic(String json) {

        String semantic = JsonUtils.parseIatResult(json,
                "semantic");

        choiseSize = ChoiseUtil.getChoiseSize(JsonUtils
                .parseIatResultChoiseSize(semantic));
        Activity topActivity = ActivitiesManager.getInstance().getTopActivity();
        if(topActivity!=null&&(topActivity instanceof LocationActivity)&& MapManager.getInstance().isShowAddress()){

            ((LocationActivity) topActivity)
                    .startChoiseResult(choiseSize, json);
            return true;
        }
        return false;
    }
}
