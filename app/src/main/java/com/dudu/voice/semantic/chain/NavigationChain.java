package com.dudu.voice.semantic.chain;

/**
 * Created by Administrator on 2015/10/28.
 */
public class NavigationChain extends SemanticChain {

    @Override
    public boolean matchSemantic(String service) {
        return false;
    }

    @Override
    public boolean doSemantic(String json) {
        return false;
    }
}
