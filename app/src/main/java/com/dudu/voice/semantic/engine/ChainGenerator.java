package com.dudu.voice.semantic.engine;

import com.dudu.voice.semantic.chain.AdjustVolumeChain;
import com.dudu.voice.semantic.chain.BaikeChain;
import com.dudu.voice.semantic.chain.ChatChain;
import com.dudu.voice.semantic.chain.ChoiseChain;
import com.dudu.voice.semantic.chain.ChoosePageChain;
import com.dudu.voice.semantic.chain.FaqChain;
import com.dudu.voice.semantic.chain.MapSearchChain;
import com.dudu.voice.semantic.chain.CmdChain;
import com.dudu.voice.semantic.chain.NavigationChain;
import com.dudu.voice.semantic.chain.OpenQaChain;
import com.dudu.voice.semantic.chain.PoiChain;
import com.dudu.voice.semantic.chain.SemanticChain;
import com.dudu.voice.semantic.chain.WeatherChain;

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

    public WeatherChain getWeatherChain() {
        WeatherChain chain = new WeatherChain();
        return chain;
    }

    public ChoiseChain getChoiseChain(){
        ChoiseChain chain = new ChoiseChain();
        return chain;
    }

    public BaikeChain getBaikeChain(){
        BaikeChain chain = new BaikeChain();
        return chain;
    }

    public ChatChain getChatChain() {
        ChatChain chain = new ChatChain();
        return chain;
    }

    public FaqChain getFaqChain() {
        FaqChain chain = new FaqChain();
        return chain;
    }

    public OpenQaChain getOpenQaChain() {
        OpenQaChain chain = new OpenQaChain();
        return chain;
    }

    public ChoosePageChain getChoosePageChain(){
        ChoosePageChain chain = new ChoosePageChain();
        chain.addChildChain(chain);
        return chain;
    }

    public PoiChain getPoiChain() {
        PoiChain chain = new PoiChain();
        return chain;
    }

}
