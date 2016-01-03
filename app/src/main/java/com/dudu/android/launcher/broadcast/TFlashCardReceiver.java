package com.dudu.android.launcher.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.dudu.video.VideoManager;

/**
 * Created by 赵圣琪 on 2016/1/3.
 */
public class TFlashCardReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(Intent.ACTION_MEDIA_MOUNTED)) {
            VideoManager.getInstance().onTFlashCardInserted();
        } else if (action.equals(Intent.ACTION_MEDIA_REMOVED)) {
            VideoManager.getInstance().onTFlashCardRemoved();
        }
    }

}
