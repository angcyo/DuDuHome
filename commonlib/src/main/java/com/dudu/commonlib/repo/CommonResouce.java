package com.dudu.commonlib.repo;

import android.content.Context;

import com.dudu.commonlib.utils.DeviceIDUtil;

/**
 * Created by dengjun on 2016/1/21.
 * Description :公共资源，此类值保存应用开始运行后，保持不变的属性或其他的
 *                      对于在程序运行中需要改变的，不要放在这个类中
 */
public class CommonResouce {
    private Context context;
    private String obeId;

    public void init(Context context){
        this.context = context;
        obeId = DeviceIDUtil.getIMEI(context);
    }

    public Context getContext() {
        return context;
    }

    public String getObeId() {
        return obeId;
    }
}
