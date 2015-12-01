package com.dudu.conn;

import android.content.Context;

import com.dudu.android.launcher.LauncherApplication;
import com.dudu.android.launcher.utils.LogUtils;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.greenrobot.event.EventBus;

/**
 * Created by lxh on 2015/11/7.
 */
public class ConnectionResultHandler {

    private Context mContext;

    public ConnectionResultHandler() {
        mContext = LauncherApplication.getContext();
    }

    public void init() {
        EventBus.getDefault().unregister(this);
        EventBus.getDefault().register(this);
    }

    public void onEventBackgroundThread(ConnectionEvent.ReceivedMessage event) {
        try {
            JSONObject jsonResult = new JSONObject(event.getResultJson());
            if (jsonResult.has("result") && jsonResult.has("resultCode") && jsonResult.has("resultDesc")) {
                EventBus.getDefault().post(new ConnectionEvent.SendDatasResponse(jsonResult.getString("result"),
                        jsonResult.getString("resultCode"), jsonResult.getString("resultDesc")));
            }

            if (jsonResult.has("method")) {
                String method = jsonResult.getString("method");
                switch (method) {
                    case ConnectionConstants.METHOD_PORTALUPDATE:
                        PortalUpdate.getInstance().handleUpdate(mContext, method,
                                jsonResult.getString("url"), jsonResult.getString("group_name"));
                        break;
                    case ConnectionConstants.METHOD_TAKEPHOTO:
                        EventBus.getDefault().post(new ConnectionEvent.TakePhoto(jsonResult.getString("openid")));
                        break;
                    case ConnectionConstants.METHOD_NAVI:
                        break;
                    case ConnectionConstants.METHOD_ACTIVEDEVICE:
                        ActiveDevice.getInstance(mContext).handlerActiveDeviceResult(jsonResult);
                        break;
                    case ConnectionConstants.METHOD_LOGBANC:
                        new SendLogs().logsSend(mContext, jsonResult.getString("url"));
                        break;
                    case ConnectionConstants.METHOD_ACTIVATAIONSTATUS:
                        ActiveDevice.getInstance(mContext).handlerCheckActive(jsonResult);
                        break;
                    case ConnectionConstants.METHOD_GETFLOW:
                        FlowMonitor.getInstance().onRemainingFlowResult(jsonResult);
                        break;
                    case ConnectionConstants.METHOD_SYNCONFIGURATION:
                        FlowMonitor.getInstance().onFlowConfigurationResult(jsonResult);
                        break;
                    case ConnectionConstants.METHOD_SWITCHFLOW:
                        FlowMonitor.getInstance().onSwitchFlowResult(jsonResult);
                        break;
                    case ConnectionConstants.METHOD_DATAOVERSTEPALARM:

                        break;
                }
            }
        } catch (Exception e) {
            LogUtils.e("ConnectionResultHandler", e.getMessage() + "");
        }
    }

}
