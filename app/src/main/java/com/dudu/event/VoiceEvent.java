package com.dudu.event;

/**
 * Created by Eaway.Shen on 2015/12/5.
 */
public class VoiceEvent {

    public static final int INIT_VOICE_SERVICE = 0;
    public static final int STOP_VOICE_SERVICE = 2;

    private int voiceEvent;

    public VoiceEvent(int voiceEvent) {
        this.voiceEvent = voiceEvent;
    }

    public int getVoiceEvent() {
        return voiceEvent;
    }

}
