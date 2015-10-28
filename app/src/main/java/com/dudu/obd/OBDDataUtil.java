package com.dudu.obd;

import java.text.DecimalFormat;

public class OBDDataUtil {
	// 格式化数据
	public static double formatData(double data, int length) {
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
