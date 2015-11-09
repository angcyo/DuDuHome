package com.dudu.conn;

import org.json.JSONObject;

import de.greenrobot.event.EventBus;

/**
 * Created by lxh on 2015/11/7.
 */
public class ConnectionResultHandler {



    public ConnectionResultHandler(){

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
                switch (method){

                    case ConnMethod.METHOD_PORTALUPDATE:
                        new PortalHandler().handlerUpdate(method,jsonResult.getString("url"),jsonResult.getString("group"));
                        break;
                    case ConnMethod.METHOD_TAKEPHOTO:
                        break;
                    case ConnMethod.METHOD_NAVI:
                        break;
                }
            }

        } catch (Exception e1) {
            e1.printStackTrace();
        }

    }

}
