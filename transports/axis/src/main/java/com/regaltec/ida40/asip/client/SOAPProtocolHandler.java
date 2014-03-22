package com.regaltec.ida40.asip.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collection;
import java.util.Map;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;

import com.regaltec.asip.manager.api.client.AsipClientConfigItem;

/**
 * 
 * <p>接口平台客户端Soap协议实现</p>
 * <p>创建日期：2010-9-25 下午10:21:19</p>
 *
 * @author 封加华
 */
public class SOAPProtocolHandler extends AbstractProtocolHandler {
    private Logger logger = Logger.getLogger(this.getClass().getName());
    public static final String SOAP_NS = "http://schemas.xmlsoap.org/soap/envelope/";
    public static final String APP_NS = "http://service.asip.regaltec.com/";

    /**
     * <p>从请求上下文中创建目标地soap协议的url串,不含?wsdl</p>
     * @param asipConfig 请求上下文
     * @return url串
     */
    private String createURLString(AsipClientConfigItem asipClientConfig) {
        return "http://" + asipClientConfig.getIp() + ":" + asipClientConfig.getPort()
                + (asipClientConfig.getUri().startsWith("/") ? asipClientConfig.getUri() : "/" + asipClientConfig.getUri());
    }

    /**
     * <p>以http方式调用接口平台的soap服务,相比cxf的客户端存根调用方式和dynamicClient此方法效率要高</p>
     * <p>因为这两者都是在调用时解析wsdl才能明确消息结构，而http方式是在明确消息结构的情况下，直接发送消息包。不需要解析wsdl</p>
     * 
     * @param context 请求上下文
     * @return 响应对象
     */
    private ResponseMessage http(RequestContext context) {
        AsipClientConfigItem asipConfig = context.getAsipClientConfigItem();//当前配置为主配置
        if (isEmpty(asipConfig.getIp()) || isEmpty(asipConfig.getPort())
                || isEmpty(asipConfig.getUri())) {
            return AsipClient.buildExceptivelyResponseMessage("ASIP-0010", "客户端异常，接口平台访问参数没有正确配置", context);
        }
        HttpURLConnection con = null;
        URLConnection connection = null;
        OutputStream out = null;
        URL url = null;
        String strUrl = createURLString(asipConfig);
        try {
            url = new URL(strUrl);
        } catch(MalformedURLException e) {
            logger.error("访问地址"+strUrl+"不正确，请仔细核对。" );
            return AsipClient.buildExceptivelyResponseMessage("ASIP-0020", String.format("配置地址%s不正确", strUrl), context);
        }
        try {
            connection = url.openConnection();
        } catch (IOException e) {
            String faultString = String.format("无法创建到达%s的连接，请检查接口平台是否正常运行。", strUrl);
            logger.error(faultString, e);
            return AsipClient.buildExceptivelyResponseMessage("ASIP-0020", faultString, context);
        }
        try {
            /**
             * 构建soap消息
             */
            String soapMessage = buildSoapMessage(context);

            // if(logger.isDebugEnabled()){
            // logger.debug("调用接口平台的soap请求消息:\n"+soapMessage.toString());
            // }
            byte[] b = soapMessage.getBytes();

            con = (HttpURLConnection) connection;
            con.setRequestProperty("Content-Length", String.valueOf(b.length));
            con.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
            con.setRequestMethod("POST");
            // 设置选项
            con.setDoOutput(true);
            con.setDoInput(true);
            con.setConnectTimeout(asipConfig.getConnectionTimeout());
            con.setReadTimeout(asipConfig.getReceiveTimeout());

            // 发送soap消息
            out = con.getOutputStream();
            out.write(b);
            // 发送完毕
        } catch (ProtocolException e) {
            return AsipClient.buildExceptivelyResponseMessage("ASIP-0020", "接口平台连接失败，以下是异常堆栈信息：\n" + e.getMessage(),
                    context);
        } catch (IOException e) {
            return AsipClient.buildExceptivelyResponseMessage("ASIP-0020", "接口平台连接失败，以下是异常堆栈信息：\n" + e.getMessage(),
                    context);
        } catch (Exception e) {
            logger.error("发送失败:[" + e.toString() + "]context.getRequestMessage().toString()=["
                    + context.getRequestMessage().toString() + "]");
            return AsipClient.buildExceptivelyResponseMessage("ASIP-0030", "接口平台异常，具体错误原因不明确，以下是异常堆栈信息：\n"
                    + e.getMessage(), context);
        } finally {
            if (con != null) {
                try {
                    con.disconnect();
                } catch (Exception e) {
                    logger.error("关闭http连接时出现异常，该异常不影响综调，系统将忽略此异常：", e);
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    logger.error("关闭输出流时出现异常：", e);
                }
            }
        }
        BufferedReader in = null;
        InputStreamReader isr = null;
        StringBuffer result = new StringBuffer();
        try {
            // 读取soap响应
            if (200 == con.getResponseCode()) {
                isr = new InputStreamReader(con.getInputStream());
            } else {
                isr = new InputStreamReader(con.getErrorStream());
            }

            in = new BufferedReader(isr);
            String inputLine = null;
            while ((inputLine = in.readLine()) != null) {
                result.append(inputLine).append("\n");
            }

            return parseSoapResponse(result.toString(), context);
        } catch (ProtocolException e) {
            return AsipClient.buildExceptivelyResponseMessage("ASIP-0020", "接口平台连接失败，以下是异常堆栈信息：\n" + e.getMessage(),
                    context);
        } catch (IOException e) {
            return AsipClient.buildExceptivelyResponseMessage("ASIP-0020", "接口平台连接失败，以下是异常堆栈信息：\n" + e.getMessage(),
                    context);
        } catch (Exception e) {
            logger.error("接收失败:[" + e.toString() + "]context.getRequestMessage().toString()=["
                    + context.getRequestMessage().toString() + "]result=[" + result.toString() + "]");
            return AsipClient.buildExceptivelyResponseMessage("ASIP-0030", "接口平台异常，具体错误原因不明确，以下是异常堆栈信息：\n"
                    + e.getMessage(), context);
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } catch (Error e) {
                e.printStackTrace();
            }
            try {
                if(isr != null){
                    isr.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } catch (Error e) {
                e.printStackTrace();
            }
            try {
                if(con != null){
                    con.disconnect();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } catch (Error e) {
                e.printStackTrace();
            }
        }
    }

    private String buildSoapMessage(RequestContext context) {
        StringBuffer soapMessage = new StringBuffer();
        soapMessage.append("<soap:Envelope xmlns:soap=\"" + SOAP_NS + "\">");
        soapMessage.append("<soap:Body>");
        soapMessage.append("<call xmlns=\"" + APP_NS + "\">");
        soapMessage.append("<businessServiceName xmlns=\"\">").append(context.getBusinessModuleName()).append(
                "</businessServiceName>");
        soapMessage.append("<businessServiceParam xmlns=\"\">").append(
                StringEscapeUtils.escapeXml(context.getRequestMessage().toString())).append("</businessServiceParam>");
        soapMessage.append("<requestContextName xmlns=\"\">").append("").append("</requestContextName>");
        soapMessage.append("</call>");
        soapMessage.append("</soap:Body>");
        soapMessage.append("</soap:Envelope>");
        return soapMessage.toString();

    }

    /*private String buildSoapMessageOld(RequestContext context) {
        StringBuffer soapMessage = new StringBuffer();
        soapMessage.append("<soap:Envelope xmlns:soap=\"" + SOAP_NS + "\">");
        soapMessage.append("<soap:Body>");
        soapMessage.append("<ns2:call xmlns:ns2=\"" + APP_NS + "\">");
        soapMessage.append("<businessServiceName>").append(context.getBusinessModuleName()).append(
                "</businessServiceName>");
        soapMessage.append("<businessServiceParam>").append(
                StringEscapeUtils.escapeXml(context.getRequestMessage().toString())).append("</businessServiceParam>");
        soapMessage.append("<requestContextName>").append("").append("</requestContextName>");
        soapMessage.append("</ns2:call>");
        soapMessage.append("</soap:Body>");
        soapMessage.append("</soap:Envelope>");
        return soapMessage.toString();

    }*/

    /**
     * <p>cxf生成存根方式调用接口平台，实际上底层还是使用DynamicClientFactory，并且效率比不上上http post soap方式。</p>
     * <p>可恶的是返回的数据是以iso-8859-1解码，必须人工转换成UTF-8(接口平台全部是UTF-8)才不出现中文乱码</p>
     * <p>不仅如此，cxf跟jsf存在某种冲突，具体是什么原因就不得而知，cxf说不错不在它，所以该方法已经标记为过时。</p>
     * @param context 请求上下文
     * @return 响应消息
     * @throws Exception 
     */
    /**@Deprecated
    protected ResponseMessage stub(RequestContext context) throws Exception {
        String url = createURLString(context);
        if (url.indexOf("?wsdl") == -1) {
            url += "?wsdl";
        }
        // 没有地方可以设置超时，这种方式是无论如何都不可取的，可控性太弱了。
        AsipService_Service ss =
                new AsipService_Service(new URL(url), new QName("http://service.asip.regaltec.com/", "AsipService"));
        AsipService port = ss.getAsipService();
        String callResponse = port.call(context.getBusinessModuleName(), context.getRequestMessage().toString(), null);
        callResponse = new String(callResponse.getBytes("iso-8859-1"), "UTF-8");
        return ResponseMessage.newInstance(callResponse);
    }*/

    /*
     * <p>cxf's DynamicClientFactory调用接口平台，效率比不上上http post soap方式。因其每次必须在调用时下载再解析wsdl</p>
     * <p>可恶的是返回的数据是以iso-8859-1解码，必须人工转换成UTF-8(接口平台全部是UTF-8)才不出现中文乱码</p>
     * <p>不仅如此，cxf跟jsf存在某种冲突，具体是什么原因就不得而知，cxf说不错不在它，所以该方法已经标记为过时。</p>
     * @param context 请求上下文
     * @return 响应消息
     */
    /* @Deprecated
    protected ResponseMessage dynamicClient(RequestContext context) {
        String url = createURLString(context);
        String returnString = null;
        Client client = null;
        try {
            DynamicClientFactory factory = DynamicClientFactory.newInstance();
            if (url.indexOf("?wsdl") == -1) {
                url += "?wsdl";
            }
            client = factory.createClient(url);
        } catch (Exception e1) {
            // e1.printStackTrace();
            logger.error("dynamicClient Exception:createClient failed,Exception:" + e1.toString());
            return http(context);
        }
        try {
            HTTPConduit http = (HTTPConduit) client.getConduit();
            HTTPClientPolicy httpClientPolicy = new HTTPClientPolicy();
            httpClientPolicy.setConnectionTimeout(context.getConnectionTimeout());
            httpClientPolicy.setAllowChunking(false);
            httpClientPolicy.setReceiveTimeout(context.getReceiveTimeout());
            http.setClient(httpClientPolicy);
        } catch (Exception e1) {
            // e1.printStackTrace();
            logger.error("dynamicClient Exception:setClient failed,Exception:" + e1.toString());
        }
        Object[] res = null;
        try {
            res = client.invoke("call", new Object[] { context.getBusinessModuleName(),
                    context.getRequestMessage().toString(), null });
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (ObjectUtils.isNotEmpty(res)) {
            returnString = res[0].toString();
        }
        // System.err.println(returnString);
        try {
            return ResponseMessage.newInstance(returnString);
        } catch (AsipClientException e) {
            e.printStackTrace();
            logger.error("dynamicClient AsipClientException:returnString=[" + returnString + "]");
            return null;
        }
    }*/

    /**
     * soap协议跟接口平台对接
     * {@inheritDoc}
     */
    public ResponseMessage execute(RequestContext context) {
        return http(context);
        // return this.dynamicClient(context);
    }

    /**
     * 判断<b>对象</b>和<b>内容</b>是否为空，参数可以传任意类型。
     * @param obj 任意具有空特征的对象
     * @return 返回boolean标识
     */
    @SuppressWarnings("unchecked")
    public static boolean isEmpty(Object obj) {
        if (obj == null) {
            return true;
        } else if (obj instanceof String) {
            return ((String) obj).equals("");
        } else if (obj instanceof Collection) {
            return ((Collection) obj).isEmpty();
        } else if (obj instanceof Map) {
            return ((Map) obj).isEmpty();
        } else if (obj.getClass().isArray()) {
            return Array.getLength(obj) == 0;
        } else {
            return false;
        }
    }
    /**
     * 
     *  判断<b>对象</b>和<b>内容</b>是否不为空，参数可以传任意类型。
     * @param obj 任意具有空特征的对象
     * @return 返回boolean标识
     */
    public static boolean isNotEmpty(Object obj) {
        return !isEmpty(obj);
    }

    public String execute(String businessServiceName, String businessServiceParam, AsipClientConfigItem asipClientConfig) {
         return null;
    }
}
