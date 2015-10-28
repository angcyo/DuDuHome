package com.dudu.android.launcher.utils;

public class JourneyTool {
	private static double EARTH_RADIUS = 6378.137;// 地球平均半径,单位公里

	private static double rad(double d) {
		return d * Math.PI / 180.0;
	}
	
	/**
	 * 该方法用来根据起止点的经纬度计算两点间的距离
	 * @param s_location
	 * @param e_location
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
		s = Math.round(s * 10000) / 10;
		return s;
	}

}
