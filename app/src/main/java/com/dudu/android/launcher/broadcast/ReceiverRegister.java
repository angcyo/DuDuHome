package com.dudu.android.launcher.broadcast;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.dudu.android.launcher.LauncherApplication;
import com.dudu.commonlib.utils.TestVerify;
import com.tencent.android.tpush.XGIOperateCallback;
import com.tencent.android.tpush.XGPushConfig;
import com.tencent.android.tpush.XGPushManager;

/**
 * Created by Administrator on 2016/2/16.
 */
public class ReceiverRegister {
    private static final String TAG ="ReceiverRegister";

    public static void registPushManager(String username) {
        if(TestVerify.isEmpty(username)){
            return;
        }
        // 开启logcat输出，方便debug，发布时请关闭
        final Context context = LauncherApplication.getContext();
        XGPushConfig.enableDebug(context, true);
        XGPushManager.registerPush(context, username, new XGIOperateCallback() {
            @Override
            public void onSuccess(Object o, int i) {
//                Toast.makeText(context, "注册成功", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "注册成功");
            }

            @Override
            public void onFail(Object o, int i, String s) {
//                Toast.makeText(context, "注册失败" + s, Toast.LENGTH_SHORT).show();
                Log.d(TAG, "注册失败" + s);
            }
        });
    }
}
