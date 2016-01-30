package com.dudu.aios.ui.fragment.base;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.dudu.android.launcher.R;

public abstract class BaseFragment extends Fragment {


    protected String name;

    private LinearLayout childViewContainer;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.base_title_layout, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        childViewContainer = (LinearLayout) view.findViewById(R.id.child_container);
        childViewContainer.addView(getChildView());
    }

    public abstract View getChildView();

}
