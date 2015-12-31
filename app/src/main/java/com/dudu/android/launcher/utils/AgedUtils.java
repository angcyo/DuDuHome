package com.dudu.android.launcher.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;

import com.amap.api.maps.AMapException;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.RoutePara;
import com.dudu.android.launcher.R;
import com.dudu.android.launcher.utils.cache.AgedContacts;
import com.dudu.navi.event.NaviEvent;

import org.scf4a.Event;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by Administrator on 2015/12/24.
 */
public class AgedUtils {
    private static void installApp(Context context, File file) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
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
            }
        }

    }


}
