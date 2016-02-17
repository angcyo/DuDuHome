package com.dudu.voice.semantic.scene;

import com.dudu.voice.semantic.chain.DefaultChain;
import com.dudu.voice.semantic.chain.FaultDefaultChain;
import com.dudu.voice.semantic.constant.SemanticConstant;

/**
 * Created by lxh on 2016/2/16.
 */
public class FaultScene extends SemanticScene{
    @Override
    public void initChains() {
        mChainMap.put(SemanticConstant.SERVICE_PHONE, mChainFactory.generatePhoneChain());
        mChainMap.put(SemanticConstant.SERVICE_VOLUME, mChainFactory.generateVolumeChain());
        mChainMap.put(SemanticConstant.SERVICE_CMD, mChainFactory.generateCmdChain());
        mChainMap.put(SemanticConstant.SERVICE_MAP, mChainFactory.getMapPlaceChain());
        mChainMap.put(SemanticConstant.SERVICE_NEARBY, mChainFactory.getMapNearbyChain());
        mChainMap.put(SemanticConstant.SERVICE_NAVI, mChainFactory.getNavigationChain());
        mChainMap.put(SemanticConstant.SERVICE_DATE_TIME, mChainFactory.getDatetimeChain());
        mChainMap.put(SemanticConstant.SERVICE_DIM, mChainFactory.getDimScreenChain());
        mChainMap.put(SemanticConstant.SERVICE_VIDEO, mChainFactory.getVideoPlayChain());
        mChainMap.put(SemanticConstant.SERVICE_COMMONADDRESS,mChainFactory.getCommonAddressChain());
        mChainMap.put(SemanticConstant.SERVICE_LOCATION,mChainFactory.getMapLocationChain());
        mChainMap.put(SemanticConstant.SERVICE_NEAREST,mChainFactory.getMapNearestChain());
    }

    @Override
    public DefaultChain getDefaultChain() {
        return new FaultDefaultChain();
    }
}
