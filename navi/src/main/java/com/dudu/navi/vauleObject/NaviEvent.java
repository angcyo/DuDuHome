package com.dudu.navi.vauleObject;

/**
 * Created by lxh on 2015/11/26.
 */
public class NaviEvent {



    public enum FloatButtonEvent {

        SHOW,
        HIDE
    }

    public static class NaviVoiceBroadcast{
        private String naviVoice;

        public NaviVoiceBroadcast(String naviVoice){
            this.naviVoice = naviVoice;
        }

        public String getNaviVoice() {
            return naviVoice;
        }
    }
}