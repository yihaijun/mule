/*
 * <p>标题: 中国电信综合调度系统第4版</p>
 * <p>描述: ...</p>
 * <p>版权: Copyright (c) 2013</p>
 * <p>公司: 天讯瑞达通信技术有限公司</p>
 * <p>网址：http://www.tisson.cn/</p>
 */
package com.regaltec.asip.transformer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.activation.DataHandler;

import org.mule.api.MuleMessage;
import org.mule.api.transformer.TransformerException;
import org.mule.transport.http.multipart.MultiPartInputStream.MultiPart;
import org.mule.transport.http.multipart.PartDataSource;
import org.mule.transport.http.transformers.HttpRequestBodyToParamMap;

import com.regaltec.asip.manager.api.client.AsipReqeustParamter;

/**
 * <p>概述该类作用，请详细描述。</p>
 * <p>创建日期：2013-6-13 下午12:39:29</p>
 *
 * @author yihaijun
 */
public class AsipHttpRequestBodyToParamMap extends HttpRequestBodyToParamMap {
    public final static String PARTNAMEMIDAPPENDSYMBOL = "-ASIPPARTNAMEMIDSYMBOL-";

    public String inputStream2String(InputStream is, String outputEncoding) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int i = -1;
        int index = 0;
        try {
            while ((i = is.read()) != -1) {
                index++;
                baos.write(i);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("inputStream2String Exception(index = " + index + "):" + e.toString());
        }
        return baos.toString(outputEncoding);
    }

    private void addAsipReqeustParamter(AsipReqeustParamter asipReqeustParamter, String key, DataHandler dataHandler,
            String outputEncoding) throws Exception {
        logger.info("addAsipReqeustParamter(" + key + ") begin....");
        if (key.indexOf(PARTNAMEMIDAPPENDSYMBOL) > 0) {
            key = key.substring(key.indexOf(PARTNAMEMIDAPPENDSYMBOL) + PARTNAMEMIDAPPENDSYMBOL.length());
        }
        if (!(dataHandler.getDataSource() instanceof PartDataSource)) {
            logger.info("dataHandler.getDataSource().getClass().getName() = "
                    + dataHandler.getDataSource().getClass().getName());
            String value = inputStream2String(dataHandler.getInputStream(), outputEncoding);
            asipReqeustParamter.addParamter(key, value);
            return;
        }
        PartDataSource partDataSource = (PartDataSource) (dataHandler.getDataSource());
        if (!(partDataSource.getPart() instanceof MultiPart)) {
            logger.info("partDataSource.getPart().getClass().getName() = "
                    + partDataSource.getPart().getClass().getName());
            String value = inputStream2String(dataHandler.getInputStream(), outputEncoding);
            asipReqeustParamter.addParamter(key, value);
            return;
        }
        MultiPart multiPart = (MultiPart) (partDataSource.getPart());
        if (multiPart.getFile() != null) {
            logger.info("multiPart.getFile().getName() = " + multiPart.getFile().getPath());
            asipReqeustParamter.addMultiPart(key, multiPart);
        } else {
            String value = inputStream2String(dataHandler.getInputStream(), outputEncoding);
            asipReqeustParamter.addParamter(key, value);
        }
    }

    @Override
    public Object transformMessage(MuleMessage message, String outputEncoding) throws TransformerException {
        try {
            AsipReqeustParamter asipReqeustParamter = new AsipReqeustParamter();
            asipReqeustParamter.setPayload(message.getPayload());
            Set<String> attachmentNames = message.getInboundAttachmentNames();
            String payload = message.getPayloadAsString();

            String httpRequest = message.getInboundProperty("http.request", "");
            httpRequest = URLDecoder.decode(httpRequest);
            String httpRequestPath = message.getInboundProperty("http.request.path", "");
            if (httpRequestPath.length() < httpRequest.length() - 1) {
                String httpRequestUrlParam = httpRequest.substring(httpRequestPath.length() + 1);
                String[] httpRequestKV = httpRequestUrlParam.split("&");
                for (int i = 0; httpRequestKV != null && i < httpRequestKV.length; i++) {
                    String[] kv = httpRequestKV[i].split("=");
                    if (kv != null && kv.length == 2) {
                        asipReqeustParamter.addParamter(kv[0], kv[1]);
                    }
                }
            }
            if (attachmentNames != null && attachmentNames.size() > 0) {
                Iterator<String> nameIt = attachmentNames.iterator();
                while (nameIt.hasNext()) {
                    String name = nameIt.next();
                    DataHandler dataHandler = message.getInboundAttachment(name);

                    if (name.equals("payload") && (payload == null || payload.equals(""))) {
                        payload = inputStream2String(dataHandler.getInputStream(), outputEncoding);
                    } else {
                        addAsipReqeustParamter(asipReqeustParamter, name, dataHandler, outputEncoding);
                    }
                }
            } else {
                Map<String, Object> paramMap = (Map<String, Object>) super.transformMessage(message, outputEncoding);
                if (paramMap != null) {
                    java.util.Iterator<String> it = paramMap.keySet().iterator();
                    while (it.hasNext()) {
                        String keyName = it.next();
                        if (name.equals("payload") && (payload == null || payload.equals(""))) {
                            payload = paramMap.get(keyName).toString();
                        } else {
                            if (logger.isDebugEnabled()) {
                                logger.debug("paramMap.get(" + keyName + ").getClass().getName() = "
                                        + paramMap.get(keyName).getClass().getName());
                            }
                            asipReqeustParamter.addParamter(keyName, paramMap.get(keyName).toString());
                        }
                    }
                }
            }
            if (payload != null || !payload.equals("")) {
                asipReqeustParamter.addParamter("payload", payload);
            }
            return asipReqeustParamter;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.toString());
            throw new TransformerException(this, e);
        }
    }
}
