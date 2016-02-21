package com.dudu.voice.semantic.chain;

import android.text.TextUtils;
import android.util.Log;

import com.dudu.android.launcher.utils.ChoiseUtil;
import com.dudu.android.launcher.utils.Constants;
import com.dudu.carChecking.CarCheckingProxy;
import com.dudu.carChecking.CarNaviChoose;
import com.dudu.voice.FloatWindowUtils;
import com.dudu.voice.semantic.bean.SemanticBean;
import com.dudu.voice.semantic.constant.TTSType;

import de.greenrobot.event.EventBus;

/**
 * Created by lxh on 2016/2/16.
 */
public class FaultDefaultChain extends DefaultChain {

    public static final String FAULT_CLEAR = "清除故障码";

    public static final String FAULT_PALY = "故障播报";

    public static final String NEXT_PAGE = "下一页";
    public static final String SHORT_NEXT_PAGE = "下页";

    public static final String PREVIOUS_PAGE = "上一页";
    public static final String SHORT_PREVIOUS_PAGE = "上页";

    @Override
    public boolean matchSemantic(String service) {
        return true;
    }

    @Override
    public boolean doSemantic(SemanticBean semantic) {
        return fault(semantic);
    }

    private boolean fault(SemanticBean semantic) {
        Log.d("lxh", "voice fault");
        if (!TextUtils.isEmpty(semantic.getText())) {

            Log.d("lxh", "voice fault  " + semantic.getText());
            if (semantic.getText().contains(FAULT_CLEAR)) {
                CarCheckingProxy.getInstance().clearFault();
                FloatWindowUtils.removeFloatWindow();
                return true;
            } else {
                return handleMapChoise(semantic.getText());
            }
        }
        mVoiceManager.startSpeaking(Constants.UNDERSTAND_MISUNDERSTAND);
        return false;
    }


    private boolean handleMapChoise(String text) {
        if (text.contains(NEXT_PAGE) || text.contains(SHORT_NEXT_PAGE)) {
            EventBus.getDefault().post(new CarNaviChoose(CarNaviChoose.ChooseType.NEXT_PAGE, 0));
            mVoiceManager.startUnderstanding();
        } else if (text.contains(PREVIOUS_PAGE) || text.contains(SHORT_PREVIOUS_PAGE)) {
            EventBus.getDefault().post(new CarNaviChoose(CarNaviChoose.ChooseType.LAST_PAGE, 0));
            mVoiceManager.startUnderstanding();
        } else {
            if (!handleChoosePageOrNumber(text)) {
                mVoiceManager.startSpeaking(Constants.UNDERSTAND_CHOISE_INPUT_TIPS,
                        TTSType.TTS_START_UNDERSTANDING, true);
                return false;
            }
        }

        return true;
    }

    private boolean handleChoosePageOrNumber(String text) {
        int option;
        if (text.startsWith("第") && (text.length() == 3 || text.length() == 4)) {

            option = ChoiseUtil.getChoiseSize(text.length() == 3 ? text.substring(1, 2) : text.substring(1, 3));

            if (text.endsWith("个")) {
                EventBus.getDefault().post(new CarNaviChoose(CarNaviChoose.ChooseType.CHOOSE_NUMBER, option));
                mVoiceManager.startUnderstanding();
            } else if (text.endsWith("页")) {

                EventBus.getDefault().post(new CarNaviChoose(CarNaviChoose.ChooseType.CHOOSE_PAGE, option));
                mVoiceManager.startUnderstanding();
            } else {
                return false;
            }

            return true;
        }


        return false;
    }


}
