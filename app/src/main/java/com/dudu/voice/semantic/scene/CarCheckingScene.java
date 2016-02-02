package com.dudu.voice.semantic.scene;

import com.dudu.voice.semantic.chain.DefaultCarCheckingChain;
import com.dudu.voice.semantic.chain.DefaultChain;

/**
 * Created by 赵圣琪 on 2016/2/2.
 */
public class CarCheckingScene extends SemanticScene {

    @Override
    public void initChains() {

    }

    @Override
    public DefaultChain getDefaultChain() {
        return new DefaultCarCheckingChain();
    }
}
