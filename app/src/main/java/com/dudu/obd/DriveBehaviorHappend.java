package com.dudu.obd;

/**
 * Created by pc on 2015/10/31.
 */
public class DriveBehaviorHappend {

    public static final int TYPE_HARDACCL = 1;  			// 急加速
    public static final int TYPE_HARDBRAK = 2;				// 急减速
    public static final int TYPE_HARDTURN = 3;				// 急转弯
    public static final int TYPE_SNAP = 4;					// 急变道
    public static final int TYPE_FATIGUEDRIVING = 5;		// 疲劳驾驶
    public static final int TYPE_MISMATCH = 6;				// 发动机转速不匹配

    private DriveBehaviorHappendListener listener;

    private static DriveBehaviorHappend driveBehaviorHappend;

    public DriveBehaviorHappend(){

    }

    public static DriveBehaviorHappend getInstance(){
        if(driveBehaviorHappend==null)
            driveBehaviorHappend = new DriveBehaviorHappend();
        return  driveBehaviorHappend;

    }

    public interface DriveBehaviorHappendListener{

        /**
         *  0 普通点
         *	1 急加速  2 急减速
         *	3 急转弯 4 急变道
         *	5 疲劳驾驶
         *	6 发动机转速不匹配
         * @param type
         */
        void onDriveBehaviorHappend(int type);

    }

    public DriveBehaviorHappendListener getListener() {
        return listener;
    }

    public void setListener(DriveBehaviorHappendListener listener) {

        this.listener = listener;
    }
}
