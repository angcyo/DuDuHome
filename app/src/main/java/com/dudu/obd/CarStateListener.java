package com.dudu.obd;

public interface CarStateListener {

	/**
	 * 车辆状态改变
	 * @param state 0 熄火 1 点火
	 */
	void onCarStateChange(int state);
		
}
