package com.dudu.voice.semantic.chain;

import android.text.TextUtils;

import com.dudu.android.launcher.utils.FloatWindow;
import com.dudu.android.launcher.utils.FloatWindowUtil;
import com.dudu.android.launcher.utils.JsonUtils;
import com.dudu.map.MapManager;
import com.dudu.voice.semantic.SemanticConstants;

import org.json.JSONObject;

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

        if (!TextUtils.isEmpty(json)) {

            switch (actionType) {

                case YES:
                case YES_TWO:
                case YES_THREE:
                    MapManager.getInstance().mapControl(null, null, MapManager.SEARCH_COMMONADDRESS);
                    break;
                case NO:
                case NO_TWO:
                case NO_THREE:
                    FloatWindowUtil.removeFloatWindow();
                    mVoiceManager.startWakeup();
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
