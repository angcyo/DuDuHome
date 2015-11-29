package com.dudu.android.launcher.exception;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;

import android.content.Context;
import android.text.TextUtils;

import com.dudu.android.launcher.utils.FileUtils;
import com.dudu.android.launcher.utils.LogUtils;
import com.dudu.android.launcher.utils.TimeUtils;

public class CrashHandler implements UncaughtExceptionHandler {

	public static final String TAG = "CrashHandler";

	private static CrashHandler mInstance;

	private Context mContext;

	private UncaughtExceptionHandler mDefaultHandler;

	private CrashHandler() {

	}

	public static CrashHandler getInstance() {
		if (mInstance == null) {
			mInstance = new CrashHandler();
		}

		return mInstance;
	}

	public void init(Context context) {
		mContext = context;
		mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
		Thread.setDefaultUncaughtExceptionHandler(this);
	}

	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		if (!handleException(ex) && mDefaultHandler != null) {
			mDefaultHandler.uncaughtException(thread, ex);
		} else {
			android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(10);
		}
	}

	private boolean handleException(final Throwable ex) {
		if (ex == null) {
			return false;
		}

		new Thread(new Runnable() {

			@Override
			public void run() {
				String fileName = "crash-" + TimeUtils.format(TimeUtils.format1)
						+ ".txt";
				try {
					Writer info = new StringWriter();
					PrintWriter printWriter = new PrintWriter(info);
					ex.printStackTrace(printWriter);

					String result = info.toString();
					printWriter.close();

					FileOutputStream fos = getFileOutputStream(fileName);
					if (!TextUtils.isEmpty(result)) {
						fos.write(result.getBytes());
					}

					fos.flush();
					fos.close();
				} catch (Exception e) {
					LogUtils.e(TAG, e.getMessage());
				}
			}
		}).start();

		return false;
	}

	private FileOutputStream getFileOutputStream(String fileName)
			throws FileNotFoundException {
		if (FileUtils.isSdCard()) {
			File directory = new File(FileUtils.getSdPath() + "/dudu/crash");
			if (!directory.exists()) {
				directory.mkdirs();
			}

			return new FileOutputStream(new File(directory, fileName));
		}

		return mContext.openFileOutput(fileName, Context.MODE_PRIVATE);
	}

}
