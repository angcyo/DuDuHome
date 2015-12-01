package com.dudu.voice.semantic.chain;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.dudu.android.launcher.LauncherApplication;
import com.dudu.android.launcher.ui.activity.NaviCustomActivity;
import com.dudu.android.launcher.utils.ActivitiesManager;
import com.dudu.android.launcher.utils.Constants;
import com.dudu.android.launcher.utils.JsonUtils;
import com.dudu.navi.NavigationManager;
import com.dudu.navi.vauleObject.NavigationType;
import com.dudu.voice.semantic.SemanticConstants;

import org.json.JSONObject;

/**
 * Created by Administrator on 2015/10/28.
 */
public class NavigationChain extends SemanticChain {

    private Activity mContext = null;
    
    private String optionType = "";

    private String actionType = "";

    private JSONObject semantic;

    private JSONObject slotsObject;

    private  Bundle mBundle = null;

    private boolean isNavi  = false;

    @Override
    public boolean matchSemantic(String service) {
        return service.equalsIgnoreCase(SemanticConstants.SERVICE_NAVI);
    }

    @Override
    public boolean doSemantic(String json) {
        NavigationType type = NavigationManager.getInstance(LauncherApplication.getContext()).getNavigationType();
        if(type!=NavigationType.DEFAULT){
            mContext = ActivitiesManager
                    .getInstance().getTopActivity();
            mBundle = new Bundle();
            getActionType(json);
           if(!TextUtils.isEmpty(actionType)){
                   switch (actionType) {
                       case Constants.OPEN:
                           handleOpenAction();
                           break;
                       case Constants.NAVI_LISTEN:
                           handleListen();
                           break;
                       case Constants.CLOSE:
                       case Constants.EXIT:
                          handleCloseAction();
                           break;
                       case Constants.NAVI_LOOK:
                           handleLookAction();
                           break;
                   }

           } else {

               switch (optionType) {
                   case Constants.NAVI_PREVIEW:
                       openPriview();
                       break;
                   case Constants.NAVI_TRAFFIC:
                   case Constants.REALTIME_TRAFFIC:
                   case Constants.NAVI_TRAFFIC_BROADCAST:
                       openTraffic();
                       break;
//                   case Constants.RERURN_JOURNEY:
//                       goBack();
//                       break;
               }

           }

            return  true;
        }

        return false;
    }

    private void handleOpenAction(){
        switch (optionType) {
            case Constants.NAVI_PREVIEW:
                openPriview();
                break;
            case Constants.NAVI_TRAFFIC:
            case Constants.REALTIME_TRAFFIC:
            case Constants.NAVI_TRAFFIC_BROADCAST:
                openTraffic();
                break;
//            case Constants.RERURN_JOURNEY:
//                goBack();
//                break;
        }
    }

    private void handleCloseAction(){

        switch (optionType) {
            case Constants.NAVI_TRAFFIC:
            case Constants.REALTIME_TRAFFIC:
            case Constants.NAVI_TRAFFIC_BROADCAST:
                closeTraffic();
                break;
            case Constants.NAVI_PREVIEW:
                closePriview();
                break;
            default:
                break;
        }
    }

    private void handleLookAction(){
        switch (optionType) {
            case Constants.NAVI_PREVIEW:
                openPriview();
                break;
            case Constants.NAVI_TRAFFIC:
            case Constants.REALTIME_TRAFFIC:
                openTraffic();
                break;
            default:
                break;
        }
    }

    private void handleListen(){
        switch (optionType) {
            case Constants.NAVI_PREVIEW:
                openPriview();
                break;
            case Constants.NAVI_TRAFFIC:
            case Constants.REALTIME_TRAFFIC:
                openTraffic();
                break;
            default:
                break;
        }
    }
    private String getActionType(String json){
        try {
            semantic = new JSONObject(JsonUtils.parseIatResult(json,
                    "semantic"));

            slotsObject = semantic
                    .getJSONObject("slots");
            optionType = slotsObject.getJSONObject(
                    "option").getString("type");
            mContext = ActivitiesManager
                    .getInstance().getTopActivity();

            if(slotsObject.has("action")){

                actionType = slotsObject.getJSONObject(
                        "action").getString("type");
            }

        }catch (Exception e){
            e.printStackTrace();
            actionType = "";
        }
        return  actionType;
    }


    // 打开路况
    private void openTraffic(){
        if (mContext != null
                    && mContext instanceof NaviCustomActivity) {
                ((NaviCustomActivity) mContext)
                        .trafficInfo();
             } else {
                Intent intent = new Intent();
                intent.setClass(mContext,
                        NaviCustomActivity.class);
                mBundle.putString("type",
                        Constants.NAVI_TRAFFIC);
                intent.putExtras(mBundle);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
        }

    }
    // 关闭路况
    private void closeTraffic(){

            if (mContext != null
                    && mContext instanceof NaviCustomActivity) {
                ((NaviCustomActivity) mContext)
                        .closeTraffic();
            } else {
                Intent intent = new Intent();
                intent.setClass(mContext,
                        NaviCustomActivity.class);
                mBundle.putString("type",
                        Constants.CLOSE+Constants.NAVI_TRAFFIC);
                intent.putExtras(mBundle);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            }
    }
    // 全程预览
    private void openPriview(){
            if (mContext != null
                    && mContext instanceof NaviCustomActivity) {
                ((NaviCustomActivity) mContext)
                        .mapPriview();
            } else {
                Intent intent = new Intent();
                intent.setClass(mContext,
                        NaviCustomActivity.class);
                mBundle.putString("type",
                        Constants.NAVI_PREVIEW);
                intent.putExtras(mBundle);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            }
    }

    private void closePriview(){
            if (mContext != null
                    && mContext instanceof NaviCustomActivity) {
                ((NaviCustomActivity) mContext)
                        .closePriview();
            } else {
                Intent intent = new Intent();
                intent.setClass(mContext,
                        NaviCustomActivity.class);
                mBundle.putString("type",
                        Constants.CLOSE+Constants.NAVI_PREVIEW);
                intent.putExtras(mBundle);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            }

    }
    private void goBack(){

            if (mContext != null
                    && mContext instanceof NaviCustomActivity) {
                ((NaviCustomActivity) mContext)
                        .goBack();
            } else {
                Intent intent = new Intent();
                intent.setClass(mContext,
                        NaviCustomActivity.class);
                mBundle.putString("type",
                        Constants.RERURN_JOURNEY);
                intent.putExtras(mBundle);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            }

    }
}
