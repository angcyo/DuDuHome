package com.dudu.navi;

import android.content.Context;

import com.dudu.navi.entity.Navigation;
import com.dudu.navi.repo.ResourceManager;
import com.dudu.navi.service.NaviProcess;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Created by lxh on 2015/11/14.
 */
public class NavigationManager {

    private static  NavigationManager navigationManager;

    private Context mContext;

    private Logger log;

    public NavigationManager(Context context){

        this.mContext = context;
        log = LoggerFactory.getLogger("lbs.navi");
    }

    public static NavigationManager getInstance(Context context){
        if(navigationManager==null)
            navigationManager = new NavigationManager(context);

        return navigationManager;
    }


    public void initNaviManager(){
        ResourceManager.getInstance(mContext).init();
        NaviProcess.getInstance(mContext).initNaviProcess();
    }

    public void startCalculate(Navigation navigation){

    }

    public void existNavigation(){

    }

}
