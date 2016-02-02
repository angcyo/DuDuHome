package com.dudu.voice.semantic.scene;

import com.dudu.voice.semantic.chain.map.WhetherDefaultChain;
import com.dudu.voice.semantic.constant.SemanticConstant;
import com.dudu.voice.semantic.chain.DefaultChain;

/**
 * Created by lxh on 2015/12/1.
 */
public class WhetherScene extends SemanticScene {


    @Override
    public void initChains() {
        mChainMap.put(SemanticConstant.SERVICE_CMD, mChainFactory.generateCmdChain());
    }

    @Override
    public DefaultChain getDefaultChain() {
        return new WhetherDefaultChain();
    }
}
