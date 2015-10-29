package com.dudu.voice.semantic.chain;

import com.dudu.voice.semantic.VoiceManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/10/28.
 */
public abstract class SemanticChain {

    protected VoiceManager mVoiceManager;

    private List<SemanticChain> mChilds = new ArrayList<>();

    public abstract boolean matchSemantic(String service);

    public abstract boolean doSemantic(String json);

    public SemanticChain() {
        mVoiceManager = VoiceManager.getInstance();
    }

    public SemanticChain getNextChild() {
        if (!mChilds.isEmpty()) {
            return mChilds.get(0);
        }

        return null;
    }

    public void addChildChain(SemanticChain child) {
        if (child != null) {
            mChilds.add(child);
        }
    }

}
