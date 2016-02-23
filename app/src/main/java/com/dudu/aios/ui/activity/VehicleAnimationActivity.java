package com.dudu.aios.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dudu.aios.ui.base.BaseActivity;
import com.dudu.aios.ui.utils.StringUtil;
import com.dudu.aios.ui.vehicle.SearchAddress;
import com.dudu.android.launcher.R;
import com.dudu.android.launcher.utils.Constants;
import com.dudu.android.launcher.utils.LogUtils;
import com.dudu.carChecking.CarNaviChoose;
import com.dudu.carChecking.VehicleCheckResultAnimation;
import com.dudu.map.NavigationProxy;
import com.dudu.navi.entity.Navigation;
import com.dudu.navi.entity.PoiResultInfo;
import com.dudu.navi.entity.Point;
import com.dudu.navi.vauleObject.NaviDriveMode;
import com.dudu.navi.vauleObject.NavigationType;
import com.dudu.voice.VoiceManagerProxy;
import com.dudu.voice.semantic.constant.SceneType;
import com.dudu.voice.semantic.constant.TTSType;
import com.dudu.voice.semantic.engine.SemanticEngine;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

public class VehicleAnimationActivity extends BaseActivity implements View.OnClickListener {

    private RelativeLayout container;

    private ImageButton buttonBack;

    private VehicleCheckResultAnimation vehicleCheckResultAnimation;

    private ListView repairShopList;

    private VehicleAdapter adapter;

    private ArrayList<PoiResultInfo> vehicleData;

    private TextView tvCategoryCh, tvCategoryEn, tvMessage1, tvMessage2;

    private SearchAddress address;

