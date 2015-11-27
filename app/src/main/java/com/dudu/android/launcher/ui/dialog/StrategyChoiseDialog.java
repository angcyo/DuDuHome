package com.dudu.android.launcher.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.maps.model.LatLng;
import com.amap.api.navi.AMapNavi;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.RouteSearch.OnRouteSearchListener;
import com.amap.api.services.route.WalkRouteResult;
import com.dudu.android.launcher.R;
import com.dudu.monitor.Monitor;

public class StrategyChoiseDialog extends Dialog {

    private Context context;

    private OnStrategyClickListener listener;

    private Button backButton;

    private View mStrategy1;

    private View mStrategy2;

    private View mStrategy3;

    private View mStrategy4;

    private String endPoint[];

    private TextView tv_address, tv_address_detial;

    private TextView tv_distance_1, tv_distance_2, tv_distance_3, tv_distance_4;

    private LatLonPoint endpoint = null;

    private AMapLocation cur_Location;

    public StrategyChoiseDialog(Context context) {
        this(context, R.style.RouteSearchPoiDialogStyle);
    }

    public StrategyChoiseDialog(Context context, int theme) {
        super(context, theme);
    }

    public StrategyChoiseDialog(Context context, String[] strategyMethods) {
        this(context, R.style.RouteSearchPoiDialogStyle);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.context = context;

    }

    public StrategyChoiseDialog(Context context, String[] endPoint, LatLonPoint points) {
        this(context, R.style.RouteSearchPoiDialogStyle);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.context = context;

        this.endPoint = endPoint;
        this.endpoint = points;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.route_plan_manual_dialog);

        backButton = (Button) findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        mStrategy1 = findViewById(R.id.ll_plan1);
        mStrategy1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (listener != null) {
                    if (endpoint != null) {
                        listener.onStrategyClick(AMapNavi.DrivingDefault);
                    }

                }
            }
        });

        mStrategy2 = findViewById(R.id.ll_plan2);
        mStrategy2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (listener != null) {
                    if (endpoint != null) {
                        listener.onStrategyClick(AMapNavi.DrivingFastestTime);
                    }

                }
            }
        });

        mStrategy3 = findViewById(R.id.ll_plan3);
        mStrategy3.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (listener != null) {
                    if (endpoint != null) {
                        listener.onStrategyClick(AMapNavi.DrivingShortDistance);
                    }
                }
            }
        });

        mStrategy4 = findViewById(R.id.ll_plan4);
        mStrategy4.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (listener != null) {
                    if (endpoint != null) {
                        listener.onStrategyClick(AMapNavi.DrivingSaveMoney);
                    }

                }
            }
        });
        tv_address = (TextView) findViewById(R.id.tv_detail_address);
        tv_address.setText(endPoint[0]);
        tv_address_detial = (TextView) findViewById(R.id.tv_address);
        tv_address_detial.setText(endPoint[1]);
        tv_distance_1 = (TextView) findViewById(R.id.tv_distance_1);
        tv_distance_2 = (TextView) findViewById(R.id.tv_distance_2);
        tv_distance_3 = (TextView) findViewById(R.id.tv_distance_3);
        tv_distance_4 = (TextView) findViewById(R.id.tv_distance_4);
        getDistance();

    }

    public void setOnStrategyClickListener(OnStrategyClickListener l) {
        listener = l;
    }

    public interface OnStrategyClickListener {
        void onStrategyClick(int position);
    }

    private void getDistance() {
        cur_Location = Monitor.getInstance(context).getCurrentLocation();
        if (cur_Location != null) {
            LatLonPoint startPoint = new LatLonPoint(cur_Location.getLatitude(), cur_Location.getLongitude());
            RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(startPoint, endpoint);
            RouteSearch routeSearch = new RouteSearch(context);
            routeSearch.setRouteSearchListener(listener1);
            // 第一个参数表示路径规划的起点和终点，第二个参数表示驾车模式
            // 第三个参数表示途经点，第四个参数表示避让区域，第五个参数表示避让道路
            RouteSearch.DriveRouteQuery query = new RouteSearch.DriveRouteQuery(fromAndTo, RouteSearch.DrivingMultiStrategy, null, null, "");
            routeSearch.calculateDriveRouteAsyn(query);
            RouteSearch routeSearch2 = new RouteSearch(context);
            routeSearch2.setRouteSearchListener(listener2);
            RouteSearch.DriveRouteQuery query2 = new RouteSearch.DriveRouteQuery(fromAndTo, RouteSearch.DrivingSaveMoney, null, null, "");
            routeSearch2.calculateDriveRouteAsyn(query2);

            RouteSearch routeSearch3 = new RouteSearch(context);
            routeSearch3.setRouteSearchListener(listener3);
            RouteSearch.DriveRouteQuery query3 = new RouteSearch.DriveRouteQuery(fromAndTo, RouteSearch.DrivingAvoidCongestion, null, null, "");
            query3.getPassedByPoints();
            routeSearch3.calculateDriveRouteAsyn(query3);
        }
    }

    OnRouteSearchListener listener1 = new OnRouteSearchListener() {

        @Override
        public void onWalkRouteSearched(WalkRouteResult arg0, int arg1) {

        }

        @Override
        public void onDriveRouteSearched(DriveRouteResult result, int arg1) {

            for (int i = 0; i < result.getPaths().size(); i++) {
                DrivePath path = result.getPaths().get(i);
                String strategy = path.getStrategy();
                float d = path.getDistance();
                String dstr = d >= 1000 ? d / 1000 + "千米" : d + "米";
                if (strategy.equals("速度最快")) {
                    tv_distance_1.setText(dstr);
                }
                if (strategy.equals("距离最短")) {
                    tv_distance_3.setText(dstr);
                }
            }
        }

        @Override
        public void onBusRouteSearched(BusRouteResult arg0, int arg1) {

        }
    };
    OnRouteSearchListener listener2 = new OnRouteSearchListener() {

        @Override
        public void onWalkRouteSearched(WalkRouteResult arg0, int arg1) {

        }

        @Override
        public void onDriveRouteSearched(DriveRouteResult result, int arg1) {

            DrivePath path = result.getPaths().get(0);
            float d = path.getDistance();
            String dstr = d >= 1000 ? d / 1000 + "千米" : d + "米";
            tv_distance_4.setText(dstr);
        }

        @Override
        public void onBusRouteSearched(BusRouteResult result, int arg1) {

        }
    };

    OnRouteSearchListener listener3 = new OnRouteSearchListener() {

        @Override
        public void onWalkRouteSearched(WalkRouteResult arg0, int arg1) {

        }

        @Override
        public void onDriveRouteSearched(DriveRouteResult result, int arg1) {

            DrivePath path = result.getPaths().get(0);
            float d = path.getDistance();
            String dstr = d >= 1000 ? d / 1000 + "千米" : d + "米";
            tv_distance_2.setText(dstr);

        }

        @Override
        public void onBusRouteSearched(BusRouteResult arg0, int arg1) {

        }
    };
}
