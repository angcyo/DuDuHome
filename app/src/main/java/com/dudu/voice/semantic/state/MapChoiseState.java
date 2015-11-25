package com.dudu.voice.semantic.state;

import com.dudu.voice.semantic.SemanticConstants;
import com.dudu.voice.semantic.chain.DefaultChain;
import com.dudu.voice.semantic.chain.MapChoiseDefalutChain;
import com.dudu.voice.semantic.chain.SemanticChain;

/**
 * Created by 赵圣琪 on 2015/11/25.
 */
public class MapChoiseState extends SemanticState {

    @Override
    public void initChains() {
        mChainMap.put(SemanticConstants.SERVICE_CMD, mChainFactory.generateCmdChain());
        mChainMap.put(SemanticConstants.SERVICE_VOICE, mChainFactory.generateVoiceChain());
        mChainMap.put(SemanticConstants.SERVICE_MAP, mChainFactory.getMapSearchChain());
        mChainMap.put(SemanticConstants.SERVICE_NEARBY, mChainFactory.getMapSearchChain());
        mChainMap.put(SemanticConstants.SERVICE_RESTAURANT, mChainFactory.getMapSearchChain());
        mChainMap.put(SemanticConstants.SERVICE_HOTEL, mChainFactory.getMapSearchChain());
        mChainMap.put(SemanticConstants.SERVICE_POI, mChainFactory.getPoiChain());
        mChainMap.put(SemanticConstants.SERVICE_CHOISE, mChainFactory.getChoiseChain());
        mChainMap.put(SemanticConstants.SERVICE_CHOOSEPAGE, mChainFactory.getChoosePageChain());
    }

    @Override
    public DefaultChain getDefaultChain() {
        return new MapChoiseDefalutChain();
    }

}
