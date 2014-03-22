package com.regaltec.asip.transport.http;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.ByteArrayRequestEntity;
import org.apache.commons.httpclient.methods.FileRequestEntity;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.cxf.common.util.StringUtils;

/**
 * 
 * <p>Asip 公共Http组件</p>
 * <p>创建日期：2011-5-11 下午05:19:09</p>
 *
 * @author 封加华
 */
public class HttpComponent {
    //private static Logger logger = LoggerFactory.getLogger(HttpComponent.class);
    /**
     *  请求地址
     */
    private String url;

    /**
     * 请求方法
     */
    private String method = "POST";

    /**
     * 连接超时
     */
    private int connectionTimeout = 3000;

    /**
     * 读取超时
     */
    private int readTimeout = 30 * 1000;

    /**
     * 内容类型
     */
    private String contentType = "text/xml";

    /**
     * 字符集
     */
    private String charset = "UTF-8";

    /**
     * 请求头
     */
    private Map<String, String> requestHeader;
    /**
     * 请求参数
     */
    private Map<String, String> parameters;

    /**
     * 
     * <p>业务方法</p>
     * @param data 要发送的数据
     * @return 返回请求结果
     * @throws Exception 异常
     */
    public Object call(String data) throws Exception {
        RequestEntity requestEntity = new StringRequestEntity(data, contentType, charset);
        return send(requestEntity);
    }

    /**
     * 
     * <p>业务方法</p>
     * @param data 要发送的数据
     * @return 返回请求结果
     * @throws Exception 异常
     */
    public Object call(byte[] data) throws Exception {
        RequestEntity requestEntity = new ByteArrayRequestEntity(data, contentType);
        return send(requestEntity);
    }

    /**
     * 
     * <p>业务方法</p>
     * @param data 要发送的数据
     * @return 返回请求结果
     * @throws Exception 异常
     */
    public Object call(File data) throws Exception {
        RequestEntity requestEntity = new FileRequestEntity(data, contentType);
        return send(requestEntity);
    }

    /**
     * 
     * <p>业务方法</p>
     * @param data 要发送的数据
     * @return 返回请求结果
     * @throws Exception 异常
     */
    public Object call(InputStream data) throws Exception {
        RequestEntity requestEntity = new InputStreamRequestEntity(data, contentType);
        return send(requestEntity);
    }

    /**
     * 
     * <p>发送请求</p>
     * @param requestEntity 请求实体
     * @return 返回http 请求收到的原始内容
     * @throws HttpException http异常
     * @throws IOException IO异常
     */
    private String send(RequestEntity requestEntity) throws HttpException, IOException {
        if (StringUtils.isEmpty(url)) {
            throw new IllegalArgumentException("The url cannot be empty");
        }
        HttpClient httpClient = new HttpClient();
        PostMethod postMethod = null;
        String result = null;

        try {
            httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(connectionTimeout);
            httpClient.getHttpConnectionManager().getParams().setSoTimeout(readTimeout);

            if ("POST".equalsIgnoreCase(this.getMethod())) {
                postMethod = new PostMethod(url);

                postMethod.setRequestEntity(requestEntity);
                // 添加自定义http头
                if (MapUtils.isNotEmpty(this.requestHeader)) {
                    Set<Map.Entry<String, String>> entrySet = this.requestHeader.entrySet();
                    for (Map.Entry<String, String> entry : entrySet) {
                        postMethod.addRequestHeader(entry.getKey(), entry.getValue());
                    }
                }
                // 添加参数
                if (MapUtils.isNotEmpty(this.parameters)) {
                    Set<Map.Entry<String, String>> entrySet = this.parameters.entrySet();
                    for (Map.Entry<String, String> entry : entrySet) {
                        postMethod.addRequestHeader(entry.getKey(), entry.getValue());
                    }
                }

                httpClient.executeMethod(postMethod);
                result = postMethod.getResponseBodyAsString();
            } else if ("GET".equalsIgnoreCase(this.getMethod())) {
                throw new UnsupportedOperationException("method=get not unsupport");
            } else {
                throw new IllegalArgumentException("method invalid parameter");
            }
        } finally {
            if ("POST".equalsIgnoreCase(this.getMethod()) && postMethod != null) {
                postMethod.releaseConnection();
            }
        }
        return result;
    }

    /**
     * @return the url
     */
    public String getUrl() {
        return url;
    }

    /**
     * @param url the url to set
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * @return the connectionTimeout
     */
    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    /**
     * @param connectionTimeout the connectionTimeout to set
     */
    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    /**
     * @return the readTimeout
     */
    public int getReadTimeout() {
        return readTimeout;
    }

    /**
     * @param readTimeout the readTimeout to set
     */
    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }

    /**
     * @return the contentType
     */
    public String getContentType() {
        return contentType;
    }

    /**
     * @param contentType the contentType to set
     */
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    /**
     * @return the charset
     */
    public String getCharset() {
        return charset;
    }

    /**
     * @param charset the charset to set
     */
    public void setCharset(String charset) {
        this.charset = charset;
    }

    /**
     * @return the method
     */
    public String getMethod() {
        return method;
    }

    /**
     * @param method the method to set
     */
    public void setMethod(String method) {
        this.method = method;
    }

    /**
     * @return the requestHeader
     */
    public Map<String, String> getRequestHeader() {
        return requestHeader;
    }

    /**
     * @param requestHeader the requestHeader to set
     */
    public void setRequestHeader(Map<String, String> requestHeader) {
        this.requestHeader = requestHeader;
    }

    /**
     * @return the parameters
     */
    public Map<String, String> getParameters() {
        return parameters;
    }

    /**
     * @param parameters the parameters to set
     */
    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }
}
