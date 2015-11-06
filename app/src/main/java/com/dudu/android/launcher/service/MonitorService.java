package com.dudu.android.launcher.service;

import java.util.Date;
import android.app.Service;
import android.content.Intent;
import android.net.TrafficStats;
import android.os.IBinder;
import com.dudu.android.launcher.db.DbHelper;
import com.dudu.android.launcher.utils.LogUtils;


public class MonitorService extends Service {

	private static final String TAG = "MonitorService";

	private static final int WAKE_INTERVAL_MS = 6000;

	private DbHelper mDbHelper;

	private long mMobileRx = 0, mMobileTx = 0, mMobileTotalRx = 0, mMobileTotalTx = 0;

	private long mOldMobileRx = 0, mOldMobileTx = 0;

	private long mDeltaRx = 0, mDeltaTx = 0;

	private MonitorThread mMonitorThread;

	private boolean mMonitoring = true;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		mDbHelper = DbHelper.getDbHelper(MonitorService.this);

		mOldMobileRx = TrafficStats.getMobileRxBytes();
		mOldMobileTx = TrafficStats.getMobileTxBytes();

		mMonitorThread = new MonitorThread();
		mMonitorThread.setPriority(Thread.NORM_PRIORITY - 1);
		mMonitorThread.start();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
//		if (mMonitorThread == null || !mMonitorThread.isAlive()) {
//			mMonitoring = true;
//			mMonitorThread = new MonitorThread();
//			mMonitorThread.start();
//		}
		return START_STICKY;
	}

	private class MonitorThread extends Thread {

		@Override
		public void run() {
			while (mMonitoring) {
				if (Thread.interrupted()) {
					return;
				}

				mMobileRx = TrafficStats.getMobileRxBytes();
				mMobileTx = TrafficStats.getMobileTxBytes();

				if (mMobileRx == -1 && mMobileRx == -1) {
					continue;
				} else {
					mDeltaRx = mMobileRx - mOldMobileRx;
					mOldMobileRx = mMobileRx;
					mDeltaTx = mMobileTx - mOldMobileTx;
					mOldMobileTx = mMobileTx;

					mDeltaRx = (long) ((float) (Math.round(mDeltaRx * 100.0)) / 100);
					mDeltaTx = (long) ((float) (Math.round(mDeltaTx * 100.0)) / 100);
				}

				mMobileTotalRx += mDeltaRx;
				mMobileTotalTx += mDeltaTx;

				if (mMobileTotalRx != 0 || mMobileTotalTx != 0) {
					Date date = new Date();
					if (mDbHelper.checkRecord(1, date)) {
						long up = mDbHelper.getProFlowUp(1, date);
						long dw = mDbHelper.getProFlowDw(1, date);

						mMobileTotalRx += dw;
						mMobileTotalTx += up;

						mDbHelper.updateFlow(mMobileTotalRx, mMobileTotalTx, 1, date);
					} else {
						mDbHelper.insertFlow(mMobileTotalTx, mMobileTotalRx, 1, date);
					}

					mMobileTotalRx = 0;
					mMobileTotalTx = 0;
				}

				try {
					Thread.sleep(WAKE_INTERVAL_MS);
				} catch (InterruptedException e) {
					LogUtils.e(TAG, e.getMessage());
				}
			}
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mMonitorThread == null) {
			return;
		}

		mMonitoring = false;
		mMonitorThread.interrupt();
		try {
			mMonitorThread.join();
		} catch (InterruptedException e) {
			// 忽略
		}

		mMonitorThread = null;
	}

}
