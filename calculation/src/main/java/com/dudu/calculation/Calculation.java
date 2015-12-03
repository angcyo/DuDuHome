package com.dudu.calculation;

import android.content.Context;

import com.dudu.calculation.service.CalculateService;

/**
 * Created by dengjun on 2015/12/2.
 * Description : 此类管理应用计算，如驾驶行为分析，碰撞检测
 */
public class Calculation {
    private static  Calculation instance = null;
    private Context mContext;

    private CalculateService calculateService;

    public static  Calculation getInstance(Context context){
        if (instance == null){
            synchronized (Calculation.class){
                if (instance == null){
                    instance = new Calculation(context);
                }
            }
        }
        return instance;
    }

    public Calculation(Context context) {
        mContext = context;
        calculateService = new CalculateService(context);
    }

    public void init(){
        calculateService.startService();
    }

    public void release(){
        calculateService.stopService();
    }
}
