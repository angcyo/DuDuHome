package com.dudu.android.launcher.ui.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import com.dudu.android.launcher.R;

public class SpeechDialogWindow extends LinearLayout {

	public SpeechDialogWindow(Context context) {
		super(context);
		LayoutInflater.from(context).inflate(R.layout.speech_dialog_window_new,
				this);
	}

}
