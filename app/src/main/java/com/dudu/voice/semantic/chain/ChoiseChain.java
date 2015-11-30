package com.dudu.voice.semantic.chain;

import android.app.Activity;

import com.dudu.android.launcher.ui.activity.LocationMapActivity;
import com.dudu.android.launcher.utils.ActivitiesManager;
import com.dudu.android.launcher.utils.ChoiseUtil;
import com.dudu.android.launcher.utils.JsonUtils;
import com.dudu.map.NavigationClerk;
import com.dudu.voice.semantic.SemanticConstants;

/**
 * Created by pc on 2015/10/30.
 */
public class ChoiseChain extends SemanticChain {

    private int choiseSize = 0;

    private String chooseType = "";

    public static final String PAGE = "页";

    public static final String PAGE_TWO = "夜";

    public static final int TYPE_NORMAL = 1;

    public static final int TYPE_PAGE = 2;

    private int type = 0;

    @Override
    public boolean matchSemantic(String service) {
        return service.equalsIgnoreCase(SemanticConstants.SERVICE_CHOISE);
    }

    @Override
    public boolean doSemantic(String json) {
        String semantic = JsonUtils.parseIatResult(json,
                "semantic");
        choiseSize = ChoiseUtil.getChoiseSize(JsonUtils
                .parseIatResultChoiseSize(semantic));
        chooseType = JsonUtils.getChooseType(semantic);
        if (chooseType.equals(PAGE) || chooseType.equals(PAGE_TWO))
            type = TYPE_PAGE;
        else
            type = TYPE_NORMAL;
        if (NavigationClerk.getInstance().isShowAddress()) {
            mVoiceManager.startUnderstanding();
            NavigationClerk.getInstance().startChooseResult(choiseSize, type);
            return true;
        }

        return false;
    }
}
