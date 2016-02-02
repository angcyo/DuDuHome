package com.dudu.aios.ui.utils.customFontUtils;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.text.InputType;
import android.util.AttributeSet;
import android.widget.EditText;

public class FZLFontEditView extends EditText {
    public FZLFontEditView(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        AssetManager assetManager = context.getAssets();
        Typeface font = Typeface.createFromAsset(assetManager, "FZLTXHK.TTF");
        setTypeface(font);
    }

    public FZLFontEditView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public FZLFontEditView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }
}
