package com.dudu.android.launcher.utils;

import java.io.UnsupportedEncodingException;


import android.R.integer;
import android.annotation.SuppressLint;


public class ByteTools {
	public static final int LEN_CHAR = 1;
	public static final int LEN_SHORT = 2;
	public static final int LEN_LONG = 4;


	public static int parseShortFromArrayAsBig(byte[] data, int pos)
	{
		return ((data[pos] & 0xFF) << 8) | (data[pos + 1] & 0xFF);
	}

	public static int parseShortFromArrayAsLittle(byte[] data, int pos)
	{
		return data[pos] & 0xFF | ((data[pos + 1] & 0xFF) << 8);
	}

	public static long parseIntFromArrayAsBig(byte[] data, int pos)
	{
		long code = (data[pos] & 0xFF) << 24;
		for (int i = 1; i < 4; i++)
		{
			long timeByte = data[pos + i] & 0xFF;
			code |= timeByte << ((3 - i) * 8);
		}
		return code;
	}

	public static long parseIntFromArrayAsLittle(byte[] data, int pos)
	{
		long value = data[pos] & 0xFF;
		for (int i = 1; i < 4; i++)
		{
			long timeByte = data[pos + i] & 0xFF;
			value |= timeByte << (i * 8);
		}
		return value;
	}

	
	/**
	 * 将byte专程 二进制字符串
	 * 
	 * @param b
	 * @return
	 */
	public static String byte2bits(byte b)
	{
		int z = b;
		z |= 0x100;
		String str = Integer.toBinaryString(z);
		int len = str.length();
		return str.substring(len - 8, len);
	}

	/**
	 * 将int 转换成固定长度的二进制字符串
	 * 
	 * @param inter
	 *            - 要转换的int数字
	 * @param lenOut
	 *            - 输出的字符串长度
	 * @return - int 对应的二进制字符串
	 */
	public static String int2bits(int inter, int lenOut)
	{
		long z = inter;
		// TODO z 是否越界 (没有越界 0x100000000L，中的L 相当于将 0x100000000 转化成 long型 )
		z |= 0x100000000L;
		String str = Long.toBinaryString(z);
		int len = str.length();
		return str.substring(len - lenOut, len);
	}
	
	/**
	 * 将long 转换成固定长度的二进制字符串
	 * 
	 *
	 *            - 要转换的long数字
	 * @param lenOut
	 *            - 输出的字符串长度
	 * @return - int 对应的二进制字符串
	 */
	public static String int2bits(long longData, int lenOut)
	{
		long z = longData;
		// TODO z 是否越界 (没有越界 0x100000000L，中的L 相当于将 0x100000000 转化成 long型 )
		z |= 0x100000000L;
		String str = Long.toBinaryString(z);
		int len = str.length();
		return str.substring(len - lenOut, len);
	}
	
	
	//将int放到4字节的字节数组
	public static byte[] intToByteArray(int inter)
	{
		byte[] array = new byte[4];
		array[0] = (byte) (inter >> 24 & 0xFF);  //消息标识
		array[1] = (byte) (inter >> 16 & 0xFF);
		array[2] = (byte) (inter >> 8 & 0xFF);
		array[3] = (byte) (inter & 0xFF);
		return array;
	}
	
	public static void appendInt(int int1, byte[] dat, int pos)
	{
		int index = pos;
		for (int i = 0; i < 4; i++)
		{
			dat[index++] = (byte) (int1 >> (i * 8) & 0xFF);
		}
	}
	
	public static void appendIntToLittle(int int1, byte[] dat, int pos)
	{
		int index = pos;
		for (int i = 0; i < 4; i++)
		{
			dat[index++] = (byte) (int1 >> (i * 8) & 0xFF);
		}
	}
	
	public static void appendIntToBig(int int1, byte[] dat, int pos)
	{
		int index = pos;
		for (int i = 0; i < 4; i++)
		{
			dat[index++] = (byte) (int1 >> ((3-i) * 8) & 0xFF);
		}
	}
	
