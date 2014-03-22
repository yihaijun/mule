/*
 * <p>标题: 中国电信综合调度系统第4版</p>
 * <p>描述: ...</p>
 * <p>版权: Copyright (c) 2013</p>
 * <p>公司: 天讯瑞达通信技术有限公司</p>
 * <p>网址：http://www.tisson.cn/</p>
 */
package com.regaltec.asip.routing;

import java.io.CharArrayReader;
import java.io.File;
import java.io.IOException;
import java.util.List;

import mx4j.log.Logger;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.mule.api.MuleContext;
import org.mule.api.MuleEvent;
import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.mule.api.routing.RoutePathNotFoundException;
import org.mule.config.i18n.CoreMessages;

/**
 * <p>概述该类作用，请详细描述。</p>
 * <p>创建日期：2013-4-13 下午07:04:37</p>
 *
 * @author yihaijun
 */
public class AsipDynamicRouteProcessor {
    private Element rooElement = null;
    private Element appElement = null;
    private Element serviceElement = null;
    private List outboundElementList = null;
    private Element currOutboundElement = null;
    private Element transformerParentElement = null;
    // private MuleContext muleContext = null;
    private int currOutboundElementIndex = 0;
    private Document document = null;
    private long lastModified = 0L;

    public AsipDynamicRouteProcessor(AsipDynamicRouteProcessor processor) {
        document = processor.getDocument();
        lastModified = processor.getLastModified();

        rooElement = document.getRootElement();
        appElement = rooElement.getChild("app");
        serviceElement = appElement.getChild("service");
        outboundElementList = serviceElement.getChildren();
        currOutboundElementIndex = 0;
        currOutboundElement = (Element) outboundElementList.get(currOutboundElementIndex);
    }

    public AsipDynamicRouteProcessor(String routerServiceFileName) throws IOException {
        java.io.File file = new java.io.File(routerServiceFileName);

        String serviceConf = "";
        serviceConf = org.mule.util.FileUtils.readFileToString(file);

        // this.muleContext = muleContext;
        SAXBuilder sb = null;
        sb = new SAXBuilder();
        try {
            document = sb.build(new CharArrayReader(serviceConf.toCharArray()));
            File f = new File(routerServiceFileName);
            lastModified = f.lastModified();

            rooElement = document.getRootElement();
            appElement = rooElement.getChild("app");
            serviceElement = appElement.getChild("service");
            outboundElementList = serviceElement.getChildren();
            currOutboundElementIndex = 0;
            currOutboundElement = (Element) outboundElementList.get(currOutboundElementIndex);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Element getNextValidOutboundElement(MuleEvent event) {
        if (currOutboundElementIndex < outboundElementList.size()) {
            currOutboundElement = (Element) outboundElementList.get(currOutboundElementIndex);
        } else {
            currOutboundElement = null;
        }

        if (currOutboundElement == null) {
            return null;
        }
        if (currOutboundElement.getChild("filtering-router") == null) {
            return currOutboundElement;
        }
        Element filterElement = (Element) currOutboundElement.getChild("filtering-router");
        if (filterElement.getChildren() == null || filterElement.getChildren().size() <= 0) {
            return currOutboundElement;
        }
        Element outboundElement = currOutboundElement;
        filterElement = (Element) filterElement.getChildren().get(0);
        AsipDynamicRouteFilter filter = new AsipDynamicRouteFilter(filterElement);
        boolean ret = filter.accept(event);
        currOutboundElementIndex++;
        if (ret) {
            return outboundElement;
        }
        return getNextValidOutboundElement(event);
    }

    public MuleEvent requestTransformer(MuleEvent event) throws MuleException {
        if (currOutboundElement == null) {
            return null;
        }
        if (currOutboundElement.getChild("request") == null) {
            return event;
        }
        transformerParentElement = (Element) currOutboundElement.getChild("request");

        return tranformer(event);
    }

    public MuleEvent responseTransformer(MuleEvent event) throws MuleException {
        if (currOutboundElement == null) {
            return null;
        }
        if (currOutboundElement.getChild("response") == null) {
            return event;
        }
        transformerParentElement = (Element) currOutboundElement.getChild("response");
        return tranformer(event);
    }

    private MuleEvent tranformer(MuleEvent event) throws MuleException {
        if (currOutboundElement == null) {
            return null;
        }
        if (transformerParentElement == null) {
            return event;
        }
        if (transformerParentElement.getChildren() == null) {
            return event;
        }
        List transformerElementList = transformerParentElement.getChildren();
        for (int i = 0; i < transformerElementList.size(); i++) {
            Element transformerElement = (Element) transformerElementList.get(i);
            AsipDynamicRouteTransformer transFormer = new AsipDynamicRouteTransformer(transformerElement);
            event = transFormer.transform(event);
        }
        return event;
    }

    /**
     * @return the document
     */
    public Document getDocument() {
        return document;
    }

    /**
     * @return the lastModified
     */
    public long getLastModified() {
        return lastModified;
    }
}
