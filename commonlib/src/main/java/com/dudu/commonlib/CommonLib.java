package com.dudu.commonlib;

import android.content.Context;

import com.dudu.commonlib.repo.CommonResouce;

/**
 * Created by dengjun on 2016/1/21.
 * Description :公共库聚合根,需要在application中初始化
 */
public class CommonLib {
    private CommonResouce commonResouce;

    public CommonLib() {
        commonResouce = new CommonResouce();
    }

    public void init(Context context){
        commonResouce.init(context);
    }

    public Context getContext(){
        return commonResouce.getContext();
    }

    public String getObeId(){
        return commonResouce.getObeId();
    }
}
