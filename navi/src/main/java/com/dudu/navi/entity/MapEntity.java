package com.dudu.navi.entity;

import java.io.Serializable;

public class MapEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	private MapSlots slots;

	public MapSlots getSlots() {
		return slots;
	}

	public void setSlots(MapSlots slots) {
		this.slots = slots;
	}
}
