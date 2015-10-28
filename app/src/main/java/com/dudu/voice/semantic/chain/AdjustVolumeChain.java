package com.dudu.voice.semantic.chain;

import com.dudu.voice.semantic.SemanticConstants;

/**
 * Created by 赵圣琪 on 2015/10/28.
 */
public class AdjustVolumeChain extends SemanticChain {

    @Override
    public boolean matchSemantic(String service) {
        return SemanticConstants.SERVICE_VOICE.equalsIgnoreCase(service);
    }

    @Override
    public boolean doSemantic(String json) {

        return false;
    }

    @Override
    public SemanticChain getNextChild() {
        return null;
    }

}
