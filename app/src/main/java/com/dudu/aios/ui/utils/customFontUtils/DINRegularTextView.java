package com.dudu.aios.ui.utils.customFontUtils;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by Administrator on 2016/2/14.
 */
public class DINRegularTextView extends TextView {
    public DINRegularTextView(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        AssetManager assetManager = context.getAssets();
        Typeface font = Typeface.createFromAsset(assetManager, "DINNEXTLTPRO-REGULAR.OTF");
        setTypeface(font);
    }

    public DINRegularTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public DINRegularTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }
}
