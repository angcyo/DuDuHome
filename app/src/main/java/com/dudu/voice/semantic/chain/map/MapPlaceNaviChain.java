package com.dudu.voice.semantic.chain.map;

import com.dudu.map.NavigationProxy;
import com.dudu.navi.vauleObject.SearchType;
import com.dudu.voice.semantic.bean.SemanticBean;
import com.dudu.voice.semantic.bean.map.MapPlaceNaviBean;
import com.dudu.voice.semantic.chain.SemanticChain;
import com.dudu.voice.semantic.constant.SemanticConstant;

/**
 * Created by lxh on 2015/12/28.
 */
public class MapPlaceNaviChain extends SemanticChain {
    @Override
    public boolean matchSemantic(String service) {
        return service.equalsIgnoreCase(SemanticConstant.SERVICE_MAP);
    }

    @Override
    public boolean doSemantic(SemanticBean semantic) {
        if (semantic != null) {

            NavigationProxy.getInstance().searchControl(null, null,
                    ((MapPlaceNaviBean) semantic).getPoiName(), SearchType.SEARCH_PLACE);
            return true;
        }
        return false;
    }
}
