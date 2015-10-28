package com.dudu.android.launcher.bean;

import java.io.Serializable;

public class MapSlots implements Serializable {
	private static final long serialVersionUID = 1L;
	private MapLocation location;
	private MapSlotsLoc startLoc;
	private MapSlotsLoc endLoc;

	public MapLocation getLocation() {
		return location;
	}

	public void setLocation(MapLocation location) {
		this.location = location;
	}

	public MapSlotsLoc getStartLoc() {
		return startLoc;
	}

	public void setStartLoc(MapSlotsLoc startLoc) {
		this.startLoc = startLoc;
	}

	public MapSlotsLoc getEndLoc() {
		return endLoc;
	}

	public void setEndLoc(MapSlotsLoc endLoc) {
		this.endLoc = endLoc;
	}
}
