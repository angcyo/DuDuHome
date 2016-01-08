package com.dudu.event;

/**
 * Created by Administrator on 2016/1/8.
 */
public class GaoMapEvent {
    private String message;

    public GaoMapEvent(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
