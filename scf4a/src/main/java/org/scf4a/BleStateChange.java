package org.scf4a;

/**
 * Created by Administrator on 2015/11/19.
 */
public class BleStateChange {
    public static final int BLECONNECTED = 1;

    public static final int BLEDISCONNECTED = 0;

    private int connState ;

    public BleStateChange(int connState){
        this.connState = connState;
    }
    public int getConnState(){
        return  connState;
    }
}
