package com.dudu.map;

import android.content.Context;

import com.dudu.navi.NaviUtils;

/**
 * Created by lxh on 2015/11/26.
 */
public class NavigationClerk {

    private static NavigationClerk navigationClerk;

    private Context mContext;

    public NavigationClerk (Context context){
        this.mContext = context;
    }

    public static NavigationClerk getInstance(Context context){
        if(navigationClerk==null)
            navigationClerk = new NavigationClerk(context);
        return navigationClerk;
    }


    public void openNavi(){

        switch (NaviUtils.getOpenMode(mContext)){
            case INSIDE:
                NaviUtils.startGaodeApp(mContext);
                break;
            case OUTSIDE:
                openActivity();
                break;
        }
    }


    private void openActivity(){




    }


}
