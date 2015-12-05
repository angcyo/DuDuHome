package com.dudu.voice.semantic;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.dudu.android.hideapi.SystemPropertiesProxy;
import com.dudu.android.launcher.LauncherApplication;
import com.dudu.android.launcher.utils.Constants;
import com.dudu.android.launcher.utils.FloatWindow;
import com.dudu.android.launcher.utils.FloatWindowUtil;
import com.dudu.android.launcher.utils.JsonUtils;
import com.dudu.android.launcher.utils.LogUtils;
import com.dudu.android.launcher.utils.NetworkUtils;
import com.dudu.android.launcher.utils.ToastUtils;
import com.dudu.voice.semantic.engine.SemanticProcessor;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SpeechUnderstander;
import com.iflytek.cloud.SpeechUnderstanderListener;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.SynthesizerListener;
import com.iflytek.cloud.UnderstanderResult;
import com.iflytek.cloud.VoiceWakeuper;
import com.iflytek.cloud.WakeuperListener;
import com.iflytek.cloud.WakeuperResult;
import com.iflytek.cloud.util.ResourceUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by 赵圣琪 on 2015/10/27.
 */
public class VoiceManager {

    private static final int MISUNDERSTAND_REPEAT_COUNT = 2;

    private static VoiceManager mInstance;

    private Context mContext;

    /**
     * 语音识别对象
     */
    private SpeechUnderstander mSpeechUnderstander;

    private SpeechSynthesizer mSpeechSynthesizer;

    /**
     * 当前语音合成类型
     */
    private int mSynthesizerType;

    /**
     * 语音唤醒
     */
    private VoiceWakeuper mWakeuper = null;

