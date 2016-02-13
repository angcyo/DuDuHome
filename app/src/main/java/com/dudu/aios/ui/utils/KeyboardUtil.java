package com.dudu.aios.ui.utils;

import android.app.Activity;
import android.content.Context;
import android.inputmethodservice.Keyboard;

import android.inputmethodservice.KeyboardView;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;


import com.dudu.android.launcher.R;
import com.dudu.android.launcher.utils.LogUtils;

import java.util.List;


/**
 * Created by Administrator on 2016/2/12.
 */
public class KeyboardUtil {
    private KeyboardView keyboardView;
    private Keyboard k1;
    public boolean isUpper = false;// 是否大写
    public boolean isSymbol = false;
    boolean isShow = false;
    private EditText ed;
    private Context ctx;
    private Keyboard k2;

    public KeyboardUtil(Context ctx, EditText edit, KeyboardView keyboardView) {
        this.ed = edit;
        this.ctx = ctx;
        k1 = new Keyboard(ctx, R.xml.custom);
        k2 = new Keyboard(ctx, R.xml.symbol);
        this.keyboardView = keyboardView;
        this.keyboardView.setKeyboard(k1);
        this.keyboardView.setEnabled(true);
        this.keyboardView.setPreviewEnabled(false);
        this.keyboardView.setOnKeyboardActionListener(listener);
    }

    private KeyboardView.OnKeyboardActionListener listener = new KeyboardView.OnKeyboardActionListener() {
        @Override
        public void swipeUp() {
        }

        @Override
        public void swipeRight() {
        }

        @Override
        public void swipeLeft() {
        }

        @Override
        public void swipeDown() {
        }

        @Override
        public void onText(CharSequence text) {
        }

        @Override
        public void onRelease(int primaryCode) {
        }

        @Override
        public void onPress(int primaryCode) {
        }

        @Override
        public void onKey(int primaryCode, int[] keyCodes) {
            Editable editable = ed.getText();
            int start = ed.getSelectionStart();
            if (primaryCode == Keyboard.KEYCODE_CANCEL) {// 完成
                hideKeyboard();
            } else if (primaryCode == Keyboard.KEYCODE_DELETE) {// 回退
                if (editable != null && editable.length() > 0) {
                    if (start > 0) {
                        editable.delete(start - 1, start);
                    }
                }
            } else if (primaryCode == Keyboard.KEYCODE_SHIFT) {// 大小写切换
                changeKey();
                keyboardView.setKeyboard(k1);
            } else if (primaryCode == 57419) { // go left
                if (start > 0) {
                    ed.setSelection(start - 1);
                }
            } else if (primaryCode == 57421) { // go right
                if (start < ed.length()) {
                    ed.setSelection(start + 1);
                }
            } else if (primaryCode == Keyboard.KEYCODE_MODE_CHANGE) {
                changeMode();
            } else {
                editable.insert(start, Character.toString((char) primaryCode));
            }
        }
    };

    private void changeMode() {
        if (isSymbol) {
            isSymbol = false;
            keyboardView.setKeyboard(k1);
        } else {
            isSymbol = true;
            keyboardView.setKeyboard(k2);
        }

    }

    /**
     * 键盘大小写切换
     */
    private void changeKey() {
        List<Keyboard.Key> keyList = k1.getKeys();
        LogUtils.v("flow", ".." + keyList.size());
        if (isUpper) {//大写切换小写
            isUpper = false;
            for (Keyboard.Key key : keyList) {
                if (key.codes != null && key.label != null && isword(key.label.toString())) {
                    key.label = key.label.toString().toLowerCase();
                    key.codes[0] = key.codes[0] + 32;
                } else if (key.codes != null) {
                    if (key.codes[0] == Keyboard.KEYCODE_SHIFT) {
                        LogUtils.v("flow", "  " + key.codes[0]);
                        key.icon = ctx.getResources().getDrawable(R.drawable.capital_arrows);
                    }
                }
            }
        } else {//小写切换大写
            isUpper = true;
            for (Keyboard.Key key : keyList) {
                if (key.codes != null && key.label != null && isword(key.label.toString())) {
                    key.label = key.label.toString().toUpperCase();
                    key.codes[0] = key.codes[0] - 32;
                } else if (key.codes != null) {
                    if (key.codes[0] == Keyboard.KEYCODE_SHIFT) {
                        key.icon = ctx.getResources().getDrawable(R.drawable.lowercase_arrows);
                    }
                }
            }
        }
    }

    public void showKeyboard() {
        int visibility = keyboardView.getVisibility();
        if (visibility == View.GONE || visibility == View.INVISIBLE) {
            keyboardView.setVisibility(View.VISIBLE);
            isShow = true;
        }
    }

    public void hideKeyboard() {
        int visibility = keyboardView.getVisibility();
        if (visibility == View.VISIBLE) {
            keyboardView.setVisibility(View.INVISIBLE);
            isShow = false;
        }
    }

    private boolean isword(String str) {
        String wordstr = "abcdefghijklmnopqrstuvwxyz";
        if (wordstr.indexOf(str.toLowerCase()) > -1) {
            return true;
        }
        return false;
    }
}