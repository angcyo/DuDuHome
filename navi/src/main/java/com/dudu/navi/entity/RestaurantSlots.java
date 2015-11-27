package com.dudu.navi.entity;

import java.io.Serializable;

public class RestaurantSlots implements Serializable {

	private static final long serialVersionUID = -3823107799040699013L;

	private MapLocation location;
	
	private String category;

	public MapLocation getLocation() {
		return location;
	}

	public void setLocation(MapLocation location) {
		this.location = location;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}
	
}
