package com.dudu.obd;

import java.io.Serializable;

public class OBDData implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int spd;					    //车速
	private String time;					//采集时间
	private float engLoad;			 		//发动机负荷
	private float engCoolant;		  		//发动机冷却液

	private float curon;				 	//瞬时油耗
	private float engSpd;				 	//发动机转速
	private float batteryV;			    	//电瓶电压
	private int runState;		   		 	//汽车当前运行状态（1：运行 0：熄火）
	
	public OBDData(){
		
	}
	
	public OBDData(int spd,String time,float engload,float engCoolant,float curon,float engSpd,float batteryV,int runState){
		this.spd = spd;
		this.time = time;
		this.engLoad = engload;
		this.engCoolant = engCoolant;
		this.curon = curon;
		this.engSpd = engSpd;
		this.batteryV = batteryV;
		this.runState = runState;
	}
	
	public int getSpd() {
		return spd;
	}
	public void setSpd(int spd) {
		this.spd = spd;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public float getEngineLoad() {
		return engLoad;
	}
	public void setEngLoad(float engineLoad) {
		this.engLoad = engineLoad;
	}
	public float getEngCoolant() {
		return engCoolant;
	}
	public void setEngCoolant(float engineCoolant) {
		this.engCoolant = engineCoolant;
	}
	public float getCuron() {
		return curon;
	}
	public void setCuron(float curon) {
		this.curon = curon;
	}
	public float getEngSpd() {
		return engSpd;
	}
	public void setEngSpd(float engSpd) {
		this.engSpd = engSpd;
	}
	public float getBatteryV() {
		return batteryV;
	}
	public void setBatteryV(float batteryV) {
		this.batteryV = batteryV;
	}
	public int getRunState() {
		return runState;
	}
	public void setRunState(int runningState) {
		this.runState = runningState;
	}
	
	
}
