package com.dudu.voice.semantic.engine;

import com.dudu.android.launcher.bean.Rsphead;
import com.dudu.android.launcher.utils.JsonUtils;
import com.dudu.voice.semantic.SemanticConstants;
import com.dudu.voice.semantic.SemanticType;
import com.dudu.voice.semantic.VoiceManager;
import com.dudu.voice.semantic.chain.CarCheckingDefault;
import com.dudu.voice.semantic.chain.DefaultChain;
import com.dudu.voice.semantic.chain.MapChoiseDefalutChain;
import com.dudu.voice.semantic.chain.NavigationDefaultChain;
import com.dudu.voice.semantic.chain.SemanticChain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Stack;

/**
 * 语义处理引擎
 */
public class SemanticProcessor {

    private Logger log;

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

        log = LoggerFactory.getLogger("voice.semantic");

        mChainGenerator = new ChainGenerator();

        mDefaultChain = new DefaultChain();

        addNormalChains();
    }

    public void processSemantic(final String text) {
        VoiceManager.getInstance().clearMisUnderstandCount();

        log.debug("语音识别返回: ", text);
        Rsphead head = JsonUtils.getRsphead(text);
        if (head.getRc() == 0 && isChainExists(head)) {

            String service = head.getService();

            if (mCurChain.matchSemantic(service)) {
                log.debug("用户输入与当前语义链匹配，开始处理语义...");

                doSemantic(text);
            } else {
                log.debug("将当前操作链入栈...");

                pushSemanticStack(mCurChain);

                mCurChain = mChainMap.get(service);
                if (mCurChain != null) {
                    log.debug("匹配到其它的语义链，开始处理语义...");

                    doSemantic(text);
                } else {
                    log.debug("没有匹配到其它语义链，使用默认语义链处理...");

                    mDefaultChain.doSemantic(text);

                    log.debug("栈顶语义链出栈...");

                    mCurChain = popSemanticStack();

                }
            }

            return;
        }

        log.debug("没有匹配到相关语义处理链，使用默认语义链处理...");
        mDefaultChain.doSemantic(text);
    }

    private boolean isChainExists(Rsphead head) {
        if (mCurChain == null) {
            mCurChain = mChainMap.get(head.getService());
        }

        return mCurChain != null;
    }

    private void doSemantic(String text) {

        if (!mCurChain.doSemantic(text)) {
            log.debug("当前链处理语义失败, 使用默认语义链处理...");

            mDefaultChain.doSemantic(text);
        }

        // 如果有孩子，则设置孩子为当前语义
        SemanticChain child = mCurChain.getNextChild();
        if (child != null) {
            log.debug("当前语义链孩子不为空，并设置为当前链...");
            mCurChain = child;
        } else {
            log.debug("当前语义链孩子为空，栈顶语义链设置为当前链...");
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

    public void switchSemanticType(SemanticType type) {
        mChainMap.clear();

        switch (type) {
            case NORMAL:
                log.debug("设置当前语义类型为正常...");

                VoiceManager.getInstance().setShowMessageWindow(true);

                addNormalChains();

                mDefaultChain = new DefaultChain();
                break;
            case CAR_CHECKING:
                log.debug("设置当前语义类型为车辆自检...");

                VoiceManager.getInstance().setShowMessageWindow(false);

                addCarCheckingChains();

                mDefaultChain = new CarCheckingDefault();
                break;
            case NAVIGATION:
                log.debug("设置当前语义类型为导航...");

                VoiceManager.getInstance().setShowMessageWindow(true);

                addNavigationChains();

                mDefaultChain = new NavigationDefaultChain();
                break;
            case MAP_CHOISE:
                log.debug("设置当前语义类型为地址选择...");

                addMapChoiseChains();

                mDefaultChain = new MapChoiseDefalutChain();
                break;
        }
    }

    private void addNormalChains() {
        mChainMap.put(SemanticConstants.SERVICE_VOICE, mChainGenerator.generateVoiceChain());
        mChainMap.put(SemanticConstants.SERVICE_CMD, mChainGenerator.generateCmdChain());
        mChainMap.put(SemanticConstants.SERVICE_MAP, mChainGenerator.getMapSearchChain());
        mChainMap.put(SemanticConstants.SERVICE_NEARBY, mChainGenerator.getMapSearchChain());
        mChainMap.put(SemanticConstants.SERVICE_RESTAURANT, mChainGenerator.getMapSearchChain());
        mChainMap.put(SemanticConstants.SERVICE_HOTEL, mChainGenerator.getMapSearchChain());
        mChainMap.put(SemanticConstants.SERVICE_NAVI, mChainGenerator.getNavigationChain());
        mChainMap.put(SemanticConstants.SERVICE_WEATHER, mChainGenerator.getWeatherChain());
        mChainMap.put(SemanticConstants.SERVICE_CHOISE, mChainGenerator.getChoiseChain());
        mChainMap.put(SemanticConstants.SERVICE_CHAT, mChainGenerator.getChatChain());
        mChainMap.put(SemanticConstants.SERVICE_OPENQA, mChainGenerator.getOpenQaChain());
        mChainMap.put(SemanticConstants.SERVICE_CHOOSEPAGE, mChainGenerator.getChoosePageChain());
        mChainMap.put(SemanticConstants.SERVICE_POI, mChainGenerator.getPoiChain());
        mChainMap.put(SemanticConstants.SERVICE_COMMONADDRESS, mChainGenerator.getCommonAddressChain());
        mChainMap.put(SemanticConstants.SERVICE_CAR_CHECKING, mChainGenerator.getCarCheckingChain());
        mChainMap.put(SemanticConstants.SERVICE_WIFI, mChainGenerator.getWIFIChain());
        mChainMap.put(SemanticConstants.SERVICE_BAIKE, mChainGenerator.getBaikeChain());
        mChainMap.put(SemanticConstants.SERVICE_DEATETIME, mChainGenerator.getDatetimeChain());
        mChainMap.put(SemanticConstants.SERVICE_DUDU, mChainGenerator.getDuDuChain());
    }

    private void addCarCheckingChains() {
        mChainMap.put(SemanticConstants.SERVICE_VOICE, mChainGenerator.generateVoiceChain());
        mChainMap.put(SemanticConstants.SERVICE_CMD, mChainGenerator.generateCmdChain());
        mChainMap.put(SemanticConstants.SERVICE_WHETHER, mChainGenerator.getCarCheckingWhetherChain());
        mChainMap.put(SemanticConstants.SERVICE_CAR_CHECKING, mChainGenerator.getCarCheckingChain());
        mChainMap.put(SemanticConstants.SERVICE_CHOISE, mChainGenerator.getCarCheckingChoiseChain());
    }

    private void addNavigationChains() {
        mChainMap.put(SemanticConstants.SERVICE_CMD, mChainGenerator.generateCmdChain());
        mChainMap.put(SemanticConstants.SERVICE_VOICE, mChainGenerator.generateVoiceChain());
        mChainMap.put(SemanticConstants.SERVICE_MAP, mChainGenerator.getMapSearchChain());
        mChainMap.put(SemanticConstants.SERVICE_NEARBY, mChainGenerator.getMapSearchChain());
        mChainMap.put(SemanticConstants.SERVICE_RESTAURANT, mChainGenerator.getMapSearchChain());
        mChainMap.put(SemanticConstants.SERVICE_HOTEL, mChainGenerator.getMapSearchChain());
        mChainMap.put(SemanticConstants.SERVICE_POI, mChainGenerator.getPoiChain());
    }

    private void addMapChoiseChains() {
        mChainMap.put(SemanticConstants.SERVICE_CMD, mChainGenerator.generateCmdChain());
        mChainMap.put(SemanticConstants.SERVICE_VOICE, mChainGenerator.generateVoiceChain());
        mChainMap.put(SemanticConstants.SERVICE_MAP, mChainGenerator.getMapSearchChain());
        mChainMap.put(SemanticConstants.SERVICE_NEARBY, mChainGenerator.getMapSearchChain());
        mChainMap.put(SemanticConstants.SERVICE_RESTAURANT, mChainGenerator.getMapSearchChain());
        mChainMap.put(SemanticConstants.SERVICE_HOTEL, mChainGenerator.getMapSearchChain());
        mChainMap.put(SemanticConstants.SERVICE_POI, mChainGenerator.getPoiChain());
        mChainMap.put(SemanticConstants.SERVICE_CHOISE, mChainGenerator.getChoiseChain());
        mChainMap.put(SemanticConstants.SERVICE_CHOOSEPAGE, mChainGenerator.getChoosePageChain());
    }

}
