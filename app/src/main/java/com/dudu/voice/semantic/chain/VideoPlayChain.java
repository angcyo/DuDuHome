package com.dudu.voice.semantic.chain;

import com.dudu.voice.semantic.bean.SemanticBean;
import com.dudu.voice.semantic.constant.SemanticConstant;

/**
 * Created by 赵圣琪 on 2015/11/25.
 */
public class VideoPlayChain extends SemanticChain {

    @Override
    public boolean matchSemantic(String service) {
        return SemanticConstant.SERVICE_VIDEO.equals(service);
    }

    @Override
    public boolean doSemantic(SemanticBean bean) {

        return false;
    }

}
