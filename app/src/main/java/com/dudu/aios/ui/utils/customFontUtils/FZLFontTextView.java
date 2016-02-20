package com.dudu.aios.ui.utils.customFontUtils;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by sunny_zhang on 2016/1/30.
 */
public class FZLFontTextView extends TextView {

    public FZLFontTextView(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        try {

            AssetManager assetManager = context.getAssets();
            Typeface font = Typeface.createFromAsset(assetManager, "FZLTXHK.TTF");
            setTypeface(font);
        }catch (Exception e){

        }
    }

    public FZLFontTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public FZLFontTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }
}
