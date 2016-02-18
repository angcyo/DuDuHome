package com.dudu.workflow;

import com.dudu.commonlib.utils.TestVerify;
import com.dudu.workflow.user.UserFlow;
import com.dudu.workflow.user.UserInfo;

/**
 * Created by Administrator on 2016/2/16.
 */
public class CommonParams {

    private static CommonParams mInstance = new CommonParams();

    private UserInfo userInfo = new UserInfo();

    public static CommonParams getInstance() {
        return mInstance;
    }

    public void init() {
        userInfo.setUserName(UserFlow.getUserName());
    }

    public String getUserName() {
        return TestVerify.isEmpty(userInfo.getUserName()) ? "13800138000" : userInfo.getUserName();
    }

    public void setUserName(String userName) {
        userInfo.setUserName(userName);
        UserFlow.saveUserName(userName);
    }
}

