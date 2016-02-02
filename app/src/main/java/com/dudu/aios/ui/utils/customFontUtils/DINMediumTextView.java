package com.dudu.aios.ui.utils.customFontUtils;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by Administrator on 2016/2/3.
 */
public class DINMediumTextView extends TextView {
    public DINMediumTextView(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        AssetManager assetManager = context.getAssets();
        Typeface font = Typeface.createFromAsset(assetManager, "DINNEXTLTPRO-MEDIUM.OTF");
        setTypeface(font);
    }

    public DINMediumTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public DINMediumTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }
}
