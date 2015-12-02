package com.dudu.conn;

import android.content.Context;

import android.text.TextUtils;
import android.util.Log;

import com.dudu.android.launcher.LauncherApplication;
import com.dudu.android.launcher.utils.Constants;
import com.dudu.android.launcher.utils.SharedPreferencesUtil;
import com.dudu.android.launcher.utils.WifiApAdmin;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by 赵圣琪 on 2015/11/28.
 */
public class FlowMonitor {

    private static FlowMonitor mInstance;

    private SendMessage mMessageSender;

    private Context mContext;

    private FlowMonitor() {
        mContext = LauncherApplication.getContext();

        mMessageSender = SendMessage.getInstance(mContext);
    }

    public static FlowMonitor getInstance() {
        if (mInstance == null) {
            mInstance = new FlowMonitor();
        }

        return mInstance;
    }

    public void queryFlowInfo() {
        mMessageSender.queryFlowInfo();
    }

    public void sendFlowMessage(float usedFlow, String time) {
        mMessageSender.sendFlowDatas(usedFlow, time);
    }

    public void synFlowConfiguration() {
        mMessageSender.synConfiguration();
    }

    public void onSwitchFlowResult(JSONObject result) throws JSONException {

        if (result.isNull(ConnectionConstants.FIELD_TRAFFIC_CONTROL)) {
            switch (result.getInt(ConnectionConstants.FIELD_TRAFFIC_CONTROL)) {
                case ConnectionConstants.RESULT_TRAFFIC_CONTROL_CLOSE:
                    WifiApAdmin.closeWifiAp(mContext);
                    break;

                case ConnectionConstants.RESULT_TRAFFIC_CONTROL_OPEN:
                    WifiApAdmin.initWifiApState(mContext);
                    break;
            }
        }
    }

    public void onRemainingFlowResult(JSONObject result) throws JSONException {
        String resultCode = result.getString(ConnectionConstants.FIELD_RESULT_CODE);
        if (ConnectionConstants.RESULT_CODE_SUCCESS.equals(
                resultCode)) {
            if (!result.isNull(ConnectionConstants.FIELD_RESULT)) {
                JSONObject object=result.getJSONObject(ConnectionConstants.FIELD_RESULT);
                putStringValue(Constants.KEY_REMAINING_FLOW,
                        ConnectionConstants.FIELD_REMAINING_FLOW, object);
            }
        }
    }

