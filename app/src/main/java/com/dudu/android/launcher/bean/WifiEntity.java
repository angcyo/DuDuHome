package com.dudu.android.launcher.bean;

import java.io.Serializable;

public class WifiEntity implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private WifiSlots slots;

	public WifiSlots getSlots() {
		return slots;
	}

	public void setSlots(WifiSlots slots) {
		this.slots = slots;
	}
	

}
