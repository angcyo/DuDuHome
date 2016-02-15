package com.dudu.commonlib.xml;

/**
 * Created by Administrator on 2016/2/15.
 */

import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Eaway on 2016/2/15.
 */
public class JDomXmlDocument {

    public Document parserXml(InputStream inputStream) {
        Document document = null;
        SAXBuilder builder = new SAXBuilder();
        try {
            document = builder.build(inputStream);
        } catch (JDOMException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return document;
    }
}
