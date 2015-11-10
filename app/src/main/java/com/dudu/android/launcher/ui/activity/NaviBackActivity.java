package com.dudu.android.launcher.ui.activity;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.AMapNaviListener;
import com.amap.api.navi.AMapNaviView;
import com.amap.api.navi.AMapNaviViewListener;
import com.amap.api.navi.AMapNaviViewOptions;
import com.amap.api.navi.model.AMapNaviInfo;
import com.amap.api.navi.model.AMapNaviLocation;
import com.amap.api.navi.model.NaviInfo;
import com.amap.api.navi.model.NaviLatLng;
import com.dudu.android.launcher.R;
import com.dudu.map.AmapLocationChangeEvent;
import com.dudu.map.AmapLocationHandler;
import com.dudu.map.MapManager;
import com.dudu.map.Navigation;
import com.dudu.map.NavigationHandler;
import com.dudu.voice.semantic.SemanticConstants;
import com.dudu.voice.semantic.VoiceManager;
import com.dudu.android.launcher.ui.activity.base.BaseNoTitlebarAcitivity;
import com.dudu.android.launcher.utils.ActivitiesManager;
import com.dudu.android.launcher.utils.Constants;
import com.dudu.android.launcher.utils.FloatWindowUtil;
import com.dudu.android.launcher.utils.LocationUtils;
import com.dudu.android.launcher.utils.NaviSettingUtil;


import de.greenrobot.event.EventBus;

