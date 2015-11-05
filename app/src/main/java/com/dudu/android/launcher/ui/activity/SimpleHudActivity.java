package com.dudu.android.launcher.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;

import com.amap.api.navi.AMapHudView;
import com.amap.api.navi.AMapHudViewListener;
import com.amap.api.navi.AMapNavi;
import com.dudu.android.launcher.R;
import com.dudu.android.launcher.ui.activity.base.BaseActivity;
import com.dudu.android.launcher.ui.activity.base.BaseNoTitlebarAcitivity;
import com.dudu.android.launcher.utils.Util;
import com.dudu.map.MapManager;

/**
 * HUD显示界面
 * */
public class SimpleHudActivity extends BaseNoTitlebarAcitivity implements
		AMapHudViewListener {
	private int code = -1;

	private AMapHudView mAmapHudView;

	private Class mclass;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public int initContentView() {
		return R.layout.activity_simple_hud;
	}

	@Override
	public void initView(Bundle savedInstanceState) {
		mAmapHudView = (AMapHudView) findViewById(R.id.hudview);
	}

	@Override
	public void initListener() {
		mAmapHudView.setHudViewListener(this);
	}

	@Override
	public void initDatas() {
	}

	// -----------------HUD返回键按钮事件-----------------------
	@Override
	public void onHudViewCancel() {
		if(MapManager.getInstance().isNavi())
			mclass = NaviCustomActivity.class;
		else if(MapManager.getInstance().isNaviBack())
			mclass = NaviBackActivity.class;

		Intent customIntent = new Intent(SimpleHudActivity.this,
				mclass);
		customIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		startActivity(customIntent);
		finish();
	}

	protected void onResume() {
		super.onResume();
		Bundle bundle = getIntent().getExtras();
		processBundle(bundle);
	}

	private void processBundle(Bundle bundle) {
		if (bundle != null) {

			code = bundle.getInt(Util.ACTIVITYINDEX, -1);
			if (code == Util.SIMPLEHUDNAVIE) {
				AMapNavi.getInstance(this).startNavi(AMapNavi.EmulatorNaviMode);
			}

		}

	}

	/**
	 * 返回键监听
	 * */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onPause() {
		super.onPause();
		mAmapHudView.onPause();

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mAmapHudView.onDestroy();
	}
}
