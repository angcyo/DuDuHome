package com.dudu.voice.semantic.chain;

import android.content.Context;

import com.dudu.android.launcher.LauncherApplication;
import com.dudu.android.launcher.bean.WifiEntity;
import com.dudu.android.launcher.bean.WifiSlots;
import com.dudu.android.launcher.utils.FloatWindowUtil;
import com.dudu.android.launcher.utils.GsonUtil;
import com.dudu.android.launcher.utils.JsonUtils;
import com.dudu.android.launcher.utils.SharedPreferencesUtil;
import com.dudu.android.launcher.utils.Util;
import com.dudu.android.launcher.utils.WifiApAdmin;
import com.dudu.voice.semantic.SemanticConstants;

/**
 * Created by pc on 2015/11/5.
 */
public class WIFIChain extends SemanticChain {

    private Context mContext;

    public WIFIChain(){

        mContext = LauncherApplication.getContext().getApplicationContext();
    }

    @Override
    public boolean matchSemantic(String service) {
        return service.equalsIgnoreCase(SemanticConstants.SERVICE_WIFI);
    }

    @Override
    public boolean doSemantic(String json) {
        if (Util.isTaxiVersion()) {
            return false;
        }

        String semantic = JsonUtils.parseIatResult(json,
                "semantic");
        WifiEntity we = (WifiEntity) GsonUtil.jsonToObject(
                semantic, WifiEntity.class);
        WifiSlots ws = we.getSlots();
        String wifiState = ws.getState();
        if (wifiState.equals("open")) {
            WifiApAdmin.startWifiAp(mContext);
            mVoiceManager.startSpeaking("热点已打开。", SemanticConstants.TTS_DO_NOTHING);
        } else if (wifiState.equals("close")) {
            WifiApAdmin.closeWifiAp(mContext);
           mVoiceManager.startSpeaking("热点已关闭。", SemanticConstants.TTS_DO_NOTHING);
        }

        FloatWindowUtil.removeFloatWindow();
        return true;
    }
}
