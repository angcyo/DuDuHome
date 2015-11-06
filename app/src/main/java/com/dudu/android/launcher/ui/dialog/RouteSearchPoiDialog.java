package com.dudu.android.launcher.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.ListView;

import com.dudu.android.launcher.R;
import com.dudu.android.launcher.bean.PoiResultInfo;
import com.dudu.android.launcher.ui.adapter.RouteSearchAdapter;
import com.dudu.voice.semantic.SemanticConstants;
import com.dudu.voice.semantic.VoiceManager;

import java.util.List;

public class RouteSearchPoiDialog extends Dialog implements
		OnItemClickListener, OnItemSelectedListener {

	private List<PoiResultInfo> poiItems;
	private Context context;
	private RouteSearchAdapter adapter;
	protected OnListItemClick mOnClickListener;
	private Button back_button;
	private ListView listView;
    public static final int VIEW_COUNT = 5;				// 每页显示5条
	private int pageIndex = 0;				// 当前页的索引


	public RouteSearchPoiDialog(Context context) {
		this(context, R.style.RouteSearchPoiDialogStyle);
	}

	public RouteSearchPoiDialog(Context context, int theme) {
		super(context, theme);
	}

	public RouteSearchPoiDialog(Context context, List<PoiResultInfo> poiItems) {
		this(context, R.style.RouteSearchPoiDialogStyle);
		this.context = context;
		this.poiItems = poiItems;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.routesearch_list_poi);
		adapter = new RouteSearchAdapter(this.context,poiItems);
		listView = (ListView) findViewById(R.id.search_list_poi);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				dismiss();
				mOnClickListener.onListItemClick(position);
			}
		});

		back_button = (Button) findViewById(R.id.back_button);
		back_button.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
	}

	@Override
	public void onItemClick(AdapterView<?> view, View view1, int arg2, long arg3) {
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {

	}

	public interface OnListItemClick {
		 void onListItemClick(int position);
	}

	public void setOnListClickListener(OnListItemClick l) {
		mOnClickListener = l;
		adapter.setOnListItemClick(l);
	}

	public void nextPage(){
		if(pageIndex>=4){
			VoiceManager.getInstance().startSpeaking("已经是最后一页", SemanticConstants.TTS_DO_NOTHING,false);
			return;
		}

		pageIndex++;
		listView.setSelection(pageIndex * VIEW_COUNT);
	}

	public void lastPage() {
		if(pageIndex>=0){
			VoiceManager.getInstance().startSpeaking("已经是第一页", SemanticConstants.TTS_DO_NOTHING,false);
			return;
		}

		pageIndex--;
		listView.setSelection(pageIndex*VIEW_COUNT);
	}

	public void choosePage(int page){
		if(page > 5|| page < 1){
			VoiceManager.getInstance().startSpeaking("选择错误，请重新选择",SemanticConstants.TTS_DO_NOTHING,false);
			return;
		}
		pageIndex = page;
		listView.setSelection(pageIndex*VIEW_COUNT);
	}
}
