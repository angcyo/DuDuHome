package com.dudu.voice.semantic.engine;

import com.dudu.android.launcher.bean.Rsphead;
import com.dudu.android.launcher.utils.JsonUtils;
import com.dudu.voice.semantic.SemanticConstants;
import com.dudu.voice.semantic.chain.SemanticChain;
import java.util.HashMap;

/**
 * 语义处理引擎
 */
public class SemanticProcessor {

    private static SemanticProcessor mInstance = null;

    private ChainGenerator mChainGenerator = null;

    public static SemanticProcessor getProcessor() {
        if (mInstance == null) {
            mInstance = new SemanticProcessor();
        }

        return mInstance;
    }

    private SemanticChain mCurChain;

    private HashMap<String, SemanticChain> mChainMap = new HashMap<>();

    private SemanticProcessor() {
        mChainGenerator = new ChainGenerator();
        mChainMap.put(SemanticConstants.SERVICE_VOICE, mChainGenerator.generateVoiceChain());

    }

    public void processSemantic(final String text) {
        Rsphead head = JsonUtils.getRsphead(text);
        if (head.getRc() == 0 && isChainExists(head)) {
            String service = head.getService();
            if (mCurChain.matchSemantic(service)) {
                doSemantic(text);
            } else {
                mCurChain = mChainMap.get(service);
                if (mCurChain != null) {
                    doSemantic(text);
                }
            }
        } else {

        }
    }

    private boolean isChainExists(Rsphead head) {
        if (mCurChain == null) {
            mCurChain = mChainMap.get(head.getService());
        }
        return mCurChain != null;
    }

    private void doSemantic(String text) {
        // 执行当前语义
        mCurChain.doSemantic(text);

        // 如果有孩子，则设置孩子为当前语义
        SemanticChain child = mCurChain.getNextChild();
        if (child != null) {
            mCurChain = child;
        } else {
            mCurChain = null;
        }
    }

}