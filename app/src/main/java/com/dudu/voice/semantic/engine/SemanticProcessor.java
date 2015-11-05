package com.dudu.voice.semantic.engine;

import com.dudu.android.launcher.bean.Rsphead;
import com.dudu.android.launcher.utils.JsonUtils;
import com.dudu.voice.semantic.SemanticConstants;
import com.dudu.voice.semantic.VoiceManager;
import com.dudu.voice.semantic.chain.DefaultChain;
import com.dudu.voice.semantic.chain.SemanticChain;

import java.util.HashMap;
import java.util.Stack;

/**
 * 语义处理引擎
 */
public class SemanticProcessor {

    private static SemanticProcessor mInstance = null;

    private ChainGenerator mChainGenerator = null;

    private Stack<SemanticChain> mSemanticStack = new Stack<>();

    /**
     * 如果没有匹配到链对象，则使用默认链对象处理。
     */
    private DefaultChain mDefaultChain;

    public static SemanticProcessor getProcessor() {
        if (mInstance == null) {
            mInstance = new SemanticProcessor();
        }

        return mInstance;
    }

    private SemanticChain mCurChain;

    private HashMap<String, SemanticChain> mChainMap = new HashMap<>();

    /**
     * 加入业务处理链条
     */
    private SemanticProcessor() {
        mChainGenerator = new ChainGenerator();

        mChainMap.put(SemanticConstants.SERVICE_VOICE, mChainGenerator.generateVoiceChain());

        mDefaultChain = new DefaultChain();
        mChainMap.put(SemanticConstants.SERVICE_CMD, mChainGenerator.generateCmdChain());
        mChainMap.put(SemanticConstants.SERVICE_MAP, mChainGenerator.getMapSearchChain());
        mChainMap.put(SemanticConstants.SERVICE_NEARBY, mChainGenerator.getMapSearchChain());
        mChainMap.put(SemanticConstants.SERVICE_RESTAURANT, mChainGenerator.getMapSearchChain());
        mChainMap.put(SemanticConstants.SERVICE_HOTEL, mChainGenerator.getMapSearchChain());
        mChainMap.put(SemanticConstants.SERVICE_NAVI, mChainGenerator.getNavigationChain());

        mChainMap.put(SemanticConstants.SERVICE_WEATHER, mChainGenerator.getWeatherChain());
        mChainMap.put(SemanticConstants.SERVICE_CHOISE, mChainGenerator.getChoiseChain());
        mChainMap.put(SemanticConstants.SERVICE_BAIKE, mChainGenerator.getBaikeChain());

        mChainMap.put(SemanticConstants.SERVICE_CHAT, mChainGenerator.getChatChain());
        mChainMap.put(SemanticConstants.SERVICE_OPENQA, mChainGenerator.getOpenQaChain());
        mChainMap.put(SemanticConstants.SERVICE_CHOOSEPAGE, mChainGenerator.getChoosePageChain());
        mChainMap.put(SemanticConstants.SERVICE_POI, mChainGenerator.getPoiChain());
        mChainMap.put(SemanticConstants.SERVICE_COMMONADDRESS,mChainGenerator.getCommonAddressChain());
        mChainMap.put(SemanticConstants.SERVICE_WIFI,mChainGenerator.getWIFIChain());
    }

    public void processSemantic(final String text) {
        Rsphead head = JsonUtils.getRsphead(text);
        if (head.getRc() == 0 && isChainExists(head)) {
            String service = head.getService();
            if (mCurChain.matchSemantic(service)) {
                doSemantic(text);
            } else {
                // 将当前的操作链入栈
                mSemanticStack.push(mCurChain);

                mCurChain = mChainMap.get(service);
                if (mCurChain != null) {
                    doSemantic(text);
                } else {
                    mDefaultChain.doSemantic(text);
                    mCurChain = mSemanticStack.pop();
                }
            }
            return;
        }

        VoiceManager.getInstance().clearMisUnderstandCount();

        mDefaultChain.doSemantic(text);
    }


    private boolean isChainExists(Rsphead head) {
        if (mCurChain == null) {
            mCurChain = mChainMap.get(head.getService());
        }

        return mCurChain != null;
    }

    private void doSemantic(String text) {

        // 执行当前语义
        if (!mCurChain.doSemantic(text)) {
            mDefaultChain.doSemantic(text);
        }

        // 如果有孩子，则设置孩子为当前语义
        SemanticChain child = mCurChain.getNextChild();
        if (child != null) {
            mCurChain = child;
        } else {
            mCurChain = !mSemanticStack.isEmpty() ? mSemanticStack.pop() : null;
        }
    }

    public void clearSemanticStack() {
        mSemanticStack.clear();
    }

    public void pushSemanticStack(SemanticChain chain) {
        mSemanticStack.push(chain);
    }

    public SemanticChain popSemanticStack() {
        if (!mSemanticStack.isEmpty()) {
            return mSemanticStack.pop();
        }

        return null;
    }

}
