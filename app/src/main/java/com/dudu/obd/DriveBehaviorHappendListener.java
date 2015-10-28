package com.dudu.obd;

public interface DriveBehaviorHappendListener {
   public static int TYPE_HARDACCL = 1;  			// 急加速
   public static int TYPE_HARDBRAK = 2;				// 急减速
   public static int TYPE_HARDTURN = 3;				// 急转弯
   public static int TYPE_SNAP = 4;					// 急变道
   public static int TYPE_FATIGUEDRIVING = 5;		// 疲劳驾驶
   public static int TYPE_MISMATCH = 6;				// 发动机转速不匹配
	
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
