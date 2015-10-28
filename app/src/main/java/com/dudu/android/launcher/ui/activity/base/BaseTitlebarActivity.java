package com.dudu.android.launcher.ui.activity.base;

import android.content.Context;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.view.Window;
import android.widget.TextView;

import com.dudu.android.launcher.R;
import com.dudu.android.launcher.utils.LogUtils;
import com.dudu.android.launcher.utils.NetworkUtils;

public abstract class BaseTitlebarActivity extends BaseActivity {

	private static final String TAG = "BaseTitlebarActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		super.onCreate(savedInstanceState);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.activity_custom_title);

		initTitle4G();

		initTitleUserState();
	}

	private void initTitle4G() {
		TextView textView = (TextView) getWindow().findViewById(
				R.id.titlebar_fourGTV);
		TelephonyManager telephoneManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		PhoneStateListener phoneStateListener = new PhoneStateListener() {

			@Override
			public void onSignalStrengthsChanged(SignalStrength signalStrength) {
				super.onSignalStrengthsChanged(signalStrength);
				int strength = signalStrength.getGsmSignalStrength();
				LogUtils.i(TAG, "信号强度  :" + strength);

			}
		};

		telephoneManager.listen(phoneStateListener,
				PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);

		String type = NetworkUtils.getCurrentNetworkType(mContext);
		textView.setText(type);

	}

	private void initTitleUserState() {
		TextView tv = (TextView) getWindow().findViewById(
				R.id.titlebar_userstate);
		tv.setText("用户未激活");
	}

	// 信号格数
	int getGsmSignalStrength(int asu) {
		int iconLevel;
		if (asu <= 2 || asu == 99)
			iconLevel = 0;
		else if (asu >= 12)
			iconLevel = 4;
		else if (asu >= 8)
			iconLevel = 3;
		else if (asu >= 5)
			iconLevel = 2;
		else
			iconLevel = 1;

		return iconLevel;
	}

}
