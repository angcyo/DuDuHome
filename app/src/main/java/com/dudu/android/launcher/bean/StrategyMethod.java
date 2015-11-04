package com.dudu.android.launcher.bean;

/**
 * Created by pc on 2015/11/3.
 */
public class StrategyMethod {

    private String strategy;

    private int driveMode;

    public StrategyMethod(){

    }

    public StrategyMethod(String strategy,int driveMode){

        this.strategy = strategy;
        this.driveMode = driveMode;
    }

    public void setDriveMode(int driveMode) {
        this.driveMode = driveMode;
    }

    public void setStrategy(String strategy) {

        this.strategy = strategy;
    }

    public int getDriveMode() {

        return driveMode;
    }

    public String getStrategy() {

        return strategy;
    }
}
