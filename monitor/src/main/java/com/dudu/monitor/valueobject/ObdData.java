package com.dudu.monitor.valueobject;

import android.text.TextUtils;
import android.util.Log;

import com.dudu.monitor.utils.TimeUtils;

/**
 * Created by dengjun on 2015/11/26.
 * Description :
 */
public class ObdData {
    private int speed;                        //车速
    private String createTime;                    //采集时间
    private float engLoad;                    //发动机负荷
    private float engCoolant;                //发动机冷却液

    private float curon;                    //瞬时油耗
    private float engineSpeed;                    //发动机转速
    private float batteryVoltage;                    //电瓶电压
    private int runState;                    //汽车当前运行状态（1：运行 0：熄火）

    public ObdData(String realTimeObdData) {
        if (!TextUtils.isEmpty(realTimeObdData)) {
            String[] realTimeDataArray = realTimeObdData.split(",");

            batteryVoltage = Float.parseFloat(realTimeDataArray[1]);
            engineSpeed = Float.parseFloat(realTimeDataArray[2]);
            speed = Integer.parseInt(realTimeDataArray[3]);
            engCoolant = Float.parseFloat(realTimeDataArray[4]);
            engLoad = Float.parseFloat(realTimeDataArray[5]);
            curon = Float.parseFloat(realTimeDataArray[6]);
            createTime = TimeUtils.dateLongFormatString(System.currentTimeMillis(), TimeUtils.format1);
            runState = 1;
        }
    }


    public ObdData(String xfaOBD, int flag) {

        String[] obdStr = xfaOBD.split(";");
        for (int i = 0; i < obdStr.length; i++) {
            String s = obdStr[i];
            if (obdStr[i].startsWith("SS")) {

            } else if (s.startsWith("S")) {
                speed = Integer.parseInt((s.substring(1, s.length())));

            } else if (s.startsWith("V")) {
                batteryVoltage = Float.parseFloat(s.substring(1, s.length()));
            } else if (s.startsWith("R")) {

                engineSpeed = Float.parseFloat(s.substring(1, s.length()));
            } else if (s.startsWith("XH") || s.startsWith("YH")) {

            } else if (s.startsWith("XM") || s.startsWith("YM")) {
                curon = Float.parseFloat(s.substring(2, s.length()));
            } else if (s.startsWith("O")) {
                engLoad = Float.parseFloat(s.substring(1, s.length()));
            } else if (s.startsWith("L")) {
                // 剩余油量


            } else if (s.startsWith("C")) {
                // 冷却液温度

            }
        }
        createTime = TimeUtils.dateLongFormatString(System.currentTimeMillis(), TimeUtils.format1);
        runState = 1;

    }

    // 转速不匹配判定
    public boolean misMatch() {
        boolean first = speed < 30 && engineSpeed > 3000;
        boolean second = (speed < 60 && speed > 30) && engineSpeed > 3500;
        boolean third = (speed < 90 && speed > 60) && engineSpeed > 4000;
        boolean forth = (speed < 110 && speed > 90) && engineSpeed > 4500;
        boolean five = (speed < 130 && speed > 110) && engineSpeed > 5000;
        boolean six = (speed < 150 && speed > 130) && engineSpeed > 5500;
        if (first || second || third || forth || five || six)
            return true;
        return false;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public float getEngLoad() {
        return engLoad;
    }

    public void setEngLoad(float engLoad) {
        this.engLoad = engLoad;
    }

    public float getEngineSpeed() {
        return engineSpeed;
    }

    public void setEngineSpeed(float engineSpeed) {
        this.engineSpeed = engineSpeed;
    }

    public int getRunState() {
        return runState;
    }

    public void setRunState(int runState) {
        this.runState = runState;
    }

    public float getBatteryVoltage() {
        return batteryVoltage;
    }

    public void setBatteryVoltage(float batteryVoltage) {
        this.batteryVoltage = batteryVoltage;
    }

    public float getCuron() {
        return curon;
    }

    public void setCuron(float curon) {
        this.curon = curon;
    }

    public float getEngCoolant() {
        return engCoolant;
    }

    public void setEngCoolant(float engCoolant) {
        this.engCoolant = engCoolant;
    }
}
