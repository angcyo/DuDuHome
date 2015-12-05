package com.duu.bluetooth;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by lxh on 2015/12/5.
 */
public class BluetoothMacUtil {

    private static String MAC_ADDRESS = "MAC_ADDRESS";


    public static void saveMac(Context context,String mac){

        SharedPreferences sharedPreferences = context.getSharedPreferences(MAC_ADDRESS,Context.MODE_PRIVATE);
        sharedPreferences.edit().putString(MAC_ADDRESS,mac).commit();
    }

    public static String getMac(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(MAC_ADDRESS,Context.MODE_PRIVATE);
        return sharedPreferences.getString(MAC_ADDRESS,"");
    }
}
