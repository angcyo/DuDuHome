package com.dudu.android.launcher.ui.activity;

import android.location.Location;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.maps.LocationSource;
import com.amap.api.navi.AMapNavi;
import com.dudu.android.launcher.R;
import com.dudu.android.launcher.ui.activity.base.BaseNoTitlebarAcitivity;
import com.dudu.android.launcher.ui.adapter.NearbyRepairAdapter;
import com.dudu.map.Navigation;
import com.dudu.voice.semantic.SemanticConstants;
import com.dudu.voice.semantic.SemanticType;
import com.dudu.voice.semantic.VoiceManager;
import com.dudu.voice.semantic.engine.SemanticProcessor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import de.greenrobot.event.EventBus;

public class NearbyRepairActivity extends BaseNoTitlebarAcitivity implements
		LocationSource, AMapLocationListener {

	private ListView mGridView;

	private Button back_button;

	private NearbyRepairAdapter mNearbyRepairAdapter;

	private List<Map<String, String>> dataList;

	private LocationManagerProxy mAMapLocationManager;

	private VoiceManager mVoiceManager;

	@Override
	public int initContentView() {
		return R.layout.activity_nearyby_repair;
	}

	@Override
	public void initView(Bundle savedInstanceState) {
		mGridView = (ListView) findViewById(R.id.nearby_repair_listView);
		back_button = (Button) findViewById(R.id.back_button);
		if (mAMapLocationManager == null) {
			mAMapLocationManager = LocationManagerProxy.getInstance(this);
			mAMapLocationManager.requestLocationData(
					LocationProviderProxy.AMapNetwork, 2000, 10, this);
		}
	}

	@Override
	public void initListener() {
		back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
	}

	@Override
	public void initDatas() {
		// 初始语音播报资源
		setVolumeControlStream(AudioManager.STREAM_MUSIC);// 设置声音控制

		setDatas();
		mNearbyRepairAdapter = new NearbyRepairAdapter(this, dataList);
		mGridView.setAdapter(mNearbyRepairAdapter);
		mGridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

                double[] location = {23.156596,113.30791};
                EventBus.getDefault().post(new Navigation(location,Navigation.NAVI_NORMAL,AMapNavi.DrivingDefault));
			}
		});

		mNearbyRepairAdapter.setOnListItemClick(new NearbyRepairAdapter.OnListItemClick() {

            @Override
            public void onListItemClick(int position) {
                double[] location = {23.156596,113.30791};
                EventBus.getDefault().post(new Navigation(location, Navigation.NAVI_NORMAL, AMapNavi.DrivingDefault));
            }
        });

        mVoiceManager = VoiceManager.getInstance();
		String playText = "为您找到如下汽车修理店，请选择第几个";
		mVoiceManager.startSpeaking(playText, SemanticConstants.TTS_START_UNDERSTANDING, false);
	}

	private void setDatas() {
		dataList = new ArrayList<>();
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("name", "1.米其林驰加店");
		map.put("Rating", "5");
		map.put("distance", "3.5");
		map.put("e_lat", "23.156596");
		map.put("e_lon", "113.30791");
		dataList.add(map);

		HashMap<String, String> map2 = new HashMap<String, String>();
		map2.put("name", "2.马牌轮胎护理中心");
		map2.put("Rating", "4.5");
		map2.put("distance", "1.8");
		map2.put("e_lat", "23.156596");
		map2.put("e_lon", "113.30791");
		dataList.add(map2);

		HashMap<String, String> map3 = new HashMap<String, String>();
		map3.put("name", "3.固特异(Goodyear)");
		map3.put("Rating", "4.5");
		map3.put("distance", "1.9");
		map3.put("e_lat", "23.156596");
		map3.put("e_lon", "113.30791");
		dataList.add(map3);

		HashMap<String, String> map4 = new HashMap<String, String>();
		map4.put("name", "4.韩泰轮胎");
		map4.put("Rating", "4");
		map4.put("distance", "2.5");
		dataList.add(map4);

		HashMap<String, String> map5 = new HashMap<String, String>();
		map5.put("name", "5.普利斯轮胎护理中心");
		map5.put("Rating", "4");
		map5.put("distance", "2.75");
		dataList.add(map5);

		HashMap<String, String> map6 = new HashMap<String, String>();
		map6.put("name", "6.米其林驰加店");
		map6.put("Rating", "4");
		map6.put("distance", "3.2");
		dataList.add(map6);

		HashMap<String, String> map7 = new HashMap<String, String>();
		map7.put("name", "7.马牌轮胎护理中心");
		map7.put("Rating", "4");
		map7.put("distance", "3.8");
		dataList.add(map7);

		HashMap<String, String> map8 = new HashMap<String, String>();
		map8.put("name", "8.固特异(Goodyear)");
		map8.put("Rating", "5");
		map8.put("distance", "3");
		dataList.add(map8);

		HashMap<String, String> map9 = new HashMap<String, String>();
		map9.put("name", "9.韩泰轮胎");
		map9.put("Rating", "4");
		map9.put("distance", "5.2");
		dataList.add(map9);

		HashMap<String, String> map10 = new HashMap<String, String>();
		map10.put("name", "10.普利斯轮胎护理中心");
		map10.put("Rating", "3");
		map10.put("distance", "5.5");
		dataList.add(map10);
	}

	@Override
	public void onLocationChanged(Location arg0) {

	}

	@Override
	public void onProviderDisabled(String arg0) {

	}

	@Override
	public void onProviderEnabled(String arg0) {

	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {

	}

	@Override
	public void onLocationChanged(AMapLocation alocation) {

	}

	@Override
	public void activate(OnLocationChangedListener listener) {

	}

	@Override
	public void deactivate() {
		if (mAMapLocationManager != null) {
			mAMapLocationManager.removeUpdates(this);
			mAMapLocationManager.destroy();
		}

		mAMapLocationManager = null;
	}

    public void onBackPressed(View v) {
        SemanticProcessor.getProcessor().switchSemanticType(SemanticType.NORMAL);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SemanticProcessor.getProcessor().switchSemanticType(SemanticType.NORMAL);
    }

}
