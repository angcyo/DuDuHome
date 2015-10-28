package com.dudu.android.launcher.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.dudu.android.launcher.R;

public class StrategyAdapter extends BaseAdapter {
	private Context mContext;
	private String[] mPoiItems = null;
	private LayoutInflater mInflater;
	public StrategyAdapter(Context context, String[] poiItems) {
		this.mContext = context;
		this.mPoiItems = poiItems;
		mInflater = LayoutInflater.from(this.mContext);
	}
	public StrategyAdapter(Context context) {
		this.mContext = context;
		mInflater = LayoutInflater.from(this.mContext);
	}
	@Override
	public int getCount() {
		return mPoiItems == null ? 0 : mPoiItems.length;
	}

	@Override
	public Object getItem(int position) {
		return mPoiItems[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
    public void setStrategyName(String[] str){
    	this.mPoiItems = str;
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
		strategyName.setText(size + "." + mPoiItems[position]);
		return convertView;
	}
}
