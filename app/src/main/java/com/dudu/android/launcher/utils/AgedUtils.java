package com.dudu.android.launcher.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;

import com.dudu.android.launcher.utils.cache.AgedContacts;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/12/24.
 */
public class AgedUtils {
    private static void installApp(Context context, File file) {
        Intent intent = new Intent("android.intent.action.VIEW.HIDE");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.parse(AgedContacts.FILE_NAME + file.toString()), AgedContacts.APPLICATION_NAME);
        context.startActivity(intent);
    }

    private static boolean isAppInstalled(Context context, String packageName) {
        final PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        List<String> pName = new ArrayList();
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                pName.add(pn);
            }
        }
        return pName.contains(packageName);
    }

    public static void proceedAgeTest(Context context) {
        // EventBus.getDefault().post(NaviEvent.FloatButtonEvent.SHOW);
        if (isAppInstalled(context, AgedContacts.PACKAGE_NAME)) {
            Utils.startThirdPartyApp(context, AgedContacts.PACKAGE_NAME);
        } else {
            File file = new File(AgedContacts.AGEDMODEL_APK_DIR, AgedContacts.AGEDMODEL_APK);
            if (file.exists()) {
                installApp(context, file);
                waitAndStart(context, AgedContacts.PACKAGE_NAME);
            }
        }

    }

    private static void waitAndStart(final Context context, final String packageName) {
        while (true) {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (isAppInstalled(context, packageName)) {
                Utils.startThirdPartyApp(context, packageName);
                break;
            }
        }
    }


    public static void uninstallAgedApk(final Context context) {
        if (isAppInstalled(context, AgedContacts.PACKAGE_NAME)) {
            Intent intent = new Intent();
            intent.setAction("android.intent.action.DELETE.HIDE");
            intent.setData(Uri.parse(AgedContacts.AKP_PACKAGE));
            context.startActivity(intent);
        }
    }
}
