package com.dudu.voice.semantic.chain;

import android.app.Activity;
import android.content.Intent;

import com.dudu.android.launcher.ui.activity.NearbyRepairActivity;
import com.dudu.android.launcher.ui.activity.OBDCheckingActivity;
import com.dudu.android.launcher.utils.ActivitiesManager;
import com.dudu.android.launcher.utils.JsonUtils;
import com.dudu.android.launcher.utils.LogUtils;
import com.dudu.voice.semantic.SemanticConstants;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2015/11/5.
 */
public class CarCheckingWhetherChain extends  SemanticChain {

    private static final String TAG = "CarCheckingWhetherChain";

    private static final String YES = "是";
    private static final String NO = "否";
    private static final String SWITCH_NOW = "立即更换";
    private static final String SWITCH = "更换";

    @Override
    public boolean matchSemantic(String service) {
        return service.equalsIgnoreCase(SemanticConstants.SERVICE_WHETHER);
    }

    @Override
    public boolean doSemantic(String json) {
        JSONObject semnticObject = null;
        try {
            semnticObject = new JSONObject(JsonUtils.parseIatResult(json,
                    "semantic"));

            String type = semnticObject.getJSONObject("slots").getJSONObject("action").
                    getString("type");

            switch (type) {
                case  YES:
                case SWITCH_NOW:
                case SWITCH:
                    Activity activity = ActivitiesManager.getInstance()
                            .getTopActivity();
                    if (activity != null
                            && activity instanceof OBDCheckingActivity) {
                        activity.startActivity(new Intent(activity,
                                NearbyRepairActivity.class));
                        activity.finish();
                    }
                    break;
                case NO:

                    break;
            }

            return true;
        } catch (JSONException e) {
            LogUtils.e(TAG, e.getMessage());
        }

        return false;
    }
}
