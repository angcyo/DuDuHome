package com.dudu.obd.gsensor;

import com.dudu.android.launcher.utils.MyDate;

public class MotionData {

	public float mX;
	public float mY;
	public float mZ;

	public String mCurrentTime;

	public String toString(){
		return (mCurrentTime+"V"+ String.valueOf(mX)+","+ String.valueOf(mY)+","+ String.valueOf(mZ));
	}
}