	public static void appendIntToBig(long int1, byte[] dat, int pos)
	{
		int index = pos;
		for (int i = 0; i < 4; i++)
		{
			dat[index++] = (byte) (int1 >> ((3-i) * 8) & 0xFF);
		}
	}
	
	public static void appendShortToLittle( short s, byte[] dat, int pos)
	{
		int index = pos;
		for (int i = 0; i < 2; i++)
		{
			dat[index++] = (byte) (s >> (i * 8) & 0xFF);
		}
	}
	
	public static void appendShortToBig(short s, byte[] dat, int pos)
	{
		int index = pos;
		for (int i = 0; i < 2; i++)
		{
			dat[index++] = (byte) (s >> ((1-i) * 8) & 0xFF);
		}
	}
	
	public static void appendShortToBig(int s, byte[] dat, int pos)
	{
		int index = pos;
		for (int i = 0; i < 2; i++)
		{
			dat[index++] = (byte) (s >> ((1-i) * 8) & 0xFF);
		}
	}
	
	public static void appendShortToBig(long s, byte[] dat, int pos)
	{
		int index = pos;
		for (int i = 0; i < 2; i++)
		{
			dat[index++] = (byte) (s >> ((1-i) * 8) & 0xFF);
		}
	}
	
	

	public static void appendDouble(double d, byte[] data, int pos)
	{
		int index = pos;
		long bits = Double.doubleToLongBits(d);
		for (int i = 0; i < 8; i++)
		{
			data[index++] = (byte) (bits >> (i * 8) & 0xFF);
		}
	}
	
	public static void appendDoubleToLitlle(double d, byte[] data, int pos)
	{
		int index = pos;
		long bits = Double.doubleToLongBits(d);
		for (int i = 0; i < 8; i++)
		{
			data[index++] = (byte) (bits >> (i * 8) & 0xFF);
		}
	}
	
	public static void appendDoubleToBig(double d, byte[] data, int pos)
	{
		int index = pos;
		long bits = Double.doubleToLongBits(d);
		for (int i = 0; i < 8; i++)
		{
			data[index++] = (byte) (bits >> ((7-i) * 8) & 0xFF);
		}
	}
	
	public static int appendData(byte[] src, byte[] datas, int index)
	{
		int srcLen = src.length;
		System.arraycopy(src, 0, datas, index, srcLen);
		return srcLen;
	}

	public static void putDouble(double d, byte[] data, int index)
	{
		Long l = Double.doubleToLongBits(d);
		for (int i = 0; i < 4; i++)
		{
			data[index + i] = l.byteValue();
			l = l >> 8;
		}
	}
	
	public static int readdpustr(byte[] buf, int pos, int len, byte[] rbuf, int rlen)
	{
		if (rlen == 0)
			return 0;
		len -= pos;
		if ((len) < LEN_SHORT)
			return 0;
		int dlen = readdpushort(buf, pos);
		pos += LEN_SHORT;
		len -= LEN_SHORT;
		if (len < dlen)
			return 0;

		if ((dlen - 1) < rlen) // 确保 rlen 不大于数据区长度。
		{
			rlen = dlen - 1;
		}
		System.arraycopy(buf, pos, rbuf, 0, rlen);

		return dlen + LEN_SHORT;
	}
	
	public static int readdpushort(byte[] buf, int pos)
	{
		int data = 0x0;
		data |= ((buf[pos + 0] & 0xff) << 8) | (buf[pos + 1] & 0xff);
		return data;
	}

	public static int readlittleu16(byte[] buf, int pos)
	{
		int data = 0x0;
		data |= ((buf[pos + 1] & 0xff) << 8) | (buf[pos + 0] & 0xff);
		return data;
	}

