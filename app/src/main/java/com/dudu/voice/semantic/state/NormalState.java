package com.dudu.voice.semantic.state;

import com.dudu.voice.semantic.SemanticConstants;
import com.dudu.voice.semantic.chain.DefaultChain;
import com.dudu.voice.semantic.chain.SemanticChain;

/**
 * Created by 赵圣琪 on 2015/11/25.
 */
public class NormalState extends SemanticState {

    @Override
    public void initChains() {
        mChainMap.put(SemanticConstants.SERVICE_VOICE, mChainFactory.generateVoiceChain());
        mChainMap.put(SemanticConstants.SERVICE_CMD, mChainFactory.generateCmdChain());
        mChainMap.put(SemanticConstants.SERVICE_MAP, mChainFactory.getMapSearchChain());
        mChainMap.put(SemanticConstants.SERVICE_NEARBY, mChainFactory.getMapSearchChain());
        mChainMap.put(SemanticConstants.SERVICE_RESTAURANT, mChainFactory.getMapSearchChain());
        mChainMap.put(SemanticConstants.SERVICE_HOTEL, mChainFactory.getMapSearchChain());
        mChainMap.put(SemanticConstants.SERVICE_NAVI, mChainFactory.getNavigationChain());
        mChainMap.put(SemanticConstants.SERVICE_WEATHER, mChainFactory.getWeatherChain());
        mChainMap.put(SemanticConstants.SERVICE_CHOISE, mChainFactory.getChoiseChain());
        mChainMap.put(SemanticConstants.SERVICE_CHAT, mChainFactory.getChatChain());
        mChainMap.put(SemanticConstants.SERVICE_OPENQA, mChainFactory.getOpenQaChain());
        mChainMap.put(SemanticConstants.SERVICE_CHOOSEPAGE, mChainFactory.getChoosePageChain());
        mChainMap.put(SemanticConstants.SERVICE_POI, mChainFactory.getPoiChain());
        mChainMap.put(SemanticConstants.SERVICE_COMMONADDRESS, mChainFactory.getCommonAddressChain());
        mChainMap.put(SemanticConstants.SERVICE_CAR_CHECKING, mChainFactory.getCarCheckingChain());
        mChainMap.put(SemanticConstants.SERVICE_WIFI, mChainFactory.getWIFIChain());
        mChainMap.put(SemanticConstants.SERVICE_BAIKE, mChainFactory.getBaikeChain());
        mChainMap.put(SemanticConstants.SERVICE_DEATETIME, mChainFactory.getDatetimeChain());
        mChainMap.put(SemanticConstants.SERVICE_DUDU, mChainFactory.getDuDuChain());
        mChainMap.put(SemanticConstants.SERVICE_DIM, mChainFactory.getDimScreenChain());
        mChainMap.put(SemanticConstants.SERVICE_VIDEO, mChainFactory.getVideoPlayChain());
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
