package com.dudu.voice.semantic.chain.map;

import com.dudu.map.NavigationProxy;
import com.dudu.navi.vauleObject.SearchType;
import com.dudu.voice.semantic.bean.SemanticBean;
import com.dudu.voice.semantic.bean.map.MapNearbyBean;
import com.dudu.voice.semantic.chain.SemanticChain;
import com.dudu.voice.semantic.constant.SemanticConstant;

/**
 * Created by lxh on 2015/12/28.
 */
public class MapNearbyChain extends SemanticChain {
    @Override
    public boolean matchSemantic(String service) {
        return service.equalsIgnoreCase(SemanticConstant.SERVICE_NEARBY);
    }

    @Override
    public boolean doSemantic(SemanticBean semantic) {
        if(semantic!=null){
            NavigationProxy.getInstance().searchControl(null, null,
                    ((MapNearbyBean) semantic).getPoiName(), SearchType.SEARCH_NEARBY);
            return true;
        }
        return false;
    }
}
