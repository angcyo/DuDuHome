package com.dudu.voice.semantic.scene;

import com.dudu.voice.semantic.constant.SemanticConstant;
import com.dudu.voice.semantic.chain.DefaultChain;
import com.dudu.voice.semantic.chain.NavigationDefaultChain;

/**
 * Created by Administrator on 2015/11/25.
 */
public class NavigationScene extends SemanticScene {

    @Override
    public void initChains() {
        mChainMap.put(SemanticConstant.SERVICE_CMD, mChainFactory.generateCmdChain());
        mChainMap.put(SemanticConstant.SERVICE_MAP, mChainFactory.getMapPlaceChain());
        mChainMap.put(SemanticConstant.SERVICE_NEARBY, mChainFactory.getMapNearbyChain());
        mChainMap.put(SemanticConstant.SERVICE_VIDEO, mChainFactory.getVideoPlayChain());
        mChainMap.put(SemanticConstant.SERVICE_COMMONADDRESS,mChainFactory.getCommonAddressChain());
        mChainMap.put(SemanticConstant.SERVICE_COMMONADDRESS,mChainFactory.getCommonAddressChain());
        mChainMap.put(SemanticConstant.SERVICE_LOCATION,mChainFactory.getMapLocationChain());
        mChainMap.put(SemanticConstant.SERVICE_NEAREST,mChainFactory.getMapNearestChain());
    }

    @Override
    public DefaultChain getDefaultChain() {
        return new NavigationDefaultChain();
    }

}
