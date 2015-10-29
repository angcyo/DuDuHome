package com.dudu.android.launcher.bean;

import java.io.Serializable;

public class CmdEntity implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private CmdSlots slots;

	public CmdSlots getSlots() {
		return slots;
	}

	public void setSlots(CmdSlots slots) {
		this.slots = slots;
	}

}
