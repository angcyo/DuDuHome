package com.dudu.navi.Util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import com.dudu.navi.NavigationManager;
import com.dudu.navi.R;
import com.dudu.navi.vauleObject.OpenMode;

import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * Created by lxh on 2015/11/25.
 */
public class NaviUtils {

    public static OpenMode getOpenMode(Context context){

        String openModestr = context.getString(R.string.open_naviMode);
        switch (openModestr){
            case "1":
                return OpenMode.OUTSIDE;
            case "0":
                return OpenMode.INSIDE;
        }

        return OpenMode.OUTSIDE;
    }




    public static String parseIatResultNearby(String json) {
        try {
            JSONTokener tokener = new JSONTokener(json);
            JSONObject joResult = new JSONObject(tokener);
            joResult = joResult.optJSONObject("slots");
            if(joResult != null){
                joResult = joResult.optJSONObject("nearby");
                if(joResult != null){
                    return joResult.optString("type");
                }
            }
        } catch (Exception e) {
        }
        return null;
    }

}
