package com.dudu.monitor.utils;

import java.text.DecimalFormat;

/**
 * Created by lxh on 2015/12/17.
 */
public class DataFormatUtil {

    public static double formatDoubleValue(double data, int length) {
        String format = "#0";
        for (int i = 0; i < length; i++) {
            if (i == 0)
                format += ".0";
            else
                format += "0";
        }
        DecimalFormat df1 = new DecimalFormat(format);
        return Double.valueOf(df1.format(data));
    }
}
