package com.dudu.voice.semantic.chain;

import android.content.Intent;

import com.dudu.android.launcher.LauncherApplication;
import com.dudu.android.launcher.bean.CmdEntity;
import com.dudu.android.launcher.bean.CmdSlots;
import com.dudu.android.launcher.ui.activity.MainActivity;
import com.dudu.android.launcher.ui.activity.OBDCheckingActivity;
import com.dudu.android.launcher.utils.Constants;
import com.dudu.android.launcher.utils.FloatWindowUtil;
import com.dudu.android.launcher.utils.GsonUtil;
import com.dudu.android.launcher.utils.JsonUtils;
import com.dudu.android.launcher.utils.Util;
import com.dudu.voice.semantic.SemanticConstants;
import com.dudu.voice.semantic.SemanticType;
import com.dudu.voice.semantic.engine.SemanticProcessor;

/**
 * Created by 赵圣琪 on 2015/11/5.
 */
public class CarCheckingChain extends  SemanticChain  {

    private LauncherApplication mApplication;

    public CarCheckingChain() {
        super();
        mApplication = LauncherApplication.getContext();
    }

    @Override
    public boolean matchSemantic(String service) {
        return SemanticConstants.SERVICE_CAR_CHECKING.equals(service);
    }

    @Override
    public boolean doSemantic(String json) {
        if (Util.isTaxiVersion()) {
            return false;
        }

        mVoiceManager.setShowMessageWindow(false);

        FloatWindowUtil.removeFloatWindow();

        String semantic = JsonUtils.parseIatResult(json,
                "semantic");
        CmdEntity cmdEntity = (CmdEntity) GsonUtil
                .jsonToObject(semantic, CmdEntity.class);
        CmdSlots slots = cmdEntity.getSlots();
        String option = slots.getCmd().getOption();
        switch (option) {
            case Constants.OPEN:
            case Constants.START:
                SemanticProcessor.getProcessor().switchSemanticType(
                        SemanticType.CAR_CHECKING);

                startCarSelfChecking();
                break;
            case Constants.CLOSE:
            case Constants.END:
                SemanticProcessor.getProcessor().switchSemanticType(
                        SemanticType.NORMAL);

                startMainActivity();
                break;
        }

        return true;
    }

    private void startCarSelfChecking() {
        Intent intent = new Intent(mApplication, OBDCheckingActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mApplication.startActivity(intent);
    }

    private void startMainActivity() {
        Intent intent = new Intent(mApplication, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mApplication.startActivity(intent);
    }

}
