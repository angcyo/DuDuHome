package com.dudu.android.launcher.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.util.Log;

public class ActivitiesManager {
	
	public static final int EXIT_APPLICATION = 0x0001;

	private LinkedList<Activity> mActivities;
	
	private static ActivitiesManager mInstance = null;

	private ActivitiesManager() {
		mActivities = new LinkedList<Activity>();
	};

	public synchronized static ActivitiesManager getInstance() {
		if (mInstance == null) {
			mInstance = new ActivitiesManager();
		}
		
		return mInstance;
	}

	public void addActivity(Activity activity) {
		synchronized (ActivitiesManager.this) {
			mActivities.addFirst(activity);
		}
	}

	public void removeActivity(Activity activity) {
		synchronized (ActivitiesManager.this) {
			if (mActivities != null && mActivities.indexOf(activity) >= 0) {
				mActivities.remove(activity);
			}
		}
	}

	public Activity getTopActivity() {
		synchronized (ActivitiesManager.this) {
			return (mActivities == null || mActivities.size() <= 0) ? null : mActivities.get(0);
		}
	}

	public void setTopActivity(Activity activity){
		synchronized (ActivitiesManager.this) {
			if (mActivities!=null){
				mActivities.add(0,activity);
			}
		}
	}

	public Activity getSecondActivity() {
		synchronized (ActivitiesManager.this) {
			return (mActivities == null || mActivities.size() <= 1) ? null : mActivities.get(1);
		}
	}

	public void closeAll() {
		synchronized (ActivitiesManager.this) {
			Activity act;
			while (mActivities.size() != 0) {
				act = mActivities.poll();
				act.finish();
			}
		}
	}

	/**
	 * 关闭其他activity，唯独排除activityClass指定的activity
	 * 
	 * @param activityClass
	 */
	public void closeExcept(Class<?> activityClass) {
		synchronized (ActivitiesManager.this) {
			Activity act;
			Iterator<Activity> activityIterator = mActivities.iterator();
			while (activityIterator.hasNext()) {
				act = activityIterator.next();
				if (!act.getClass().getName().equals(activityClass.getName())) {
					act.finish();
					activityIterator.remove();
				}
			}
		}
	}

	/**
	 * 关闭activityClass指定的activity
	 * 
	 * @param activityClass
	 */
	public void closeTargetActivity(Class<?> activityClass) {
		synchronized (ActivitiesManager.this) {
			Activity act;
			Iterator<Activity> activityIterator = mActivities.iterator();
			while (activityIterator.hasNext()) {
				act = activityIterator.next();
				if (act.getClass().getName().equals(activityClass.getName())) {
					act.finish();
					activityIterator.remove();
				}
			}
		}
	}

	public ArrayList<Activity> getTargetActivity(Class<?> activityClass) {
		ArrayList<Activity> activities = new ArrayList<Activity>();
		synchronized (ActivitiesManager.this) {
			Activity act;
			int size = mActivities.size();
			for (int i = 0; i < size; i++) {
				act = mActivities.get(i);
				if (act.getClass().getName().equals(activityClass.getName())) {
					activities.add(act);
				}
			}
		}

		return activities;
	}

	public boolean hasActivity(Class<?> activityClass) {
		synchronized (ActivitiesManager.this) {
			int size = mActivities.size();
			for (int i = 0; i < size; i++) {
				Activity act = mActivities.get(i);
				if (act.getClass().getName().equals(activityClass.getName())) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 判断应用是否在运行
	 * 
	 * @param context
	 * @param MY_PKG_NAME
	 * @return
	 */
	public boolean isActivityRunning(Context context, String MY_PKG_NAME) {
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> list = am.getRunningTasks(20);
		for (RunningTaskInfo info : list) {
			if (info.topActivity.getPackageName().equals(MY_PKG_NAME)
					|| info.baseActivity.getPackageName().equals(MY_PKG_NAME)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 检测某ActivityUpdate是否在当前Task的栈顶
	 * 
	 * @param context
	 * @param packageName
	 * @return
	 */
	public boolean isTopActivy(Context context, String packageName) {
		ActivityManager manager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> runningTaskInfos = manager.getRunningTasks(1);
		String cmpNameTemp = null;

		if (null != runningTaskInfos) {
			cmpNameTemp = (runningTaskInfos.get(0).topActivity).toString();
		}

		if (null == cmpNameTemp)
			return false;
		return cmpNameTemp.equals(packageName);
	}

	/**
	 * 检测某ActivityUpdate是否在当前Task的栈顶
	 * 
	 * @param context
	 * @param packageName
	 * @return
	 */
	public boolean isTopActivity(Context context, String packageName) {
		ActivityManager manager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> tasksInfo = manager.getRunningTasks(1);
		if (tasksInfo.size() > 0) {

			 Log.d("lxh"," top activity >>>>>>>>>>>>>>>>>>>>>>>>"+tasksInfo.get(0).topActivity.getClass().toString());
			// 应用程序位于堆栈的顶层
			if (packageName.equals(tasksInfo.get(0).topActivity
					.getPackageName())) {
				return true;
			}
		}
		return false;
	}

}
