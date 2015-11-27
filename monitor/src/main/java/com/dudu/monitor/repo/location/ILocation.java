package com.dudu.monitor.repo.location;

import android.content.Context;

/**
 * Created by dengjun on 2015/11/26.
 * Description :
 */
public interface ILocation {
//    public void initLoction(Context context);
    public void startLocation(Context context);

    public void stopLocation();

    public void setLocationListener(ILocationListener iLocationListener);

}