// 返程导航
public class NaviBackActivity extends BaseNoTitlebarAcitivity implements
AMapNaviViewListener{

	private AMapNaviView mAmapAMapNaviView;
	// 导航可以设置的参数
	private boolean mDayNightFlag = NaviSettingUtil.DAY_MODE;// 默认为白天模式
	private boolean mDeviationFlag = NaviSettingUtil.YES_MODE;// 默认进行偏航重算
	private boolean mJamFlag = NaviSettingUtil.YES_MODE;// 默认进行拥堵重算
	private boolean mTrafficFlag = NaviSettingUtil.OPEN_MODE;// 默认进行交通播报
	private boolean mCameraFlag = NaviSettingUtil.OPEN_MODE;// 默认进行摄像头播报
	private boolean mScreenFlag = NaviSettingUtil.YES_MODE;// 默认是屏幕常亮
	// 导航界面风格
	private int mThemeStle;

	private Button back_button;

	private Handler mHandler;
	private boolean needBack;

	private NavigationHandler navigationHandle;

	@Override
	public int initContentView() {
		return R.layout.activity_navicustom;
	}

	@Override
	public void initView(Bundle savedInstanceState) {

		mAmapAMapNaviView = (AMapNaviView) findViewById(R.id.customnavimap);
		mAmapAMapNaviView.onCreate(savedInstanceState);
		mAmapAMapNaviView.setAMapNaviViewListener(this);
		setAmapNaviViewOptions();
		back_button = (Button) findViewById(R.id.back_button);
		MapManager.getInstance().setNaviBack(true);
		EventBus.getDefault().unregister(this);
		EventBus.getDefault().register(this);

	}

	public void onEventMainThread(AmapLocationChangeEvent event){

	}
	@Override
	public void initListener() {
		back_button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					Activity activity = ActivitiesManager.getInstance().getSecondActivity();
					if(activity!=null){
						startActivity(new Intent(NaviBackActivity.this, activity.getClass()));
					}else{
						startActivity(new Intent(NaviBackActivity.this,MainActivity.class));
					}
				} catch (Exception e) {
					e.printStackTrace();
					startActivity(new Intent(NaviBackActivity.this,MainActivity.class));
				}
				
			}
		});
	}

	@Override
	public void initDatas() {
		mHandler = new Handler();
		initNavi();
	}

	/**
	 * 设置导航的参数
	 */
	private void setAmapNaviViewOptions() {
		if (mAmapAMapNaviView == null) {
			return;
		}
		AMapNaviViewOptions viewOptions = new AMapNaviViewOptions();
		viewOptions.setSettingMenuEnabled(true);// 设置导航setting可用
		viewOptions.setNaviNight(mDayNightFlag);// 设置导航是否为黑夜模式
		viewOptions.setReCalculateRouteForYaw(mDeviationFlag);// 设置导偏航是否重算
		viewOptions.setReCalculateRouteForTrafficJam(mJamFlag);// 设置交通拥挤是否重算
		viewOptions.setTrafficInfoUpdateEnabled(true);// 设置是否更新路况
		viewOptions.setCameraInfoUpdateEnabled(mCameraFlag);// 设置摄像头播报
		viewOptions.setScreenAlwaysBright(mScreenFlag);// 设置屏幕常亮情况
		viewOptions.setNaviViewTopic(mThemeStle);// 设置导航界面主题样式
		viewOptions.setTrafficLayerEnabled(true);
		viewOptions.setTrafficLine(true);
		viewOptions.setTrafficBarEnabled(true);
		mAmapAMapNaviView.setViewOptions(viewOptions);
		mAmapAMapNaviView.getMap().setTrafficEnabled(true);

	}
	// 全程预览
    public void mapPriview(){
    	if(mAmapAMapNaviView!=null&&mAmapAMapNaviView.getMap()!=null){
    		FloatWindowUtil.removeFloatWindow();
    		mAmapAMapNaviView.getMap().moveCamera(CameraUpdateFactory.zoomTo(11));
    	}
    		
    }
    // 路况播报
    public void trafficInfo(){
    	if(mAmapAMapNaviView!=null&&mAmapAMapNaviView.getViewOptions()!=null){
    		FloatWindowUtil.removeFloatWindow();
    		mAmapAMapNaviView.getViewOptions().setTrafficInfoUpdateEnabled(true);
    		VoiceManager.getInstance().startSpeaking("路况播报已打开", SemanticConstants.TTS_DO_NOTHING);
    	}
    }
    // 关闭路况播报
    public void closeTraffic(){
    	if(mAmapAMapNaviView!=null&&mAmapAMapNaviView.getViewOptions()!=null){
    		FloatWindowUtil.removeFloatWindow();
    		mAmapAMapNaviView.getViewOptions().setTrafficInfoUpdateEnabled(false);
    		VoiceManager.getInstance().startSpeaking("路况播报已关闭", SemanticConstants.TTS_DO_NOTHING);
    	}
    }
    public void closePriview(){
    	FloatWindowUtil.removeFloatWindow();
    	mAmapAMapNaviView.getMap().moveCamera(CameraUpdateFactory.zoomTo(20));
    }
    // 继续之前的导航
    public void continueNavi(){
    	final double[] point = LocationUtils.getInstance(this).getNaviEndPoint();
    	final NaviLatLng startLatLng = new NaviLatLng(point[0], point[1]);

    	if(startLatLng!=null){
    		VoiceManager.getInstance().startSpeaking("正在为您进行路线规划");
    		mHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					EventBus.getDefault().post(new Navigation(point, Navigation.NAVI_TWO,AMapNavi.DrivingDefault));
				}
			}, 800);
    		
    	}
    }
    


	/**
	 * 导航界面返回按钮监听
	 * */
	@Override
	public void onNaviCancel() {
		finish();
	}

	/**
	 * 点击设置按钮的事件
	 */
	@Override
	public void onNaviSetting() {
	}

	@Override
	public void onNaviMapMode(int arg0) {

	}

	@Override
	public void onNaviTurnClick() {

		Intent intent = new Intent(NaviBackActivity.this,
				SimpleHudActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		Bundle bundle = new Bundle();
		bundle.putInt(NaviSettingUtil.ACTIVITYINDEX, NaviSettingUtil.EMULATORNAVI);
		intent.putExtras(bundle);
		startActivity(intent);
	}

	@Override
	public void onNextRoadClick() {

	}

	@Override
	public void onScanViewButtonClick() {

	}

	private void processBundle(Bundle bundle) {
		if (bundle != null) {
			mDayNightFlag = bundle.getBoolean(NaviSettingUtil.DAY_NIGHT_MODE,
					mDayNightFlag);
			mDeviationFlag = bundle.getBoolean(NaviSettingUtil.DEVIATION, mDeviationFlag);
			mJamFlag = bundle.getBoolean(NaviSettingUtil.JAM, mJamFlag);
			mTrafficFlag = bundle.getBoolean(NaviSettingUtil.TRAFFIC, mTrafficFlag);
			mCameraFlag = bundle.getBoolean(NaviSettingUtil.CAMERA, mCameraFlag);
			mScreenFlag = bundle.getBoolean(NaviSettingUtil.SCREEN, mScreenFlag);
			mThemeStle = bundle.getInt(NaviSettingUtil.THEME);

		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {

			finish();
		}
		return super.onKeyDown(keyCode, event);
	}

	// ------------------------------生命周期方法---------------------------
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mAmapAMapNaviView.onSaveInstanceState(outState);
	}

	@Override
	public void onResume() {
		super.onResume();
		setAmapNaviViewOptions();
		navigationHandle.initNaviListener();

		Bundle bundle = getIntent().getExtras();
		processBundle(bundle);
		if(bundle!=null){
			String type = bundle.getString("type");
			if(!TextUtils.isEmpty(type)){
				switch (type) {
				case Constants.NAVI_TRAFFIC:
					trafficInfo();
					break;
				case Constants.NAVI_PREVIEW:
					mapPriview();	
					break;
				case Constants.RERURN_JOURNEY:
					needBack = true;
					continueNavi();
					break;
				case Constants.CLOSE+Constants.NAVI_TRAFFIC:
					closeTraffic();
					break;
				case Constants.CLOSE+Constants.NAVI_PREVIEW:
					closePriview();
					break;
				default:
					break;
				}
			}
		}

		mAmapAMapNaviView.onResume();

	}

	@Override
	public void onPause() {
		mAmapAMapNaviView.onPause();
		super.onPause();
	}

	@Override
	public void onDestroy() {
		navigationHandle.destoryAmapNavi();
		MapManager.getInstance().setNaviBack(false);
		mAmapAMapNaviView.onDestroy();
		super.onDestroy();
	}

	@Override
	public void onLockMap(boolean arg0) {

	}


	@Override
	public boolean onNaviBackClick() {
		return false;
	}

	private void initNavi(){
		new Thread(new Runnable() {
			@Override
			public void run() {

				navigationHandle = new NavigationHandler();
				navigationHandle.initNavigationHandle(NaviBackActivity.this);

			}
		}).start();
	}
}
