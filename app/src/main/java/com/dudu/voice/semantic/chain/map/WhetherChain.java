package com.dudu.voice.semantic.chain.map;

import com.dudu.voice.semantic.bean.SemanticBean;
import com.dudu.voice.semantic.chain.SemanticChain;
import com.dudu.voice.semantic.constant.SemanticConstant;


/**
 * Created by pc on 2015/11/3.
 */
public class WhetherChain extends SemanticChain {

    @Override
    public boolean matchSemantic(String service) {
        return service.equalsIgnoreCase(SemanticConstant.SERVICE_WHETHER);
    }

    @Override
    public boolean doSemantic(SemanticBean semantic) {
        return false;
    }

}
