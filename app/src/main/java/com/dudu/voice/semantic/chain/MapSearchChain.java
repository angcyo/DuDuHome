package com.dudu.voice.semantic.chain;

import android.text.TextUtils;

import com.dudu.android.launcher.LauncherApplication;
import com.dudu.android.launcher.utils.JsonUtils;
import com.dudu.map.NavigationClerk;
import com.dudu.navi.vauleObject.SearchType;
import com.dudu.voice.semantic.SemanticConstants;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by lxh on 2015/10/29.
 */
public class MapSearchChain extends SemanticChain {

    private String TAG = "MapSearchChain";


    private String mapOperation = "";

    private static final String ROUTE = "ROUTE";

    private static final String POSITION = "POSITION";

    private static final String NEAREST = "最近";

    private SearchType type;

    NavigationClerk navigationClerk;
    public MapSearchChain() {
        navigationClerk = NavigationClerk.getInstance();
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
                            type = SearchType.SEARCH_PLACE;
                        } else if (mapOperation.equals(POSITION)) {
                            type = SearchType.SEARCH_PLACE_LOCATION;
                        }
                    }
                    break;
                case SemanticConstants.SERVICE_RESTAURANT:
                case SemanticConstants.SERVICE_HOTEL:
                    type = SearchType.SEARCH_NEARBY;
                    break;
                case SemanticConstants.SERVICE_NEARBY:
                    String optionType = JsonUtils.getNearbyOptionType(semantic);
                    if (optionType.equals(NEAREST)) {
                        type = SearchType.SEARCH_NEAREST;
                    } else {
                        type = SearchType.SEARCH_NEARBY;
                    }
                    break;
            }
            navigationClerk.searchControl(semantic,service,null,type);
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
