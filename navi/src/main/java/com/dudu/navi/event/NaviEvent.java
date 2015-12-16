package com.dudu.navi.event;

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
        private boolean isShow;
        public NaviVoiceBroadcast(String naviVoice,boolean isShow){
            this.naviVoice = naviVoice;
            this.isShow = isShow;
        }

        public String getNaviVoice() {
            return naviVoice;
        }

        public boolean isShow() {
            return isShow;
        }
    }


    public enum  ChangeSemanticType{

        NORMAL,

        NAVIGATION,

        MAP_CHOISE

    }

    public enum  SearchResult{
        SUCCESS,
        FAIL
    }

    public static class NavigationInfoBroadcast{
        private String info;
        public NavigationInfoBroadcast(String info){
            this.info = info;
        }

        public String getInfo() {
            return info;
        }
    }
}
