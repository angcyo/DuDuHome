package com.dudu.aios.ui.map;

import android.content.Context;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;

import com.dudu.aios.ui.map.adapter.MapListAdapter;
import com.dudu.android.launcher.R;
import com.dudu.android.launcher.databinding.GaodeMapLayoutBinding;
import com.dudu.android.launcher.utils.ToastUtils;

import java.util.ArrayList;

/**
 * Created by lxh on 2016/2/13.
 */
public class MapObservable {
    public final ObservableBoolean showList = new ObservableBoolean();
    public final ObservableBoolean showBottomButton = new ObservableBoolean();
    public final ObservableField<String> mapListTitle = new ObservableField<>();
    public final ObservableBoolean showEdt = new ObservableBoolean();
    public final ObservableInt historyCount = new ObservableInt();
    public final ObservableBoolean showDelete = new ObservableBoolean();

    private MapListAdapter mapListAdapter;

    private GaodeMapLayoutBinding binding;

    private Context mContext;

    private ArrayList<MapListItemObservable> mapListItemObservableArrayList;

    public MapObservable(GaodeMapLayoutBinding binding) {

        this.binding = binding;
        this.mContext = binding.getRoot().getContext();

        setDefault();
    }

    private void setDefault() {

        showEdt.set(false);
        showList.set(false);
        showBottomButton.set(true);
        showDelete.set(false);

        binding.mapSearchEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (s.length() > 0) {
                    showDelete.set(true);
                } else {
                    showDelete.set(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mapListItemObservableArrayList = new ArrayList<>();

        mapListAdapter = new MapListAdapter(mapListItemObservableArrayList);

//        initData();
    }

    private void initData() {

        MapListItemObservable mapListItemObservable = new MapListItemObservable();
        mapListItemObservableArrayList.add(mapListItemObservable);
        binding.mapListView.setAdapter(mapListAdapter);
        showList.set(true);
    }

    public void mapSearchBtn(View view) {
        this.showEdt.set(showEdt.get() ? false : true);
    }

    public void mapSearchEdt(View view) {

        if (historyCount.get() > 0)
            showList.set(true);
    }

    public void searchManual(View view) {
        if (TextUtils.isEmpty(binding.mapSearchEdt.getText().toString()))
            return;
        if (containsEmoji(binding.mapSearchEdt.getText().toString())) {
            ToastUtils.showToast(mContext.getString(R.string.notice_searchKeyword));
            return;
        }
    }

    public void deleteEdt(View view) {

        binding.mapSearchEdt.setText("");
    }

    private static boolean containsEmoji(String str) {
        int len = str.length();
        for (int i = 0; i < len; i++) {
            if (isEmojiCharacter(str.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    private static boolean isEmojiCharacter(char codePoint) {
        return !((codePoint == 0x0) ||
                (codePoint == 0x9) ||
                (codePoint == 0xA) ||
                (codePoint == 0xD) ||
                ((codePoint >= 0x20) && (codePoint <= 0xD7FF)) ||
                ((codePoint >= 0xE000) && (codePoint <= 0xFFFD)) ||
                ((codePoint >= 0x10000) && (codePoint <= 0x10FFFF)));
    }

}
