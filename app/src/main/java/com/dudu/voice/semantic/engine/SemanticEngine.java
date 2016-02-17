package com.dudu.voice.semantic.engine;

import android.text.TextUtils;

import com.dudu.voice.FloatWindowUtils;
import com.dudu.voice.VoiceManagerProxy;
import com.dudu.voice.semantic.bean.SemanticBean;
import com.dudu.voice.semantic.chain.DefaultChain;
import com.dudu.voice.semantic.chain.SemanticChain;
import com.dudu.voice.semantic.constant.SceneType;
import com.dudu.voice.semantic.scene.FaultScene;
import com.dudu.voice.semantic.scene.HomeScene;
import com.dudu.voice.semantic.scene.MapChoiseScene;
import com.dudu.voice.semantic.scene.NavigationScene;
import com.dudu.voice.semantic.scene.SemanticScene;
import com.dudu.voice.semantic.scene.WhetherScene;
import com.dudu.voice.window.MessageType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Stack;

/**
 * 语义处理引擎
 */
public class SemanticEngine {

    private Logger log;

    private static SemanticEngine mInstance = null;

    private VoiceManagerProxy mVoiceManager;

    private Stack<SemanticChain> mSemanticStack = new Stack<>();

    private SemanticScene mSemanticState;

    private HomeScene mHomeState;

    private NavigationScene mNavigationState;

    private MapChoiseScene mMapChoiseState;

    private WhetherScene mCommonWhetherState;

    private FaultScene mFaultScene;

    /**
     * 如果没有匹配到链对象，则使用默认链对象处理。
     */
    private DefaultChain mDefaultChain;

    public static SemanticEngine getProcessor() {
        if (mInstance == null) {
            mInstance = new SemanticEngine();
        }

        return mInstance;
    }

    private SemanticChain mCurChain;

    /**
     * 加入业务处理链条
     */
    private SemanticEngine() {
        log = LoggerFactory.getLogger("voice.semantic");

        mVoiceManager = VoiceManagerProxy.getInstance();

        mDefaultChain = new DefaultChain();

        initSemanticScenes();
    }

    public void processSemantic(SemanticBean semantic) {
        if (semantic != null) {

            if (TextUtils.isEmpty(semantic.getText())) {
                mVoiceManager.incrementMisUnderstandCount();
            } else {
                mVoiceManager.clearMisUnderstandCount();

                FloatWindowUtils.showMessage(semantic.getText(), MessageType.MESSAGE_OUTPUT);
            }

            if (semantic.hasResult() && isChainExists(semantic.getService())) {

                String service = semantic.getService();

                if (mCurChain.matchSemantic(service)) {
                    log.debug("用户输入与当前语义链匹配，开始处理语义...");
                    doSemantic(semantic);
                } else {
                    log.debug("将当前操作链入栈...");
                    pushSemanticStack(mCurChain);

                    mCurChain = mSemanticState.getChain(service);
                    if (mCurChain != null) {
                        log.debug("匹配到其它的语义链，开始处理语义...");
                        doSemantic(semantic);
                    } else {
                        log.debug("没有匹配到其它语义链，使用默认语义链处理...");
                        mDefaultChain.doSemantic(semantic);

                        log.debug("栈顶语义链出栈...");
                        mCurChain = popSemanticStack();
                    }
                }

                return;
            }
        }

        log.debug("没有匹配到相关语义处理链，使用默认语义链处理...");
        mDefaultChain.doSemantic(semantic);
    }

    private boolean isChainExists(String service) {
        if (mCurChain == null) {
            mCurChain = mSemanticState.getChain(service);
        }

        return mCurChain != null;
    }

    private void doSemantic(SemanticBean semantic) {
        if (!mCurChain.doSemantic(semantic)) {
            log.debug("当前链处理语义失败, 使用默认语义链处理...");
            mDefaultChain.doSemantic(semantic);
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

    public void switchSemanticType(SceneType type) {
        switch (type) {
            case HOME:
                log.debug("设置当前语义类型为正常...");
                mSemanticState = mHomeState;
                break;

            case NAVIGATION:
                log.debug("设置当前语义类型为导航...");
                mSemanticState = mNavigationState;
                break;

            case MAP_CHOISE:
                log.debug("设置当前语义类型为地址选择...");
                mSemanticState = mMapChoiseState;
                break;
            case COMMON_WHETHER:
                log.debug("设置当前语义为是否...");
                mSemanticState = mCommonWhetherState;
                break;
            case CAR_CHECKING:
                log.debug("设置当前语义为车辆自检...");
                mSemanticState = mFaultScene;
                break;

        }

        mDefaultChain = mSemanticState.getDefaultChain();
    }

    private void initSemanticScenes() {
        mHomeState = new HomeScene();

        mNavigationState = new NavigationScene();

        mMapChoiseState = new MapChoiseScene();

        mSemanticState = mHomeState;

        mCommonWhetherState = new WhetherScene();

        mFaultScene = new FaultScene();
    }

}
