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
import com.dudu.voice.semantic.SemanticType;
import com.dudu.voice.semantic.engine.SemanticProcessor;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by lxh on 2015/10/29.
 */
public class MapSearchChain extends SemanticChain {

    private String TAG = "MapSearchChain";

    private MapManager mapManager = null;

    private String mapOperation = "";

    private static final String ROUTE = "ROUTE";

    private static final String POSITION = "POSITION";

    private static final String NEAREST = "最近";

    private int type = 0;

    public MapSearchChain() {
        mapManager = MapManager.getInstance();
    }

    @Override
    public boolean doSemantic(String json) {
        String service = JsonUtils.getRsphead(json).getService();

        String semantic = JsonUtils.parseIatResult(json,
                "semantic");

        if (!TextUtils.isEmpty(service)) {
            switch (service) {
                case SemanticConstants.SERVICE_MAP:

                    parseOperation(json);

                    if (!TextUtils.isEmpty(mapOperation)) {

                        if (mapOperation.equals(ROUTE)) {
                            type = MapManager.SEARCH_POI;
                        } else if (mapOperation.equals(POSITION)) {
                            type = MapManager.SEARCH_PLACE_LOCATION;
                        }

                        MapEntity mapEntity = (MapEntity) GsonUtil
                                .jsonToObject(semantic, MapEntity.class);
                        mapManager.mapControl(mapEntity, null, type);
                    }
                    break;
                case SemanticConstants.SERVICE_HOTEL:
                    MapEntity hotelEntity = (MapEntity) GsonUtil
                            .jsonToObject(semantic, MapEntity.class);
                    mapManager.mapControl(hotelEntity, null, MapManager.SEARCH_NEARBY);
                    break;
                case SemanticConstants.SERVICE_NEARBY:
                    String optionType = JsonUtils.getNearbyOptionType(semantic);
                    if (optionType.equals(NEAREST)) {
                        type = MapManager.SEARCH_NEAREST;
                    } else {
                        type = MapManager.SEARCH_NEARBY;
                    }

                    String poiKeyWord = JsonUtils
                            .parseIatResultNearby(semantic);
                    mapManager.mapControl(null, poiKeyWord,
                            type);
                    break;
                case SemanticConstants.SERVICE_RESTAURANT:
                    RestaurantEntity restaurantEntity = (RestaurantEntity) GsonUtil
                            .jsonToObject(semantic,
                                    RestaurantEntity.class);
                    mapManager.mapControl(null, restaurantEntity.getRestaurantSlots()
                            .getCategory(), MapManager.SEARCH_NEARBY);
                    break;
            }

            return true;
        }

        return false;
    }

    @Override
    public boolean matchSemantic(String service) {
        return service.equalsIgnoreCase(SemanticConstants.SERVICE_MAP) ||
                service.equalsIgnoreCase(SemanticConstants.SERVICE_NEARBY) ||
                service.equalsIgnoreCase(SemanticConstants.SERVICE_RESTAURANT) ||
                service.equalsIgnoreCase(SemanticConstants.SERVICE_HOTEL);
    }

    private void parseOperation(String json) {
        try {
            mapOperation = new JSONObject(json).getString("operation");
        } catch (JSONException e) {
            mapOperation = "";
        }
    }

}
