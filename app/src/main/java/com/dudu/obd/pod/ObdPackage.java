/**
 * 
 */
package com.dudu.obd.pod;


import com.dudu.network.utils.DuduLog;


/**
 *
 *
 *
 * @author dengjun.cy
 * @version 1.0
 * @date 2015-1-14
 *
 *命令格式：起始标志（2字节）+目标地址（1字节）+源地址（1字节）+包长度（2字节）
 *                 +计数器（1字节）+命令字（2字节）+数据区（n 字节）+包校验（1字节）
 *
 *<包长度 > ：<计数器>+<命令字>+<数据区>”三部分数据长度之和。
 *<命令字>： 2 个字节， 分为主功能命令字及子功能命令字
 *<包校验>： 对“<目标地址>+<源地址>+<包长度>+<计数器>+<命令字>+<数据区>”等部分
 *                      按字节进行异或运算， 其结果等于“校验值”
 */
/**
 * @author dengjun.cy
 *
 */
public class ObdPackage {
	
	public final static byte  START_FLAG0 = (byte)0x55;  //起始标志
	public final static byte START_FLAG1 =  (byte)0xaa;  //起始标志

	public final static int BASE_LENGTH = 7; //数据包中除 <计数器>+<命令字>+<数据区>后的长度
	//public final static int HEAD_BASE_LENGTH = 7; 
	
	public  byte  startFlag0;              //起始标志
	public  byte  startFlag1;              //起始标志

	public int packageLength;          //包长度  <命令>+<参数>+<包校验>”三部分数据长度之和。
	public byte counter;                      //计数器
	public byte mainCommand;      //主命令
	public byte subCommand;       //子命令
	protected byte[] data;                  //数据区
	public byte packageCheck;       //包校验

	public int dpuPackageLength; //DpuPackage字节流长度
	
	public ObdPackage() {
		
		// TODO Auto-generated constructor stub
	}
	

	public ObdPackage(byte cmd, byte[] srcData){
		startFlag0 = START_FLAG0;
		startFlag1 = START_FLAG1;

		mainCommand = cmd;

		if (srcData == null) {
			packageLength = 2;
		} else {
			packageLength = 2 + srcData.length;
		}
	}


	//dpu传输来的字和双字是大端格式
	public static ObdPackage fromBigData(byte[] data) {
		ObdPackage dpuPackage = new ObdPackage();
		//TODO
		dpuPackage.dpuPackageLength = data.length;
		
		int index = 0;
		dpuPackage.startFlag0 = data[index++];
		dpuPackage.startFlag1 = data[index++];

		dpuPackage.packageLength = ByteTools.parseShortFromArrayAsBig(data, index);
		index += 2;
		
		if ((dpuPackage.dpuPackageLength != dpuPackage.packageLength + BASE_LENGTH) | (doCheck(data) == false)) {
			DuduLog.e("收到错误的DpuPackage");
			return null;
		}
		
		dpuPackage.counter = data[index++];
		
		dpuPackage.mainCommand = data[index++];
		dpuPackage.subCommand = data[index++];
		
		dpuPackage.data = new byte[dpuPackage.packageLength - 3];
		System.arraycopy(data, index, dpuPackage.data, 0, dpuPackage.packageLength - 3);
		index += dpuPackage.data.length;
		dpuPackage.packageCheck = data[index++];
			
		return dpuPackage;
	}
	

	
	
	/**校验函数
	 * @param data
	 * @return
	 */
	private static boolean doCheck(byte[] data)
	{
		byte checkByte = (byte) 0xFF;
		for (int i = 0; i < data.length; i++)
		{
			checkByte ^= data[i];
		}
		if (checkByte == 0)
		{
			return true;
		} else
		{
			return false;

		}
	}
	

	/**
	 * @return 大端格式DpuPackage字节流
	 */
	public byte[] getBytesToBigArray()
	{
		byte[] dpuPackageArray = new byte[dpuPackageLength];
		//TODO
		int index = 0;
		dpuPackageArray[index++] = startFlag0;
		dpuPackageArray[index++] = startFlag1;

		ByteTools.u16endian(dpuPackageArray, index, packageLength);

		index += 2;

		dpuPackageArray[index++] = mainCommand;

		if (data != null) {
			System.arraycopy(data, 0, dpuPackageArray, index, data.length);
			index += data.length;
		}

		byte checkTemp = packageCheck;
		for (int i = 2; i < index ; i++) {
			checkTemp ^= dpuPackageArray[i];
		}
		dpuPackageArray[index] = checkTemp;

		

		return dpuPackageArray;
	}
	

	public byte getStartFlag0() {
		return startFlag0;
	}


	public void setStartFlag0(byte startFlag0) {
		this.startFlag0 = startFlag0;
	}

	public byte getStartFlag1() {
		return startFlag1;
	}


	public void setStartFlag1(byte startFlag1) {
		this.startFlag1 = startFlag1;
	}



	public int getPackageLength() {
		return packageLength;
	}


	public void setPackageLength(short packageLength) {
		this.packageLength = packageLength;
	}


	public byte getCounter() {
		return counter;
	}


	public void setCounter(byte counter) {
		this.counter = counter;
	}


	public byte getMainCommand() {
		return mainCommand;
	}


	public void setMainCommand(byte mainCommand) {
		this.mainCommand = mainCommand;
	}


	public byte getSubCommand() {
		return subCommand;
	}


	public void setSubCommand(byte subCommand) {
		this.subCommand = subCommand;
	}


	public byte[] getData() {
		return data;
	}


	public void setData(byte[] data) {
		this.data = data;
	}


	public byte getPackageCheck() {
		return packageCheck;
	}


	public void setPackageCheck(byte packageCheck) {
		this.packageCheck = packageCheck;
	}


	public int getDpuPackageLength() {
		return dpuPackageLength;
	}


	public void setDpuPackageLength(int dpuPackageLength) {
		this.dpuPackageLength = dpuPackageLength;
	}
    
}
