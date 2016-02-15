package com.dudu.aios.ui.map.event;

/**
 * Created by Administrator on 2016/2/15.
 */
public class ChooseEvent {

    public static final int ADDRESS_NUMBER = 1;

    public static final int STRATEGY_NUMBER = 2;

    public static final int CHOOSEPAGE = 3;

    public static final int NEXTPAGE = 4;

    public static final int PREVIOUSPAGE = 5;

    private int chooseType;

    private int position;


    public int getChooseType() {
        return chooseType;
    }

    public int getPosition() {
        return position;
    }


    public ChooseEvent(int position, int chooseType) {
        this.position = position;
        this.chooseType = chooseType;
    }
}
