package com.dudu.voice.semantic.chain;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;

import com.dudu.android.launcher.LauncherApplication;
import com.dudu.android.launcher.bean.CmdEntity;
import com.dudu.android.launcher.bean.CmdSlots;
import com.dudu.android.launcher.ui.activity.MainActivity;
import com.dudu.android.launcher.ui.activity.OBDCheckingActivity;
import com.dudu.android.launcher.ui.activity.video.VideoActivity;
import com.dudu.android.launcher.ui.activity.video.VideoListActivity;
import com.dudu.android.launcher.utils.ActivitiesManager;
import com.dudu.android.launcher.utils.Constants;
import com.dudu.android.launcher.utils.FloatWindowUtil;
import com.dudu.android.launcher.utils.GsonUtil;
import com.dudu.android.launcher.utils.JsonUtils;
import com.dudu.android.launcher.utils.ToastUtils;
import com.dudu.android.launcher.utils.Utils;
import com.dudu.map.NavigationClerk;
import com.dudu.voice.semantic.SemanticConstants;
import com.dudu.voice.semantic.SemanticType;
import com.dudu.voice.semantic.engine.SemanticProcessor;

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
        if (type == null) {
            type = option;
        }

        if (type.contains(Constants.NAVIGATION)) {
            return handleNavigationCmd(option);
        } else if (type.contains(Constants.LUXIANG) ||
                type.contains(Constants.CAMERA)) {
            handleVideoCmd(option);
            return true;
        } else if (type.contains(Constants.JIE)) {
            handleOrderCmd();
            return true;
        } else if (type.contains(Constants.SPEECH)) {
            handleExitCmd();
            return true;
        } else if (type.contains(Constants.EXIT)) {
            handleExitCmd();
            return true;
        } else if (type.contains(Constants.BACK)) {
            handleBackCmd();
            return true;
        } else if (type.contains(Constants.CAR_RECORD)) {
            handleCarRecord(option);
            return true;
        } else if (type.contains(Constants.MAP)) {
            return handleMapCmd(option);
        }

        return false;
    }

    private boolean handleNavigationCmd(String option) {
        switch (option) {
            case Constants.OPEN:
            case Constants.START:
                return NavigationClerk.getInstance().openNavi(NavigationClerk.OPEN_VOICE);
            case Constants.CLOSE:
            case Constants.EXIT:
                FloatWindowUtil.removeFloatWindow();
                NavigationClerk.getInstance().existNavi();
                break;
        }
        return true;
    }

    private void handleVideoCmd(String option) {
        FloatWindowUtil.removeFloatWindow();
        switch (option) {
            case Constants.OPEN:
            case Constants.START:
            case Constants.KAIQI:
                if (mApplication.getRecordService() != null) {
                    mApplication.getRecordService().startRecord();
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
        FloatWindowUtil.removeFloatWindow();
        Utils.openJD(mApplication);
    }

    private void handleBackCmd() {
        FloatWindowUtil.removeFloatWindow();
        if (mApplication.isReceivingOrder()) {
            mApplication.setReceivingOrder(false);
            Intent intent = new Intent(mApplication, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mApplication.startActivity(intent);
        }
        Activity topActivity = ActivitiesManager.getInstance()
                .getTopActivity();
        if (topActivity != null && !(topActivity instanceof MainActivity)) {
            if (topActivity instanceof OBDCheckingActivity) {
                mVoiceManager.setShowMessageWindow(true);
            }

            topActivity.startActivity(new Intent(topActivity,
                    MainActivity.class));
        }
    }

    private void handleExitCmd() {
        FloatWindowUtil.removeFloatWindow();
        SemanticProcessor.getProcessor().clearSemanticStack();
        SemanticProcessor.getProcessor().switchSemanticType(SemanticType.NORMAL);
    }

    private void handleCarRecord(String option) {
        FloatWindowUtil.removeFloatWindow();
        switch (option) {
            case Constants.OPEN:
            case Constants.START:
            case Constants.KAIQI:
                Intent intent = new Intent();
                intent.setClass(mApplication, VideoListActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mApplication.startActivity(intent);
                break;
            case Constants.CLOSE:
            case Constants.EXIT:
                ActivitiesManager.getInstance().closeTargetActivity(
                        VideoListActivity.class);
                break;
        }
    }

    private boolean handleMapCmd(String option) {
        FloatWindowUtil.removeFloatWindow();
        switch (option) {
            case Constants.OPEN:
            case Constants.START:
            case Constants.KAIQI:
                if (NavigationClerk.getInstance().openNavi(NavigationClerk.OPEN_MAP)) {
                    FloatWindowUtil.removeFloatWindow();
                    return true;
                }
                return false;
            case Constants.CLOSE:
            case Constants.EXIT:
                NavigationClerk.getInstance().closeMap();
                break;
        }
        return true;
    }

}
