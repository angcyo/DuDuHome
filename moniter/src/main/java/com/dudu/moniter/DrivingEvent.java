package com.dudu.moniter;

/**
 * Created by Administrator on 2015/11/19.
 */
public class DrivingEvent {

    public static final int TYPE_HARDACCL = 1;  			// 急加速
    public static final int TYPE_HARDBRAK = 2;				// 急减速
    public static final int TYPE_HARDTURN = 3;				// 急转弯
    public static final int TYPE_SNAP = 4;					// 急变道
    public static final int TYPE_FATIGUEDRIVING = 5;		// 疲劳驾驶
    public static final int TYPE_MISMATCH = 6;				// 发动机转速不匹配

    private int eventType;

    public DrivingEvent(int eventType){
        this.eventType = eventType;
    }

    public int getEventType(){
        return eventType;
    }
}

