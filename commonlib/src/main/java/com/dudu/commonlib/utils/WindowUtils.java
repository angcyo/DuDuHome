package com.dudu.commonlib.utils;

import com.dudu.commonlib.CommonLib;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;

public final class WindowUtils {

	// 图片类型
	public static final String DRAWABLE = "drawable";
	// 字符类型
	public static final String STRING = "string";

	public static DisplayMetrics dm = new DisplayMetrics();

	public static WindowManager wm = (WindowManager) CommonLib.getInstance().getContext()
			.getSystemService(Context.WINDOW_SERVICE);



	static {
		wm.getDefaultDisplay().getMetrics(dm);
	}

	/**
	 * 获得屏幕的大小
	 * 
	 * @return new int[]{width,height}
	 */
	public static int[] getScreenWidthAndHeight() {
		return new int[] { dm.widthPixels, dm.heightPixels };
	}

	/**
	 * 获得除了标题栏之外的屏幕的大小
	 * 
	 * @param activity
	 * @return
	 */
	public static int[] getDisplayWidthAndHeight(Activity activity) {
		Rect frame = new Rect();
		activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
		int statusBarHeight = frame.top;
		int[] wh = getScreenWidthAndHeight();
		return new int[] { wh[0], wh[1] - statusBarHeight };
	}

	/**
	 * 获取状态栏宽高
	 * 
	 * @param activity
	 * @return
	 */
	public static int[] getTitleWidthAndHeight(Activity activity) {
		Rect frame = new Rect();
		activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
		int statusBarHeight = frame.top;
		int[] wh = getScreenWidthAndHeight();
		return new int[] { wh[0], statusBarHeight };
	}

	/**
	 * 在onCreate方法或者onstart方法总之窗口没有获得焦点时获取控件 的大小的方法
	 * 
	 * @return new int[]{width,height}
	 */
	public static int[] getViewWidhAndHeight(View view) {
		int w = View.MeasureSpec.makeMeasureSpec(0,
				View.MeasureSpec.UNSPECIFIED);
		int h = View.MeasureSpec.makeMeasureSpec(0,
				View.MeasureSpec.UNSPECIFIED);
		view.measure(w, h);
		int width = view.getMeasuredWidth();
		int height = view.getMeasuredHeight();
		return new int[] { width, height };
	}
}