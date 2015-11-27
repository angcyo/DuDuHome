package com.dudu.monitor.valueobject;

import com.dudu.monitor.utils.TimeUtils;

/**
 * Created by dengjun on 2015/11/26.
 * Description :
 */
public class FlamoutData {
    private int maxrpm;			//最大发动机转速(rpm)
    private int minrpm;			//最小发动机转速(rpm)
    private int maxspd;			//最大车速(km/h)
    private int avgspd;			//平均车速(km/h)
    private int maxacl;			//最大加速度(km/h)
    private float mileT;		//此次里程(km)
    private float fuelT;		//此次油耗(L/h)
    private float miles;		//累计总里程(km)
    private float fuels;		//累计总油耗(L)
    private int times;			//行车时间(s)
    private int starts;			//点火启动次数
    private int power;			//汽车当前运行状态
    private String createTime;	//采集时间


    public FlamoutData(String flamoutDataString) {
        String[] flamoutDataArray = flamoutDataString.split(",");

        fuels = Float.parseFloat(flamoutDataArray[6]);
        miles = Float.parseFloat(flamoutDataArray[3]);
        times = Integer.parseInt(flamoutDataArray[2]) * 60;
        maxrpm = Integer.parseInt(flamoutDataArray[8]);
        maxspd = Integer.parseInt(flamoutDataArray[7]);
        createTime = TimeUtils.dateLongFormatString(
                System.currentTimeMillis(), TimeUtils.format1);
        power = 0;
    }




    public int getMaxrpm() {
        return maxrpm;
    }

    public void setMaxrpm(int maxrpm) {
        this.maxrpm = maxrpm;
    }

    public int getMinrpm() {
        return minrpm;
    }

    public void setMinrpm(int minrpm) {
        this.minrpm = minrpm;
    }

    public int getMaxspd() {
        return maxspd;
    }

    public void setMaxspd(int maxspd) {
        this.maxspd = maxspd;
    }

    public int getAvgspd() {
        return avgspd;
    }

    public void setAvgspd(int avgspd) {
        this.avgspd = avgspd;
    }

    public int getMaxacl() {
        return maxacl;
    }

    public void setMaxacl(int maxacl) {
        this.maxacl = maxacl;
    }

    public float getMileT() {
        return mileT;
    }

    public void setMileT(float mileT) {
        this.mileT = mileT;
    }

    public float getFuelT() {
        return fuelT;
    }

    public void setFuelT(float fuelT) {
        this.fuelT = fuelT;
    }

    public float getMiles() {
        return miles;
    }

    public void setMiles(float miles) {
        this.miles = miles;
    }

    public float getFuels() {
        return fuels;
    }

    public void setFuels(float fuels) {
        this.fuels = fuels;
    }

    public int getTimes() {
        return times;
    }

    public void setTimes(int times) {
        this.times = times;
    }

    public int getStarts() {
        return starts;
    }

    public void setStarts(int starts) {
        this.starts = starts;
    }

    public int getPower() {
        return power;
    }

    public void setPower(int power) {
        this.power = power;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}
