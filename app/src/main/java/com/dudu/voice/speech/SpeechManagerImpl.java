package com.dudu.voice.speech;

import android.text.TextUtils;

import com.aispeech.AIError;
import com.aispeech.AIResult;
import com.aispeech.IMergeRule;
import com.aispeech.common.AIConstant;
import com.aispeech.common.Util;
import com.aispeech.export.engines.AILocalGrammarEngine;
import com.aispeech.export.engines.AILocalTTSEngine;
import com.aispeech.export.engines.AILocalWakeupDnnEngine;
import com.aispeech.export.engines.AIMixASREngine;
import com.aispeech.export.listeners.AIASRListener;
import com.aispeech.export.listeners.AIAuthListener;
import com.aispeech.export.listeners.AILocalGrammarListener;
import com.aispeech.export.listeners.AILocalWakeupDnnListener;
import com.aispeech.export.listeners.AITTSListener;
import com.aispeech.speech.AIAuthEngine;
import com.dudu.aios.ui.voice.VoiceEvent;
import com.dudu.android.hideapi.SystemPropertiesProxy;
import com.dudu.android.launcher.utils.Constants;
import com.dudu.voice.BaseVoiceManager;
import com.dudu.voice.FloatWindowUtils;
import com.dudu.voice.semantic.constant.TTSType;
import com.dudu.voice.semantic.engine.SemanticEngine;
import com.dudu.voice.semantic.parser.SpeechJsonParser;
import com.dudu.voice.window.MessageType;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import de.greenrobot.event.EventBus;
import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;

/**
 * Created by 赵圣琪 on 2015/10/27.
 */
public class SpeechManagerImpl extends BaseVoiceManager {

    private static final String TTS_ZIP_FILE_NAME = "tts.zip";

    private AILocalWakeupDnnEngine mWakeupEngine;

    private AIAuthEngine mAuthEngine;

    private AILocalGrammarEngine mGrammarEngine;

    private AIMixASREngine mAsrEngine;

    private AILocalTTSEngine mTTSEngine;

    private volatile boolean mSpeaking = false;

    private BlockingQueue<TTSTask> mTTSQueue;

    public SpeechManagerImpl() {
        mTTSQueue = new ArrayBlockingQueue<>(500, true);
    }

    public Subscription reAuthSub;


    private void initWakeupEngine() {
        mWakeupEngine = AILocalWakeupDnnEngine.createInstance();
        mWakeupEngine.setResBin(SpeechConstant.wakeup_dnn_res);
        mWakeupEngine.init(mContext, new AISpeechListenerImpl(),
                SpeechConstant.APPKEY, SpeechConstant.SECRETKEY);
        mWakeupEngine.setStopOnWakeupSuccess(true);
        mWakeupEngine.setWords(new String[]{"嗨伊娃"});
        mWakeupEngine.setDeviceId(Util.getIMEI(mContext));

        startWakeup();
    }

    private void initAuthEngine() {
        mAuthEngine = AIAuthEngine.getInstance(mContext);
        try {
            mAuthEngine.init(SpeechConstant.APPKEY, SpeechConstant.SECRETKEY, "444b-a34a-eafc-8f95");
        } catch (FileNotFoundException e) {
            log.error("语音授权文件没有找到...");
        }

        if (!mAuthEngine.isAuthed()) {
            mAuthEngine.setOnAuthListener(new AIAuthListener() {
                @Override
                public void onAuthSuccess() {

                }

                @Override
                public void onAuthFailed(String s) {

                    reAuthSub = Observable.timer(10, TimeUnit.SECONDS).subscribe(new Action1<Long>() {
                        @Override
                        public void call(Long aLong) {
                            doAuth();
                        }
                    });
                }
            });
            doAuth();
        }
    }

    private void doAuth() {
        final boolean authRet = mAuthEngine.doAuth();
        if (authRet) {
            log.debug("语音授权成功...");
        } else {
            log.error("语音授权失败...");
        }
    }

