package com.dudu.navi.service;

import android.text.TextUtils;

import com.dudu.navi.Util.GsonUtil;
import com.dudu.navi.Util.NaviUtils;
import com.dudu.navi.entity.MapEntity;
import com.dudu.navi.entity.MapLocation;
import com.dudu.navi.entity.MapSlots;
import com.dudu.navi.entity.MapSlotsLoc;
import com.dudu.navi.entity.RestaurantEntity;
import com.dudu.navi.entity.RestaurantSlots;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by lxh on 2015/11/26.
 */
public class KeywordProcess {

    public static final String SERVICE_MAP = "map";

    public static final String SERVICE_HOTEL = "hotel";

    public static final String SERVICE_RESTAURANT = "restaurant";

    public static final String SERVICE_NEARBY = "nearby";

    public static final String SERVICE_POI = "poi";

    private static String mapOperation = "";

    private static final String ROUTE = "ROUTE";

    private static final String POSITION = "POSITION";

    private static final String NEAREST = "最近";

    private static String keyWord;

    public static String parseKeyword(String semantic,String service){
         keyWord = null;
        if (!TextUtils.isEmpty(service)) {

                switch (service){
                    case SERVICE_MAP:
                        MapEntity mapEntity = (MapEntity) GsonUtil
                                .jsonToObject(semantic, MapEntity.class);
                        getMapEntityKeyword(mapEntity);
                        break;
                    case SERVICE_HOTEL:
                        MapEntity hotelEntity = (MapEntity) GsonUtil
                                .jsonToObject(semantic, MapEntity.class);
                        getMapEntityKeyword(hotelEntity);
                        break;
                    case SERVICE_NEARBY:
                        keyWord = NaviUtils
                                .parseIatResultNearby(semantic);
                        break;
                    case SERVICE_RESTAURANT:
                        RestaurantEntity restaurantEntity = (RestaurantEntity) GsonUtil
                                .jsonToObject(semantic,
                                        RestaurantEntity.class);
                        getRestaurantEntityKeyword(restaurantEntity);
                        break;
                }


        }
        return  keyWord;
    }

    private static void getMapEntityKeyword(MapEntity entity){
        MapSlots slots = entity.getSlots();
        MapSlotsLoc location = slots.getEndLoc();
        if (location != null) {
            if (!TextUtils.isEmpty(location.getPoi())) {
                keyWord = location.getPoi();

            } else {
                if (!TextUtils.isEmpty(location.getAreaAddr())) {
                    keyWord = location.getAreaAddr();
                } else {
                    if (!TextUtils.isEmpty(location.getCity())) {
                        keyWord = location.getCity();
                    }
                }
            }

        } else {
            MapLocation mapLocation = slots.getLocation();
            if (mapLocation != null) {
                if (!TextUtils.isEmpty(mapLocation.getPoi())) {
                    keyWord = mapLocation.getPoi();
                } else {

                    if (!TextUtils.isEmpty(mapLocation.getAreaAddr())) {
                        keyWord = mapLocation.getAreaAddr();
                    } else {
                        if (!TextUtils.isEmpty(mapLocation.getCity())) {
                            keyWord = mapLocation.getCity();
                        }
                    }
                }

            }

        }

    }

    private static void getRestaurantEntityKeyword(RestaurantEntity restaurantEntity){
        RestaurantSlots slots = restaurantEntity.getRestaurantSlots();
        if (slots != null) {
            keyWord = slots.getCategory();
        }

    }
}
