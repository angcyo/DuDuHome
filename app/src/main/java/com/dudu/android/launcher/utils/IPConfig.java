package com.dudu.android.launcher.utils;

import android.content.Context;
import android.util.Log;

import com.dudu.android.launcher.R;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by lxh on 2016/1/2.
 */
public class IPConfig {
    public final static String CONFIG_FILE_NAME = "ipConfig.xml";

    private final int READ_SDFILE = 1;
    private final int READ_RAWFILE = 2;

    private Context mContext;
    private String configPath = "";
    private static IPConfig mIPConfig;

    private String mServerIP = "119.29.65.127";
    private int mServerPort = 8000;
    private String mTestServerIP = "119.29.132.60";
    private int mTestServerPort = 8888;
    private String configDirectory;

    private boolean isTest_Server = false;

    public IPConfig(Context context) {
        this.mContext = context;
    }

    public static IPConfig getInstance(Context context) {

        if (mIPConfig == null)
            mIPConfig = new IPConfig(context);
        return mIPConfig;
    }

    public void init() {
        configDirectory =  FileUtils.getSdPath() + "/dudu/config";
        configPath = configDirectory +"/"+CONFIG_FILE_NAME;
        File configFile = new File(configPath);
        if (!configFile.exists()) {
            readDefault_config(READ_RAWFILE, configFile);
            copyDefault(mContext, configFile);
        } else {
            readDefault_config(READ_SDFILE, configFile);
        }

    }

    public String getServerIP() {
        return mServerIP;
    }

    public int getServerPort() {
        return mServerPort;
    }

    public String getTestServerIP() {
        return mTestServerIP;
    }

    public int getTestServerPort() {
        return mTestServerPort;
    }

    public boolean isTest_Server() {

        return isTest_Server;
    }

    private void readDefault_config(int readType, File configFile) {
        try {
            InputStream in = mContext.getResources().openRawResource(R.raw.ip_config);
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xmlParser = factory.newPullParser();
            switch (readType) {
                case READ_RAWFILE:
                    xmlParser.setInput(in, "UTF-8");
                    break;
                case READ_SDFILE:
                    try {
                        xmlParser.setInput(new FileReader(configFile));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    break;
            }
            String tagName = new String("");
            int eventType = xmlParser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    tagName = xmlParser.getName();
                } else if (eventType == XmlPullParser.TEXT) {
                    switch (tagName) {
                        case "server_ip":
                            mServerIP = xmlParser.getText();
                            break;
                        case "server_port":
                            mServerPort = Integer.parseInt(xmlParser.getText());
                            break;
                        case "test_server_ip":
                            mTestServerIP = xmlParser.getText();
                            break;
                        case "test_server_port":
                            mTestServerPort =Integer.parseInt(xmlParser.getText());
                            break;
                        case "is_test":
                            isTest_Server = xmlParser.getText().equals("1");
                            break;
                    }

                } else {
                    tagName = new String("");
                }
                try {
                    eventType = xmlParser.next();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }

    }

    private void copyDefault(Context context, File configFile) {
        File fileDir = new File(configDirectory);
        if(!fileDir.exists()){
            fileDir.mkdir();
        }
        FileOutputStream fileOutput = null;
        try {
            fileOutput = new FileOutputStream(configFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        if (null != fileOutput) {
            InputStream in = null;
            try {
                in = context.getResources().openRawResource(R.raw.ip_config);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            // 拷贝数据
            if (null != in) {
                byte[] buf = new byte[1024];
                int length = buf.length;

                try {
                    while (-1 != (length = in.read(buf, 0, length))) {
                        fileOutput.write(buf, 0, length);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            // 关闭文件
            try {
                fileOutput.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean changeConfig() {
        File configFile = new File(configPath);
        XmlSerializer xmlFile;
        try {
            configFile.delete();
            configFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            xmlFile = XmlPullParserFactory.newInstance().newSerializer();
            xmlFile.setOutput(new FileWriter(configFile));
            xmlFile.startDocument("UTF-8", false);

            // <config>
            xmlFile.startTag(null, "config");

            // <server_ip>
            xmlFile.startTag(null, "server_ip");
            xmlFile.text(mServerIP);
            xmlFile.endTag(null, "server_ip");

            // <server_port>
            xmlFile.startTag(null, "server_port");
            xmlFile.text(Integer.valueOf(mServerPort).toString());
            xmlFile.endTag(null, "server_port");

            // <test_server_ip>
            xmlFile.startTag(null, "test_server_ip");
            xmlFile.text(mTestServerIP);
            xmlFile.endTag(null, "test_server_ip");

            // <test_server_port>
            xmlFile.startTag(null, "test_server_port");
            xmlFile.text(Integer.valueOf(mTestServerPort).toString());
            xmlFile.endTag(null, "test_server_port");

            // is_test_server
            xmlFile.startTag(null, "is_test");
            xmlFile.text(Integer.valueOf(isTest_Server ? 1 : 0).toString());
            xmlFile.endTag(null, "is_test");

            xmlFile.endTag(null, "config");

            xmlFile.endDocument();
            xmlFile.flush();
            return true;
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean changeConfig(boolean isTest_Server) {
        return changeConfig();
    }

    public boolean changeConfig(String ip, String testIp, int port, int testPort, boolean istest) {

        this.mServerIP = ip;
        this.mTestServerIP = testIp;
        this.mServerPort = port;
        this.mTestServerPort = testPort;
        this.isTest_Server = istest;

        return changeConfig();
    }


}
