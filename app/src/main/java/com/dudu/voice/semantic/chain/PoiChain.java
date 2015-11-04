package com.dudu.voice.semantic.chain;

import android.content.Context;
import android.text.TextUtils;

import com.dudu.android.launcher.LauncherApplication;
import com.dudu.android.launcher.bean.MapEntity;
import com.dudu.android.launcher.bean.PoiEntity;
import com.dudu.android.launcher.bean.PoiSlots;
import com.dudu.android.launcher.utils.Constants;
import com.dudu.android.launcher.utils.GsonUtil;
import com.dudu.android.launcher.utils.JsonUtils;
import com.dudu.map.MapManager;
import com.dudu.voice.semantic.SemanticConstants;

/**
 * Created by Administrator on 2015/11/2.
 */
public class PoiChain extends SemanticChain {

    @Override
    public boolean matchSemantic(String service) {
        return SemanticConstants.SERVICE_POI.equals(service);
    }

    @Override
    public boolean doSemantic(String json) {
        String semantic = JsonUtils.parseIatResult(json,
                "semantic");
        PoiEntity poiEntity = (PoiEntity) GsonUtil
                .jsonToObject(semantic, PoiEntity.class);
        PoiSlots poiSlots = poiEntity.getSlots();
        String keyword = poiSlots.getKeyword();
        if (TextUtils.isEmpty(keyword)) {
            return false;
        }

        MapManager mapManager = MapManager.getInstance();
        Context context = LauncherApplication.getContext();
        switch (keyword) {
            case Constants.REFUEL:
                mapManager.mapControl(null, Constants.GAS_STATION,
                        MapManager.SEARCH_NEARBY);
                break;
            case Constants.SLEEPY:
            case Constants.TIRED:
            case Constants.SLEEP:
                mapManager.mapControl(null, Constants.HOTEL,
                        MapManager.SEARCH_NEARBY);
                break;
            case Constants.DRAW_MONEY:
                mapManager.mapControl(null, Constants.BANK,
                        MapManager.SEARCH_NEARBY);
                break;
            default:
                mapManager.mapControl(null, keyword,
                        MapManager.SEARCH_NEARBY);
        }

        return true;
    }

}
