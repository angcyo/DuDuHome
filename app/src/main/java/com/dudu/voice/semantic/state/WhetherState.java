package com.dudu.voice.semantic.state;

import com.dudu.voice.semantic.SemanticConstants;
import com.dudu.voice.semantic.chain.DefaultChain;

/**
 * Created by lxh on 2015/12/1.
 */
public class WhetherState  extends SemanticState{

    @Override
    public void initChains() {
        mChainMap.put(SemanticConstants.SERVICE_VOICE, mChainFactory.generateVoiceChain());
        mChainMap.put(SemanticConstants.SERVICE_CMD, mChainFactory.generateCmdChain());
        mChainMap.put(SemanticConstants.SERVICE_WHETHER, mChainFactory.getCarCheckingWhetherChain());
    }

    @Override
    public DefaultChain getDefaultChain() {
        return new DefaultChain();
    }


}
