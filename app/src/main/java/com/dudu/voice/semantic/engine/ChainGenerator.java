package com.dudu.voice.semantic.engine;

import com.dudu.voice.semantic.chain.AdjustVolumeChain;
import com.dudu.voice.semantic.chain.MapSearchChain;
import com.dudu.voice.semantic.chain.CmdChain;
import com.dudu.voice.semantic.chain.NavigationChain;

public class ChainGenerator {

    public ChainGenerator() {

    }

    public AdjustVolumeChain generateVoiceChain() {
        AdjustVolumeChain chain = new AdjustVolumeChain();
        chain.addChildChain(chain);
        return chain;
    }

    public CmdChain generateCmdChain() {
        CmdChain chain = new CmdChain();
        return chain;
    }

    public MapSearchChain getMapSearchChain(){
        MapSearchChain chain = new MapSearchChain();
        return chain;
    }

    public NavigationChain getNavigationChain(){
        NavigationChain chain = new NavigationChain();
        return chain;
    }

}
