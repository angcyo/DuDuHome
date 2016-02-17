package com.dudu.voice.semantic.chain;

import com.dudu.android.launcher.utils.CmdType;
import com.dudu.voice.semantic.bean.SemanticBean;
import com.dudu.voice.semantic.bean.WeatherBean;

/**
 * Created by lxh on 2016/2/17.
 */
public class WeatherChain extends SemanticChain{
    @Override
    public boolean matchSemantic(String service) {
        return CmdType.SERVICE_WEATHER.equals(service);
    }

    @Override
    public boolean doSemantic(SemanticBean bean) {

        WeatherBean weatherBean = (WeatherBean) bean;



        return true;
    }
}
