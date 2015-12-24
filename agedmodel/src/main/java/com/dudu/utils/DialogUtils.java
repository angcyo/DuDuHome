package com.dudu.utils;

import android.content.Context;

import android.view.Window;
import android.view.WindowManager;



/**
 * Created by Administrator on 2015/11/25.
 */
public class DialogUtils {



    private static CopyingDialog mCopyingDialog;

    public static void showCopyMessage(Context context, String message) {
        if (mCopyingDialog != null && mCopyingDialog.isShowing()) {
            return;
        }
        mCopyingDialog = new CopyingDialog(context, message);

        mCopyingDialog.show();
    }

    public static void dismissCopyMessage() {
        if (mCopyingDialog != null && mCopyingDialog.isShowing()) {
            mCopyingDialog.dismiss();
            mCopyingDialog = null;
        }
    }

}
