package com.dudu.android.launcher.ui.adapter;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import com.dudu.android.launcher.R;
import com.dudu.android.launcher.ui.dialog.RouteSearchPoiDialog.OnListItemClick;

public class NearbyRepairAdapter extends BaseAdapter {
	private Context mContext;
	private List<Map<String, String>> dataList;
	private LayoutInflater mInflater;
	private OnListItemClick mOnListItemClick;

	public NearbyRepairAdapter(Context context, List<Map<String, String>> dataList) {
		this.mContext = context;
		this.dataList = dataList;
		mInflater = LayoutInflater.from(context);

	}

	public void setData(List<Map<String, String>> dataList) {
		this.dataList = dataList;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return dataList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return dataList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}
	

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.nearyby_list_item, parent, false);
		}
		Map<String, String> map = dataList.get(position);
		TextView tvName = (TextView) convertView.findViewById(R.id.Brand_name);
		tvName.setText(map.get("name"));
		RatingBar ratingBar = (RatingBar) convertView.findViewById(R.id.nearyby_ratingBar);
		ratingBar.setRating(Float.parseFloat(map.get("Rating")));
		TextView distance = (TextView) convertView.findViewById(R.id.tv_Distance);
		distance.setText("距离" + map.get("distance") + "千米");
		TextView btn = (TextView)convertView.findViewById(R.id.btn_appointment);
		btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				if(mOnListItemClick!=null)
					mOnListItemClick.onListItemClick(position);
			}
		});
		return convertView;
	}

	public interface OnListItemClick {
        void onListItemClick(int position);
	}

	public void setOnListItemClick(OnListItemClick listener) {
		this.mOnListItemClick = listener;
	}
}
