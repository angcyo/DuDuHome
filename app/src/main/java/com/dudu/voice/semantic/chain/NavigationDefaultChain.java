package com.dudu.voice.semantic.chain;


import com.dudu.android.launcher.utils.JsonUtils;
import com.dudu.map.NavigationClerk;
import com.dudu.navi.vauleObject.SearchType;

/**
 * Created by lxh on 2015/11/10.
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

        NavigationClerk.getInstance().searchControl(null,null,text, SearchType.SEARCH_PLACE);
        return true;
    }
}
