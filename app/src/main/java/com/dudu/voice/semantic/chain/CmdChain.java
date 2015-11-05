package com.dudu.voice.semantic.chain;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;

import com.dudu.android.launcher.LauncherApplication;
import com.dudu.android.launcher.bean.CmdEntity;
import com.dudu.android.launcher.bean.CmdSlots;
import com.dudu.android.launcher.ui.activity.LocationActivity;
import com.dudu.android.launcher.ui.activity.LocationMapActivity;
import com.dudu.android.launcher.ui.activity.MainActivity;
import com.dudu.android.launcher.ui.activity.NaviBackActivity;
import com.dudu.android.launcher.ui.activity.NaviCustomActivity;
import com.dudu.android.launcher.ui.activity.OBDCheckingActivity;
import com.dudu.android.launcher.ui.activity.video.VideoActivity;
import com.dudu.android.launcher.utils.ActivitiesManager;
import com.dudu.android.launcher.utils.Constants;
import com.dudu.android.launcher.utils.FloatWindowUtil;
import com.dudu.android.launcher.utils.GsonUtil;
import com.dudu.android.launcher.utils.JsonUtils;
import com.dudu.android.launcher.utils.ToastUtils;
import com.dudu.map.MapManager;
import com.dudu.voice.semantic.SemanticConstants;
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
        if (type == null) {
            type = option;
        }

        if (type.contains(Constants.NAVIGATION)) {
            handleNavigationCmd(option);
            return true;
        } else if (type.contains(Constants.LUXIANG)) {
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
        }

        return false;
    }

    private void handleNavigationCmd(String option) {
        switch (option) {
            case Constants.OPEN:
            case Constants.START:
                Intent intent = new Intent();
                if (MapManager.getInstance().isNavi()) {
                    intent.setClass(mApplication, NaviCustomActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mApplication.startActivity(intent);
                } else if (MapManager.getInstance().isNaviBack()) {
                    intent.setClass(mApplication, NaviBackActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mApplication.startActivity(intent);
                } else {
                    MapManager.getInstance().mapControl(null, null, MapManager.SEARCH_NAVI);
                }
                break;
            case Constants.CLOSE:
            case Constants.EXIT:
                ActivitiesManager.getInstance().closeTargetActivity(
                        NaviCustomActivity.class);
                ActivitiesManager.getInstance().closeTargetActivity(
                        LocationMapActivity.class);
                ActivitiesManager.getInstance().closeTargetActivity(
                        NaviBackActivity.class);
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
        Intent intent;
        PackageManager packageManager = mApplication.getPackageManager();
        intent = packageManager.getLaunchIntentForPackage("com.sdu.didi.gsui");
        if (intent != null) {
            mApplication.setReceivingOrder(true);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
                    | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            mApplication.startActivity(intent);
        } else {
            ToastUtils.showToast("您还没安装滴滴客户端，请先安装滴滴出行客户端");
        }
    }

    private void handleBackCmd() {
        if (mApplication.isReceivingOrder()) {
            mApplication.setReceivingOrder(false);
            Intent intent = new Intent(mApplication, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mApplication.startActivity(intent);
        }


        Activity topActivity = ActivitiesManager.getInstance()
                .getTopActivity();
        if (topActivity != null && !(topActivity instanceof MainActivity)) {
            if (topActivity instanceof  OBDCheckingActivity) {
                mVoiceManager.setShowMessageWindow(true);
            }

            topActivity.startActivity(new Intent(topActivity,
                    MainActivity.class));
        }
    }

    private void handleExitCmd() {
        SemanticProcessor.getProcessor().clearSemanticStack();
    }

}
