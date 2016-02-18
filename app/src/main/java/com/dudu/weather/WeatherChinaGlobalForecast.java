package com.dudu.weather;

/**
 * Created by Jervis on 2015/9/15.
 */
public class WeatherChinaGlobalForecast {

    private String area;
    private String day;
    private Cond cond;
    private Tmp tmp;
    private Wind wind;

    public String getSpeakWord(int witchDay) {

        day = "今天";

        if (witchDay == 1) {
            day = "明天";
        } else if (witchDay == 2) {
            day = "后天";
        }

        return area + day + "天气," + cond.getTxt() + ",最高气温" + tmp.getMax() + "摄氏度,最低气温" + tmp.getMin() + "摄氏度,风向是" + wind.getDir() + ",风速是" + wind.getSc().replace("-","到");
    }



    public static class Cond {
        private String txt_n;

        public String getTxt() {
            return txt_n;
        }

        public void setTxt(String txt) {
            this.txt_n = txt;
        }
    }


    public static class Tmp {
        private String max;
        private String min;

        public String getMax() {
            return max;
        }

        public void setMax(String max) {
            this.max = max;
        }

        public String getMin() {
            return min;
        }

        public void setMin(String min) {
            this.min = min;
        }
    }

    public static class Wind {
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

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public Cond getCond() {
        return cond;
    }

    public void setCond(Cond cond) {
        this.cond = cond;
    }

    public Tmp getTmp() {
        return tmp;
    }

    public void setTmp(Tmp tmp) {
        this.tmp = tmp;
    }

    public Wind getWind() {
        return wind;
    }

    public void setWind(Wind wind) {
        this.wind = wind;
    }
}
