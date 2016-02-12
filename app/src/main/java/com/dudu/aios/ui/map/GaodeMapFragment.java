package com.dudu.aios.ui.map;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.amap.api.maps.MapView;
import com.dudu.aios.ui.fragment.MainFragment;
import com.dudu.android.launcher.R;

/**
 * Created by dudusmart on 16/2/11.
 */
public class GaodeMapFragment extends Fragment implements View.OnClickListener{

    private MapView mapView;

    private Button backButton;


    private void initView(View view) {

        mapView = (MapView) view.findViewById(R.id.gaode_MapView);
        backButton  = (Button)view.findViewById(R.id.map_BackButton);
        backButton.setOnClickListener(this);
    }

    private void initData() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.gaode_map_layout, container, false);
        initView(view);
        mapView.onCreate(savedInstanceState);
        initData();
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.map_BackButton:
                getActivity().getFragmentManager().beginTransaction().replace(R.id.container,new MainFragment()).commit();
                this.onDestroy();
                break;
        }
    }

}
