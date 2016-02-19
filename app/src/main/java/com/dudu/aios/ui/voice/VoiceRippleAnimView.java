package com.dudu.aios.ui.voice;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.dudu.android.launcher.utils.LogUtils;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by lxh on 2016/2/12.
 */
public class VoiceRippleAnimView extends VoiceAnimView {

    public static final String VOICE_RIPPLE_PATH = "voice_ripple/d02_voice_";


    public VoiceRippleAnimView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected int getMaxPicCount() {
        return 44;
    }

    @Override
    protected String getPicPath() {
        return VOICE_RIPPLE_PATH;
    }
}
