package com.dudu.voice.semantic.chain.map;

import com.dudu.android.launcher.utils.ChoiseUtil;
import com.dudu.android.launcher.utils.Constants;
import com.dudu.map.NavigationProxy;
import com.dudu.voice.semantic.bean.SemanticBean;
import com.dudu.voice.semantic.chain.DefaultChain;
import com.dudu.voice.semantic.constant.TTSType;

/**
 * Created by lxh on 2015/11/12.
 */
public class MapChoiseDefaultChain extends DefaultChain {

    public static final String NEXT_PAGE = "下一页";
    public static final String SHORT_NEXT_PAGE = "下页";

    public static final String PREVIOUS_PAGE = "上一页";
    public static final String SHORT_PREVIOUS_PAGE = "上页";

    private NavigationProxy mProxy;

    public MapChoiseDefaultChain() {
        mProxy = NavigationProxy.getInstance();
    }

    @Override
    public boolean matchSemantic(String service) {
        return true;
    }

    @Override
    public boolean doSemantic(SemanticBean semantic) {
        String text = semantic == null ? "" : semantic.getText();
        return handleMapChoise(text);
    }

    private boolean handleMapChoise(String text) {
        if (text.contains(NEXT_PAGE) || text.contains(SHORT_NEXT_PAGE)) {
            mProxy.onNextPage();
            mVoiceManager.startUnderstanding();
        } else if (text.contains(PREVIOUS_PAGE) || text.contains(SHORT_PREVIOUS_PAGE)) {
            mProxy.onPreviousPage();
            mVoiceManager.startUnderstanding();
        } else {
            if (!handleChoosePageOrNumber(text)) {
                mVoiceManager.startSpeaking(Constants.UNDERSTAND_CHOISE_INPUT_TIPS,
                        TTSType.TTS_START_UNDERSTANDING, false);
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
                mProxy.onChooseNumber(option);
            } else if (text.endsWith("页")) {
                mProxy.onChoosePage(option);
                mVoiceManager.startUnderstanding();
            } else {
                return false;
            }

            return true;
        }

        if (mProxy.getChooseStep() == 2) {
            switch (text) {
                case "速度最快":
                    option = 1;
                    break;
                case "避免收费":
                    option = 2;
                    break;
                case "距离最短":
                    option = 3;
                    break;
                case "不走高速快速路":
                    option = 4;
                    break;
                case "时间最短且躲避拥堵":
                    option = 5;
                    break;
                case "避免收费且躲避拥堵":
                    option = 6;
                    break;
                default:
                    return false;
            }

            mProxy.onChooseNumber(option);
            return true;
        }

        return false;
    }

}