    public void onFlowConfigurationResult(JSONObject result) throws JSONException {
        String resultCode = result.getString(ConnectionConstants.FIELD_RESULT_CODE);
        if (ConnectionConstants.RESULT_CODE_SUCCESS.equals(
                resultCode)) {
            if (!result.isNull(ConnectionConstants.FIELD_RESULT)) {

                JSONObject object = result.getJSONObject(ConnectionConstants.FIELD_RESULT);

                if (!object.isNull(ConnectionConstants.FIELD_PORTAL_VERSION) &&
                        !object.isNull(ConnectionConstants.FIELD_PORTAL_ADDRESS)) {
                    String portalVersion = object.getString(ConnectionConstants.FIELD_PORTAL_VERSION);
                    String portalAddress = object.getString(ConnectionConstants.FIELD_PORTAL_ADDRESS);
                    if (!TextUtils.isEmpty(portalVersion)) {
                        PortalUpdate.getInstance().updatePortal(mContext, portalVersion, portalAddress);
                    }
                }

                putStringValue(Constants.KEY_TRAFFICE_CONTROL,
                        ConnectionConstants.FIELD_TRAFFIC_CONTROL, object);

                putStringValue(Constants.KEY_MONTH_MAX_VALUE,
                        ConnectionConstants.FIELD_MONTH_MAX_VALUE, object);

                putStringValue(Constants.KEY_FREE_ADD_VALUE,
                        ConnectionConstants.FIELD_FREE_ADD_VALUE, object);

                putStringValue(Constants.KEY_DAILY_MAX_VALUE,
                        ConnectionConstants.FIELD_DAILY_MAX_VALUE, object);

                putStringValue(Constants.KEY_UP_LIMIT_MAX_VALUE,
                        object.getString(ConnectionConstants.FIELD_UP_LIMIT_MAX_VALUE), object);

                putStringValue(Constants.KEY_PORTAL_ADDRESS,
                        ConnectionConstants.FIELD_PORTAL_ADDRESS, object);

                putStringValue(Constants.KEY_DOWN_LIMIT_MAX_VALUE,
                        ConnectionConstants.FIELD_DOWN_LIMIT_MAX_VALUE, object);

                putStringValue(Constants.KEY_LIFE_TYPE,
                        ConnectionConstants.FIELD_LIFE_TYPE, object);

                putStringValue(Constants.KEY_UPLOAD_LIMIT,
                        ConnectionConstants.FIELD_UPLOAD_LIMIT, object);

                putStringValue(Constants.KEY_FREE_ADD_TIMES,
                        ConnectionConstants.FIELD_FREE_ADD_TIMES, object);

                putStringValue(Constants.KEY_REMAINING_FLOW,
                        ConnectionConstants.FIELD_REMAINING_FLOW, object);

                putStringValue(Constants.KEY_MONTH_MAX_VALUE,
                        object.getString(ConnectionConstants.FIELD_MONTH_MAX_VALUE), object);

                putStringValue(Constants.KEY_MIDDLE_ARLAM_VALUE,
                        ConnectionConstants.FIELD_MIDDLE_ARLAM_VALUE, object);

                putStringValue(Constants.KEY_HIGH_ARLAM_VALUE,
                        ConnectionConstants.FIELD_HIGH_ARLAM_VALUE, object);

                putStringValue(Constants.KEY_LOW_ARLAM_VALUE,
                        ConnectionConstants.FIELD_LOW_ARLAM_VALUE, object);

                putStringValue(Constants.KEY_DOWNLOAD_LIMIT,
                        ConnectionConstants.FIELD_DOWNLOAD_LIMIT, object);

                putStringValue(Constants.KEY_FREE_ARRIVE_VALUE,
                        ConnectionConstants.FIELD_FREE_ARRIVE_VALUE, object);
            }
        }
    }

    private void putStringValue(String key, String field, JSONObject object)
            throws JSONException {
        if (object.isNull(field)) {
            return;
        }

        String value = object.getString(field);
        if (!TextUtils.isEmpty(value)) {
            SharedPreferencesUtil.putStringValue(mContext, key, value);
        }
    }

    public void dataOverstepAlarm(JSONObject result) throws JSONException {
        if (result.isNull(ConnectionConstants.FIELD_ALARM_LEVEL)) {
            switch (result.getInt(ConnectionConstants.FIELD_ALARM_LEVEL)) {
                case ConnectionConstants.FIELD_ALARM_LEVEL_OPEN:
                    //   0:正常
                    break;
                case ConnectionConstants.FIELD_ALARM_LEVEL_ADVANCED_WARNING:
                    //   1:高级预警   当前剩余值>最大阀值95%
                    break;
                case ConnectionConstants.FIELD_ALARM_LEVEL_INTERMEDIATE_WARNING:
                    //   2:中级预警   当前剩余值>最大阀值90%
                    break;
                case ConnectionConstants.FIELD_ALARM_LEVEL_LOWLEVEL_WARNING:
                    //   3:低级预警   当前剩余值>最大阀值80%
                    break;
                case ConnectionConstants.FIELD_ALARM_LEVEL_CLOSE:
                    //   4:关闭
                    break;
            }
        }
    }

    public void dataExceptionAlarm(JSONObject result) throws JSONException {
        if (result.isNull(ConnectionConstants.FIELD_ALARM_LEVEL)) {
            switch (result.getInt(ConnectionConstants.FIELD_ALARM_LEVEL)) {
                case ConnectionConstants.FIELD_ALARM_LEVEL_OPEN:
                    //   0:正常
                    break;
                case 1:
                    //   1:关闭（后台说的是关闭）
                    break;
            }

        }
    }
}