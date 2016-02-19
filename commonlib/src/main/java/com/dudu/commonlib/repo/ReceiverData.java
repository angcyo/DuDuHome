package com.dudu.commonlib.repo;

/**
 * Created by Administrator on 2016/2/17.
 */
public class ReceiverData {
    //防盗推送数据：XGPushShowedResult [title=theft, content={"switch":1}, customContent=null]
    //加速测试推送数据：XGPushShowedResult [title=acceleratedTestStart, content=1, customContent=null]
    public static final String XGPUSHSHOWEDRESULT_KEY = "XGPushShowedResult";
    public static final String TITLE_KEY = "title";
    public static final String CONTENT_KEY = "content";
    public static final String SWITCH_KEY = "switch";
    public static final String CUSTOMCONTENT_KEY = "customContent";
    public static final String THEFT_VALUE = "theft";
    public static final String ROBBERY_VALUE = "robbery";
    public static final String ACCELERATEDTESTSTART_VALUE = "acceleratedTestStart";

    private String title;
    private String content;
    private String customContent;
    private String switchValue;
    private String switch0Value;
    private String switch1Value;
    private String switch2Value;
    private String switch3Value;

    public ReceiverData(String title,String content, String customContent){
        this.title = title;
        this.content = content;
        this.customContent = customContent;
    }

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
    }

    public String getSwitchValue() {
        return switchValue;
    }

    public void setSwitchValue(String switchValue) {
        this.switchValue = switchValue;
    }

    public String getSwitch0Value() {
        return switch0Value;
    }

    public void setSwitch0Value(String switch0Value) {
        this.switch0Value = switch0Value;
    }

    public String getSwitch1Value() {
        return switch1Value;
    }

    public void setSwitch1Value(String switch1Value) {
        this.switch1Value = switch1Value;
    }

    public String getSwitch2Value() {
        return switch2Value;
    }

    public void setSwitch2Value(String switch2Value) {
        this.switch2Value = switch2Value;
    }

    public String getSwitch3Value() {
        return switch3Value;
    }

    public void setSwitch3Value(String switch3Value) {
        this.switch3Value = switch3Value;
    }
}