	public static int readbigu16(byte[] buf, int pos)
	{
		int data = 0x0;
		data |= ((buf[pos + 0] & 0xff) << 8) | (buf[pos + 1] & 0xff);
		return data;
	}

	public static long readdpulong(byte[] buf, int pos)
	{
		long data = 0x0;
		data |= ((buf[pos + 0] & 0xff) << 24) | ((buf[pos + 1] & 0xff) << 16) | ((buf[pos + 2] & 0xff) << 8) | (buf[pos + 3] & 0xff);
		return data;
	}

	public static long readlittleu32(byte[] buf, int pos)
	{
		long data = 0x0;
		data |= ((buf[pos + 3] & 0xff) << 24) | ((buf[pos + 2] & 0xff) << 16) | ((buf[pos + 1] & 0xff) << 8) | (buf[pos + 0] & 0xff);
		return data;
	}

	public static long readbigu32(byte[] buf, int pos)
	{
		long data = 0x0;
		data |= ((buf[pos] & 0xff) << 24) | ((buf[pos + 1] & 0xff) << 16) | ((buf[pos + 2] & 0xff) << 8) | (buf[pos + 3] & 0xff);
		return data;
	}
	
	/**
	 * 读取N字节数据从数据数组中
	 * @param dataArray 
	 * @param pos
	 * @param bytesNum
	 * @return
	 */
	public static byte[] readNByteDataFromArray(byte[] dataArray, int pos, int bytesNum){
		byte[] reDataArray = new byte[bytesNum];
		System.arraycopy(dataArray, pos, reDataArray, 0, reDataArray.length);
		return reDataArray;
	}

	public static void u32little(byte[] buf, int pos, long data)
	{
		buf[pos] = (byte) (data & 0xff);
		buf[pos + 1] = (byte) ((data >> 8) & 0xff);
		buf[pos + 2] = (byte) ((data >> 16) & 0xff);
		buf[pos + 3] = (byte) ((data >> 24) & 0xff);
	}

	public static void u16little(byte[] buf, int pos, int data)
	{
		buf[pos] = (byte) (data & 0xff);
		buf[pos + 1] = (byte) ((data >> 8) & 0xff);
	}

	public static void u16endian(byte[] buf, int pos, int data)
	{
		buf[pos + 1] = (byte) (data & 0xff);
		buf[pos] = (byte) ((data >> 8) & 0xff);
	}

	public static void u32endian(byte[] buf, int pos, long data)
	{
		buf[pos + 3] = (byte) (data & 0xff);
		buf[pos + 2] = (byte) ((data >> 8) & 0xff);
		buf[pos + 1] = (byte) ((data >> 16) & 0xff);
		buf[pos] = (byte) ((data >> 24) & 0xff);
	}

	public static void appendFloat(float d, byte[] data, int pos)
	{
		int index = pos;
		long bits = Float.floatToIntBits(d);
		data[index] = (byte) (bits & 0xff);
		data[index + 1] = (byte) ((bits >> 8) & 0xff);
		data[index + 2] = (byte) ((bits >> 16) & 0xff);
		data[index + 3] = (byte) ((bits >> 24) & 0xff);

	}
	
	/**
	 * 将字节数据转成16进制的字符串
	 * 
	 * @Team Golo Terminal
	 * @date: 2014-7-30
	 * @param src
	 * @return String
	 */
	public static String bytesToHexString(byte[] src)
	{
		StringBuilder stringBuilder = new StringBuilder("");
		if (src == null || src.length <= 0)
		{
			return "";
		}
		for (int i = 0; i < src.length; i++)
		{
			int v = src[i] & 0x000000FF;
			String hv;
			if (v < 0x10)
			{
				hv = "0" + Integer.toHexString(v);
			} else
			{
				hv = Integer.toHexString(v);
			}

			stringBuilder.append(hv);
		}
		return stringBuilder.toString();

	}
	
	
	public static int byteToInt(byte src)
	{
		return src & 0xFF;
	}
	
