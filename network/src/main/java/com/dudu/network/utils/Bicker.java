package com.dudu.network.utils;


public class Bicker {
	public Bicker() {
	}

	/**
	 * getBick
	 * 
	 * @return String
	 */
	public static synchronized String getBick() {

		long name = new java.util.Date().getTime();
		int i = (int) (Math.random() * 10000);
		name = name * 10000 + i;
		return "" + name;
	}

	/**
	 * getReference
	 * 
	 * @return String (12bit)
	 */
	public static synchronized String getReference() {
		long name = new java.util.Date().getTime();
		int i = (int) (Math.random() * 10000);
		name = name * 10000 + i;
		String result = "" + name;
		return "1"+result.substring(6);
	}
	
	public static synchronized String getBusinessCode(int code){
		return code+getReference();
	}

	public static void main(String args[]) {
	    for (int i = 0; i < 100; i++)
        {
	        System.out.println(getReference()); 
        }
	}
}
