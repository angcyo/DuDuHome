package com.dudu.aios.ui.map.adapter;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dudu.aios.ui.map.MapListItemObservable;
import com.dudu.android.launcher.R;
import com.dudu.android.launcher.databinding.MapListItemLayoutBinding;

import java.util.ArrayList;

/**
 * Created by lxh on 2016/2/14.
 */
public class MapListAdapter extends RecyclerView.Adapter<MapListAdapter.MapListItemHolder> {
    private ArrayList<MapListItemObservable> mapListObservableArrayList;


    public MapListAdapter(ArrayList<MapListItemObservable> mapListObservableArrayList){
        this.mapListObservableArrayList = mapListObservableArrayList;
    }

    @Override
    public MapListItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.map_list_item_layout, parent, false);

        return new MapListItemHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MapListItemHolder holder, int position) {
        holder.bind(mapListObservableArrayList.get(position));
    }

    @Override
    public int getItemCount() {
        return mapListObservableArrayList.size();
    }

    class MapListItemHolder extends RecyclerView.ViewHolder {

        private MapListItemLayoutBinding mapListItemLayoutBinding;

        public MapListItemHolder(View itemView) {
            super(itemView);
            mapListItemLayoutBinding = DataBindingUtil.bind(itemView);
        }

        public void bind(@NonNull MapListItemObservable mapListItemObservable) {
            mapListItemLayoutBinding.setMapItem(mapListItemObservable);
        }

    }
}
