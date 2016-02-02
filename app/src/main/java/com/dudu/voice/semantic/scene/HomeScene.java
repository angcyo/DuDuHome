package com.dudu.voice.semantic.scene;

import com.dudu.voice.semantic.constant.SemanticConstant;
import com.dudu.voice.semantic.chain.DefaultChain;
import com.dudu.voice.semantic.chain.SemanticChain;

/**
 * Created by 赵圣琪 on 2015/11/25.
 */
public class HomeScene extends SemanticScene {

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
    public SemanticChain getChain(String service) {
        return mChainMap.get(service);
    }

    @Override
    public DefaultChain getDefaultChain() {
        return new DefaultChain();
    }

}
