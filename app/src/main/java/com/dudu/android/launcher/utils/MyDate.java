package com.dudu.android.launcher.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;



public class MyDate
{
	
	public static String getCurrentYear()
	{
		SimpleDateFormat format = new SimpleDateFormat("yyyy");
		String date = format.format(new Date(System.currentTimeMillis()));
		return date;
	}

	public static String getFileName()
	{
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
		String date = format.format(new Date(System.currentTimeMillis()));
		return date;
	}

	public static String getDateEN()
	{
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String date = format.format(new Date(System.currentTimeMillis()));
		return date;
	}
	
	public static String getDateForLog()
	{
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
		String date = format.format(new Date(System.currentTimeMillis()));
		return date;
	}

	public static String date(long time)
	{
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String date = format.format(new Date(time));
		return date;
	}
	
	/**
	 * 
	 * @return yyMMddHHmmss格式时间
	 */
	public static String getDateBCDTime(){
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
		String date = format.format(new Date(System.currentTimeMillis()));
		return date.substring(2);
	}
	
	/**
	 * 6字节格式BCD时间
	 * @return yyMMddHHmmss格式时间的字节组数
	 */
	public static byte[] getDateBCDTimeArray(){
		return ByteTools.str2Bcd(getDateBCDTime());
	}
	
	  /* unix时间戳转换为dateFormat
	     * 
	     * @param beginDate
	     * @return
	     */
	    public static String timestampToDate(String beginDate) {
	        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	        String sd = sdf.format(new Date(Long.parseLong(beginDate)));
	        return sd;
	    }
	 
	    /**
	     * 自定义格式时间戳转换
	     * 
	     * @param beginDate
	     * @return
	     */
	    public static String timestampToDate(String beginDate,String format) {
	        SimpleDateFormat sdf = new SimpleDateFormat(format);
	        String sd = sdf.format(new Date(Long.parseLong(beginDate)));
	        return sd;
	    }
	 
	    /**
	     * 将格式（yyyyMMddHHmmss）字符串转为时间戳字符串
	     * @param user_time
	     * @return
	     */
	    public static String dateToTimestamp(String user_time) {
	        String re_time = null;
	        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
	        Date d;
	        try {
	            d = sdf.parse(user_time);
	            long l = d.getTime();
	            String str = String.valueOf(l);
	            re_time = str.substring(0, 10);
	        } catch (ParseException e) {
	            e.printStackTrace();
	        }
	        return re_time;
	    }
	    
	    
	    /**
	     * 6字节BCD时间转换成UNIX时间戳
	     * @param bcdTimeArray
	     * @return
	     */
	    public static long bcdTimeArrayToTimestamp(byte[] bcdTimeArray){
	    	String time = "20" + ByteTools.bytesToHexString(bcdTimeArray);
	    	return Long.valueOf(dateToTimestamp(time));
	    }


	/**
	 * 将格式（yyyyMMddHHmmss） yyyy-MM-dd HH:mm:ss 字符串转为时间戳字符串
	 * @param user_time
	 * @return
	 */
	public static String dateToTimestamp(String user_time, String formatString) {
		String re_time = null;
		SimpleDateFormat sdf = new SimpleDateFormat(formatString);
		Date d;
		try {
			d = sdf.parse(user_time);
			long l = d.getTime();
			String str = String.valueOf(l);
			re_time = str.substring(0, 10);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return re_time;
	}

	public static long dateStringToTimestamp(String dateString, String formatString){


		return Long.valueOf(dateToTimestamp(dateString, formatString));
	}
}
