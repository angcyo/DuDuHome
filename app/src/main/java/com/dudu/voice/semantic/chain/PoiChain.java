package com.dudu.voice.semantic.chain;

import android.text.TextUtils;

import com.dudu.android.launcher.bean.PoiEntity;
import com.dudu.android.launcher.bean.PoiSlots;
import com.dudu.android.launcher.utils.Constants;
import com.dudu.android.launcher.utils.GsonUtil;
import com.dudu.android.launcher.utils.JsonUtils;
import com.dudu.map.NavigationClerk;
import com.dudu.navi.vauleObject.SearchType;
import com.dudu.voice.semantic.SemanticConstants;

/**
 * Created by Administrator on 2015/11/2.
 */
public class PoiChain extends SemanticChain {

    private String searchKeyword;
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

        switch (keyword) {
            case Constants.REFUEL:

                searchKeyword = Constants.GAS_STATION;
                break;
            case Constants.SLEEPY:
            case Constants.TIRED:
            case Constants.SLEEP:
                searchKeyword = Constants.HOTEL;
                break;
            case Constants.DRAW_MONEY:
                searchKeyword = Constants.BANK;
                break;
            default:
                searchKeyword = keyword;

        }
        NavigationClerk.getInstance().searchControl(null, null, searchKeyword, SearchType.SEARCH_NEARBY);
        return true;
    }

}
