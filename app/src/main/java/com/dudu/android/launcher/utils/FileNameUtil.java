package com.dudu.android.launcher.utils;

public class FileNameUtil {

	public static String randomString(int length){
		char[] str = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'j', 'k',
			    'm', 'n', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w',
			    'x', 'y', 'z', '2', '3', '4', '5', '6', '7', '8', '9' };
		char[] letter = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'j', 'k','m', 
				'n', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w','x', 'y', 'z'};
		StringBuffer result = new StringBuffer("");
		result.append(letter[(int) Math.floor(Math.random()*letter.length)]);
		for(int i=0;i<length-1;i++){
			result.append(str[(int)Math.floor(Math.random()*str.length)]);
		}
		return result.toString();
	}

}
