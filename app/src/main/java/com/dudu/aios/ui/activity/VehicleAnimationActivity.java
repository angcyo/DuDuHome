package com.dudu.aios.ui.activity;

import android.content.Context;
import android.content.Intent;

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
import android.widget.Toast;

import com.dudu.aios.ui.base.BaseActivity;
import com.dudu.aios.ui.utils.StringUtil;
import com.dudu.aios.ui.vehicle.SearchAddress;
import com.dudu.android.launcher.R;
import com.dudu.android.launcher.utils.LogUtils;
import com.dudu.carChecking.VehicleCheckResultAnimation;
import com.dudu.map.NavigationProxy;
import com.dudu.navi.entity.Navigation;
import com.dudu.navi.entity.PoiResultInfo;
import com.dudu.navi.entity.Point;
import com.dudu.navi.vauleObject.NaviDriveMode;
import com.dudu.navi.vauleObject.NavigationType;
import com.dudu.voice.semantic.constant.SceneType;
import com.dudu.voice.semantic.engine.SemanticEngine;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class VehicleAnimationActivity extends BaseActivity implements View.OnClickListener {

    private RelativeLayout container;

    private ImageButton buttonBack;

    private VehicleCheckResultAnimation vehicleCheckResultAnimation;

    private ListView repairShopList;

    private VehicleAdapter adapter;

    private ArrayList<PoiResultInfo> vehicleData;

    private TextView tvCategoryCh, tvCategoryEn, tvMessage1, tvMessage2;

    private SearchAddress address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initListener();
        initData();

        SemanticEngine.getProcessor().switchSemanticType(SceneType.CAR_CHECKING);
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
        tvMessage2 = (TextView) findViewById(R.id.text_message2);
    }


    private void initListener() {
        buttonBack.setOnClickListener(this);

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
                message = "发动机检测到异常";
                break;
            case "gearbox":
                categoryCh = "变速箱";
                message = "变速箱工作异常";
                tvCategoryEn.setTextSize(24);
                break;
            case "abs":
                categoryCh = "防抱死";
                message = "防抱死机制破损";
                break;
            case "wsb":
                categoryCh = "胎压";
                message = "左前轮胎压异常";
                break;
            case "srs":
                categoryCh = "气囊";
                message = "检测到气囊异常";
                break;
        }
        tvMessage1.setText(message);
        tvMessage2.setText(message);
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
            holder.btNavigate.setOnClickListener(v -> {
                vehicleCheckResultAnimation.stopAnim();
                Navigation navigation = new Navigation(new Point(vehicle.getLatitude(), vehicle.getLongitude()), NaviDriveMode.FASTESTTIME, NavigationType.NAVIGATION);
                NavigationProxy.getInstance().startNavigation(navigation);
            });
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
        SemanticEngine.getProcessor().switchSemanticType(SceneType.HOME);
    }
}
