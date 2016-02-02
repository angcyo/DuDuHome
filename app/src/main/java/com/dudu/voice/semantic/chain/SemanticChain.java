package com.dudu.voice.semantic.chain;

import android.content.Context;

import com.dudu.android.launcher.LauncherApplication;
import com.dudu.voice.VoiceManagerProxy;
import com.dudu.voice.semantic.bean.SemanticBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/10/28.
 */
public abstract class SemanticChain {

    protected Context mContext;

    protected VoiceManagerProxy mVoiceManager;

    private List<SemanticChain> mChildren = new ArrayList<>();

    public abstract boolean matchSemantic(String service);

    public abstract boolean doSemantic(SemanticBean bean);

    public SemanticChain() {
        mContext = LauncherApplication.getContext();

        mVoiceManager = VoiceManagerProxy.getInstance();
    }

    public SemanticChain getNextChild() {
        if (!mChildren.isEmpty()) {
            return mChildren.get(0);
        }

        return null;
    }

    public void addChildChain(SemanticChain child) {
        if (child != null) {
            mChildren.add(child);
        }
    }

}
