package com.dudu.voice.window;

import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.dudu.aios.ui.activity.MainRecordActivity;
import com.dudu.aios.ui.utils.ScreenUtil;
import com.dudu.aios.ui.utils.blur.RxBlurEffective;
import com.dudu.aios.ui.voice.VoiceCircleAnimView;
import com.dudu.aios.ui.voice.VoiceEvent;
import com.dudu.aios.ui.voice.VoiceRippleAnimView;
import com.dudu.android.launcher.R;
import com.dudu.android.launcher.model.WindowMessageEntity;
import com.dudu.android.launcher.ui.adapter.MessageAdapter;
import com.dudu.android.launcher.utils.ActivitiesManager;
import com.dudu.android.launcher.utils.Constants;
import com.dudu.map.NavigationProxy;
import com.dudu.voice.VoiceManagerProxy;
import com.dudu.voice.semantic.constant.SceneType;
import com.dudu.voice.semantic.engine.SemanticEngine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by 赵圣琪 on 2016/1/4.
 */
public class BlueWindowManager extends BaseWindowManager {

    private static final int MAX_PAGE_COUNT = 5;

    private boolean mMapChoosing = false;

    private ListView mMessageListView;

    private MessageAdapter mMessageAdapter;

    private List<WindowMessageEntity> mMessageData;

    private int mCurPageNum = 0;

    private Logger logger;

    private Button voiceBack;

    private LinearLayout voice_animLayout;

    private VoiceCircleAnimView voiceCircleAnimView;

    private VoiceRippleAnimView voiceRippleAnimView;

    private View message_layout;

    private boolean isInit = false;


    @Override
    public void initWindow() {

        if (isInit)
            return;


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


        voiceBack = (Button) mFloatWindowView.findViewById(R.id.voiceBack);

        voiceBack.setOnClickListener(v -> {
            VoiceManagerProxy.getInstance().stopSpeaking();
            removeFloatWindow();
        });

        mMessageData = new ArrayList<>();
        mMessageListView = (ListView) mFloatWindowView.findViewById(R.id.message_listView);
        mMessageAdapter = new MessageAdapter(mContext, mMessageData);
        mMessageListView.setAdapter(mMessageAdapter);
        message_layout = mFloatWindowView.findViewById(R.id.message_layout);

        initAnimView();

        isInit = true;
    }

    private void initAnimView() {
        voice_animLayout = (LinearLayout) mFloatWindowView.findViewById(R.id.voice_anim_layout);
        voiceRippleAnimView = (VoiceRippleAnimView) mFloatWindowView.findViewById(R.id.voice_ripple);
        voiceCircleAnimView = (VoiceCircleAnimView) mFloatWindowView.findViewById(R.id.voice_circle);

        voiceRippleAnimView.setZOrderOnTop(true);
        voiceRippleAnimView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
        voiceCircleAnimView.setZOrderOnTop(true);
        voiceCircleAnimView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
    }

    @Override
    public int getFloatWindowLayout() {
        return R.layout.speech_dialog_window_new;
    }

    @Override
    public void showMessage(WindowMessageEntity message) {

        if (NavigationProxy.getInstance().isShowList()) {
            return;
        }

        addFloatView();

        stopAnimWindow();

        blur(message_layout);

        message_layout.setVisibility(View.VISIBLE);

        mMessageListView.setVisibility(View.VISIBLE);

        mMessageAdapter.addMessage(message);

        mMessageListView.smoothScrollToPosition(mMessageData.size() - 1);


    }

    @Override
    public void showStrategy() {
        mMapChoosing = true;

        mCurPageNum = 0;

        mMessageListView.setVisibility(View.GONE);

    }

    @Override
    public void showAddress() {
        mMapChoosing = true;

        mCurPageNum = 0;

        mMessageListView.setVisibility(View.GONE);

    }

    @Override
    public void onVolumeChanged(int volume) {
    }

    @Override
    public void onNextPage() {
        if (mCurPageNum > MAX_PAGE_COUNT) {
            mVoiceManager.startSpeaking(Constants.ALREADY_LAST_PAGE);
            return;
        }

        mCurPageNum++;

    }

    @Override
    public void onPreviousPage() {
        if (mCurPageNum <= 0) {
            mVoiceManager.startSpeaking(Constants.ALREADY_FIRST_PAGE);
            return;
        }

        mCurPageNum--;

    }

    @Override
    public void onChoosePage(int page) {
        if (page < 0 || page > MAX_PAGE_COUNT) {
            mVoiceManager.startSpeaking(Constants.MAP_CHOISE_ERROR);
            return;
        }

        mCurPageNum = page - 1;

    }

    @Override
    public void removeFloatWindow() {

        isInit = false;

        stopAnimWindow();

        SemanticEngine.getProcessor().switchSemanticType(SceneType.HOME);

        removeFloatView();

        mMapChoosing = false;

        mMessageData.clear();

        EventBus.getDefault().post(VoiceEvent.DISMISS_WINDOW);


    }

    @Override
    public void setItemClickListener(AdapterView.OnItemClickListener listener) {

    }

    public void showAnimWindow() {

        stopAnimWindow();

        addFloatView();

        voice_animLayout.setVisibility(View.VISIBLE);

        message_layout.setVisibility(View.GONE);

//        voiceCircleAnimView.startAnim();
//
//        voiceRippleAnimView.startAnim();

    }

    public void stopAnimWindow() {
        if (voice_animLayout != null) {
            voice_animLayout.setVisibility(View.GONE);
        }
        if (voiceCircleAnimView != null && voiceRippleAnimView != null) {
            voiceCircleAnimView.stopAnim();
            voiceRippleAnimView.stopAnim();
        }
    }

    private void blur(View view) {


        if (ActivitiesManager.getInstance().getTopActivity() instanceof MainRecordActivity) {
//            Bitmap blurBitmap = RxBlurEffective
//                    .bestBlur(mContext, ScreenUtil.cacheCurrentScreen(ActivitiesManager.getInstance().getTopActivity()), 25, 0.2f)
//                    .toBlocking()
//                    .first();
            view.setBackground(new BitmapDrawable(mContext.getResources(),  ScreenUtil.cacheCurrentScreen(ActivitiesManager.getInstance().getTopActivity())));
        } else {
            voice_animLayout.setVisibility(View.GONE);
            view.setBackground(null);
            view.setBackgroundResource(R.color.video_unchecked_textColor);
            view.setAlpha(0.8f);
        }
    }
}
