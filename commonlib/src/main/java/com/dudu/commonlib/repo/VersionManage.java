package com.dudu.commonlib.repo;

import android.content.Context;

import com.dudu.commonlib.utils.VersionTools;

/**
 * Created by dengjun on 2016/1/21.
 * Description :
 */
public class VersionManage {
    private boolean demoVersionFlag;

    public void init(Context context){
        initDemoVersionFlag(context);
    }

    private void initDemoVersionFlag(Context context){
        if (VersionTools.getAppVersion(context).contains("demo")){
            demoVersionFlag = true;
        }else {
            demoVersionFlag = false;
        }
    }

    public boolean isDemoVersionFlag() {
        return demoVersionFlag;
    }
}
