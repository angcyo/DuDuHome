package com.dudu.voice.semantic.chain;

import android.content.Context;
import android.content.Intent;
import com.dudu.android.launcher.LauncherApplication;
import com.dudu.android.launcher.ui.activity.video.VideoActivity;
import com.dudu.android.launcher.ui.activity.video.VideoListActivity;
import com.dudu.android.launcher.utils.FloatWindowUtil;
import com.dudu.voice.semantic.SemanticConstants;

/**
 * Created by 赵圣琪 on 2015/11/25.
 */
public class VideoPlayChain extends SemanticChain {

    @Override
    public boolean matchSemantic(String service) {
        return SemanticConstants.SERVICE_VIDEO.equals(service);
    }

    @Override
    public boolean doSemantic(String json) {
        FloatWindowUtil.removeFloatWindow();
        Context context = LauncherApplication.getContext();
        Intent intent = new Intent();
        intent.setClass(context, VideoListActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        return true;
    }

}
