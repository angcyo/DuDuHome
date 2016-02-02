package com.dudu.voice.window;

import android.graphics.PixelFormat;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;

import com.dudu.android.launcher.R;
import com.dudu.android.launcher.model.WindowMessageEntity;
import com.dudu.android.launcher.ui.adapter.MessageAdapter;
import com.dudu.android.launcher.ui.adapter.RouteSearchAdapter;
import com.dudu.android.launcher.ui.adapter.StrategyAdapter;
import com.dudu.android.launcher.ui.view.RadioDialog;
import com.dudu.android.launcher.utils.Constants;
import com.dudu.voice.VoiceManagerProxy;
import com.dudu.voice.semantic.constant.SceneType;
import com.dudu.voice.semantic.engine.SemanticEngine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 赵圣琪 on 2016/1/4.
 */
public class BlueWindowManager extends BaseWindowManager {

    private static final int SINGLE_PAGE_COUNT = 4;

    private static final int MAX_PAGE_COUNT = 5;

    private boolean mMapChoosing = false;

    private RadioDialog mRadioDialog;

    private ListView mMessageListView;

    private ListView mMapListView;

    private RouteSearchAdapter mRouteSearchAdapter;

    private StrategyAdapter mStrategyAdapter;

    private MessageAdapter mMessageAdapter;

    private List<WindowMessageEntity> mMessageData;

    private int mCurPageNum = 0;

    private Logger logger;

    @Override
    public void initWindow() {
        logger = LoggerFactory.getLogger("voice.float");

        mLayoutParams = new WindowManager.LayoutParams();
        mLayoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        mLayoutParams.format = PixelFormat.RGBA_8888;
        mLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        mLayoutParams.width = mContext.getResources().getDisplayMetrics().widthPixels;
        mLayoutParams.height = mContext.getResources().getDisplayMetrics().heightPixels;
        mLayoutParams.x = 0;
        mLayoutParams.y = 0;
        mLayoutParams.alpha = 1.0f;

        mRadioDialog = (RadioDialog) mFloatWindowView.findViewById(R.id.radioDialog);
        mRadioDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logger.debug("点击语音退出按钮，退出语音...");

                VoiceManagerProxy.getInstance().stopSpeaking();

                removeFloatWindow();
            }
        });

        mMessageData = new ArrayList<>();
        mMessageListView = (ListView) mFloatWindowView.findViewById(R.id.message_listView);
        mMessageAdapter = new MessageAdapter(mContext, mMessageData);
        mMessageListView.setAdapter(mMessageAdapter);

        mMapListView = (ListView) mFloatWindowView.findViewById(R.id.map_ListView);
        mRouteSearchAdapter = new RouteSearchAdapter(mContext, 1);
        mStrategyAdapter = new StrategyAdapter(mContext);
    }

    @Override
    public int getFloatWindowLayout() {
        return R.layout.speech_dialog_window;
    }

    @Override
    public void showMessage(WindowMessageEntity message) {
        if (mMapChoosing) {
            return;
        }

        addFloatView();

        mMessageListView.setVisibility(View.VISIBLE);
        mMapListView.setVisibility(View.GONE);

        mMessageAdapter.addMessage(message);

        mMessageListView.smoothScrollToPosition(mMessageData.size() - 1);
    }

    @Override
    public void showStrategy() {
        mMapChoosing = true;

        mCurPageNum = 0;

        mMessageListView.setVisibility(View.GONE);
        mMapListView.setVisibility(View.VISIBLE);

        mMapListView.setAdapter(mStrategyAdapter);
    }

    @Override
    public void showAddress() {
        mMapChoosing = true;

        mCurPageNum = 0;

        mMessageListView.setVisibility(View.GONE);
        mMapListView.setVisibility(View.VISIBLE);

        mRouteSearchAdapter.initPoiData(mContext);
        mMapListView.setAdapter(mRouteSearchAdapter);
    }

    @Override
    public void onVolumeChanged(int volume) {
        if (mShowFloatWindow && mRadioDialog != null) {
            mRadioDialog.setPressCounts(volume);
        }
    }

    @Override
    public void onNextPage() {
        if (mCurPageNum > MAX_PAGE_COUNT) {
            mVoiceManager.startSpeaking(Constants.ALREADY_LAST_PAGE);
            return;
        }

        mCurPageNum++;

        mMapListView.setSelection(mCurPageNum * SINGLE_PAGE_COUNT);
    }

    @Override
    public void onPreviousPage() {
        if (mCurPageNum <= 0) {
            mVoiceManager.startSpeaking(Constants.ALREADY_FIRST_PAGE);
            return;
        }

        mCurPageNum--;

        mMapListView.setSelection(mCurPageNum * SINGLE_PAGE_COUNT);
    }

    @Override
    public void onChoosePage(int page) {
        if (page < 0 || page > MAX_PAGE_COUNT) {
            mVoiceManager.startSpeaking(Constants.MAP_CHOISE_ERROR);
            return;
        }

        mCurPageNum = page - 1;

        mMapListView.setSelection(mCurPageNum * SINGLE_PAGE_COUNT);
    }

    @Override
    public void removeFloatWindow() {
        SemanticEngine.getProcessor().switchSemanticType(SceneType.HOME);

        removeFloatView();

        mMapChoosing = false;

        mMessageData.clear();
    }

    @Override
    public void setItemClickListener(AdapterView.OnItemClickListener listener) {
        if (listener != null) {
            mMapListView.setOnItemClickListener(listener);
        }
    }

}
