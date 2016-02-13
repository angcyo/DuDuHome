package com.dudu.aios.ui.voice;

import android.content.Context;
import android.util.AttributeSet;

/**
 * Created by lxh on 2016/2/13.
 */
public class VoiceCircleAnimView extends VoiceAnimView{

    public VoiceCircleAnimView(Context context) {
        super(context);
    }

    public VoiceCircleAnimView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected int getMaxPicCount() {
        return 41;
    }

    @Override
    protected String getPicPath() {
        return "voice_circle/voice_circle_";
    }
}
