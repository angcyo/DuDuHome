package com.dudu.android.launcher.utils;

import android.content.Context;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.dudu.android.launcher.R;
import com.dudu.android.launcher.ui.dialog.ErrorMessageDialog;
import com.dudu.voice.semantic.VoiceManager;

/**
 * Created by Administrator on 2015/11/25.
 */
public class DialogUtils {

    private static ErrorMessageDialog mOBDErrorDialog;

    private static ErrorMessageDialog mWithoutSimCardDialog;

    private static ErrorMessageDialog mSimCardReplaceDialog;

    public static void showOBDErrorDialog(Context context) {
        if (Utils.isDemoVersion(context)) {
            return;
        }

        if (mOBDErrorDialog != null && mOBDErrorDialog.isShowing()) {
            return;
        }
        mOBDErrorDialog = new ErrorMessageDialog(context, R.string.obd_checking_unconnected,
                R.drawable.obd_checking_icon);

        Window dialogWindow = mOBDErrorDialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.x = 10;
        lp.y = 0;
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        dialogWindow.setAttributes(lp);
        mOBDErrorDialog.show();
    }

    public static void dismissOBDErrorDialog(Context context) {
        if (Utils.isDemoVersion(context)) {
            return;
        }

        if (mOBDErrorDialog != null && mOBDErrorDialog.isShowing()) {
            mOBDErrorDialog.dismiss();
            mOBDErrorDialog = null;
        }
    }

    public static void showWithoutSimCardDialog(Context context) {
        if (Utils.isDemoVersion(context)) {
            return;
        }

        if (mWithoutSimCardDialog != null && mWithoutSimCardDialog.isShowing()) {
            return;
        }

        mWithoutSimCardDialog = new ErrorMessageDialog(context, R.string.without_sim_card,
                R.drawable.sim_card_uninserted_icon);
        mWithoutSimCardDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        mWithoutSimCardDialog.show();
    }

    public static void dismissWithoutSimCardDialog() {
        if (mWithoutSimCardDialog != null && mWithoutSimCardDialog.isShowing()) {
            mWithoutSimCardDialog.dismiss();
            mWithoutSimCardDialog = null;
        }
    }

    public static void showSimCardReplaceDialog(Context context) {
        if (Utils.isDemoVersion(context)) {
            return;
        }

        if (mSimCardReplaceDialog != null && mSimCardReplaceDialog.isShowing()) {
            return;
        }

        mSimCardReplaceDialog = new ErrorMessageDialog(context, R.string.sim_card_replaced_error,
                R.drawable.sim_card_uninserted_icon);
        mSimCardReplaceDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        mSimCardReplaceDialog.show();
    }

    public static void dismissSimCardReplaceDialog() {
        if (mSimCardReplaceDialog != null && mSimCardReplaceDialog.isShowing()) {
            mSimCardReplaceDialog.dismiss();
            mSimCardReplaceDialog = null;
        }
    }

}
