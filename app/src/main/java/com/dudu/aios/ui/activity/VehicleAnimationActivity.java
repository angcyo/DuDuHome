package com.dudu.aios.ui.activity;

import android.content.Context;
import android.content.Intent;

import android.os.Bundle;

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
import com.dudu.aios.ui.vehicle.Vehicle;
import com.dudu.android.launcher.R;
import com.dudu.android.launcher.utils.LogUtils;
import com.dudu.android.launcher.utils.cache.AsyncTask;
import com.dudu.carChecking.VehicleCheckResultAnimation;
import com.dudu.navi.service.SearchProcess;

import java.util.ArrayList;
import java.util.List;

public class VehicleAnimationActivity extends BaseActivity implements View.OnClickListener {

    private RelativeLayout container;

    private ImageButton buttonBack;

    private VehicleCheckResultAnimation vehicleCheckResultAnimation;

    private ListView repairShopList;

    private VehicleAdapter adapter;

    private ArrayList<com.dudu.aios.ui.vehicle.Vehicle> vehicleData;

    private TextView tvCategoryCh, tvCategoryEn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initListener();
        initData();
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
        new LoadVehicleTask().execute();
    }

    private String getCategoryCh(String category) {
        String categoryCh = "";
        switch (category) {
            case "engine":
                categoryCh = "发动机";
                break;
            case "gearbox":
                categoryCh = "变速箱";
                tvCategoryEn.setTextSize(24);
                break;
            case "abs":
                categoryCh = "防抱死";
                break;
            case "wsb":
                categoryCh = "胎压";
                break;
            case "srs":
                categoryCh = "气囊";
                break;
        }

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

    private class LoadVehicleTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            loadVehicles();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            adapter.setData(vehicleData);
        }
    }

    private void loadVehicles() {
        SearchAddress address = new SearchAddress(this);
        address.search("汽车修理店");
        List<Vehicle> list = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            Vehicle vehicle = new Vehicle();
            vehicle.setName("米奇菱加米奇" + i);
            vehicle.setDistance(Float.parseFloat("10." + i));
            vehicle.setGrade(4);
            list.add(vehicle);
        }
        if (list != null && !list.isEmpty()) {
            vehicleData.addAll(list);
        }
    }

    private class VehicleAdapter extends BaseAdapter {
        private Context context;

        private ArrayList<Vehicle> data;

        private LayoutInflater inflater;

        public VehicleAdapter(Context context, ArrayList<Vehicle> data) {
            this.context = context;
            this.data = data;
            inflater = LayoutInflater.from(context);
        }

        public void setData(ArrayList<Vehicle> data) {
            this.data = (ArrayList<Vehicle>) data.clone();
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
            Vehicle vehicle = data.get(position);
            holder.tvName.setText(vehicle.getName());
            holder.tvDistance.setText(getResources().getString(R.string.distance_ch) + String.valueOf(vehicle.getDistance()) + "KM");
            for (int i = 1; i <= 5; i++) {
                ImageView imageView = new ImageView(context);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(5, 0, 5, 0);
                imageView.setLayoutParams(params);
                if (vehicle.getGrade() >= i) {
                    imageView.setImageResource(R.drawable.star_full);
                } else {
                    imageView.setImageResource(R.drawable.star_null);
                }
                holder.gradeContainer.addView(imageView);
            }
            holder.btNavigate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, "走了", Toast.LENGTH_SHORT).show();
                }
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


}
