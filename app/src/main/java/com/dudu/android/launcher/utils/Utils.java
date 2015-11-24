package com.dudu.android.launcher.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.dudu.android.launcher.LauncherApplication;
import com.dudu.android.launcher.R;
import com.dudu.android.launcher.ui.dialog.ErrorMessageDialog;

public class Utils {

    private static final String TAG = "Utils";

    private static ErrorMessageDialog mOBDErrorDialog;

    public static boolean isTaxiVersion() {
        int code = LauncherApplication.getContext().
                getResources().getInteger(R.integer.dudu_version_code);
        return code == Constants.VERSION_TYPE_TAXI;
    }

    public static void startThirdPartyApp(Context context, String packageName) {
        Intent intent;
        PackageManager packageManager = context.getPackageManager();
        intent = packageManager.getLaunchIntentForPackage(packageName);
        if (intent != null) {
            LauncherApplication.getContext().setReceivingOrder(true);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
                    | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(intent);
        }
    }

    public static void startThirdPartyApp(Context context, String packageName, int stringId) {
        Intent intent;
        PackageManager packageManager = context.getPackageManager();
        intent = packageManager.getLaunchIntentForPackage(packageName);
        if (intent != null) {
            LauncherApplication.getContext().setReceivingOrder(true);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
                    | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(intent);
        } else {
            ToastUtils.showToast(stringId);
        }
    }

    /**
     * 判断当前是demo版本还是正式版
     * @param context
     * @return
     */
    public static boolean isDemoVersion(Context context) {
        PackageInfo packageInfo = null;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(),
                    PackageManager.GET_CONFIGURATIONS);
            String versionName = packageInfo.versionName;
            if (versionName.contains("demo")) {
                return true;
            }
        } catch (PackageManager.NameNotFoundException e) {
           LogUtils.e(TAG, e.getMessage() + "");
        }

        return false;
    }

    public static void showOBDErrorDialog(Context context) {
        if (isDemoVersion(context)) {
            return;
        }

        if (mOBDErrorDialog != null && mOBDErrorDialog.isShowing()) {
            return;
        }

        mOBDErrorDialog = new ErrorMessageDialog(context, R.string.obd_checking_unconnected,
                R.drawable.obd_checking_icon);
        mOBDErrorDialog.show();
    }

    public static void dismissOBDErrorDialog(Context context) {
        if (Utils.isDemoVersion(context)) {
            return;
        }

        if (mOBDErrorDialog != null && mOBDErrorDialog.isShowing()) {
            mOBDErrorDialog.dismiss();
        }
    }

}
