package com.dudu.android.launcher.ui.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocalWeatherForecast;
import com.amap.api.location.AMapLocalWeatherListener;
import com.amap.api.location.AMapLocalWeatherLive;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.services.core.PoiItem;
import com.dudu.android.launcher.LauncherApplication;
import com.dudu.android.launcher.R;
import com.dudu.android.launcher.core.manager.MapManager;
import com.dudu.android.launcher.service.RecordBindService;
import com.dudu.android.launcher.service.RecordBindService.MyBinder;
import com.dudu.android.launcher.ui.activity.base.BaseTitlebarActivity;
import com.dudu.android.launcher.ui.activity.video.VideoActivity;
import com.dudu.android.launcher.utils.Constants;
import com.dudu.android.launcher.utils.LocationUtils;
import com.dudu.android.launcher.utils.LogUtils;
import com.dudu.android.obd.OBDDataService;

public class MainActivity extends BaseTitlebarActivity implements
		OnClickListener, AMapLocalWeatherListener {

	private static final String TAG = "MainActivity";

	private Button mVideoButton, mNavigationButton, mPhoneButton,
			mNearbyButton, mWlanButton;
	private LinearLayout nearbyLL;

	private ProgressBar mFlowProgressbar;
	private FlowUpdateReciever mFlowReciever;
	private LocationManagerProxy mLocationManagerProxy;
	private TextView mDateTextView, mWeatherView, mTemperatureView;

	private ImageView mWeatherImage;

	private RecordBindService mRecordService = null;
	private ServiceConnection serviceConnection;
	private Timer timer;

	private List<PoiItem> poiList = new ArrayList<PoiItem>();

	private SensorManager mSensorManager;

	private Sensor mLigthSensor;

	private LinearLayout mActivationContainer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		registerFlowReciever();
		startOBDService();
		startFloatMessageShowService();
	}

	@Override
	public int initContentView() {
		return R.layout.main_layout;
	}

	@Override
	public void initView(Bundle savedInstanceState) {

		setContext(this);

		nearbyLL = (LinearLayout) findViewById(R.id.nearbyLL);
		mPhoneButton = (Button) findViewById(R.id.phone_button);
		mNearbyButton = (Button) findViewById(R.id.nearby_button);
		mWlanButton = (Button) findViewById(R.id.wlan_button);
		mVideoButton = (Button) findViewById(R.id.video_button);
		mNavigationButton = (Button) findViewById(R.id.navigation_button);
		mDateTextView = (TextView) findViewById(R.id.date_text);

		mFlowProgressbar = (ProgressBar) findViewById(R.id.flow_progressbar);
		mWeatherView = (TextView) findViewById(R.id.weather_text);
		mTemperatureView = (TextView) findViewById(R.id.temperature_text);
		mWeatherImage = (ImageView) findViewById(R.id.weather_image);

		mActivationContainer = (LinearLayout) findViewById(R.id.activation_container);
	}

	@Override
	public void initListener() {
		mPhoneButton.setOnClickListener(this);
		mNearbyButton.setOnClickListener(this);
		mWlanButton.setOnClickListener(this);
		mVideoButton.setOnClickListener(this);
		mNavigationButton.setOnClickListener(this);
		mActivationContainer.setOnClickListener(this);
		mNearbyButton.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				Intent intent = new Intent();
				intent.setComponent(new ComponentName("com.android.settings",
						"com.android.settings.Settings"));
				startActivity(intent);
				return true;
			}
		});
	}

	@Override
	public void initDatas() {
		startFloatMessageShowService();

		initNeabyPoi();

		getDate();

		initVideoService();

		initWeatherInfo();

		initLightSensor();

		startService(new Intent(MainActivity.this, MonitorService.class));
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		unregisterFlowReciever();

		if (timer != null) {
			timer.cancel();
		}

		if (serviceConnection != null)
			unbindService(serviceConnection);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.video_button:
			if (!recordService.isCarDriving()) {
				recordService.startRecord();
				recordService.startRecordTimer();
				recordService.setWatchingVideo(true);
			}

			startActivity(new Intent(mContext, VideoActivity.class));
			break;

		case R.id.nearby_button:
			// NearbyPoiActivity.launch(this);
			// openApp("com.sdu.didi.gsui");
			// openApp("com.estrongs.android.pop");
			// Intent intent = new Intent();
			// intent.setComponent(new ComponentName("com.android.dialer",
			// "com.android.dialer.WldDialtactsActivity"));
			// startActivity(intent);
			// Intent intent = new Intent();
			// intent.setComponent(new ComponentName("com.android.settings",
			// "com.android.settings.Settings"));
			// startActivity(intent);
			break;

		case R.id.wlan_button:
			startActivity(new Intent(mContext, ActivationActivity.class));
			break;

		case R.id.navigation_button:
			Intent navigationintent = new Intent();
			Bundle bundle = new Bundle();
			bundle.putBoolean("isManual", true);
			
//			String mapType = MapChooseUtil.getMapType(this);
			if (MapManager.getInstance().isNavi() ||MapManager.getInstance().isNaviBack()) {
					if (MapManager.getInstance().isNavi()) {
						navigationintent.setClass(mContext,
								NaviCustomActivity.class);
					} else if (MapManager.getInstance().isNaviBack()) {
						navigationintent.setClass(mContext,
								NaviBackActivity.class);
					}
			} else {
				navigationintent.setClass(mContext, LocationActivity.class);

			}

			startActivity(navigationintent);
			break;

		case R.id.activation_container:
			break;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void registerFlowReciever() {
		mFlowReciever = new FlowUpdateReciever();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(Constants.FLOW_UPDATE_BROADCAST);
		registerReceiver(mFlowReciever, intentFilter);
	}

	private void unregisterFlowReciever() {
		if (mFlowReciever != null) {
			unregisterReceiver(mFlowReciever);
		}
	}

	/**
	 * 实例化日期
	 */
	private void getDate() {
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"yyyy年MM月dd日   EEEE", Locale.getDefault());
		mDateTextView.setText(dateFormat.format(new Date()));
	}

	/**
	 * 实例化附近接单数据
	 */
	private void initNeabyPoi() {
		for (int i = 0; i < 9; i++) {
			PoiItem pi = new PoiItem("", null, "", "");
			poiList.add(pi);
		}

		BadgeView badge = new BadgeView(this, nearbyLL);
		badge.setText("" + poiList.size());
		badge.setBadgeMargin(38, 8);
		badge.show();
	}

	/**
	 * 实例化光感
	 */
	private void initLightSensor() {

		// 获取SensorManager对象
		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		// 获取Sensor对象
		mLigthSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

		mSensorManager.unregisterListener(new MySensorListener(), mLigthSensor);
	}

	/**
	 * 实例化录像服务
	 */
	private void initVideoService() {

		serviceConnection = new ServiceConnection() {

			@Override
			public void onServiceDisconnected(ComponentName name) {
				mRecordService = null;
			}

			@Override
			public void onServiceConnected(ComponentName name, IBinder service) {
				mRecordService = ((MyBinder) service).getService();
				((LauncherApplication) getApplicationContext())
						.setRecordService(recordService);
			}
		};

		Intent intent = new Intent(mContext, RecordBindService.class);
		bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
	}

	/**
	 * 实例化请求地图天气接口
	 */
	private void initWeatherInfo() {
		mLocationManagerProxy = LocationManagerProxy.getInstance(this);
		mLocationManagerProxy.requestWeatherUpdates(
				LocationManagerProxy.WEATHER_TYPE_LIVE, this);

		if (timer == null) {
			timer = new Timer();
		}

		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				requestWeatherInfo();
			}
		}, 10 * 1000, 60 * 60 * 1000);
	}

	private void requestWeatherInfo() {
		if (mLocationManagerProxy != null) {
			mLocationManagerProxy.requestWeatherUpdates(
					LocationManagerProxy.WEATHER_TYPE_LIVE, MainActivity.this);
		}
	}

	/**
	 * 更新流量进度条
	 */
	private void updateFlowUsage() {
		mFlowProgressbar.setProgress(0);
	}

	private class FlowUpdateReciever extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			updateFlowUsage();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		requestWeatherInfo();
	}

	@Override
	public void onWeatherForecaseSearched(AMapLocalWeatherForecast arg0) {

	}

	@Override
	public void onWeatherLiveSearched(AMapLocalWeatherLive aMapLocalWeatherLive) {
		if (aMapLocalWeatherLive != null
				&& aMapLocalWeatherLive.getAMapException().getErrorCode() == 0) {
			String weather = aMapLocalWeatherLive.getWeather();
			String temperature = aMapLocalWeatherLive.getTemperature();
			if (TextUtils.isEmpty(weather) || TextUtils.isEmpty(temperature)) {
				return;
			}

			if (weather.contains("-")) {
				weather = weather
						.replace("-", getString(R.string.weather_turn));
			}

			mTemperatureView.setText(temperature
					+ getString(R.string.temperature_degree));
			mWeatherView.setText(weather);
			mWeatherImage.setImageResource(WeatherIconsUtils
					.getWeatherIcon(WeatherIconsUtils.getWeatherType(weather)));
			LocationUtils.getInstance(this).setCurrentCity(aMapLocalWeatherLive.getCity());
		} else {
			Toast.makeText(this, R.string.get_weather_info_failed,
					Toast.LENGTH_SHORT).show();
			mWeatherView.setText(R.string.unkown_weather_info);
		}
	}

	private class MySensorListener implements SensorEventListener {

		private StringBuffer sb;

		public void onAccuracyChanged(Sensor sensor, int accuracy) {
			LogUtils.i(TAG, "onAccuracyChanged accuracy is : " + accuracy);
		}

		public void onSensorChanged(SensorEvent event) {
			// 获取精度
			float acc = event.accuracy;
			// 获取光线强度
			float lux = event.values[0];

			sb.append("acc ----> " + acc);
			sb.append("\n");
			sb.append("lux ----> " + lux);
			sb.append("\n");

			LogUtils.i(TAG, sb.toString());
		}
	}

	private void startOBDService() {
		Intent i = new Intent(MainActivity.this, OBDDataService.class);
		startService(i);
	}

	private void startFloatMessageShowService() {
		Intent i = new Intent(MainActivity.this, NewMessageShowService.class);
		startService(i);
	}

	private void openApp(String packageName) {
		Intent intent = new Intent();
		PackageManager packageManager = this.getPackageManager();
		intent = packageManager.getLaunchIntentForPackage(packageName);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
				| Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
				| Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}

}
