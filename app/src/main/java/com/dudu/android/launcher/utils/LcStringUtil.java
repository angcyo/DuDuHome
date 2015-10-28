/**
 * 
 */
package com.dudu.android.launcher.utils;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.text.Html;
import android.text.Spanned;
import android.widget.EditText;

import com.amap.api.maps.model.LatLng;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;

@SuppressLint("SimpleDateFormat")
public class LcStringUtil {
	/**
	 * 判断edittext是否null
	 */
	public static String checkEditText(EditText editText) {
		if (editText != null && editText.getText() != null
				&& !(editText.getText().toString().trim().equals(""))) {
			return editText.getText().toString().trim();
		} else {
			return "";
		}
	}

	/**
	 * 判断String是否null
	 */
	public static String checkString(String text) {
		if (text != null && !(text.trim().equals(""))) {
			return text.trim();
		} else {
			return "";
		}
	}

	/**
	 * 判断String是否null true 表示不为null false 表示为null ""
	 */
	public static boolean checkStringNotNull(String text) {
		if (text != null && !(text.trim().equals(""))) {
			return true;
		} else {
			return false;
		}
	}

	public static String formatString(int type, Object object) {
		StringBuffer sBuffer = new StringBuffer();
		switch (type) {
		case 1:
			@SuppressWarnings("unchecked")
			List<PoiItem> poiItems = (List<PoiItem>) object;
			for (int i = 0; i < poiItems.size(); i++) {
				int size = i + 1;
				sBuffer.append(size + "、" + poiItems.get(i).getTitle());
				sBuffer.append("\n");
				String address = poiItems.get(i).getSnippet();
				// if (address != null && !"".equals(address)) {
				sBuffer.append("地址:" + address);
				sBuffer.append("\n");
				// }
			}
			break;
		case 2:
			String[] mStrategyMethods = (String[]) object;
			for (int i = 0; i < mStrategyMethods.length; i++) {
				int size = i + 1;
				sBuffer.append(size + "、" + mStrategyMethods[i]);
				sBuffer.append("\n");
			}
			break;
		}
		return sBuffer.toString();
	}

	public static Spanned stringToSpan(String src) {
		return src == null ? null : Html.fromHtml(src.replace("\n", "<br />"));
	}

	public static String colorFont(String src, String color) {
		StringBuffer strBuf = new StringBuffer();

		strBuf.append("<font color=").append(color).append(">").append(src)
				.append("</font>");
		return strBuf.toString();
	}

	public static String makeHtmlNewLine() {
		return "<br />";
	}

	public static String makeHtmlSpace(int number) {
		final String space = "&nbsp;";
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < number; i++) {
			result.append(space);
		}
		return result.toString();
	}

	public static String getFriendlyLength(int lenMeter) {
		if (lenMeter > 10000) // 10 km
		{
			int dis = lenMeter / 1000;
			return dis + ChString.Kilometer;
		}

		if (lenMeter > 1000) {
			float dis = (float) lenMeter / 1000;
			DecimalFormat fnum = new DecimalFormat("##0.0");
			String dstr = fnum.format(dis);
			return dstr + ChString.Kilometer;
		}

		if (lenMeter > 100) {
			int dis = lenMeter / 50 * 50;
			return dis + ChString.Meter;
		}

		int dis = lenMeter / 10 * 10;
		if (dis == 0) {
			dis = 10;
		}

		return dis + ChString.Meter;
	}

	public static boolean IsEmptyOrNullString(String s) {
		return (s == null) || (s.trim().length() == 0);
	}

	/**
	 * 把LatLng对象转化为LatLonPoint对象
	 */
	public static LatLonPoint convertToLatLonPoint(LatLng latlon) {
		return new LatLonPoint(latlon.latitude, latlon.longitude);
	}

	/**
	 * 把LatLonPoint对象转化为LatLon对象
	 */
	public static LatLng convertToLatLng(LatLonPoint latLonPoint) {
		return new LatLng(latLonPoint.getLatitude(), latLonPoint.getLongitude());
	}

	/**
	 * 把集合体的LatLonPoint转化为集合体的LatLng
	 */
	public static ArrayList<LatLng> convertArrList(List<LatLonPoint> shapes) {
		ArrayList<LatLng> lineShapes = new ArrayList<LatLng>();
		for (LatLonPoint point : shapes) {
			LatLng latLngTemp = LcStringUtil.convertToLatLng(point);
			lineShapes.add(latLngTemp);
		}
		return lineShapes;
	}

	/**
	 * long类型时间格式化
	 */
	public static String convertToTime(long time) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date(time);
		return df.format(date);
	}

	public static final String HtmlBlack = "#000000";
	public static final String HtmlGray = "#808080";

	public static String getDateTime() {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
		String str = formatter.format(curDate);
		return str;
	}
}
