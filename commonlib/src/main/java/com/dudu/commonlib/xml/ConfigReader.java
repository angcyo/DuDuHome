package com.dudu.commonlib.xml;

/**
 * Created by Administrator on 2016/2/15.
 */

import com.dudu.commonlib.CommonLib;
import com.dudu.commonlib.R;
import com.dudu.commonlib.repo.Config;

import org.jdom2.Document;
import org.jdom2.Element;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/2/14.
 */
public class ConfigReader {

    private static ConfigReader mInstance;

    private boolean mIsTest = false;
    private int mTestIndex = 0;
    private int mNormalIndex = 1;
    private List<Config> mConfigList = new ArrayList<>();

    public static ConfigReader getInstance(){
        if(mInstance==null){
            mInstance = new ConfigReader();
        }
        return mInstance;
    }

    private ConfigReader(){}

    public void readDefaultConfig() {
        InputStream in = CommonLib.getInstance().getContext()
                .getResources().openRawResource(R.raw.config);

        Document document = new JDomXmlDocument().parserXml(in);
        Element configs = document.getRootElement();
        Element isTestElement = configs.getChild("is_test");
        String isTest = isTestElement.getValue();
        mIsTest = Integer.valueOf(isTest)==1;

        List configElementList = configs.getChildren("config");

        for (int i = 0; i < configElementList.size(); i++) {
            Element configElement = (Element) configElementList.get(i);
            Config config1 = new Config();
            String is_test = configElement.getAttributeValue("is_test");
            boolean configIsTest = Integer.valueOf(is_test)==1;
            config1.setTest(configIsTest);
            if(configIsTest){
                mTestIndex = i;
            }else{
                mNormalIndex = i;
            }
            List configValues = configElement.getChildren();
            for (int j = 0; j < configValues.size(); j++) {
                Element element = (Element) configValues.get(j);
                switch (element.getName()){
                    case "ip":
                        config1.setServerAddress(element.getText());
                        break;
                }
            }
            mConfigList.add(config1);
        }
    }

    public void initTestConfig() {
        Config config = new Config();
        config.setTest(true);
        config.setServerAddress("http://192.168.0.177:8080/");
        mConfigList.add(config);
    }

    public Config getConfig() {
        if(mIsTest){
            return mConfigList.get(mTestIndex);
        }else{
            return mConfigList.get(mNormalIndex);
        }
    }

    public void setConfigList(List<Config> mConfigList) {
        this.mConfigList = mConfigList;
    }

    public boolean isTest() {
        return mIsTest;
    }

    public void setIsTest(boolean mIsTest) {
        this.mIsTest = mIsTest;
    }
}

