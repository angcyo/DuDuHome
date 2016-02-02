package com.dudu.voice.semantic.parser;

import com.aispeech.common.JSONResultParser;
import com.dudu.voice.semantic.bean.SemanticBean;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Administrator on 2015/12/28.
 */
public abstract class SemanticParser {

    protected JSONResultParser mResultParser;

    protected JSONObject mSemantics;

    protected Logger logger;

    public SemanticParser() {
        logger = LoggerFactory.getLogger("voice.parser");
    }

    public abstract SemanticBean getSemanticBean(String result);

}
