package com.dudu.voice.semantic.chain;

import com.dudu.event.DeviceEvent;
import com.dudu.voice.semantic.SemanticConstants;

import de.greenrobot.event.EventBus;

/**
 * Created by Administrator on 2015/11/18.
 */
public class DimScreenChain extends SemanticChain {

    @Override
    public boolean matchSemantic(String service) {
        return SemanticConstants.SERVICE_DIM.equals(service);
    }

    @Override
    public boolean doSemantic(String json) {
        //TODO
        EventBus.getDefault().post(new DeviceEvent.Screen(DeviceEvent.OFF));
        return true;
    }

}
