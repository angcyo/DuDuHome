package com.dudu.voice.semantic.state;

import com.dudu.voice.semantic.chain.DefaultChain;
import com.dudu.voice.semantic.chain.SemanticChain;
import com.dudu.voice.semantic.engine.ChainSimpleFactory;

import java.util.HashMap;

/**
 * Created by 赵圣琪 on 2015/11/25.
 */
public abstract class SemanticState {

    protected HashMap<String, SemanticChain> mChainMap = new HashMap<>();

    protected ChainSimpleFactory mChainFactory;

    public SemanticState() {
        mChainFactory = ChainSimpleFactory.getInstance();

        initChains();
    }

    public abstract void initChains();

    public SemanticChain getChain(String service) {
        return mChainMap.get(service);
    }

    public abstract DefaultChain getDefaultChain();

}
