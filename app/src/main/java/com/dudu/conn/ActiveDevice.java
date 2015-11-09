package com.dudu.conn;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by pc on 2015/11/9.
 */
public class ActiveDevice {

    public static int ACTIVE_OK = 1;

    public static int UNACTIVE = 0;

    private static ActiveDevice activeDevice;

    private SharedPreferences sp;

    public ActiveDevice(Context context){
        sp = context.getSharedPreferences("ActiveDevice",Context.MODE_PRIVATE);
    }

    public static ActiveDevice getInstance(Context context){
        if(activeDevice==null)
            activeDevice = new ActiveDevice(context);

        return  activeDevice;
    }

    public  void setActiveFlag(int flag){
        if(sp!=null){
            sp.edit().putInt("activeFlag",flag).commit();
        }

    }

    public int getActiveFlag(){
        if(sp!=null){
            sp.getInt("activeFlag", 0);
        }
        return 0;
    }

    public void handlerActiveDeviceResult(JSONObject json){
        try {
            if(json.has("resultDesc")){
                if(json.getString("resultDesc").equals("激活成功"))
                    setActiveFlag(ACTIVE_OK);
            }
        }catch (JSONException e){

        }

    }
}
