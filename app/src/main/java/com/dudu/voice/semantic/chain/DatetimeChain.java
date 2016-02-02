package com.dudu.voice.semantic.chain;

import com.dudu.voice.semantic.bean.SemanticBean;
import com.dudu.voice.semantic.constant.SemanticConstant;
import com.dudu.voice.semantic.constant.TTSType;

/**
 * Created by Administrator on 2015/11/16.
 */
public class DatetimeChain extends SemanticChain {

    @Override
    public boolean matchSemantic(String service) {
        return SemanticConstant.SERVICE_DATE_TIME.equals(service);
    }

    @Override
    public boolean doSemantic(SemanticBean semantic) {
        return false;
    }

}
