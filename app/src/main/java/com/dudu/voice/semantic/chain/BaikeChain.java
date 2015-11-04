package com.dudu.voice.semantic.chain;

import android.app.Activity;

import com.dudu.android.launcher.ui.activity.LocationActivity;
import com.dudu.android.launcher.ui.activity.LocationMapActivity;
import com.dudu.android.launcher.utils.ActivitiesManager;
import com.dudu.android.launcher.utils.JsonUtils;
import com.dudu.map.MapManager;
import com.dudu.voice.semantic.SemanticConstants;

/**
 * Created by pc on 2015/10/30.
 */
public class BaikeChain extends SemanticChain {

    private MapManager mapManager = null;

    @Override
    public boolean matchSemantic(String service) {
        return service.equalsIgnoreCase(SemanticConstants.SERVICE_BAIKE);
    }

    @Override
    public boolean doSemantic(String json) {
        mapManager = MapManager.getInstance();
        if(mapManager.isSearch()){

            mapManager.setSearch(false);
            String message = JsonUtils.parseIatResult(json, "text");
            Activity activity = ActivitiesManager
                    .getInstance().getTopActivity();
            if(activity instanceof LocationMapActivity){
                ((LocationMapActivity) activity).handlerSarch(null,message);
            }
            return  true;
        }

        return false;
    }
}
