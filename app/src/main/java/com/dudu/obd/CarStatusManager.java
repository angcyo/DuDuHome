package com.dudu.obd;

import java.util.LinkedList;

/**
 * Created by pc on 2015/10/31.
 */
public class CarStatusManager {

    private LinkedList<CarStatusListener> mCarStateListenerList = new LinkedList<CarStatusListener>();

    private static CarStatusManager carStatusManager;

    public CarStatusManager(){
        if (mCarStateListenerList == null)
            mCarStateListenerList = new LinkedList<CarStatusListener>();
    }

    public static CarStatusManager getInstance(){

        if(carStatusManager==null)
            carStatusManager = new CarStatusManager();
        return  carStatusManager;
    }

    public interface CarStatusListener {

        /**
         * 车辆状态改变
         * @param state 0 熄火 1 点火
         */
        void onCarStateChange(int state);

    }


    // 添加车辆状态改变监听
    public boolean addCarStateListener(CarStatusListener listener) {
        if (mCarStateListenerList != null)
            return mCarStateListenerList.add(listener);
        return false;
    }

    // 移除车辆状态改变监听
    public boolean removeCarStateListener(CarStatusListener listener) {
        if (!mCarStateListenerList.isEmpty())
            return mCarStateListenerList.remove(listener);
        return false;
    }

    public LinkedList<CarStatusListener> getmCarStateListenerList(){

        return mCarStateListenerList;
    }
}
