package com.dudu.voice.window;

import android.widget.AdapterView;

import com.dudu.android.launcher.model.WindowMessageEntity;

/**
 * Created by 赵圣琪 on 2016/1/4.
 */
public interface FloatWindowManager {

    void showMessage(WindowMessageEntity message);

    void showStrategy();

    void showAddress();

    void onVolumeChanged(int volume);

    void onNextPage();

    void onPreviousPage();

    void onChoosePage(int page);

    void removeFloatWindow();

    void setItemClickListener(AdapterView.OnItemClickListener listener);
}
