package com.dudu.voice.semantic.chain;

import android.app.Activity;

import com.dudu.android.launcher.ui.activity.LocationActivity;
import com.dudu.android.launcher.ui.activity.LocationMapActivity;
import com.dudu.android.launcher.utils.ActivitiesManager;
import com.dudu.android.launcher.utils.JsonUtils;
import com.dudu.android.launcher.utils.LogUtils;
import com.dudu.voice.semantic.SemanticConstants;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by pc on 2015/10/30.
 */
public class ChoosePageChain extends SemanticChain {

    public static final int NEXT_PAGE = 1;

    public static final int LAST_PAGE = 2;

    public static final String NEXT = "下一页";

    public static final String LAST = "上一页";

    private int type = 0;

    @Override
    public boolean matchSemantic(String service) {
        return service.equalsIgnoreCase(SemanticConstants.SERVICE_CHOOSEPAGE);
    }

    @Override
    public boolean doSemantic(String json) {
        mVoiceManager.startUnderstanding();
        Activity activity = ActivitiesManager.getInstance().getTopActivity();
        if(activity!=null && activity instanceof LocationMapActivity){
            getPageType(json);
            if(type!=0){
                ((LocationMapActivity) activity).choosePage(type);
                return  true;
            }
        }
        return false;
    }

    private int getPageType(String json){
        try {
            JSONObject semantic = new JSONObject(JsonUtils.parseIatResult(json,
                    "semantic"));
            JSONObject actionObject = semantic.getJSONObject("slots").getJSONObject("action");
            if(actionObject!=null){
                switch (actionObject.getString("type")){
                    case NEXT:
                        type = NEXT_PAGE;
                      break;
                    case LAST:
                        type = LAST_PAGE;
                       break;
                }
            }


        }catch (Exception e){
            LogUtils.e("ChoosePageChain", e.getMessage());
        }

        return  type;
    }
}
