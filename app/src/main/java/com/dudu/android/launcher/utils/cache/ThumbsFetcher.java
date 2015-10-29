package com.dudu.android.launcher.utils.cache;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.text.TextUtils;

public class ThumbsFetcher extends ImageWorker {

	private MediaMetadataRetriever retriever;
	
	public ThumbsFetcher(Context context) {
		super(context);
		retriever = new MediaMetadataRetriever();
	}

	@Override
	protected Bitmap processBitmap(Object data) {
		String path = (String) data;
		if (!TextUtils.isEmpty(path)) {
			retriever.setDataSource(path);
			return retriever.getFrameAtTime();
		}
		
		return null;
	}

}