    /**
     * 初始化资源编译引擎
     */
    private void initGrammarEngine() {
        if (mGrammarEngine != null) {
            mGrammarEngine.destroy();
        }

        mGrammarEngine = AILocalGrammarEngine.createInstance();

        mGrammarEngine.setResFileName(SpeechConstant.ebnfc_res);

        mGrammarEngine.init(mContext, new AILocalGrammarListenerImpl(), SpeechConstant.APPKEY,
                SpeechConstant.SECRETKEY);

        mGrammarEngine.setDeviceId(Util.getIMEI(mContext));
    }

    private void initAsrEngine() {
        if (mAsrEngine != null) {
            mAsrEngine.destroy();
        }

        mAsrEngine = AIMixASREngine.createInstance();
        mAsrEngine.setResBin(SpeechConstant.ebnfr_res);
        mAsrEngine.setNetBin(AILocalGrammarEngine.OUTPUT_NAME, true);
        mAsrEngine.setVadResource(SpeechConstant.vad_res);
        if (mContext.getExternalCacheDir() != null) {
            mAsrEngine.setTmpDir(mContext.getExternalCacheDir().getAbsolutePath());
            mAsrEngine.setUploadEnable(true);
            mAsrEngine.setUploadInterval(1000);
        }
        mAsrEngine.setServer("ws://s-test.api.aispeech.com:10000");
        mAsrEngine.setRes("aicar");
        mAsrEngine.setUseXbnfRec(true);
        mAsrEngine.setUsePinyin(true);
        mAsrEngine.setUseForceout(false);
        mAsrEngine.setAthThreshold(0.7f);
        mAsrEngine.setIsRelyOnLocalConf(true);
        mAsrEngine.setLocalBetterDomains(new String[]{"phone", "music", "radio", "volume",
                "brightness", "eq", "opt", "app"});

        mAsrEngine.setWaitCloudTimeout(2000);
        mAsrEngine.setPauseTime(1000);
        mAsrEngine.setUseConf(true);
        mAsrEngine.setNoSpeechTimeOut(6000);
        mAsrEngine.setDeviceId(Util.getIMEI(mContext));
        // 自行设置合并规则:
        // 1. 如果无云端结果,则直接返回本地结果
        // 2. 如果有云端结果,则直接返回云端结果
        mAsrEngine.setMergeRule(new IMergeRule() {

            @Override
            public AIResult mergeResult(AIResult localResult, AIResult cloudResult) {
                AIResult result = null;
                try {
                    if (cloudResult == null) {
                        // 为结果增加标记,以标示来源于云端还是本地
                        JSONObject localJsonObject = new JSONObject(localResult.getResultObject()
                                .toString());
                        localJsonObject.put("src", "native");

                        localResult.setResultObject(localJsonObject);
                        result = localResult;
                    } else {
                        JSONObject cloudJsonObject = new JSONObject(cloudResult.getResultObject()
                                .toString());
                        cloudJsonObject.put("src", "cloud");
                        cloudResult.setResultObject(cloudJsonObject);
                        result = cloudResult;
                    }
                } catch (JSONException e) {

                }

                return result;
            }
        });

        mAsrEngine.init(mContext, new AIASRListenerImpl(),
                SpeechConstant.APPKEY, SpeechConstant.SECRETKEY);
        mAsrEngine.setUseCloud(true);
    }

    private void initTTSEngine() {
        if (mTTSEngine != null) {
            mTTSEngine.destory();
        }

        mTTSEngine = AILocalTTSEngine.createInstance();
        mTTSEngine.setResource(TTS_ZIP_FILE_NAME, "zhilingf.v0.4.11.bin");
        mTTSEngine.setRealBack(true);
        mTTSEngine.setUseCahce(false, 20);
        mTTSEngine.init(mContext, new AILocalTTSListenerImpl(), SpeechConstant.APPKEY, SpeechConstant.SECRETKEY);
        mTTSEngine.setLeftMargin(25);
        mTTSEngine.setRightMargin(25);
        mTTSEngine.setSpeechRate(0.85f);
        mTTSEngine.setDeviceId(Util.getIMEI(mContext));
    }

