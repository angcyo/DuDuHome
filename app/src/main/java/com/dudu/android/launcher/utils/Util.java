package com.dudu.android.launcher.utils;

import com.dudu.android.launcher.LauncherApplication;
import com.dudu.android.launcher.R;

public class Util {

    public static final String ACTIVITYINDEX="activityindex";

    public static final int SIMPLEHUDNAVIE=0;

    public static boolean isTaxiVersion() {
        int code = LauncherApplication.getContext().
                getResources().getInteger(R.integer.dudu_version_code);
        return code == Constants.VERSION_TYPE_TAXI;
    }
}
