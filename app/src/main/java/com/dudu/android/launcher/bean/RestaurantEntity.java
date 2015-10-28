package com.dudu.android.launcher.bean;

import java.io.Serializable;

public class RestaurantEntity implements Serializable {

	private static final long serialVersionUID = -7447833054127056322L;
	
	private RestaurantSlots slots;

	public RestaurantSlots getRestaurantSlots() {
		return slots;
	}

	public void setRestaurantSlots(RestaurantSlots slots) {
		this.slots = slots;
	}
	
}
