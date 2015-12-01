package com.dudu.android.launcher.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

public class MTextView  extends TextView{
	
    public MTextView(Context context) {
		super(context);
	}
    
    public MTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

	@Override
    public boolean isFocused() {
        return true;
    }

}
