package com.dudu.aios.ui.map.adapter;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dudu.aios.ui.map.observable.RouteStrategyObservable;
import com.dudu.android.launcher.R;
import com.dudu.android.launcher.databinding.RouteStrategyItemLayoutBinding;
import com.dudu.navi.vauleObject.NaviDriveMode;

import java.util.ArrayList;

/**
 * Created by lxh on 2016/2/15.
 */
public class RouteStrategyAdapter extends RecyclerView.Adapter<RouteStrategyAdapter.RouteStrategyHolder> {


    private ArrayList<RouteStrategyObservable> routeStrategyObservableArrayList;

    private MapListItemClickListener itemClickListener;


    public RouteStrategyAdapter(MapListItemClickListener itemClickListener) {

        this.routeStrategyObservableArrayList = getStrategyList();
        this.itemClickListener = itemClickListener;
    }

    @Override
    public RouteStrategyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.route_strategy_item_layout, parent, false);

        return new RouteStrategyHolder(itemView, itemClickListener);
    }

    @Override
    public void onBindViewHolder(RouteStrategyHolder holder, int position) {
        holder.bind(routeStrategyObservableArrayList.get(position));
    }

    @Override
    public int getItemCount() {
        return routeStrategyObservableArrayList.size();
    }


    class RouteStrategyHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private RouteStrategyItemLayoutBinding routeStrategyItemLayoutBinding;

        private MapListItemClickListener mapListItemClickListener;

        public RouteStrategyHolder(View itemView, MapListItemClickListener listItemClickListener) {
            super(itemView);
            routeStrategyItemLayoutBinding = DataBindingUtil.bind(itemView);
            this.mapListItemClickListener = listItemClickListener;
            itemView.setOnClickListener(this);
        }

        public void bind(@NonNull RouteStrategyObservable routeStrategyObservable) {
            routeStrategyItemLayoutBinding.setStrategy(routeStrategyObservable);
        }

        @Override
        public void onClick(View v) {
            if (mapListItemClickListener != null) {
                mapListItemClickListener.onItemClick(v, getPosition());
            }
        }

    }


    private ArrayList<RouteStrategyObservable> getStrategyList() {

        ArrayList<RouteStrategyObservable> list = new ArrayList<>();

        RouteStrategyObservable drive1 = new RouteStrategyObservable(NaviDriveMode.SPEEDFIRST, "1.");
        RouteStrategyObservable drive2 = new RouteStrategyObservable(NaviDriveMode.SHORTDESTANCE, "2.");
        RouteStrategyObservable drive3 = new RouteStrategyObservable(NaviDriveMode.SAVEMONEY, "3.");
        RouteStrategyObservable drive4 = new RouteStrategyObservable(NaviDriveMode.FASTESTTIME, "4.");
        RouteStrategyObservable drive5 = new RouteStrategyObservable(NaviDriveMode.NOEXPRESSWAYS, "5.");
        RouteStrategyObservable drive6 = new RouteStrategyObservable(NaviDriveMode.AVOIDCONGESTION, "6.");


        list.add(drive1);
        list.add(drive2);
        list.add(drive3);
        list.add(drive4);
        list.add(drive5);
        list.add(drive6);

        return list;

    }


    public ArrayList<RouteStrategyObservable> getDriveModeList() {
        return routeStrategyObservableArrayList;
    }
}
