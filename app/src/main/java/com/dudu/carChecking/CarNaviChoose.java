package com.dudu.carChecking;

/**
 * Created by lxh on 2016/2/22.
 */
public class CarNaviChoose {

    public enum ChooseType {
        CHOOSE_PAGE,

        CHOOSE_NUMBER,

        NEXT_PAGE,

        LAST_PAGE
    }

    private ChooseType type;

    private int position;

    public CarNaviChoose(ChooseType type, int position) {
        this.type = type;
        this.position = position;
    }

    public ChooseType getType() {
        return type;
    }

    public int getPosition() {
        return position;
    }
}
