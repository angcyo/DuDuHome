package com.dudu.android.launcher.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2015/11/2.
 */
public class PoiEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    private PoiSlots slots;

    public PoiSlots getSlots() {
        return slots;
    }

    public void setSlots(PoiSlots slots) {
        this.slots = slots;
    }

}