    private int pageIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initListener();
        initData();
        EventBus.getDefault().unregister(this);
        EventBus.getDefault().register(this);
    }

    @Override
    protected View getChildView() {
        return LayoutInflater.from(this).inflate(R.layout.activity_vehicle_animation, null);
    }

    private void initView() {
        container = (RelativeLayout) findViewById(R.id.vehicle_anim_container);
        buttonBack = (ImageButton) findViewById(R.id.button_back);
        repairShopList = (ListView) findViewById(R.id.repair_shop_listView);
        tvCategoryCh = (TextView) findViewById(R.id.vehicle_category_text_ch);
        tvCategoryEn = (TextView) findViewById(R.id.vehicle_category_text_en);
        tvMessage1 = (TextView) findViewById(R.id.text_message1);
//        tvMessage2 = (TextView) findViewById(R.id.text_message2);
    }


    private void initListener() {
        buttonBack.setOnClickListener(this);
        repairShopList.setOnItemClickListener((parent, view, position, id) -> startNavi(position));

    }

    private void initData() {
        Intent intent = getIntent();
        if (intent != null) {
            initViewData(intent);
        }
        initListData();
    }

    private void initViewData(Intent intent) {
        String category = intent.getStringExtra("vehicle");
        LogUtils.v("vehicle", "需要检查的车辆部件:" + category);
        vehicleCheckResultAnimation = new VehicleCheckResultAnimation(this, category);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        vehicleCheckResultAnimation.setZOrderOnTop(true);
        vehicleCheckResultAnimation.getHolder().setFormat(PixelFormat.TRANSLUCENT);
        container.addView(vehicleCheckResultAnimation, params);
        tvCategoryCh.setText(getCategoryCh(category));
        tvCategoryEn.setText(StringUtil.changeUpper(category));

    }

    private void initListData() {
        vehicleData = new ArrayList<>();
        adapter = new VehicleAdapter(this, vehicleData);
        repairShopList.setAdapter(adapter);
        address = new SearchAddress(this);
        address.search("汽车修理店");
        address.setOnGestureLockViewListener(new SearchAddress.OnObtainAddressListener() {
            @Override
            public void onAddress(List<PoiResultInfo> poiResultList) {
                LogUtils.v("kkk", "size:" + poiResultList.size());
                if (poiResultList != null && poiResultList.size() != 0) {
                    vehicleData.addAll(poiResultList);
                    adapter.setData(vehicleData);
                }
            }
        });
    }

    private String getCategoryCh(String category) {
        String categoryCh = "";
        String message = "";
        switch (category) {
            case "engine":
                categoryCh = "发动机";
                message = "质量或体积空气流量传感器B电路故障";
                break;
            case "gearbox":
                categoryCh = "变速箱";
                message = "来自发动机控制模块/动力传动控制模块的数据无效";
                tvCategoryEn.setTextSize(24);
                break;
            case "abs":
                categoryCh = "防抱死";
                message = "回油泵电路";
                break;
            case "wsb":
                categoryCh = "胎压";
                message = "左前轮胎压异常";
                break;
            case "srs":
                categoryCh = "气囊";
                message = "乘客座椅安全带传感器";
                break;
        }
        tvMessage1.setText(message);
//        tvMessage2.setText(message);
        return categoryCh;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_back:
                vehicleCheckResultAnimation.stopAnim();
                Intent intent = new Intent();
                setResult(0, intent);
                finish();
                break;
        }

    }

    private class VehicleAdapter extends BaseAdapter {
        private Context context;

        private ArrayList<PoiResultInfo> data;

        private LayoutInflater inflater;

        public VehicleAdapter(Context context, ArrayList<PoiResultInfo> data) {
            this.context = context;
            this.data = data;
            inflater = LayoutInflater.from(context);
        }

        public void setData(ArrayList<PoiResultInfo> data) {
            this.data = (ArrayList<PoiResultInfo>) data.clone();
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = inflater.inflate(R.layout.repair_shop_item, parent, false);
                holder.tvName = (TextView) convertView.findViewById(R.id.repair_shop_name);
                holder.tvDistance = (TextView) convertView.findViewById(R.id.repair_shop_distance);
                holder.gradeContainer = (LinearLayout) convertView.findViewById(R.id.grade_container);
                holder.btNavigate = (ImageButton) convertView.findViewById(R.id.repair_shop_navigate);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            PoiResultInfo vehicle = data.get(position);
            holder.tvName.setText(vehicle.getAddressTitle());
            DecimalFormat df = new java.text.DecimalFormat("#.##");
            double distance = vehicle.getDistance();
            String unit = "M";
            if (distance >= 1000) {
                distance = distance / 1000;
                unit = "KM";
            }
            holder.tvDistance.setText(getResources().getString(R.string.distance_ch) + df.format(distance) + unit);
            for (int i = 1; i <= 5; i++) {
                ImageView imageView = new ImageView(context);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(5, 0, 5, 0);
                imageView.setLayoutParams(params);
                if (4 >= i) {
                    imageView.setImageResource(R.drawable.star_full);
                } else {
                    imageView.setImageResource(R.drawable.star_null);
                }
                holder.gradeContainer.addView(imageView);
            }
            holder.btNavigate.setOnClickListener(v -> startNavi(position));
            holder.tvDistance.setOnClickListener(v -> startNavi(position));
            holder.gradeContainer.setOnClickListener(v -> startNavi(position));
            return convertView;
        }

        private class ViewHolder {
            TextView tvName;
            TextView tvDistance;
            LinearLayout gradeContainer;
            ImageButton btNavigate;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        vehicleCheckResultAnimation.stopAnim();
        SemanticEngine.getProcessor().switchSemanticType(SceneType.HOME);
        EventBus.getDefault().unregister(this);

    }

    public void onEventMainThread(CarNaviChoose event) {

        pageIndex = (int) Math.floor(repairShopList.getFirstVisiblePosition() / Constants.ADDRESS_VIEW_COUNT);

        switch (event.getType()) {
            case CHOOSE_PAGE:
                choosepage(event.getPosition());
                break;
            case CHOOSE_NUMBER:
                if (event.getPosition() <= 0 || event.getPosition() > 20) {
                    return;
                }
                startNavi(event.getPosition());
                break;
            case NEXT_PAGE:
                nextPage();
                break;
            case LAST_PAGE:
                lastPage();
                break;
        }

    }

    private void startNavi(int position) {

        Log.d("lxh", " carchecking ---------startNavi: ");
        vehicleCheckResultAnimation.stopAnim();
        Navigation navigation = new Navigation(new Point(vehicleData.get(position).getLatitude(), vehicleData.get(position).getLongitude()), NaviDriveMode.FASTESTTIME, NavigationType.NAVIGATION);
        NavigationProxy.getInstance().startNavigation(navigation);
        finish();
    }

    private void choosepage(int page) {

        if (page < 0 || page > 5) {
            VoiceManagerProxy.getInstance().startSpeaking("选择错误，请重新选择", TTSType.TTS_START_UNDERSTANDING, false);
            return;
        }
        pageIndex = page - 1;

        repairShopList.setSelection(pageIndex * Constants.ADDRESS_VIEW_COUNT);
    }

    private void nextPage() {

        if (repairShopList.getLastVisiblePosition() == vehicleData.size() - 1) {
            VoiceManagerProxy.getInstance().startSpeaking("已经是最后一页", TTSType.TTS_START_UNDERSTANDING, false);
            return;
        }
        pageIndex++;
        repairShopList.setSelection(pageIndex * Constants.ADDRESS_VIEW_COUNT);
    }

    private void lastPage() {
        if (repairShopList.getLastVisiblePosition() == 0) {
            VoiceManagerProxy.getInstance().startSpeaking("已经是第一页", TTSType.TTS_START_UNDERSTANDING, false);
            return;
        }
        pageIndex--;
        repairShopList.setSelection(pageIndex * Constants.ADDRESS_VIEW_COUNT);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SemanticEngine.getProcessor().switchSemanticType(SceneType.CAR_CHECKING);
    }
}
