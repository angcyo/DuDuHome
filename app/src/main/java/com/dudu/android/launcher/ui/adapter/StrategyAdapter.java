package com.dudu.android.launcher.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.dudu.android.launcher.R;
import com.dudu.navi.NavigationManager;
import com.dudu.navi.vauleObject.NaviDriveMode;

import java.util.List;


public class StrategyAdapter extends BaseAdapter {
	private Context mContext;
	private List<NaviDriveMode> mStrategyMethods = null;
	private LayoutInflater mInflater;
	public StrategyAdapter(Context context) {
		this.mContext = context;
		mInflater = LayoutInflater.from(this.mContext);
		mStrategyMethods = NavigationManager.getInstance(mContext).getDriveModeList();
	}
	@Override
	public int getCount() {
		return mStrategyMethods == null ? 0 : mStrategyMethods.size();
	}

	@Override
	public Object getItem(int position) {
		return mStrategyMethods.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}


	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.strategy_result_list,
					parent, false);
		}

		TextView strategyName = ((TextView) convertView
				.findViewById(R.id.strategyName));
		int size = position + 1;
		strategyName.setText(size + "." + mStrategyMethods.get(position).getName());
		return convertView;
	}
}
