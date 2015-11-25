package com.dudu.voice.semantic.state;

import com.dudu.voice.semantic.SemanticConstants;
import com.dudu.voice.semantic.chain.CarCheckingDefault;
import com.dudu.voice.semantic.chain.DefaultChain;

/**
 * Created by 赵圣琪 on 2015/11/25.
 */
public class CarCheckingState extends SemanticState {


    @Override
    public void initChains() {
        mChainMap.put(SemanticConstants.SERVICE_VOICE, mChainFactory.generateVoiceChain());
        mChainMap.put(SemanticConstants.SERVICE_CMD, mChainFactory.generateCmdChain());
        mChainMap.put(SemanticConstants.SERVICE_WHETHER, mChainFactory.getCarCheckingWhetherChain());
        mChainMap.put(SemanticConstants.SERVICE_CAR_CHECKING, mChainFactory.getCarCheckingChain());
        mChainMap.put(SemanticConstants.SERVICE_CHOISE, mChainFactory.getCarCheckingChoiseChain());
    }

    @Override
    public DefaultChain getDefaultChain() {
        return new CarCheckingDefault();
    }
}
