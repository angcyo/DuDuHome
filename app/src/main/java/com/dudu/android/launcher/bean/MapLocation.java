package com.dudu.android.launcher.bean;

import java.io.Serializable;

public class MapLocation implements Serializable{
	private static final long serialVersionUID = 1L;
	private String poi; // 碧云寺
	private String city; // CURRENT_CITY
	private String areaAddr; // 海淀
	private String type; // LOC_POI
	private String area; // 海淀区"

	public String getPoi() {
		return poi;
	}

	public void setPoi(String poi) {
		this.poi = poi;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getAreaAddr() {
		return areaAddr;
	}

	public void setAreaAddr(String areaAddr) {
		this.areaAddr = areaAddr;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}
}
