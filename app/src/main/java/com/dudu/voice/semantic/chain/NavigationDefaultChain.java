package com.dudu.voice.semantic.chain;


import com.dudu.map.NavigationProxy;
import com.dudu.navi.vauleObject.SearchType;
import com.dudu.voice.semantic.bean.SemanticBean;

/**
 * Created by lxh on 2015/11/10.
 */
public class NavigationDefaultChain extends DefaultChain {

    @Override
    public boolean matchSemantic(String service) {
        return true;
    }

    @Override
    public boolean doSemantic(SemanticBean semantic) {
        NavigationProxy.getInstance().searchControl(null, null,
                semantic.getText(), SearchType.SEARCH_PLACE);
        return true;
    }

}
