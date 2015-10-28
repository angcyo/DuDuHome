package com.dudu.voice.semantic.engine;

import com.dudu.voice.semantic.chain.AdjustVolumeChain;

public class ChainGenerator {

    public ChainGenerator() {

    }

    AdjustVolumeChain generateVoiceChain() {
        AdjustVolumeChain chain = new AdjustVolumeChain();
        chain.addChildChain(chain);
        return chain;
    }

}