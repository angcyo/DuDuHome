package com.dudu.voice.semantic.engine;

import com.dudu.voice.semantic.chain.AdjustVolumeChain;
import com.dudu.voice.semantic.chain.MapSearchChain;

public class ChainGenerator {

    public ChainGenerator() {

    }

    public AdjustVolumeChain generateVoiceChain() {
        AdjustVolumeChain chain = new AdjustVolumeChain();
        chain.addChildChain(chain);
        return chain;
    }

    public MapSearchChain getMapSearchChain(){
        MapSearchChain chain = new MapSearchChain();
        chain.addChildChain(chain);
        return  chain;
    }
}