/*
 * <p>标题: 中国电信综合调度系统第4版</p>
 * <p>描述: ...</p>
 * <p>版权: Copyright (c) 2013</p>
 * <p>公司: 天讯瑞达通信技术有限公司</p>
 * <p>网址：http://www.tisson.cn/</p>
 */
package com.regaltec.asip.manager.api.client;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringEscapeUtils;
import org.mule.transport.http.multipart.MultiPartInputStream.MultiPart;

/**
 * <p>概述该类作用，请详细描述。</p>
 * <p>创建日期：2013-6-14 上午09:56:36</p>
 *
 * @author yihaijun
 */
public class AsipReqeustParamter implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = -3842895630993799447L;
    private Map<String, List<String>> paramterMap = null;
    private Map<String, List<MultiPart>> multiPartMap = null;
    private Object payload = null;

    public AsipReqeustParamter() {
        this.paramterMap = new HashMap<String, List<String>>();
        this.multiPartMap = new HashMap<String, List<MultiPart>>();
    }

    public void addParamter(String key, String value) {
        List<String> values = paramterMap.get(key);
        if (values == null) {
            values = new ArrayList<String>();
            paramterMap.put(key, values);
        }
        values.add(value);
    }

    public void addMultiPart(String key, MultiPart value) {
        List<MultiPart> values = multiPartMap.get(key);
        if (values == null) {
            values = new ArrayList<MultiPart>();
            multiPartMap.put(key, values);
        }
        values.add(value);
    }

    public List<String> getParamterValues(String key) {
        return paramterMap.get(key);
    }

    public String getParamter(String key) {
        List<String> values = this.getParamterValues(key);
        if (values != null && values.size() > 0)
            return values.get(0);
        return null;
    }

    public List<MultiPart> getMultiPartList() {
        if (!multiPartMap.isEmpty()) {
            List<MultiPart> multiPartList = new ArrayList<MultiPart>();
            for (Entry<String, List<MultiPart>> entery : multiPartMap.entrySet()) {
                multiPartList.addAll(entery.getValue());
            }
            return multiPartList;
        }
        return null;
    }

    public List<MultiPart> getMultiPartList(String name) {
        return multiPartMap.get(name);
    }

    public MultiPart getMultiPart(String name) {
        List<MultiPart> multiPartList = this.getMultiPartList(name);
        if (multiPartList != null && multiPartList.size() > 0)
            return multiPartList.get(0);
        return null;
    }

    /**
     * @return the paramterMap
     */
    public Map<String, List<String>> getParamterMap() {
        return paramterMap;
    }

    /**
     * @param paramterMap the paramterMap to set
     */
    public void setParamterMap(Map<String, List<String>> paramterMap) {
        this.paramterMap = paramterMap;
    }

    /**
     * @return the multiPartMap
     */
    public Map<String, List<MultiPart>> getMultiPartMap() {
        return multiPartMap;
    }

    /**
     * @param multiPartMap the multiPartMap to set
     */
    public void setMultiPartMap(Map<String, List<MultiPart>> multiPartMap) {
        this.multiPartMap = multiPartMap;
    }

    /**
     * @return the payload
     */
    public Object getPayload() {
        return payload;
    }

    /**
     * @param payload the payload to set
     */
    public void setPayload(Object payload) {
        this.payload = payload;
    }

    public String toText() {
        StringBuffer outBuf = new StringBuffer();
        outBuf.delete(0, outBuf.length());
        outBuf.append("<AsipReqeustParamter>");
        outBuf.append("\r\n\t<paramterMap>");
        Iterator<String> it = null;
        it = paramterMap.keySet().iterator();
        for (; it.hasNext();) {
            String keyName = it.next();
            outBuf.append("\r\n\t\t<" + StringEscapeUtils.escapeXml(keyName) + ">");
            outBuf.append(StringEscapeUtils.escapeXml("" + paramterMap.get(keyName)));
            outBuf.append("</" + StringEscapeUtils.escapeXml(keyName) + ">");
        }
        outBuf.append("\r\n\t</paramterMap>");
        outBuf.append("\r\n\t<multiPartMap>");
        it = multiPartMap.keySet().iterator();
        for (; it.hasNext();) {
            String keyName = it.next();
            outBuf.append("\r\n\t\t<" + StringEscapeUtils.escapeXml(keyName) + ">");
            outBuf.append(StringEscapeUtils.escapeXml("" + multiPartMap.get(keyName)));
            outBuf.append("</" + StringEscapeUtils.escapeXml(keyName) + ">");
        }
        outBuf.append("\r\n\t</multiPartMap>");
        outBuf.append("\r\n\t<payload>");
        outBuf.append(StringEscapeUtils.escapeXml("" + payload));
        outBuf.append("\r\n\t</payload>");
        outBuf.append("</AsipReqeustParamter>");
        return outBuf.toString();
    }
}
