package com.dudu.navi;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import com.dudu.navi.vauleObject.OpenMode;

/**
 * Created by lxh on 2015/11/25.
 */
public class Utils {

    public static OpenMode getOpenMode(Context context){

        String openModestr = context.getString(R.string.open_naviMode);

        switch (openModestr){

            case "1":
                return OpenMode.OUTSIDE;
            case "0":
                return OpenMode.INSIDE;

        }

        return OpenMode.OUTSIDE;
    }

    public static void startThirdPartyApp(Context context, String packageName) {
        Intent intent;
        PackageManager packageManager = context.getPackageManager();
        intent = packageManager.getLaunchIntentForPackage(packageName);
        if (intent != null) {
//            LauncherApplication.getContext().setReceivingOrder(true);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
                    | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(intent);
        }
    }
}
