package com.dudu.weather;

/**
 * Created by Jervis on 2015/9/14.
 */
public class WeatherChinaGlobalToday {

    private String area;
    private Cond cond;
    private String tmp;
    private Wind wind;

    public String getSpeakWord(){
        return area+"今天天气,"+cond.getTxt()+",平均气温"+tmp+"摄氏度,风向是"+wind.getDir()+",风速是"+wind.getSc().replace("-","到");
    }

    public static class Cond{
        private String txt;

        public String getTxt() {
            return txt;
        }

        public void setTxt(String txt) {
            this.txt = txt;
        }
    }

    public static class Wind{
        private String dir;
        private String sc;

        public String getDir() {
            return dir;
        }
        public void setDir(String dir) {
            this.dir = dir;
        }
        public String getSc() {
            return sc;
        }
        public void setSc(String sc) {
            this.sc = sc;
        }
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getArea() {
        return area;
    }

    public Cond getCond() {
        return cond;
    }

    public void setCond(Cond cond) {
        this.cond = cond;
    }

    public String getTmp() {
        return tmp;
    }

    public void setTmp(String tmp) {
        this.tmp = tmp;
    }

    public Wind getWind() {
        return wind;
    }

    public void setWind(Wind wind) {
        this.wind = wind;
    }
}
