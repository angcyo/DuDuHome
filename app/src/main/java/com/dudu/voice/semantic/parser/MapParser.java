package com.dudu.voice.semantic.parser;

import com.aispeech.common.JSONResultParser;
import com.dudu.android.launcher.utils.Constants;
import com.dudu.voice.semantic.bean.SemanticBean;
import com.dudu.voice.semantic.bean.map.MapBean;
import com.dudu.voice.semantic.bean.map.MapCommonAddressBean;
import com.dudu.voice.semantic.bean.map.MapLocationBean;
import com.dudu.voice.semantic.bean.map.MapNearbyBean;
import com.dudu.voice.semantic.bean.map.MapNearestBean;
import com.dudu.voice.semantic.bean.map.MapPlaceNaviBean;
import com.dudu.voice.semantic.bean.map.MapTrafficBean;
import com.dudu.voice.semantic.bean.map.NavigationBean;
import com.dudu.voice.semantic.constant.MapConstant;
import com.dudu.voice.semantic.constant.SemanticConstant;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by lxh on 2015/12/28.
 */
public class MapParser {

    private static JSONObject requestObj, paramObj;
    private static JSONResultParser parser;
    private static String service, poiName;
    private static String inputText;

    public static SemanticBean parseMapBean(String result) {
        parser = new JSONResultParser(result);

        try {
            requestObj = parser.getSemantics().getJSONObject("request");
            paramObj = requestObj.getJSONObject("param");
            inputText = parser.getInput();
            service = getService();
            poiName = getPoiName();
            adjustService();
        } catch (JSONException e) {
            return new SemanticBean().getDefaultBean(parser.getInput());
        }
        return getMapSemantic();
    }

    private static SemanticBean getMapSemantic() {
        MapBean mapBean;
        switch (service) {
            case SemanticConstant.SERVICE_LOCATION:
                mapBean = new MapLocationBean();
                break;
            case SemanticConstant.SERVICE_NEARBY:
                mapBean = new MapNearbyBean();
                try {
                    if (paramObj.has(MapConstant.DISTANCE_SOTR) &&
                            paramObj.getString(MapConstant.DISTANCE_SOTR).equals("nearest")) {
                        mapBean = new MapNearestBean();
                        service = SemanticConstant.SERVICE_NEAREST;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case SemanticConstant.SERVICE_MAP:
                mapBean = new MapPlaceNaviBean();
                break;
            case SemanticConstant.SERVICE_TRAFFIC:
                mapBean = new MapTrafficBean();
                break;
            case SemanticConstant.SERVICE_NEAREST:
                mapBean = new MapNearestBean();
                break;
            case SemanticConstant.SERVICE_COMMONADDRESS:
                mapBean = new MapCommonAddressBean();
                break;
            case SemanticConstant.SERVICE_NAVI:
                mapBean = new NavigationBean();
                try {
                    ((NavigationBean) mapBean).setOperation(paramObj.getString("操作"));
                    ((NavigationBean) mapBean).setObject(paramObj.getString("对象"));
                } catch (JSONException e) {

                }
                break;
            default:
                return new SemanticBean().getDefaultBean(inputText);
        }
        mapBean.setService(service);
        mapBean.setPoiName(poiName);
        mapBean.setText(inputText);
        return mapBean;
    }


    public static String getService() {
        if (paramObj.has(MapConstant.REQUEST_TYPE)) {
            service = getServiceReq();
        } else {
            service = getServiceOther();
        }
        return service;
    }

    private static String getServiceReq() {

        try {
            switch (paramObj.getString(MapConstant.REQUEST_TYPE)) {

                case MapConstant.REQUEST_LOC:
                    return SemanticConstant.SERVICE_LOCATION;
                case MapConstant.REQUEST_NEARBY:
                    return SemanticConstant.SERVICE_NEARBY;
                case MapConstant.REQUEST_TRAFFIC:
                    return SemanticConstant.SERVICE_NAVI;
                default:
                    return SemanticConstant.SERVICE_MAP;
            }
        } catch (Exception e) {
            return "";
        }
    }

    private static String getServiceOther() {
        try {
            if (paramObj.has(MapConstant.POI_NAME)
                    || paramObj.has(MapConstant.POI_TYPE)
                    || paramObj.has(MapConstant.END_NAME)
                    || paramObj.has(MapConstant.ENDPOINT_TYPE)
                    || paramObj.has(MapConstant.OBJECT)) {

                if (paramObj.has(MapConstant.DISTANCE_SOTR)
                        && paramObj.getString(MapConstant.DISTANCE_SOTR).equals("nearest")) {

                    return SemanticConstant.SERVICE_NEAREST;

                } else if (paramObj.has(MapConstant.OBJECT)
                        && paramObj.getString(MapConstant.OBJECT).equalsIgnoreCase(MapConstant.REQUEST_TRAFFIC)) {

                    return SemanticConstant.SERVICE_NAVI;
                } else {
                    return SemanticConstant.SERVICE_MAP;
                }
            } else if (inputText.contains("回家")) {
                return SemanticConstant.SERVICE_COMMONADDRESS;
            }
        } catch (Exception e) {
            return "";
        }

        return "";
    }

    private static String getPoiName() {
        String poiName = "";
        try {
            if (paramObj.has(MapConstant.POI_NAME)) {
                poiName = paramObj.getString(MapConstant.POI_NAME);
            } else if (paramObj.has(MapConstant.POI_TYPE)) {
                poiName = paramObj.getString(MapConstant.POI_TYPE);
            } else if (paramObj.has(MapConstant.END_NAME)) {
                poiName = paramObj.getString(MapConstant.END_NAME);
            } else if (paramObj.has(MapConstant.ENDPOINT_TYPE)) {
                poiName = paramObj.getString(MapConstant.ENDPOINT_TYPE);
            } else {
                if (service.equalsIgnoreCase(SemanticConstant.SERVICE_LOCATION)) {

                    poiName = Constants.CURRENT_POI;
                }
            }
            toilet(poiName);
        } catch (Exception e) {
            return "";
        }
        return poiName;
    }

    private static void adjustService() {
        if (inputText.contains("回家")) {
            poiName = "家";
        }
        if (isNearby(poiName)) {
            service = SemanticConstant.SERVICE_NEARBY;
        }
        if (inputText.contains("的位置")) {
            service = SemanticConstant.SERVICE_LOCATION;
        }
        if (poiName.equals("公司") || poiName.equals("家") || poiName.equals("老家")) {
            service = SemanticConstant.SERVICE_COMMONADDRESS;
        }
    }

    private static boolean isNearby(String poiName) {
        return (poiName.equals(MapConstant.HOTLE) || poiName.equals(MapConstant.HOSPITAL)
                || poiName.equals(MapConstant.WASHROOM) || poiName.equals(MapConstant.TOILET)
                || poiName.equals(MapConstant.BANK) || poiName.equals(MapConstant.SUBWAY));
    }

    private static String toilet(String poiName) {

        if (poiName.equals("上厕所")) {
            poiName = "厕所";
        } else if (poiName.equals("上洗手间")) {
            poiName = "洗手间";
        }
        return poiName;
    }

}
