package com.dudu.monitor.utils;

import android.text.TextUtils;

public class LocationFilter {
	private static double EARTH_RADIUS = 6378.137;// 地球平均半径,单位公里
	/**
	 * 过滤经纬度  lat [-90, 90]  lon [-180, 180]
	 * @param lat
	 * @param lon
	 * @return
	 */
	public static boolean checkLatLon(double lat, double lon) {
		return lat >= -90 && lat <= 90 && lon >= -180 && lon <= 180;
	}
	
	/**
	 * 过滤精度  (0, 60]
	 * @param radius 
	 * @return
	 */
	private static boolean checkRadius(float radius) {
		return radius > 0 && radius <= 15;
	}
	
	/**
	 * 过滤方向角 [0, 359]
	 * @param direction 
	 * @return
	 */
	private static boolean checkDirection(float direction) {
		return direction >= 0 && direction < 360;
	}
	
	/**
	 * 第一阶段过滤
	 * @param lat
	 * @param lon
	 * @param radius
	 * @param direction
	 * @return
	 */
	public static boolean checkStageOne(double lat, double lon, float radius, float direction) {
		return checkLatLon(lat, lon) && checkRadius(radius) && checkDirection(direction);
	}
	
	/**
	 * 过滤速度 大于等于0
	 * @param speed 
	 * @return
	 */
	public static boolean checkSpeed(float speed) {
		return speed >= 0;
	}
	
	/**
	 * 过滤时间 currTime > lastTime
	 * @param lastTime 上一个有效点的时间
	 * @param currTime 当前点的时间
	 * @return
	 */
	private static boolean checkTime(String lastTime, String currTime) {
		if(TextUtils.isEmpty(lastTime) || TextUtils.isEmpty(currTime)) 
			return false;
		return TimeUtils.stringTolong(currTime) > TimeUtils.stringTolong(lastTime);
	}
	
	/**
	 * 过滤加速度
	 * @param lastSpeed km/h
	 * @param currSpeed	km/h
	 * @param lastTime
	 * @param currTime
	 * @return
	 */
	private static boolean checkAcceleration(float lastSpeed, float currSpeed, String lastTime, String currTime) {
		float a =  (float) ((currSpeed/3.6 - lastSpeed/3.6)/(TimeUtils.stringTolong(currTime) - TimeUtils.stringTolong(lastTime)));
		return a >= -13 && a <= 14;
	}
	
	/**
	 * 第二阶段过滤(不包含速度过滤)
	 * @param
	 * @param
	 * @param
	 * @param
	 * @return
	 */
	public static boolean checkStageTwo(float lastSpeed, float currSpeed, String lastTime, String currTime) {
		return checkTime(lastTime, currTime) && checkAcceleration(lastSpeed, currSpeed, lastTime, currTime);
	}
	
	/**
	 * 判断方向值差值 大于5
	 * @param lastDirection
	 * @param currDirection
	 * @return
	 */
	public static boolean checkDirectionDValue(float lastDirection, float currDirection) {
		return ((currDirection - lastDirection) > 5)  || ((currDirection - lastDirection) < -5);
	}
	
	/**
	 * 判断速度 aveSpeed  > speed * 1.5
	 * @param lastSpeed	km/h
	 * @param currSpeed	km/h
	 * @param lastTime
	 * @param currTime
	 * @param lat_s
	 * @param lon_s
	 * @param lat_e
	 * @param lon_e
	 * @return
	 */
	public static boolean checkSpeedDValue(float lastSpeed, float currSpeed, String lastTime, String currTime, double lat_s, double lon_s, double lat_e, double lon_e) {
		if(!checkTime(lastTime, currTime)) 
			return false;
		float speed = (float) ((lastSpeed/3.6 + currSpeed/3.6) / 2);
		float aveSpeed = (float)(getDistance(lat_s, lon_s, lat_e, lon_e) * 1000)/ (TimeUtils.stringTolong(currTime) - TimeUtils.stringTolong(lastTime));
		return aveSpeed > speed * 1.5;
	}
	private static double rad(double d) {
		return d * Math.PI / 180.0;
	}
	
	/**
	 * 该方法用来根据起止点的经纬度计算两点间的距离
	 * @param
	 * @param
	 * @return 两点之间的距离（单位为km） double
	 */
	public static double getDistance(double lat_s, double lon_s, double lat_e, double lon_e) {
		double radLat1 = rad(lat_s);
		double radLat2 = rad(lat_e);
		double a = radLat1 - radLat2;
		double b = rad(lon_s)
				- rad(lon_e);

		double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
				+ Math.cos(radLat1) * Math.cos(radLat2)
				* Math.pow(Math.sin(b / 2), 2)));
		s = s * EARTH_RADIUS;
		s = Math.round(s * 10000) / 10000;
		return s;
	}
}
