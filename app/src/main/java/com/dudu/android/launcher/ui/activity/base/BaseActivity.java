package com.dudu.android.launcher.ui.activity.base;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import com.dudu.android.launcher.utils.ActivitiesManager;

public abstract class BaseActivity extends Activity {

	protected Context mContext;
	
	protected Activity mActivity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(initContentView());

		ActivitiesManager.getInstance().addActivity(this);
		
		initView(savedInstanceState);
		
		initDatas();
		
		initListener();
	}

	public abstract int initContentView();

	public abstract void initView(Bundle savedInstanceState);

	public abstract void initListener();

	public abstract void initDatas();

	public Context getContext() {
		return mContext;
	}

	public Activity getActivity() {
		return mActivity;
	}

	public void setContext(Context mContext) {
		this.mContext = mContext;
		this.mActivity = (Activity) mContext;
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		ActivitiesManager.getInstance().removeActivity(this);
	}

}
