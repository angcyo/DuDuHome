package com.dudu.voice.semantic.bean;

/**
 * Created by 赵圣琪 on 2015/12/24.
 */
public class SemanticBean {

    protected String service;

    protected String text;

    // 标识是否有返回
    protected boolean hasResult = true;

    public static SemanticBean getDefaultBean(String text) {
        SemanticBean bean = new DefaultBean();
        bean.setHasResult(false);
        bean.setText(text);
        return bean;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean hasResult() {
        return hasResult;
    }

    public void setHasResult(boolean hasResult) {
        this.hasResult = hasResult;
    }

}
