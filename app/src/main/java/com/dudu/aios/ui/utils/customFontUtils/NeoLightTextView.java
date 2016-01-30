package com.dudu.aios.ui.utils.customFontUtils;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by Administrator on 2016/2/2.
 */
public class NeoLightTextView  extends TextView{
    public NeoLightTextView(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        AssetManager assetManager = context.getAssets();
        Typeface font = Typeface.createFromAsset(assetManager, "NeoSansStd-Light.otf");
        setTypeface(font);
    }

    public NeoLightTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public NeoLightTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }
}
