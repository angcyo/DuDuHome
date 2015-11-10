package com.dudu.android.launcher;

import android.app.Application;
import android.content.Intent;
import com.dudu.android.launcher.exception.CrashHandler;
import com.dudu.android.launcher.service.NewMessageShowService;
import com.dudu.android.launcher.service.RecordBindService;
import com.dudu.android.launcher.utils.Constants;
import com.dudu.voice.semantic.VoiceManager;
import com.iflytek.cloud.Setting;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;

public class LauncherApplication extends Application {

	public static LauncherApplication mApplication;

	private boolean mReceivingOrder = false;

	private RecordBindService mRecordService;

	@Override
	public void onCreate() {
		super.onCreate();
		mApplication = this;

		// 设置使用v5+
		StringBuffer param = new StringBuffer();
		param.append("appid=" + Constants.XUFEIID);
		param.append(",");
		param.append(SpeechConstant.ENGINE_MODE + "=" + SpeechConstant.MODE_MSC);

		SpeechUtility.createUtility(LauncherApplication.this, param.toString());

		VoiceManager.getInstance().startWakeup();

		Setting.showLogcat(false);

		CrashHandler crashHandler = CrashHandler.getInstance();

		// 注册crashHandler
		crashHandler.init(getApplicationContext());

		startFloatMessageService();
	}

	public static LauncherApplication getContext() {
		return mApplication;
	}

	private void startFloatMessageService() {
		Intent intent = new Intent(LauncherApplication.this, NewMessageShowService.class);
		startService(intent);
	}

	public RecordBindService getRecordService() {
		return mRecordService;
	}

	public boolean isReceivingOrder() {
		return mReceivingOrder;
	}

	public void setReceivingOrder(boolean receivingOrder) {
        mReceivingOrder = receivingOrder;
	}

	public void setRecordService(RecordBindService service) {
		mRecordService = service;
	}

}
