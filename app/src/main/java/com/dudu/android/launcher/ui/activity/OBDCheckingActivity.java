package com.dudu.android.launcher.ui.activity;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import com.dudu.android.launcher.R;
import com.dudu.android.launcher.ui.activity.base.BaseNoTitlebarAcitivity;
import com.dudu.android.launcher.ui.dialog.CarCheckingDialog;
import com.dudu.android.launcher.ui.view.VideoView;
import com.dudu.android.launcher.utils.LogUtils;
import com.dudu.voice.semantic.SemanticConstants;
import com.dudu.voice.semantic.SemanticType;
import com.dudu.voice.semantic.VoiceManager;
import com.dudu.voice.semantic.engine.SemanticProcessor;

/**
 * Created by 赵圣琪 on 2015/11/4.
 */
public class OBDCheckingActivity extends BaseNoTitlebarAcitivity {

    private static final String TAG = "OBDCheckingActivity";

    private VideoView mSelfCheckView;

    private VoiceManager mVoiceManager;

    @Override
    public int initContentView() {
        return R.layout.activity_obd_check;
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        mSelfCheckView = (VideoView) findViewById(R.id.obd_video_view);
        mSelfCheckView.setVideoURI(Uri.parse("android.resource://" +
                getApplicationContext().getPackageName() + "/" + R.raw.obd_check));
        mSelfCheckView.start();
    }

    @Override
    public void initListener() {
        mSelfCheckView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                mVoiceManager.clearMisUnderstandCount();
                mVoiceManager.startSpeaking("检测到您的左前轮破损，是否立即更换?", SemanticConstants.TTS_START_UNDERSTANDING, false);

                showCarCheckingDialog();
            }
        });
    }

    @Override
    public void initDatas() {
        mVoiceManager = VoiceManager.getInstance();
        SemanticProcessor.getProcessor().switchSemanticType(SemanticType.CAR_CHECKING);
    }

    public void onBackPressed(View v) {
        SemanticProcessor.getProcessor().switchSemanticType(SemanticType.NORMAL);
        finish();
    }

    private void showCarCheckingDialog() {
        try {
            CarCheckingDialog dialog = new CarCheckingDialog(OBDCheckingActivity.this);
            dialog.setCanceledOnTouchOutside(true);
            Window dialogWindow = dialog.getWindow();
            dialogWindow.setGravity(Gravity.CENTER);
            WindowManager.LayoutParams lps = dialogWindow.getAttributes();
            lps.x = 10;
            lps.y = 10;
            lps.width = WindowManager.LayoutParams.WRAP_CONTENT;
            lps.height = WindowManager.LayoutParams.WRAP_CONTENT;
            lps.alpha = 1.0f;
            dialogWindow.setAttributes(lps);

            if (!dialog.isShowing()) {
                dialog.show();
            }
        } catch (Exception e) {
            LogUtils.e(TAG, e.getMessage());
        }
    }

}