    /**
     * 听不懂的次数，限制为3次
     */
    private int mMisunderstandCount = 0;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            opening = false;
        }
    };

    private boolean mShowMessageWindow = true;

    private Logger log;

    private int log_step;

    private static boolean isListening = false;

    private boolean opening = false;

    private SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyy-MM-dd HH.mm.ss",
            Locale.getDefault());

    private Runnable mRemoveFloatWindow = new Runnable() {
        @Override
        public void run() {
            FloatWindowUtil.removeFloatWindow();
        }
    };

    /**
     * 获取整个应用唯一语音控制对象
     */
    public synchronized static VoiceManager getInstance() {
        if (mInstance == null) {
            mInstance = new VoiceManager();
        }

        return mInstance;
    }


    private VoiceManager() {
        mContext = LauncherApplication.mApplication;
        log = LoggerFactory.getLogger("voice.manager");
        log_step = 0;
        log.debug("[voice][{}]初始化语音manager...", log_step++);

        StringBuffer param = new StringBuffer();
        param.append("appid=" + Constants.XUFEIID);
        param.append(",");
        param.append(SpeechConstant.ENGINE_MODE + "=" + SpeechConstant.MODE_MSC);
        SpeechUtility.createUtility(mContext, param.toString());

//        registerWakeuper();

        log.debug("[voice][{}]set SpeechUnderstander Listener", log_step++);
        mSpeechUnderstander = SpeechUnderstander.createUnderstander(mContext,
                mSpeechUnderstanderListener);

        setTtsParameter();

        mHandler = new Handler();

    }

    public void clearMisUnderstandCount() {
        mMisunderstandCount = 0;
    }

    public void setShowMessageWindow(boolean showMessageWindow) {
        mShowMessageWindow = showMessageWindow;
    }

    /**
     * 开始启动唤醒服务
     */
    public void startWakeup() {
        log.debug("[voice][{}]开始启动唤醒服务...", log_step++);
        mWakeuper = VoiceWakeuper.getWakeuper();
        if (mWakeuper != null) {
            if (mWakeuper.isListening()) {
                log.debug("重复启动唤醒服务...");
                return;
            }

            mWakeuper.setParameter(SpeechConstant.IVW_THRESHOLD, "0:"
                    + Constants.VOICE_WAKEUP_CURTHRESH);
            mWakeuper.setParameter(SpeechConstant.IVW_SST, "wakeup");
            mWakeuper.setParameter(SpeechConstant.KEEP_ALIVE, "1");
            mWakeuper.startListening(mWakeuperListener);
            isListening = true;
        }

    }

    public void startLinteningBroadcast(Context context) {
        Intent intent = new Intent(Constants.VOICE_START_LISTENING);
        try {
            context.sendBroadcast(intent);
        } catch (ActivityNotFoundException exception) {
            log.error(exception.toString());
        }
    }

    public void stopLinteningBroadcast(Context context) {
        Intent intent = new Intent(Constants.VOICE_STOP_LISTENING);
        try {
            context.sendBroadcast(intent);
        } catch (ActivityNotFoundException exception) {
            log.error(exception.toString());
        }
    }

    public void stopWakeup() {
        log.debug("[voice][{}]停止唤醒服务", log_step++);
        mWakeuper = VoiceWakeuper.getWakeuper();
        if (mWakeuper != null) {
            mWakeuper.stopListening();
            isListening = false;
        }
    }

    public static boolean isWakeup() {
        if(mInstance!=null) {
            return isListening;
        }else{
            return false;
        }
    }

    public void destroyWakeup() {
        log.debug("[voice][{}]销毁唤醒服务", log_step++);
        if (mWakeuper != null) {
            mWakeuper.destroy();
        }

        if (mSpeechUnderstander != null) {
            mSpeechUnderstander.destroy();
        }
    }

    private void registerWakeuper() {
        log.debug("[voice][{}]register Wakeup er", log_step++);
        StringBuffer params = new StringBuffer();
        String resPath = ResourceUtil.generateResourcePath(mContext,
                ResourceUtil.RESOURCE_TYPE.assets, "ivw/55bda6e9.jet");
        params.append(ResourceUtil.IVW_RES_PATH + "=" + resPath);
        params.append("," + ResourceUtil.ENGINE_START + "="
                + SpeechConstant.ENG_IVW);

        boolean ret = SpeechUtility.getUtility().setParameter(
                ResourceUtil.ENGINE_START, params.toString());

        log.debug("[voice][{}]启动本地引擎结果{}！", log_step++, ret);

        if (!ret) {
            //TODO
        }

        mWakeuper = VoiceWakeuper.createWakeuper(mContext, null);
    }

    /**
     * 语音唤醒监听器
     */
    private WakeuperListener mWakeuperListener = new WakeuperListener() {

        public void onResult(WakeuperResult result) {
            log.debug("[voice][{}]语音唤醒监听器:{}", log_step++, result.getResultString());
//            startVoiceService();
        }

        public void onError(SpeechError error) {
            log.debug("[voice][{}]语音唤醒监听器, error:{}", log_step++, error.getErrorCode());
            if (error.getErrorCode() == 20006) {
                ToastUtils.showToast("录音失败，请查看是否有其他进程正在占用麦克风");
            }

        }

        @Override
        public void onEvent(int i, int i1, int i2, Bundle bundle) {

        }

        public void onBeginOfSpeech() {

        }

    };

    public void startVoiceLineter(){

        registerWakeuper();
        startWakeup();
        startVoiceService();
    }

    public void startVoiceService() {
        log.debug("[voice][{}]startVoiceService", log_step++);
        mMisunderstandCount = 0;

        if (!NetworkUtils.isNetworkConnected(mContext)) {
            startSpeaking(Constants.WAKEUP_NETWORK_UNAVAILABLE);
            mHandler.postDelayed(mRemoveFloatWindow, 4000);
            return;
        }

        startSpeaking(Constants.WAKEUP_WORDS, SemanticConstants.TTS_START_UNDERSTANDING);

    }

    public void removeFloatCallback() {
        mHandler.removeCallbacks(mRemoveFloatWindow);
    }

    /**
     * 开始语义理解
     */
    public void startUnderstanding() {
        log.debug("[voice][{}]开始语义理解", log_step++);

        stopWakeup();

        setUnderstanderParams();

        if (mSpeechUnderstander.isUnderstanding()) {
            mSpeechUnderstander.stopUnderstanding();
        } else {
            final int ret = mSpeechUnderstander.startUnderstanding(mRecognizerListener);
            log.debug("[voice][{}]开始语义理解结果:{}", log_step++, ret);
        }
    }

    /**
     * 停止语义理解
     */
    public void stopUnderstanding() {
        log.debug("[voice][{}]停止语义理解", log_step++);

        mSpeechUnderstander.cancel();

//        startWakeup();
    }

    /**
     * 设置语义理解相关参数
     */
    private void setUnderstanderParams() {
        log.debug("[voice][{}]设置语义理解相关参数", log_step++);
        mSpeechUnderstander.setParameter(SpeechConstant.RESULT_TYPE, "json");

        // 设置语言
        mSpeechUnderstander.setParameter(SpeechConstant.LANGUAGE, "zh_cn");

        // 设置语言区域
        // 普通话(mandarin)、英语(en_us)、粤语(cantonese)、四川话(lmz)、河南话(henanese)
        mSpeechUnderstander.setParameter(SpeechConstant.ACCENT, "mandarin");

        // 设置语音前端点 前端点检测；静音超时时间，即用户多长时间不说话则当做超时处理；
        mSpeechUnderstander.setParameter(SpeechConstant.VAD_BOS, "6000");

        // 设置语音后端点 后断点检测；后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
        mSpeechUnderstander.setParameter(SpeechConstant.VAD_EOS, "2000");

        // 设置标点符号
        mSpeechUnderstander.setParameter(SpeechConstant.ASR_PTT, "1");

        // 网络连接超时时间
        mSpeechUnderstander.setParameter(SpeechConstant.NET_TIMEOUT, "5000");

        // 设置音频保存路径
        mSpeechUnderstander.setParameter(SpeechConstant.ASR_AUDIO_PATH,
                Environment.getExternalStorageDirectory()
                        + "/dudu/wavaudio.pcm");
    }

    /**
     * 语音合成参数设置
     */
    private void setTtsParameter() {
        log.debug("[voice][{}]语音合成参数设置", log_step++);
        mSpeechSynthesizer = SpeechSynthesizer.createSynthesizer(mContext,
                mTtsInitListener);

        // 清空参数
        mSpeechSynthesizer.setParameter(SpeechConstant.PARAMS, null);

        mSpeechSynthesizer.setParameter(SpeechConstant.ENGINE_TYPE,
                SpeechConstant.TYPE_LOCAL);

        mSpeechSynthesizer.setParameter(ResourceUtil.TTS_RES_PATH,
                getResourcePath());

        mSpeechSynthesizer.setParameter(SpeechConstant.VOICE_NAME, "xiaoyan");

        // 设置合成语速
        mSpeechSynthesizer.setParameter(SpeechConstant.SPEED, "50");

        // 设置合成音量
        mSpeechSynthesizer.setParameter(SpeechConstant.VOLUME, "30");

        // 设置合成音调
        mSpeechSynthesizer.setParameter(SpeechConstant.PITCH, "50");

        // 设置播放器音频流类型
        mSpeechSynthesizer.setParameter(SpeechConstant.STREAM_TYPE, "3");

        mSpeechSynthesizer.setParameter(SpeechConstant.KEY_REQUEST_FOCUS,
                "true");
    }

    private String getResourcePath() {
        StringBuffer sb = new StringBuffer();
        sb.append(ResourceUtil.generateResourcePath(mContext,
                ResourceUtil.RESOURCE_TYPE.assets, "tts/common.jet"));
        sb.append(";");
        sb.append(ResourceUtil.generateResourcePath(mContext,
                ResourceUtil.RESOURCE_TYPE.assets, "tts/xiaoyan.jet"));
        return sb.toString();
    }

    /**
     * 语音合成初始化监听。
     */
    private InitListener mTtsInitListener = new InitListener() {
        @Override
        public void onInit(int code) {
            log.debug("[voice][{}]语音合成初始化监听:{}", log_step++, code);
        }
    };

    public void startSpeaking(String playText) {
        startSpeaking(playText, SemanticConstants.TTS_DO_NOTHING, true);
    }

    public void startSpeaking(String playText, int type) {
        startSpeaking(playText, type, true);
    }

    public void startSpeaking(String playText, int type, boolean showMessage) {
        if (mMisunderstandCount >= MISUNDERSTAND_REPEAT_COUNT) {
            playText = Constants.UNDERSTAND_EXIT;
        }

        mSynthesizerType = type;

        if (mShowMessageWindow && showMessage) {
            FloatWindowUtil.showMessage(playText, FloatWindow.MESSAGE_IN);
        }

        int code = mSpeechSynthesizer.startSpeaking(playText, mSynthesizerListener);

        log.debug("[voice][{}]语音合成结果:{}", log_step++, code);

        if ("off".equals(SystemPropertiesProxy.getInstance().get("persist.sys.screen", "unkonw"))) {
            SystemPropertiesProxy.getInstance().set(mContext, "persist.sys.screen", "on");
        }
    }

    public void stopSpeaking() {
        mSpeechSynthesizer.stopSpeaking();
    }

    private SpeechUnderstanderListener mRecognizerListener = new SpeechUnderstanderListener() {

        @Override
        public void onVolumeChanged(int i) {
            FloatWindowUtil.changeVoice(i);
        }

        @Override
        public void onBeginOfSpeech() {

        }

        @Override
        public void onEndOfSpeech() {

        }

        @Override
        public void onResult(UnderstanderResult result) {
            log.debug("[voice][{}]语义理解结果:{}", log_step++, result != null);

            stopUnderstanding();

            if (null != result) {
                String text = result.getResultString();
                log.trace("[voice][{}]语义理解结果:{}", log_step++, text);
                if (!TextUtils.isEmpty(text)) {
                    String message = JsonUtils.parseIatResult(text, "text");

                    if (mShowMessageWindow) {
                        FloatWindowUtil.showMessage(message, FloatWindow.MESSAGE_OUT);
                    }

                    SemanticProcessor.getProcessor().processSemantic(text);
                }
            }
        }

        @Override
        public void onError(SpeechError speechError) {
            log.warn("[voice][{}]语义理解失败", log_step++);

            stopUnderstanding();

            mMisunderstandCount++;

            if (speechError.getErrorCode() == 10118) {
                startSpeaking(Constants.UNDERSTAND_NO_INPUT, SemanticConstants.TTS_START_UNDERSTANDING);
                log.warn("[voice][{}]语义理解失败:没有检测到语音输入", log_step++);
            } else if (speechError.getErrorCode() == 10114) {
                startSpeaking(Constants.UNDERSTAND_NETWORK_PROBLEM, SemanticConstants.TTS_START_UNDERSTANDING);
                log.warn("[voice][{}]语义理解失败:语义理解网络超时", log_step++);
            } else {
                startSpeaking(Constants.UNDERSTAND_MISUNDERSTAND, SemanticConstants.TTS_START_UNDERSTANDING);
                log.warn("[voice][{}]语义理解其他错误:{}", log_step++, speechError.getErrorCode());
            }
        }

        @Override
        public void onEvent(int i, int i1, int i2, Bundle bundle) {

        }
    };

    /**
     * 初始化监听器（语音到语义）。
     */
    private InitListener mSpeechUnderstanderListener = new InitListener() {
        @Override
        public void onInit(int code) {
            log.debug("[voice][{}]InitListener:{}", log_step++, code);
            if (code != ErrorCode.SUCCESS) {

            }
        }
    };

    private SynthesizerListener mSynthesizerListener = new SynthesizerListener() {
        @Override
        public void onSpeakBegin() {

        }

        @Override
        public void onBufferProgress(int i, int i1, int i2, String s) {

        }

        @Override
        public void onSpeakPaused() {

        }

        @Override
        public void onSpeakResumed() {

        }

        @Override
        public void onSpeakProgress(int i, int i1, int i2) {

        }

        @Override
        public void onCompleted(SpeechError speechError) {
            if (speechError == null) {
                log.debug("[voice][{}]语音合成完成,下一步:{},连续出错次数:{}", log_step++, mSynthesizerType, mMisunderstandCount);
                switch (mSynthesizerType) {
                    case SemanticConstants.TTS_START_WAKEUP:
//                        startWakeup();
                        break;
                    case SemanticConstants.TTS_START_UNDERSTANDING:
                        if (mMisunderstandCount >= MISUNDERSTAND_REPEAT_COUNT) {
                            FloatWindowUtil.removeFloatWindow();
                            SemanticProcessor.getProcessor().switchSemanticType(SemanticType.NORMAL);
                            return;
                        }

                        startUnderstanding();
                        break;
                }
            } else {
                log.debug("[voice][{}]语音合出错:{}", log_step++, speechError.getErrorDescription());
            }
        }

        @Override
        public void onEvent(int i, int i1, int i2, Bundle bundle) {

        }
    };

    public void setIsListening(boolean isListening){
        this.isListening = isListening;
    }

    public void setOpening(){
        this.opening = true;
        mHandler.sendEmptyMessageDelayed(1,500);
    }

    public boolean getOpening(){
        return opening;
    }

}