	public static String intToHexString(int id)
	{
		String hexString = Integer.toHexString(id);
		int len = hexString.length();
		if (len == 1) {
			hexString = "000" + hexString;
		}else if (len == 2) {
			hexString = "00" + hexString;
		}else if (len == 3) {
			hexString = "0" + hexString;
		}
		return hexString;
	}

	public static byte[] intToHexBytes(int id)
	{
		String hexString = Integer.toHexString(id);
		int len = hexString.length();
		while (len < 2)
		{
			hexString = "0" + hexString;
			len = hexString.length();
		}
		return hexStringToBytes(hexString);
	}

	public static byte[] intToFourHexBytes(int id)
	{
		String hexString = Integer.toHexString(id);
		int len = hexString.length();
		while (len < 8)
		{
			hexString = "0" + hexString;
			len = hexString.length();
		}
		return hexStringToBytes(hexString);
	}

	public static byte[] intToFourHexBytesTwo(int id)
	{
		String hexString = Integer.toHexString(id);
		int len = hexString.length();
		if (len < 2)
		{
			hexString = "0" + hexString;
			len = hexString.length();
		}
		while (len < 8)
		{
			hexString = hexString + "0";
			len = hexString.length();
		}
		return hexStringToBytes(hexString);
	}

	public static byte intToHexByte(int id)
	{
		String hexString = Integer.toHexString(id);
		int len = hexString.length();
		while (len < 2)
		{
			hexString = "0" + hexString;
			len = hexString.length();
		}
		return hexStringToByte(hexString);
	}

	@SuppressLint("DefaultLocale")
	public static byte[] hexStringToBytes(String hexString)
	{
		if (hexString == null || hexString.equals(""))
		{
			byte[] bytes = new byte[0];
			return bytes;
		}
		hexString = hexString.toUpperCase();
		int length = hexString.length() / 2;
		char[] hexChars = hexString.toCharArray();
		byte[] d = new byte[length];
		for (int i = 0; i < length; i++)
		{
			int pos = i * 2;
			d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
		}
		return d;
	}

	@SuppressLint("DefaultLocale")
	public static byte hexStringToByte(String hexString)
	{
		hexString = hexString.toUpperCase();
		int length = hexString.length() / 2;
		char[] hexChars = hexString.toCharArray();
		byte[] d = new byte[length];
		for (int i = 0; i < length; i++)
		{
			int pos = i * 2;
			d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
		}
		return d[0];
	}

	private static byte charToByte(char c)
	{
		return (byte) "0123456789ABCDEF".indexOf(c);
	}
	
	public static void memset(byte[] data, byte value, int len)
	{
		//memset 第3个参数  len 本身是 data 的长度
		if(data.length>0&&data.length>=len){
		for (int i = 0; i < len; i++)
		{
			data[i] = value;
		}
		}
	}

	public static void memcpy(byte[] dest, int pos, byte[] src, int spos, int dataLen)
	{
		int srcLen = src.length - spos;
		final int destLengh = dest.length - pos;
		
		if (srcLen > dataLen)
			srcLen = dataLen;
		if (srcLen > destLengh) 
			srcLen = destLengh;
		for (int i = 0; i < srcLen; i++)
		{
			dest[pos + i] = src[i + spos];
		}
		for (int i = srcLen; i < dataLen; i++)
		{
			dest[pos + i] = 0x0;
		}
	}
	
	public static String parseStringFromArray(byte[] data, int pos, int length)
	{
		byte[] bytes = new byte[length];
		System.arraycopy(data, pos, bytes, 0, length);
		return new String(bytes);
	}
	
