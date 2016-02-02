package com.dudu.voice.semantic.bean.map;

import com.dudu.voice.semantic.bean.SemanticBean;

/**
 * Created by lxh on 2015/12/25.
 */
public abstract class MapBean extends SemanticBean {

    private String poiName;

    public String getPoiName() {
        return poiName;
    }

    public void setPoiName(String poiName) {

        this.poiName = poiName;
    }
}
