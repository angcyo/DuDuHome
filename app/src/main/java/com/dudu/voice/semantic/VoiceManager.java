package com.dudu.voice.semantic;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;

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

/**
 * Created by 赵圣琪 on 2015/10/27.
 */
public class VoiceManager {

    private static final String TAG = "VoiceManager";

    private static final int MISUNDERSTAND_REPEAT_COUNT = 3;

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

    private int ret = 0;

    /**
     * 听不懂的次数，限制为3次
     */
    private int mMisunderstandCount = 0;

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

        registerWakeuper();

        mSpeechUnderstander = SpeechUnderstander.createUnderstander(mContext,
                mSpeechUnderstanderListener);

        setTtsParameter();
    }

    /**
     * 开始启动唤醒服务
     */
    public void startWakeup() {
        mWakeuper = VoiceWakeuper.getWakeuper();
        if (mWakeuper != null) {
            mWakeuper.setParameter(SpeechConstant.IVW_THRESHOLD, "0:"
                    + Constants.VOICE_WAKEUP_CURTHRESH);
            mWakeuper.setParameter(SpeechConstant.IVW_SST, "wakeup");
            mWakeuper.setParameter(SpeechConstant.KEEP_ALIVE, "1");
            mWakeuper.startListening(mWakeuperListener);
        }
    }

    public void stopWakeup() {
        mWakeuper = VoiceWakeuper.getWakeuper();
        if (mWakeuper != null) {
            mWakeuper.stopListening();
        }
    }

    private void registerWakeuper() {
        StringBuffer params = new StringBuffer();
        String resPath = ResourceUtil.generateResourcePath(mContext,
                ResourceUtil.RESOURCE_TYPE.assets, "ivw/55bda6e9.jet");
        params.append(ResourceUtil.IVW_RES_PATH + "=" + resPath);
        params.append("," + ResourceUtil.ENGINE_START + "="
                + SpeechConstant.ENG_IVW);

        boolean ret = SpeechUtility.getUtility().setParameter(
                ResourceUtil.ENGINE_START, params.toString());

        if (!ret) {
            LogUtils.d(TAG, "启动本地引擎失败！");
        }

        mWakeuper = VoiceWakeuper.createWakeuper(mContext, null);
    }

    /**
     * 语音唤醒监听器
     */
    private WakeuperListener mWakeuperListener = new WakeuperListener() {
        public void onResult(WakeuperResult result) {

            mMisunderstandCount = 0;

            if (!NetworkUtils.isNetworkConnected(mContext)) {
                startSpeaking(Constants.WAKEUP_NETWORK_UNAVAILABLE);
                return;
            }

            startSpeaking(Constants.WAKEUP_WORDS, Constants.TTS_ONE);
        }

        public void onError(SpeechError error) {
            LogUtils.e(TAG, "error: " + error.getErrorCode());
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

    /**
     * 开始语义理解
     */
    public void startUnderstanding() {

        stopWakeup();

        setUnderstanderParams();

        if (mSpeechUnderstander.isUnderstanding()) {
            mSpeechUnderstander.stopUnderstanding();
        } else {
            ret = mSpeechUnderstander.startUnderstanding(mRecognizerListener);
            if (ret != 0) {
                LogUtils.e(TAG, "---------语义理解失败, 错误码: " + ret);
            }
        }
    }

    /**
     * 停止语义理解
     */
    public void stopUnderstanding() {

        mSpeechUnderstander.stopUnderstanding();

        startWakeup();
    }

    /**
     * 设置语义理解相关参数
     */
    private void setUnderstanderParams() {
        mSpeechUnderstander.setParameter(SpeechConstant.RESULT_TYPE, "json");

        // 设置语言
        mSpeechUnderstander.setParameter(SpeechConstant.LANGUAGE, "zh_cn");

        // 设置语言区域
        // 普通话(mandarin)、英语(en_us)、粤语(cantonese)、四川话(lmz)、河南话(henanese)
        mSpeechUnderstander.setParameter(SpeechConstant.ACCENT, "mandarin");

        // 设置语音前端点 前端点检测；静音超时时间，即用户多长时间不说话则当做超时处理；
        mSpeechUnderstander.setParameter(SpeechConstant.VAD_BOS, "4000");

        // 设置语音后端点 后断点检测；后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
        mSpeechUnderstander.setParameter(SpeechConstant.VAD_EOS, "1000");

        // 设置标点符号
        mSpeechUnderstander.setParameter(SpeechConstant.ASR_PTT, "1");

        // 网络连接超时时间
        mSpeechUnderstander.setParameter(SpeechConstant.NET_TIMEOUT, "6000");

        // 设置音频保存路径
        mSpeechUnderstander.setParameter(SpeechConstant.ASR_AUDIO_PATH,
                Environment.getExternalStorageDirectory()
                        + "/dudu/wavaudio.pcm");
    }

    /**
     * 语音合成参数设置
     */
    private void setTtsParameter() {
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
            LogUtils.d(TAG, "mTtsInitListener init() code = " + code);
            if (code == ErrorCode.SUCCESS) {

            }
        }
    };

    public void startSpeaking(String playText) {

        FloatWindowUtil.showMessage(playText, FloatWindow.MESSAGE_IN);

        int code = mSpeechSynthesizer.startSpeaking(playText, null);

        if (code != ErrorCode.SUCCESS) {
            LogUtils.d(TAG, "语音合成失败,错误码: " + code);
        }
    }

    public void startSpeaking(String playText, int type) {

        if (mMisunderstandCount >= MISUNDERSTAND_REPEAT_COUNT) {
            playText = Constants.UNDERSTAND_EXIT;
        }

        FloatWindowUtil.showMessage(playText, FloatWindow.MESSAGE_IN);

        mSynthesizerType = type;

        int code = mSpeechSynthesizer.startSpeaking(playText,
                mSynthesizerListener);

        if (code != ErrorCode.SUCCESS) {
            LogUtils.d(TAG, "语音合成失败,错误码: " + code);
        }
    }

    public void stopSpeaking() {
        mSpeechSynthesizer.stopSpeaking();
    }


    private SpeechUnderstanderListener mRecognizerListener = new SpeechUnderstanderListener() {
        @Override
        public void onVolumeChanged(int i) {

        }

        @Override
        public void onBeginOfSpeech() {
            FloatWindowUtil.createWindow();
        }

        @Override
        public void onEndOfSpeech() {

        }

        @Override
        public void onResult(UnderstanderResult result) {

            stopUnderstanding();

            if (null != result) {
                String text = result.getResultString();
                if (!TextUtils.isEmpty(text)) {
                    String message = JsonUtils.parseIatResult(text, "text");

                    FloatWindowUtil.showMessage(message, FloatWindow.MESSAGE_OUT);

                    SemanticProcessor.getProcessor().processSemantic(text);
                }
            }
        }

        @Override
        public void onError(SpeechError speechError) {
            LogUtils.d(TAG, "--------SpeechError:" + speechError.getErrorCode());
            String playText;
            if (speechError.getErrorCode() == 10118) {
                playText = Constants.UNDERSTAND_NO_INPUT;
            } else {
                playText = Constants.UNDERSTAND_MISUNDERSTAND;
            }

            mMisunderstandCount++;

            stopUnderstanding();

            startSpeaking(playText, SemanticConstants.TTS_START_UNDERSTANDING);
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
            LogUtils.d(TAG, "speechUnderstanderListener init() code = " + code);
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
                switch (mSynthesizerType) {
                    case SemanticConstants.TTS_START_WAKEUP:
                        startWakeup();
                        break;
                    case SemanticConstants.TTS_START_UNDERSTANDING:
                        if (mMisunderstandCount >= MISUNDERSTAND_REPEAT_COUNT) {
                            FloatWindowUtil.removeFloatWindow();
                            return;
                        }

                        startUnderstanding();
                        break;
                }
            }
        }

        @Override
        public void onEvent(int i, int i1, int i2, Bundle bundle) {

        }
    };

}
