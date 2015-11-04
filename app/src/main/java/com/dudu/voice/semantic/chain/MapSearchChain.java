package com.dudu.voice.semantic.chain;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

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

    private String TAG = "MapSearchChain";

    private MapManager mapManager = null;

    private Context mContext;

    public MapSearchChain(){
        mapManager = MapManager.getInstance();
        mContext = LauncherApplication.getContext();
    }

    @Override
    public boolean doSemantic(String json) {
        Log.d(TAG,"---------------map : " + json);

        String service = JsonUtils.getRsphead(json).getService();
        String semantic = JsonUtils.parseIatResult(json,
                "semantic");
        if(!TextUtils.isEmpty(service)) {
            switch (service){
                case SemanticConstants.SERVICE_MAP:

                    MapEntity mapEntity = (MapEntity) GsonUtil
                            .jsonToObject(semantic, MapEntity.class);
                    mapManager.mapControl(mapEntity, null, MapManager.SEARCH_POI);
                    break;
                case SemanticConstants.SERVICE_HOTEL:
                    MapEntity hotelEntity = (MapEntity) GsonUtil
                            .jsonToObject(semantic, MapEntity.class);
                    mapManager.mapControl(hotelEntity, null, MapManager.SEARCH_NEARBY);
                    break;
                case SemanticConstants.SERVICE_NEARBY:
                    String poiKeyWord = JsonUtils
                            .parseIatResultNearby(semantic);
                    mapManager.mapControl(null, poiKeyWord,
                            MapManager.SEARCH_NEARBY);
                    break;
                case SemanticConstants.SERVICE_RESTAURANT:
                    RestaurantEntity restaurantEntity = (RestaurantEntity) GsonUtil
                            .jsonToObject(semantic,
                                    RestaurantEntity.class);
                    mapManager.mapControl(null,restaurantEntity.getRestaurantSlots()
                                    .getCategory(), MapManager.SEARCH_NEARBY);

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
