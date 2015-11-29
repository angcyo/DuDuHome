package com.dudu.conn;

import android.content.Context;

import com.dudu.android.launcher.LauncherApplication;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.greenrobot.event.EventBus;

/**
 * Created by lxh on 2015/11/7.
 */
public class ConnectionResultHandler {

    private Context mContext;

    private Logger log;

    public ConnectionResultHandler(){
        mContext = LauncherApplication.getContext().getApplicationContext();
        log = LoggerFactory.getLogger("net.conn.mina");
    }

    public void init(){
        EventBus.getDefault().unregister(this);
        EventBus.getDefault().register(this);
    }

    public void onEventBackgroundThread(ConnectionEvent.ReceivedMessage event){
        try {
            JSONObject jsonResult = new JSONObject(event.getResultJson());

            if (jsonResult.has("result") && jsonResult.has("resultCode") && jsonResult.has("resultDesc")) {
                EventBus.getDefault().post(new ConnectionEvent.SendDatasResponse(jsonResult.getString("result"),
                        jsonResult.getString("resultCode"),jsonResult.getString("resultDesc")));
            }
            if (jsonResult.has("method")) {
                String method = jsonResult.getString("method");
                log.debug("method:{},resultCode{}",method,jsonResult.getString("resultCode"));
                switch (method){
                    case ConnMethod.METHOD_PORTALUPDATE:
                      new PortalHandler().handlerUpdate(mContext,method,jsonResult.getString("url"),jsonResult.getString("group"));
                        break;
                    case ConnMethod.METHOD_TAKEPHOTO:
                        EventBus.getDefault().post(new ConnectionEvent.TakePhoto(jsonResult.getString("openid")));
                        break;
                    case ConnMethod.METHOD_NAVI:
                        break;
                    case ConnMethod.METHOD_ACTIVEDEVICE:
                        ActiveDevice.getInstance(mContext).handlerActiveDeviceResult(jsonResult);
                        break;
                    case ConnMethod.METHOD_LOGBANC:
                        new SendLogs().logsSend(mContext, jsonResult.getString("url"));
                        break;
                    case ConnMethod.METHOD_ACTIVATAIONSTATUS:
                        ActiveDevice.getInstance(mContext).handlerCheckActive(jsonResult);
                        break;

                }
            }

        } catch (Exception e1) {
            e1.printStackTrace();
        }

    }

}