    private void importLocalGrammar() {
        // 生成ebnf语法
        GrammarHelper gh = new GrammarHelper(mContext);
        String contactString = gh.getConatcts();
        String appString = gh.getApps();

        // 如果手机通讯录没有联系人
        if (TextUtils.isEmpty(contactString)) {
            contactString = "无联系人";
        }

        String ebnf = gh.importAssets(contactString, appString, "grammar.xbnf");
        // 设置ebnf语法
        mGrammarEngine.setEbnf(ebnf);
        // 启动语法编译引擎，更新资源
        mGrammarEngine.update();
    }

    @Override
    public void onInit() {

        initAuthEngine();

        initWakeupEngine();

        initGrammarEngine();

        initAsrEngine();

        initTTSEngine();

        importLocalGrammar();
    }

    @Override
    public void startWakeup() {
        if (mWakeupEngine != null) {
            log.debug("开始唤醒监听...");
            mWakeupEngine.start();
        }
    }

    @Override
    public void stopWakeup() {
        if (mWakeupEngine != null) {
            log.debug("停止唤醒监听...");
            mWakeupEngine.stop();
        }
    }

    @Override
    public void startUnderstanding() {
        if (mAsrEngine != null) {
            log.debug("开启语音听写前，停止语音唤醒...");
            stopWakeup();

            log.debug("开始语义理解...");
            mAsrEngine.start();
        }
    }

    @Override
    public void stopUnderstanding() {
        if (mAsrEngine != null) {
            log.debug("结束语义理解...");
            mAsrEngine.stopRecording();
            mAsrEngine.cancel();
        }
    }

    @Override
    public void startSpeaking(String playText, TTSType type, boolean showMessage) {
        if (checkMisUnderstandCount()) {
            type = TTSType.TTS_DO_NOTHING;
            playText = Constants.UNDERSTAND_EXIT;
        }

        if (showMessage) {
            FloatWindowUtils.showMessage(playText, MessageType.MESSAGE_INPUT);
        }

        if (mTTSEngine != null) {
            startSpeakingWithQueue(playText, type);
        }

        if ("off".equals(SystemPropertiesProxy.getInstance().get("persist.sys.screen", "unkonw"))) {
            SystemPropertiesProxy.getInstance().set(mContext, "persist.sys.screen", "on");
        }
    }

    private void startSpeakingWithQueue(String playText, TTSType type) {
        if (mSpeaking) {
            mTTSQueue.add(new TTSTask(playText, type));
            return;
        }

        mType = type;
        mSpeaking = true;
        mTTSEngine.speak(playText, "1024");
    }

    private void speakQueueNext() {
        if (mTTSQueue.isEmpty()) {
            return;
        }

        TTSTask task = mTTSQueue.poll();
        if (task != null) {
            mSpeaking = true;

            mType = task.type;

            mTTSEngine.speak(task.playText, "1024");
        }
    }

    @Override
    public void stopSpeaking() {
        if (mTTSEngine != null) {
            log.debug("停止说话...");
            mTTSEngine.stop();

            mSpeaking = false;

            mTTSQueue.clear();
        }
    }

    @Override
    public void onStop() {

        clearMisUnderstandCount();

        stopUnderstanding();

        startWakeup();
    }

    @Override
    public void onDestroy() {
        if (mAsrEngine != null) {
            mAsrEngine.destroy();
            mAsrEngine = null;
        }

        if (mGrammarEngine != null) {
            mGrammarEngine.destroy();
            mGrammarEngine = null;
        }

        if (mTTSEngine != null) {
            mTTSEngine.destory();
            mTTSEngine = null;
        }
    }

    private static class TTSTask {
        String playText;

        TTSType type;

        public TTSTask(String playText, TTSType type) {
            this.playText = playText;
            this.type = type;
        }
    }

    private class AISpeechListenerImpl implements AILocalWakeupDnnListener {

