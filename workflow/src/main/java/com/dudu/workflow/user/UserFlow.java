package com.dudu.workflow.user;

import com.dudu.commonlib.xml.SharedPreferencesUtils;

/**
 * Created by Administrator on 2016/2/16.
 */
public class UserFlow {

    private static final String USERNAME_KEY = "username_key";

    public static void saveUserName(String userName){
        SharedPreferencesUtils.setParam(USERNAME_KEY,userName);
    }

    public static String getUserName(){
        return SharedPreferencesUtils.getParam(USERNAME_KEY,"").toString();
    }
}
