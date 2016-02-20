package com.dudu.rest.model;

/**
 * Created by Administrator on 2016/2/18.
 */
public class AccTestData {

    private String accTotalTime;
    private String dateTime;
    private String accType;

    public AccTestData(final String accType, final String accTotalTime, final String dateTime) {
        this.accTotalTime = accTotalTime;
        this.dateTime = dateTime;
        this.accType = accType;
    }

    public String getAccTotalTime() {
        return accTotalTime;
    }

    public void setAccTotalTime(String accTotalTime) {
        this.accTotalTime = accTotalTime;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getAccType() {
        return accType;
    }

    public void setAccType(String type) {
        this.accType = type;
    }
}

