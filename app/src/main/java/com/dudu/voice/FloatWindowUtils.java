package com.dudu.voice;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.AdapterView.OnItemClickListener;

import com.dudu.aios.ui.activity.MainRecordActivity;
import com.dudu.aios.ui.voice.VoiceEvent;
import com.dudu.android.launcher.LauncherApplication;
import com.dudu.android.launcher.model.WindowMessageEntity;
import com.dudu.android.launcher.utils.ActivitiesManager;
import com.dudu.android.launcher.utils.Constants;
import com.dudu.voice.window.BlueWindowManager;
import com.dudu.voice.window.FloatWindowManager;
import com.dudu.voice.window.MessageType;

import de.greenrobot.event.EventBus;

public class FloatWindowUtils {

    private static final int FLOAT_SHOW_MESSAGE = 0;
    private static final int FLOAT_SHOW_ADDRESS = 1;
    private static final int FLOAT_SHOW_STRATEGY = 2;
    private static final int FLOAT_REMOVE_WINDOW = 3;
    private static final int FLOAT_NEXT_PAGE = 4;
    private static final int FLOAT_PREVIOUS_PAGE = 5;
    private static final int FLOAT_CHOOSE_PAGE = 6;
    private static final int FLOAT_VOLUME_CHANGED = 7;
    private static final int FLOAT_SHOW_ANIM = 8;

    private static FloatWindowManager sManager;

    static {
        sManager = new BlueWindowManager();
    }

    private static class FloatWindowHandler extends Handler {

        public FloatWindowHandler() {
            super(Looper.getMainLooper());
        }

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case FLOAT_SHOW_MESSAGE:
                    sManager.showMessage((WindowMessageEntity) msg.obj);
                    break;
                case FLOAT_SHOW_ADDRESS:
                    sManager.setItemClickListener((OnItemClickListener) msg.obj);
                    sManager.showAddress();
                    break;
                case FLOAT_SHOW_STRATEGY:
                    sManager.setItemClickListener((OnItemClickListener) msg.obj);

                    sManager.showStrategy();
                    break;
                case FLOAT_REMOVE_WINDOW:
                    sManager.removeFloatWindow();
                    break;
                case FLOAT_NEXT_PAGE:
                    sManager.onNextPage();
                    break;
                case FLOAT_PREVIOUS_PAGE:
                    sManager.onPreviousPage();
                    break;
                case FLOAT_CHOOSE_PAGE:
                    sManager.onChoosePage(msg.arg1);
                    break;
                case FLOAT_VOLUME_CHANGED:
                    sManager.onVolumeChanged(msg.arg1);
                    break;
                case FLOAT_SHOW_ANIM:
                    if (ActivitiesManager.getInstance().getTopActivity() instanceof MainRecordActivity) {
                        EventBus.getDefault().post(VoiceEvent.SHOW_ANIM);
                        ((BlueWindowManager) sManager).showAnimWindow();
                    } else {
                        sManager.showMessage(new WindowMessageEntity(Constants.WAKEUP_WORDS, MessageType.MESSAGE_INPUT));
                    }
                    break;
            }
        }

    }

    private static FloatWindowHandler sHandler = new FloatWindowHandler();


    public static void showMessage(String message, MessageType type) {
        sHandler.sendMessage(sHandler.obtainMessage(FLOAT_SHOW_MESSAGE,
                new WindowMessageEntity(message, type)));
    }

    public static void showAddress(OnItemClickListener listener) {
        sHandler.sendMessage(sHandler.obtainMessage(FLOAT_SHOW_ADDRESS, listener));
    }

    public static void showStrategy(OnItemClickListener listener) {
        sHandler.sendMessage(sHandler.obtainMessage(FLOAT_SHOW_STRATEGY, listener));
    }

    public static void removeFloatWindow() {
        sHandler.sendMessage(sHandler.obtainMessage(FLOAT_REMOVE_WINDOW));
    }

    public static void onNextPage() {
        sHandler.sendMessage(sHandler.obtainMessage(FLOAT_NEXT_PAGE));
    }

    public static void onPreviousPage() {
        sHandler.sendMessage(sHandler.obtainMessage(FLOAT_PREVIOUS_PAGE));
    }

    public static void onChoosePage(int page) {
        sHandler.sendMessage(sHandler.obtainMessage(FLOAT_CHOOSE_PAGE, page, 0));
    }

    public static void onVolumeChanged(int volume) {
        sHandler.sendMessage(sHandler.obtainMessage(FLOAT_VOLUME_CHANGED, volume, 0));
    }

    public static void showAnimWindow() {
        sHandler.sendMessage(sHandler.obtainMessage(FLOAT_SHOW_ANIM));
    }

}
