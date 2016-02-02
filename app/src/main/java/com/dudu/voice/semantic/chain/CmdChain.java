package com.dudu.voice.semantic.chain;

import android.app.Activity;
import android.content.Intent;

import com.dudu.android.launcher.ui.activity.MainActivity;
import com.dudu.android.launcher.ui.activity.OBDCheckingActivity;
import com.dudu.android.launcher.ui.activity.video.VideoActivity;
import com.dudu.android.launcher.utils.ActivitiesManager;
import com.dudu.android.launcher.utils.Constants;
import com.dudu.android.launcher.utils.FloatWindowUtils;
import com.dudu.map.NavigationProxy;
import com.dudu.voice.semantic.bean.CmdBean;
import com.dudu.voice.semantic.bean.SemanticBean;
import com.dudu.voice.semantic.constant.SceneType;
import com.dudu.voice.semantic.constant.SemanticConstant;
import com.dudu.voice.semantic.engine.SemanticEngine;

/**
 * Created by 赵圣琪 on 2015/10/29.
 */
public class CmdChain extends SemanticChain {

    @Override
    public boolean matchSemantic(String service) {
        return SemanticConstant.SERVICE_CMD.equals(service);
    }

    @Override
    public boolean doSemantic(SemanticBean semantic) {
        return handleCmd((CmdBean) semantic);
    }

    private boolean handleCmd(CmdBean bean) {
        String action = bean.getAction();
        String target = bean.getTarget();

        if (target == null) {

        } else {
            if (target.contains(Constants.NAVIGATION)) {
                return handleNavigationCmd(action);
            } else if (target.contains(SemanticConstant.RECORD_CN)) {
                handleVideoCmd(action);
                return true;
            } else if (target.contains(Constants.SPEECH)) {
                handleExitCmd();
                return true;
            } else if (target.contains(Constants.EXIT)) {
                handleExitCmd();
                return true;
            } else if (target.contains(Constants.BACK)) {
                handleBackCmd();
                return true;
            } else if (target.contains(Constants.MAP)) {
                return handleMapCmd(action);
            } else if (target.contains(Constants.SELF_CHECKING)) {
                return handleSelfChecking(action);
            }
        }

        return false;
    }

    private boolean handleNavigationCmd(String option) {
        switch (option) {
            case Constants.OPEN:
            case Constants.START:
                return NavigationProxy.getInstance().openNavi(NavigationProxy.OPEN_VOICE);
            case Constants.CLOSE:
            case Constants.EXIT:
                FloatWindowUtils.removeFloatWindow();
                NavigationProxy.getInstance().existNavi();
                break;
        }
        return true;
    }

    private void handleVideoCmd(String option) {
        FloatWindowUtils.removeFloatWindow();
        switch (option) {
            case Constants.OPEN:
            case Constants.QIDONG:
            case Constants.KAIQI:
                Intent intent = new Intent();
                intent.setClass(mContext, VideoActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
                break;
            case Constants.CLOSE:
            case Constants.EXIT:
            case Constants.GUANDIAO:
                ActivitiesManager.getInstance().closeTargetActivity(
                        VideoActivity.class);
                break;
        }
    }

    private void handleBackCmd() {
        FloatWindowUtils.removeFloatWindow();

        Activity topActivity = ActivitiesManager.getInstance()
                .getTopActivity();
        if (topActivity != null && !(topActivity instanceof MainActivity)) {
            Intent intent = new Intent(topActivity,
                    MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            topActivity.startActivity(intent);
        }
    }

    private void handleExitCmd() {
        FloatWindowUtils.removeFloatWindow();
        SemanticEngine.getProcessor().clearSemanticStack();
        SemanticEngine.getProcessor().switchSemanticType(SceneType.HOME);
    }

    private boolean handleMapCmd(String option) {
        FloatWindowUtils.removeFloatWindow();
        switch (option) {
            case Constants.OPEN:
            case Constants.START:
            case Constants.KAIQI:
                if (NavigationProxy.getInstance().openNavi(NavigationProxy.OPEN_MAP)) {
                    FloatWindowUtils.removeFloatWindow();
                    return true;
                }
                return false;
            case Constants.CLOSE:
            case Constants.EXIT:
                NavigationProxy.getInstance().closeMap();
                break;
        }
        return true;
    }

    private boolean handleSelfChecking(String action) {
        switch (action) {
            case Constants.OPEN:
            case Constants.QIDONG:
            case Constants.KAIQI:
                FloatWindowUtils.removeFloatWindow();
                Intent intent = new Intent(mContext, OBDCheckingActivity.class);
//                Intent intent=new Intent(mContext,CarCheckActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
                break;
            case Constants.CLOSE:
            case Constants.EXIT:
            case Constants.GUANDIAO:
                FloatWindowUtils.removeFloatWindow();

                ActivitiesManager.getInstance().closeTargetActivity(OBDCheckingActivity.class);
                break;
            default:
                return false;
        }

        return true;
    }

}
