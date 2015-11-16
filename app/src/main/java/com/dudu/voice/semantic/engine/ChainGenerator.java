package com.dudu.voice.semantic.engine;

import com.dudu.voice.semantic.chain.AdjustVolumeChain;
import com.dudu.voice.semantic.chain.BaikeChain;
import com.dudu.voice.semantic.chain.CarCheckingChain;
import com.dudu.voice.semantic.chain.CarCheckingChoiseChain;
import com.dudu.voice.semantic.chain.CarCheckingWhetherChain;
import com.dudu.voice.semantic.chain.ChatChain;
import com.dudu.voice.semantic.chain.ChoiseChain;
import com.dudu.voice.semantic.chain.ChoosePageChain;
import com.dudu.voice.semantic.chain.CmdChain;
import com.dudu.voice.semantic.chain.CommonAddressChain;
import com.dudu.voice.semantic.chain.DatetimeChain;
import com.dudu.voice.semantic.chain.DuDuChain;
import com.dudu.voice.semantic.chain.MapSearchChain;
import com.dudu.voice.semantic.chain.NavigationChain;
import com.dudu.voice.semantic.chain.OpenQaChain;
import com.dudu.voice.semantic.chain.PoiChain;
import com.dudu.voice.semantic.chain.WifiChain;
import com.dudu.voice.semantic.chain.WeatherChain;
import com.dudu.voice.semantic.chain.WhetherChain;

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
        chain.addChildChain(chain);
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
        chain.addChildChain(chain);
        return chain;
    }

    public ChatChain getChatChain() {
        ChatChain chain = new ChatChain();
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

    public CommonAddressChain getCommonAddressChain(){
        CommonAddressChain chain = new CommonAddressChain();
        WhetherChain whetherChain = new WhetherChain();
        chain.addChildChain(whetherChain);
        return chain;
    }

    public WifiChain getWIFIChain(){
        WifiChain chain = new WifiChain();
        return  chain;
    }

    public CarCheckingChain getCarCheckingChain() {
        CarCheckingChain chain = new CarCheckingChain();
        return chain;
    }

    public CarCheckingWhetherChain getCarCheckingWhetherChain() {
        CarCheckingWhetherChain chain = new CarCheckingWhetherChain();
        CarCheckingChoiseChain choiseChain = new CarCheckingChoiseChain();
        chain.addChildChain(choiseChain);
        return chain;
    }

    public CarCheckingWhetherChain getCarCheckingChoiseChain() {
        CarCheckingWhetherChain chain = new CarCheckingWhetherChain();
        return chain;
    }

    public BaikeChain getBaikeChain() {
        BaikeChain chain = new BaikeChain();
        return chain;
    }

    public DatetimeChain getDatetimeChain() {
        DatetimeChain chain = new DatetimeChain();
        return chain;
    }

    public DuDuChain getDuDuChain() {
        DuDuChain chain = new DuDuChain();
        return chain;
    }

}
