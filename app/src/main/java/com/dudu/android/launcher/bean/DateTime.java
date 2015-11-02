package com.dudu.android.launcher.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2015/10/30.
 */
public class DateTime implements Serializable {

    private static final long serialVersionUID = 1L;

    private String type;

    private String date;

    private String dateOrig;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDateOrig() {
        return dateOrig;
    }

    public void setDateOrig(String dateOrig) {
        this.dateOrig = dateOrig;
    }
}
