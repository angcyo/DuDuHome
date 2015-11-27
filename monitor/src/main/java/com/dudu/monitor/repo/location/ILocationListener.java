package com.dudu.monitor.repo.location;

/**
 * Created by dengjun on 2015/11/26.
 * Description :
 */
public interface ILocationListener {
    public void onLocationResult(Object locationInfo);
    public void onLocationState(int locationState);
}
