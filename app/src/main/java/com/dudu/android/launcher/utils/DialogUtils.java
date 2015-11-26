package com.dudu.android.launcher.utils;

import android.content.Context;
import com.dudu.android.launcher.R;
import com.dudu.android.launcher.ui.dialog.ErrorMessageDialog;

/**
 * Created by Administrator on 2015/11/25.
 */
public class DialogUtils {

    private static ErrorMessageDialog mOBDErrorDialog;

    public static void showOBDErrorDialog(Context context) {
        if (Utils.isDemoVersion(context)) {
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
