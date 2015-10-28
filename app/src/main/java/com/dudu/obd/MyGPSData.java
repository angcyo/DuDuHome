package com.dudu.obd;

import java.io.Serializable;

public class MyGPSData implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Double lon;	//经度
	private Double lat;	//纬度
	private String time;
	private String obeId;
	private float speeds;	//速度
	private Double accuracy;	//精度
	private float direction;	//方向
	private Double altitude;	//海拔
	private Integer type;		//类型 1.急加速 	2.急减速	3.急转弯	4.急变道	5.疲劳驾驶	6.发动机转速不匹配
	
	public MyGPSData(double lat,double lon,float speeds,double altutude,float direction ,String time,double accuracy ,int type){
		this.lat = lat;
		this.lon = lon;
		this.time = time;
		this.speeds = speeds;
		this.altitude = altutude;
		this.direction = direction;
		this.accuracy = accuracy;
		this.type = type;
	}
	public MyGPSData () {
		
	}
	
	public String getObeId() {
		return obeId;
	}
	public void setObeId(String obeId) {
		this.obeId = obeId;
	}
	public Double getLon() {
		return lon;
	}
	public void setLon(Double lon) {
		this.lon = lon;
	}
	public Double getLat() {
		return lat;
	}
	public void setLat(Double lat) {
		this.lat = lat;
	}
	public float getSpeeds() {
		return speeds;
	}
	public void setSpeeds(float speeds) {
		this.speeds = speeds;
	}
	public double getAltitude() {
		return altitude;
	}
	public void setAltitude(double altitude) {
		this.altitude = altitude;
	}
	public float getDirection() {
		return direction;
	}
	public void setDirection(float direction) {
		this.direction = direction;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public double getAccuracy() {
		return accuracy;
	}
	public void setAccuracy(double accuracy) {
		this.accuracy = accuracy;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	
	
}
