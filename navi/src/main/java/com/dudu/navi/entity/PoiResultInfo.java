package com.dudu.navi.entity;

import java.io.Serializable;
import java.util.Comparator;

public class PoiResultInfo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private double latitude;
	private double longitude;
	private String addressDetial; // 地址详细信息
	private String addressTitle; // 地址简称
	private double distance; // 距离

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public String getAddressDetial() {
		return addressDetial;
	}

	public void setAddressDetial(String addressDetial) {
		this.addressDetial = addressDetial;
	}

	public String getAddressTitle() {
		return addressTitle;
	}

	public void setAddressTitle(String addressTitle) {
		this.addressTitle = addressTitle;
	}

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}



	public static class MyComparator implements Comparator<PoiResultInfo> {

		@Override
		public int compare(PoiResultInfo lhs, PoiResultInfo rhs) {
			if (lhs.getDistance() > rhs.getDistance())
				return 1;
			else if (lhs.getDistance() == rhs.getDistance()) {
				return 0;
			} else {
				return -1;
			}

		}

	}
}