        @Override
        public void onInit(int status) {
            if (status == AIConstant.OPT_SUCCESS) {
                log.debug("唤醒引擎初始化成功...");
            } else {
                log.error("唤醒引擎初始化失败...");
            }
        }

        @Override
        public void onError(AIError aiError) {

        }

        @Override
        public void onWakeup(String s, double v, String s1) {
            log.debug("唤醒成功...");
            mWakeupEngine.stop();

            startVoiceService();
        }

        @Override
        public void onRmsChanged(float v) {

        }

        @Override
        public void onRecorderReleased() {

        }

        @Override
        public void onReadyForSpeech() {

        }

        @Override
        public void onBufferReceived(byte[] bytes) {

        }
    }


    /**
     * 语法编译引擎回调接口，用以接收相关事件
     */
    private class AILocalGrammarListenerImpl implements AILocalGrammarListener {

        @Override
        public void onError(AIError error) {
            log.error("资源生成发生错误...");
        }

        @Override
        public void onUpdateCompleted(String recordId, String path) {
            initAsrEngine();
        }

        @Override
        public void onInit(int status) {
            if (status == 0) {

            } else {
                log.error("资源定制引擎加载失败...");
            }
        }
    }

    /**
     * 本地识别引擎回调接口，用以接收相关事件
     */
    public class AIASRListenerImpl implements AIASRListener {

        @Override
        public void onBeginningOfSpeech() {
            log.debug("检测到用户开始说话...");
        }

        @Override
        public void onEndOfSpeech() {
            log.debug("检测到语音停止，开始识别...");
        }

        @Override
        public void onReadyForSpeech() {
            log.debug("请说话...");
        }

        @Override
        public void onRmsChanged(float rmsdB) {

            FloatWindowUtils.onVolumeChanged((int) rmsdB);

        }

        @Override
        public void onError(AIError error) {
            log.error("识别发生错误, errorId: " + error.getErrId());

            stopUnderstanding();

            incrementMisUnderstandCount();

            if (error.getErrId() == SpeechConstant.ERROR_TIMEOUT) {
                startSpeaking(Constants.UNDERSTAND_NO_INPUT);
            } else if (error.getErrId() == SpeechConstant.ERROR_NO_INPUT) {
                startSpeaking(Constants.UNDERSTAND_NO_INPUT);
            }
        }

        @Override
        public void onResults(AIResult results) {
            log.debug(results.getResultObject().toString());

            stopUnderstanding();

            SemanticEngine.getProcessor().processSemantic(SpeechJsonParser.getInstance().
                    parseSemanticJson(results.toString()));
        }

        @Override
        public void onInit(int status) {
            if (status == 0) {
                log.debug("本地识别引擎加载成功...");
            } else {
                log.error("本地识别引擎加载失败...");
            }
        }

        @Override
        public void onRecorderReleased() {

        }
    }

    private class AILocalTTSListenerImpl implements AITTSListener {

        @Override
        public void onInit(int status) {
            log.debug("初始化完成...");
            if (status == AIConstant.OPT_SUCCESS) {

            } else {
                log.error("初始化失败! code:" + status);
            }
        }

        @Override
        public void onProgress(int currentTime, int totalTime, boolean isRefTextTTSFinished) {

        }

        @Override
        public void onError(String utteranceId, AIError error) {

        }

        @Override
        public void onReady(String utteranceId) {

        }

        @Override
        public void onCompletion(String utteranceId) {
            mSpeaking = false;
            switch (mType) {
                case TTS_DO_NOTHING:
                    if (checkMisUnderstandCount()) {
                        EventBus.getDefault().post(VoiceEvent.THRICE_UNSTUDIED);
                        FloatWindowUtils.removeWithBlur();
                    }
                    speakQueueNext();
                    break;
                case TTS_START_WAKEUP:
                    startWakeup();
                    break;
                case TTS_START_UNDERSTANDING:

                    mTTSQueue.clear();

                    startUnderstanding();
                    break;
            }
        }
    }

}
