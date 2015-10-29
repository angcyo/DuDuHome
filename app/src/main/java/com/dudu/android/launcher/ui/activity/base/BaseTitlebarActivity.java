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

		initTitleBar();
	}

	private void initTitleBar() {
		TextView textView = (TextView) getWindow().findViewById(
				R.id.titlebar_fourGTV);
		String type = NetworkUtils.getCurrentNetworkType(mContext);
		textView.setText(type);
	}

}
