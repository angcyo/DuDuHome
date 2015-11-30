package com.dudu.voice.semantic.chain;

import android.app.Activity;
import com.dudu.android.launcher.ui.activity.LocationMapActivity;
import com.dudu.android.launcher.utils.ActivitiesManager;
import com.dudu.android.launcher.utils.JsonUtils;
import com.dudu.android.launcher.utils.LogUtils;
import com.dudu.map.NavigationClerk;
import com.dudu.voice.semantic.SemanticConstants;
import org.json.JSONObject;

/**
 * Created by pc on 2015/10/30.
 */
public class ChoosePageChain extends SemanticChain {

    public static final int NEXT_PAGE = 1;

    public static final int LAST_PAGE = 2;

    public static final int CHOOSE_PAGE = 3;

    public static final String NEXT = "下一页";

    public static final String NEXT_TWO = "夏夜";

    public static final String NEXT_THREE = "下页";

    public static final String LAST = "上一页";

    public static final String LAST_TWO = "上页";

    public static final String LAST_THREE = "上夜";

    protected int type = 0;

    @Override
    public boolean matchSemantic(String service) {
        return service.equalsIgnoreCase(SemanticConstants.SERVICE_CHOOSEPAGE);
    }

    @Override
    public boolean doSemantic(String json) {
        if(NavigationClerk.getInstance().isShowAddress()){
            getPageType(json);
            if(type!=0){
                mVoiceManager.startUnderstanding();
                NavigationClerk.getInstance().choosePage(type);
                return  true;
            }
        }

        return false;
    }

    protected int getPageType(String json){
        try {
            JSONObject semantic = new JSONObject(JsonUtils.parseIatResult(json,
                    "semantic"));
            JSONObject actionObject = semantic.getJSONObject("slots").getJSONObject("action");
            if(actionObject!=null){
                switch (actionObject.getString("type")){
                    case NEXT:
                    case NEXT_TWO:
                    case NEXT_THREE:
                        type = NEXT_PAGE;
                      break;
                    case LAST:
                    case LAST_TWO:
                    case LAST_THREE:
                        type = LAST_PAGE;
                       break;
                }
            }


        }catch (Exception e){
            LogUtils.e("ChoosePageChain", e.getMessage());
        }

        return type;
    }
}
