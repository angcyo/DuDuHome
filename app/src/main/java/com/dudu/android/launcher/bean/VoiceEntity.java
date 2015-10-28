package com.dudu.android.launcher.bean;

import java.io.Serializable;

public class VoiceEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private VoiceSlots slots;

	public VoiceSlots getSlots() {
		return slots;
	}

	public void setSlots(VoiceSlots slots) {
		this.slots = slots;
	}

}
