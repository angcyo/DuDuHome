package com.dudu.voice.semantic.chain;

import android.content.Context;
import android.text.TextUtils;

import com.dudu.android.launcher.LauncherApplication;
import com.dudu.android.launcher.bean.MapEntity;
import com.dudu.android.launcher.bean.RestaurantEntity;
import com.dudu.android.launcher.utils.GsonUtil;
import com.dudu.android.launcher.utils.JsonUtils;
import com.dudu.map.MapManager;
import com.dudu.voice.semantic.SemanticConstants;

/**
 * Created by lxh on 2015/10/29.
 */
public class MapSearchChain extends SemanticChain {
    private MapManager mapManager = null;

    private Context mContext;


    public MapSearchChain(){
        mapManager = MapManager.getInstance();
        this.mContext = LauncherApplication.getContext().getApplicationContext();
    }

    @Override
    public boolean doSemantic(String json) {

        String serivce = JsonUtils.getRsphead(json).getService();
        String semantic = JsonUtils.parseIatResult(json,
                "semantic");
        if(!TextUtils.isEmpty(serivce)){
            switch (serivce){
                case SemanticConstants.SERVICE_MAP:
                case SemanticConstants.SERVICE_HOTEL:
                    MapEntity mapEntity = (MapEntity) GsonUtil
                            .jsonToObject(semantic, MapEntity.class);
                    mapManager.mapControl(mContext, mapEntity, null, MapManager.SEARCH_NAVI);
                    break;
                case SemanticConstants.SERVICE_NEARBY:
                    String poiKeyWord = JsonUtils
                            .parseIatResultNearby(semantic);
                    mapManager.mapControl(mContext, null, poiKeyWord,
                            MapManager.SEARCH_NEARBY);

                    break;
                case SemanticConstants.SERVICE_RESTAURANT:
                    RestaurantEntity restaurantEntity = (RestaurantEntity) GsonUtil
                            .jsonToObject(semantic,
                                    RestaurantEntity.class);
                    MapManager.getInstance().mapControl(
                            mContext,
                            null,
                            restaurantEntity.getRestaurantSlots()
                                    .getCategory(), MapManager.SEARCH_NAVI);

                    break;

            }
            return  true;
        }

        return false;
    }

    @Override
    public boolean matchSemantic(String service) {
        boolean match_map = service.equalsIgnoreCase(SemanticConstants.SERVICE_MAP);

        boolean match_nearby = service.equalsIgnoreCase(SemanticConstants.SERVICE_NEARBY);

        boolean match_restaurant = service.equalsIgnoreCase(SemanticConstants.SERVICE_RESTAURANT);

        boolean match_hotel = service.equalsIgnoreCase(SemanticConstants.SERVICE_HOTEL);

        return match_map||match_nearby||match_restaurant||match_hotel;
    }


}
