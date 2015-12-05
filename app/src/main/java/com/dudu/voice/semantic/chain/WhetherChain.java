package com.dudu.voice.semantic.chain;

import android.text.TextUtils;

import com.dudu.android.launcher.utils.FloatWindowUtil;
import com.dudu.android.launcher.utils.JsonUtils;
import com.dudu.map.NavigationClerk;
import com.dudu.navi.event.NaviEvent;
import com.dudu.navi.vauleObject.SearchType;
import com.dudu.voice.semantic.SemanticConstants;
import com.dudu.voice.semantic.SemanticType;
import com.dudu.voice.semantic.engine.SemanticProcessor;

import org.json.JSONObject;

import de.greenrobot.event.EventBus;

/**
 * Created by pc on 2015/11/3.
 */
public class WhetherChain extends SemanticChain {


    public static final String YES = "是";

    public static final String NO = "否";

    public static final String YES_TWO = "设置";

    public static final String YES_THREE = "添加";

    public static final String NO_TWO = "不设置";

    public static final String NO_THREE = "不添加";

    private String actionType;


    @Override
    public boolean matchSemantic(String service) {
        return service.equalsIgnoreCase(SemanticConstants.SERVICE_WHETHER);
    }

    @Override
    public boolean doSemantic(String json) {
        getActionType(json);
        SemanticProcessor.getProcessor().switchSemanticType(SemanticType.NORMAL);
        if (!TextUtils.isEmpty(json)) {

            switch (actionType) {

                case YES:
                case YES_TWO:
                case YES_THREE:
                    NavigationClerk.getInstance().searchControl(null,null,"", SearchType.SEARCH_COMMONADDRESS);
                    break;
                case NO:
                case NO_TWO:
                case NO_THREE:
                    FloatWindowUtil.removeFloatWindow();
//                    mVoiceManager.startWakeup();
                    break;
            }

            return true;
        }

        return false;
    }

    private String getActionType(String json) {

        try {

            JSONObject semnticObject = new JSONObject(JsonUtils.parseIatResult(json,
                    "semantic"));
            actionType = semnticObject.getJSONObject("slots").getJSONObject("action").getString("type");

        } catch (Exception e) {

            actionType = "";
            e.printStackTrace();

        }

        return actionType;
    }
}
