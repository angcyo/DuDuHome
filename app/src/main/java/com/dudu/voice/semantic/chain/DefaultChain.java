package com.dudu.voice.semantic.chain;

import com.dudu.android.launcher.LauncherApplication;
import com.dudu.android.launcher.utils.Constants;
import com.dudu.android.launcher.utils.NetworkUtils;
import com.dudu.voice.semantic.bean.SemanticBean;

/**
 * Created by 赵圣琪 on 2015/10/30.
 */
public class DefaultChain extends SemanticChain {

    @Override
    public boolean matchSemantic(String service) {
        return true;
    }

    @Override
    public boolean doSemantic(SemanticBean semantic) {
        if (NetworkUtils.isNetworkConnected(LauncherApplication.getContext())) {
            mVoiceManager.startSpeaking(Constants.UNDERSTAND_MISUNDERSTAND);
        } else {
            mVoiceManager.startSpeaking(Constants.NETWORK_UNAVAILABLE);
        }

        return false;
    }

}