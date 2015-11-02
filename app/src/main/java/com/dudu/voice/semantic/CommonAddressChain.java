package com.dudu.voice.semantic;

import com.dudu.voice.semantic.chain.SemanticChain;

/**
 * Created by pc on 2015/11/2.
 */
public class CommonAddressChain extends SemanticChain{

    @Override
    public boolean matchSemantic(String service) {
        return false;
    }

    @Override
    public boolean doSemantic(String json) {
        return false;
    }
}
