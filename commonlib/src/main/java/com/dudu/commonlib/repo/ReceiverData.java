package com.dudu.commonlib.repo;

/**
 * Created by Administrator on 2016/2/17.
 */
public class ReceiverData {
    //XGPushShowedResult [title=theft, content={"switch":1}, customContent=null]

    public static final String XGPUSHSHOWEDRESULT_KEY = "XGPushShowedResult";
    public static final String TITLE_KEY = "title";
    public static final String CONTENT_KEY = "content";
    public static final String SWITCH_KEY = "switch";
    public static final String CUSTOMCONTENT_KEY = "customContent";

    private String title;
    private String content;
    private String customContent;
    private int switchContent;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCustomContent() {
        return customContent;
    }

    public void setCustomContent(String customContent) {
        this.customContent = customContent;
    }public int getSwitchContent() {
        return switchContent;
    }

    public void setSwitchContent(int switchContent) {
        this.switchContent = switchContent;
    }


}
