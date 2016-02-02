package com.dudu.android.launcher.ui.adapter;

import java.text.DecimalFormat;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.dudu.android.launcher.R;
import com.dudu.android.launcher.ui.dialog.RouteSearchPoiDialog.OnListItemClick;
import com.dudu.navi.NavigationManager;
import com.dudu.navi.entity.PoiResultInfo;

public class RouteSearchAdapter extends BaseAdapter {
	private List<PoiResultInfo> mPoiItems = null;
	private LayoutInflater mInflater;
	private OnListItemClick mOnListItemClick;
	private int type;			// 手动搜索还是语音搜索

	public RouteSearchAdapter(Context context,int type) {
		mInflater = LayoutInflater.from(context);
		this.type = type;
		mPoiItems = NavigationManager.getInstance(context).getPoiResultList();
	}

	public RouteSearchAdapter(Context context) {
		mInflater = LayoutInflater.from(context);
		mPoiItems = NavigationManager.getInstance(context).getPoiResultList();
	}

	public void setOnListItemClick(OnListItemClick listener) {
		this.mOnListItemClick = listener;
	}

	@Override
	public int getCount() {
		return mPoiItems == null ? 0 : mPoiItems.size();
	}

	@Override
	public Object getItem(int position) {
		return mPoiItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.poi_result_list, parent,
					false);
		}
		TextView PoiName = ((TextView) convertView.findViewById(R.id.poiName));
		TextView poiAddress = (TextView) convertView
				.findViewById(R.id.poiAddress);
		int size = position + 1;
		PoiName.setText(size + "." + mPoiItems.get(position).getAddressTitle());
		String address;
		if (mPoiItems.get(position).getAddressDetial() != null) {
			address = mPoiItems.get(position).getAddressDetial();
		} else {
			address = "中国";
		}
		poiAddress.setText("地址:" + address);
		TextView startNaviBT = (TextView) convertView
				.findViewById(R.id.startNaviBT);
		if(type==1)
			startNaviBT.setVisibility(View.GONE);
		startNaviBT.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(mOnListItemClick!=null)
					mOnListItemClick.onListItemClick(position);
			}
		});

		TextView distanceTV = (TextView) convertView
				.findViewById(R.id.distanceTV);
		double distance = mPoiItems.get(position).getDistance();
		String s = distance > 1000 ? formatMapDouble(distance / 1000) + "千米" : Math
				.round(distance) + "米";
		distanceTV.setText("距离" + s);
		return convertView;
	}

	private double formatMapDouble(double value) {
		DecimalFormat decimalFormat = new DecimalFormat("#.##");
		return Double.parseDouble(decimalFormat.format(value));
	}

	public void initPoiData(Context context) {
		mPoiItems = NavigationManager.getInstance(context).getPoiResultList();
		notifyDataSetChanged();
	}
}
