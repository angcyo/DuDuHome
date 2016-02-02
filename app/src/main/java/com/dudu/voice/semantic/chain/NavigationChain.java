package com.dudu.voice.semantic.chain;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.dudu.android.launcher.LauncherApplication;
import com.dudu.android.launcher.ui.activity.NaviCustomActivity;
import com.dudu.android.launcher.utils.ActivitiesManager;
import com.dudu.android.launcher.utils.Constants;
import com.dudu.navi.NavigationManager;
import com.dudu.voice.semantic.bean.SemanticBean;
import com.dudu.voice.semantic.bean.map.NavigationBean;
import com.dudu.voice.semantic.constant.SemanticConstant;

/**
 * Created by Administrator on 2015/10/28.
 */
public class NavigationChain extends SemanticChain {

    private Activity mActivity = null;

    private Bundle mBundle = null;

    @Override
    public boolean matchSemantic(String service) {
        return service.equalsIgnoreCase(SemanticConstant.SERVICE_NAVI);
    }

    @Override
    public boolean doSemantic(SemanticBean semantic) {
        String operation = ((NavigationBean) semantic).getOperation();

        if (NavigationManager.getInstance(LauncherApplication.getContext()).isNavigatining()) {
            mActivity = ActivitiesManager
                    .getInstance().getTopActivity();
            switch (operation) {
                case "打开":
                    openTraffic();
                    break;
                case "退出":
                case "关闭":
                    closeTraffic();
                    break;
                default:
                    return false;
            }

            return true;
        }

        return false;
    }

    private void openTraffic() {
        if (mActivity != null
                && mActivity instanceof NaviCustomActivity) {
            ((NaviCustomActivity) mActivity)
                    .trafficInfo();
        } else {
            Intent intent = new Intent();
            intent.setClass(mActivity,
                    NaviCustomActivity.class);
            mBundle.putString("type",
                    Constants.NAVI_TRAFFIC);
            intent.putExtras(mBundle);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mActivity.startActivity(intent);
        }

    }

    private void closeTraffic() {
        if (mActivity != null
                && mActivity instanceof NaviCustomActivity) {
            ((NaviCustomActivity) mActivity)
                    .closeTraffic();
        } else {
            Intent intent = new Intent();
            intent.setClass(mActivity,
                    NaviCustomActivity.class);
            mBundle.putString("type",
                    Constants.CLOSE + Constants.NAVI_TRAFFIC);
            intent.putExtras(mBundle);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mActivity.startActivity(intent);
        }
    }

}
