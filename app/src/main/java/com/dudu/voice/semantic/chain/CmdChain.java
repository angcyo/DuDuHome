package com.dudu.voice.semantic.chain;

import android.app.Activity;
import android.content.Intent;

import com.dudu.android.launcher.LauncherApplication;
import com.dudu.android.launcher.bean.CmdEntity;
import com.dudu.android.launcher.bean.CmdSlots;
import com.dudu.android.launcher.ui.activity.MainActivity;
import com.dudu.android.launcher.ui.activity.video.VideoActivity;
import com.dudu.android.launcher.utils.ActivitiesManager;
import com.dudu.android.launcher.utils.Constants;
import com.dudu.android.launcher.utils.FloatWindowUtil;
import com.dudu.android.launcher.utils.GsonUtil;
import com.dudu.android.launcher.utils.JsonUtils;
import com.dudu.voice.semantic.SemanticConstants;

/**
 * Created by 赵圣琪 on 2015/10/29.
 */
public class CmdChain extends SemanticChain {

    private LauncherApplication mApplication;

    public CmdChain() {
        super();
        mApplication = LauncherApplication.getContext();
    }

    @Override
    public boolean matchSemantic(String service) {
        return SemanticConstants.SERVICE_CMD.equals(service);
    }

    @Override
    public boolean doSemantic(String json) {

        FloatWindowUtil.removeFloatWindow();

        String semantic = JsonUtils.parseIatResult(json,
                "semantic");
        CmdEntity cmdEntity = (CmdEntity) GsonUtil
                .jsonToObject(semantic, CmdEntity.class);
        CmdSlots slots = cmdEntity.getSlots();
        return handleCmd(slots);
    }

    private boolean handleCmd(CmdSlots slots) {
        String type = slots.getCmd().getType();
        String option = slots.getCmd().getOption();
        switch (type) {
            case Constants.NAVIGATION:
                handleNavigationCmd(option);
                return true;
            case Constants.LUXIANG:
                handleVideoCmd(option);
                return true;
            case Constants.JIE:
                handleOrderCmd();
                return true;
            case Constants.SPEECH:
                handleExitCmd();
                return true;
            case Constants.EXIT:
                handleExitCmd();
                return true;
            case Constants.BACK:
                handleBackCmd();
                return true;
        }

        return false;
    }

    private void handleNavigationCmd(String option) {
        switch (option) {
            case Constants.OPEN:
            case Constants.START:

                break;
            case Constants.CLOSE:
            case Constants.EXIT:

                break;
        }
    }

    private void handleVideoCmd(String option) {
        switch (option) {
            case Constants.OPEN:
            case Constants.START:
                if (mApplication.getRecordService() != null) {
                    mApplication.getRecordService().startRecord();
                    mApplication.getRecordService().startRecordTimer();
                }

                Intent intent = new Intent();
                intent.setClass(mApplication, VideoActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mApplication.startActivity(intent);
                break;
            case Constants.CLOSE:
            case Constants.EXIT:
                ActivitiesManager.getInstance().closeTargetActivity(
                        VideoActivity.class);
                break;
        }
    }

    private void handleOrderCmd() {

    }

    private void handleBackCmd() {
        Activity topActivity = ActivitiesManager.getInstance()
                .getTopActivity();
        if (topActivity != null && !(topActivity instanceof MainActivity)) {
            topActivity.startActivity(new Intent(topActivity,
                    MainActivity.class));
        }
    }

    private void handleExitCmd() {

    }

}