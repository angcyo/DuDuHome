package com.dudu.conn;

import android.content.Context;

import android.text.TextUtils;
import android.util.Log;

import com.dudu.android.launcher.LauncherApplication;
import com.dudu.android.launcher.utils.Constants;
import com.dudu.android.launcher.utils.SharedPreferencesUtil;

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

    public void onRemainingFlowResult(JSONObject result) throws JSONException {
        String resultCode = result.getString(ConnectionConstants.FIELD_RESULT_CODE);
        if (ConnectionConstants.RESULT_CODE_SUCCESS.equals(
                resultCode)) {

        }
    }

    public void onFlowConfigurationResult(JSONObject result) throws JSONException {
        String resultCode = result.getString(ConnectionConstants.FIELD_RESULT_CODE);
        if (ConnectionConstants.RESULT_CODE_SUCCESS.equals(
                resultCode)) {
            if (!result.isNull(ConnectionConstants.FIELD_RESULT)) {
                JSONObject object = result.getJSONObject(ConnectionConstants.FIELD_RESULT);

                String portalVersion = object.getString(ConnectionConstants.FIELD_PORTAL_VERSION);
                String portalAddress = object.getString(ConnectionConstants.FIELD_POTAL_ADDRESS);
                if (!TextUtils.isEmpty(portalVersion)) {
                    PortalUpdate.getInstance().updatePortal(mContext, portalVersion, portalAddress);
                }

                putStringValue(Constants.KEY_TRAFFICE_CONTROL,
                        object.getString(ConnectionConstants.FIELD_TRAFFICE_CONTROL));

                putStringValue(Constants.KEY_MONTH_MAX_VALUE,
                        object.getString(ConnectionConstants.FIELD_MONTH_MAX_VALUE));

                putStringValue(Constants.KEY_FREE_ADD_VALUE,
                        object.getString(ConnectionConstants.FIELD_FREE_ADD_VALUE));

                putStringValue(Constants.KEY_DAILY_MAX_VALUE,
                        object.getString(ConnectionConstants.FIELD_DAILY_MAX_VALUE));

                putStringValue(Constants.KEY_UP_LIMIT_MAX_VALUE,
                        object.getString(ConnectionConstants.FIELD_UP_LIMIT_MAX_VALUE));

                putStringValue(Constants.KEY_POTAL_ADDRESS,
                        object.getString(ConnectionConstants.FIELD_POTAL_ADDRESS));

                putStringValue(Constants.KEY_DOWN_LIMIT_MAX_VALUE,
                        object.getString(ConnectionConstants.FIELD_DOWN_LIMIT_MAX_VALUE));

                putStringValue(Constants.KEY_LIFE_TYPE,
                        object.getString(ConnectionConstants.FIELD_LIFE_TYPE));

                putStringValue(Constants.KEY_UPLOAD_LIMIT,
                        object.getString(ConnectionConstants.FIELD_UPLOAD_LIMIT));

                putStringValue(Constants.KEY_FREE_ADD_TIMES,
                        object.getString(ConnectionConstants.FIELD_FREE_ADD_TIMES));

                putStringValue(Constants.KEY_REMAINING_FLOW,
                        object.getString(ConnectionConstants.FIELD_REMAINING_FLOW));

                putStringValue(Constants.KEY_MONTH_MAX_VALUE,
                        object.getString(ConnectionConstants.FIELD_MONTH_MAX_VALUE));

                putStringValue(Constants.KEY_MIDDLE_ARLAM_VALUE,
                        object.getString(ConnectionConstants.FIELD_MIDDLE_ARLAM_VALUE));

                putStringValue(Constants.KEY_HIGH_ARLAM_VALUE,
                        object.getString(ConnectionConstants.FIELD_HIGH_ARLAM_VALUE));

                putStringValue(Constants.KEY_LOW_ARLAM_VALUE,
                        object.getString(ConnectionConstants.FIELD_LOW_ARLAM_VALUE));

                putStringValue(Constants.KEY_DOWNLOAD_LIMIT,
                        object.getString(ConnectionConstants.FIELD_DOWNLOAD_LIMIT));

                putStringValue(Constants.KEY_FREE_ARRIVE_VALUE,
                        object.getString(ConnectionConstants.FIELD_FREE_ARRIVE_VALUE));
            }
        }
    }

    private void putStringValue(String key, String value) {
        if (!TextUtils.isEmpty(value)) {
            SharedPreferencesUtil.putStringValue(mContext, key, value);
        }
    }

}