	public static String parseStringUTF8FromArray(byte[] data, int pos, int length)
	{
		byte[] bytes = new byte[length];
		System.arraycopy(data, pos, bytes, 0, length);
		String returnString = null;
		try {
			returnString =  new String(bytes, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
//			GoloLog.e("取字符串异常", e);
		}
		return returnString;
	}
	
	public static String parseStringGBKFromArray(byte[] data, int pos, int length)
	{
		byte[] bytes = new byte[length];
		System.arraycopy(data, pos, bytes, 0, length);
		String returnString = null;
		try {
			returnString =  new String(bytes, "GBK");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
//			GoloLog.e("取字符串异常", e);
		}
		return returnString;
	}
	
	public static String parseStringGB2312FromArray(byte[] data, int pos, int length)
	{
		byte[] bytes = new byte[length];
		System.arraycopy(data, pos, bytes, 0, length);
		String returnString = null;
		try {
			returnString =  new String(bytes, "GB2312");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return returnString;
	}
	
	public static  byte doCheck(byte[] dataToDoCheck) {
		byte checkData = (byte)0xff;
		for (int i = 0; i < dataToDoCheck.length; i++) {
			checkData ^= dataToDoCheck[i];
		}
		return checkData;
	}
	
	
	
	/** *//**
	    * @函数功能: BCD码转为10进制串(阿拉伯数据)
	    * @输入参数: BCD码
	    * @输出结果: 10进制串
	    */
	public static String bcd2Str(byte[] bytes){
	    StringBuffer temp=new StringBuffer(bytes.length*2);

	    for(int i=0;i<bytes.length;i++){
	     temp.append((byte)((bytes[i]& 0xf0)>>>4));
	     temp.append((byte)(bytes[i]& 0x0f));
	    }
	    return temp.toString().substring(0,1).equalsIgnoreCase("0")?temp.toString().substring(1):temp.toString();
	}

	/** *//**
	    * @函数功能: 10进制串转为BCD码
	    * @输入参数: 10进制串
	    * @输出结果: BCD码
	    */
	public static byte[] str2Bcd(String asc) {
	    int len = asc.length();
	    int mod = len % 2;

	    if (mod != 0) {
	     asc = "0" + asc;
	     len = asc.length();
	    }

	    byte abt[] = new byte[len];
	    if (len >= 2) {
	     len = len / 2;
	    }

	    byte bbt[] = new byte[len];
	    abt = asc.getBytes();
	    int j, k;

	    for (int p = 0; p < asc.length()/2; p++) {
	     if ( (abt[2 * p] >= '0') && (abt[2 * p] <= '9')) {
	      j = abt[2 * p] - '0';
	     } else if ( (abt[2 * p] >= 'a') && (abt[2 * p] <= 'z')) {
	      j = abt[2 * p] - 'a' + 0x0a;
	     } else {
	      j = abt[2 * p] - 'A' + 0x0a;
	     }

	     if ( (abt[2 * p + 1] >= '0') && (abt[2 * p + 1] <= '9')) {
	      k = abt[2 * p + 1] - '0';
	     } else if ( (abt[2 * p + 1] >= 'a') && (abt[2 * p + 1] <= 'z')) {
	      k = abt[2 * p + 1] - 'a' + 0x0a;
	     }else {
	      k = abt[2 * p + 1] - 'A' + 0x0a;
	     }

	     int a = (j << 4) + k;
	     byte b = (byte) a;
	     bbt[p] = b;
	    }
	    return bbt;
	}
	/** *//**
	    * @函数功能: BCD码转ASC码
	    * @输入参数: BCD串
	    * @输出结果: ASC码
	    */
	/*public static String BCD2ASC(byte[] bytes) {
	    StringBuffer temp = new StringBuffer(bytes.length * 2);

	    for (int i = 0; i < bytes.length; i++) {
	     int h = ((bytes[i] & 0xf0) >>> 4);
	     int l = (bytes[i] & 0x0f);  
	     temp.append(BToA[h]).append( BToA[l]);
	    }
	    return temp.toString() ;
	}*/
	
	
	
}
