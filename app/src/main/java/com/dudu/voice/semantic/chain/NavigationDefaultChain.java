package com.dudu.voice.semantic.chain;


import com.dudu.android.launcher.utils.JsonUtils;
import com.dudu.map.MapManager;

/**
 * Created by pc on 2015/11/10.
 */
public class NavigationDefaultChain extends DefaultChain {

    @Override
    public boolean matchSemantic(String service) {
        return true;
    }

    @Override
    public boolean doSemantic(String json) {
        String text = JsonUtils.getRsphead(json).getText();
        if(text == null)
            text = json;
        MapManager.getInstance().mapControl(null, text, MapManager.SEARCH_POI);
        return true;
    }
}
