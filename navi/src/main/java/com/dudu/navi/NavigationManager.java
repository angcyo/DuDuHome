package com.dudu.navi;

import android.content.Context;

import com.dudu.navi.entity.Navigation;
import com.dudu.navi.repo.ResourceManager;
import com.dudu.navi.vauleObject.FloatButtonEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.greenrobot.event.EventBus;


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
    }

    public void startCalculate(Navigation navigation){

    }

   public void openNavi(){

       EventBus.getDefault().post(FloatButtonEvent.SHOW);
       Utils.startThirdPartyApp(mContext,"com.autonavi.minimap");
//       if (MapManager.getInstance().isNavi() || MapManager.getInstance().isNaviBack()) {
//           if (MapManager.getInstance().isNavi()) {
//               navigationIntent.setClass(MainActivity.this,
//                       NaviCustomActivity.class);
//           } else if (MapManager.getInstance().isNaviBack()) {
//               navigationIntent.setClass(MainActivity.this,
//                       NaviBackActivity.class);
//           }
//       } else {
//           navigationIntent.setClass(MainActivity.this, LocationMapActivity.class);
//
//       }
//
//       startActivity(navigationIntent);

   }


}
