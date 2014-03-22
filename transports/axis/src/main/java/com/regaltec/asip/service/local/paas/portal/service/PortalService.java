package com.regaltec.asip.service.local.paas.portal.service;

import java.security.MessageDigest;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.mule.transport.http.multipart.MultiPartInputStream.MultiPart;

import com.regaltec.asip.common.AsipLog4j;
import com.regaltec.asip.manager.api.client.AsipReqeustParamter;
import com.regaltec.asip.service.local.paas.portal.constants.Constants;
import com.regaltec.asip.service.local.paas.portal.form.Header;
import com.regaltec.asip.service.local.paas.portal.form.Paramter;
import com.regaltec.asip.service.local.paas.portal.form.RetrunValue;
import com.regaltec.ida40.asip.client.AsipClient;

public class PortalService
{
   private AsipLog4j log = new AsipLog4j(this.getClass().getName());

   private AsipClient asipClient;

   /**
    * @return the asipClient
    */
   public AsipClient getAsipClient()
   {
      return asipClient;
   }

   /**
    * @param asipClient the asipClient to set
    */
   public void setAsipClient(AsipClient asipClient)
   {
      this.asipClient = asipClient;
   }

   public String gateway(AsipReqeustParamter reqeustParamter)
   {
      if (log.isDebugEnabled())
      {
         log.debug("gateway[" + reqeustParamter.toText() + "] begin ...");
      }
      Paramter paramter = null;
      try
      {
         paramter = this.getParamter(reqeustParamter);
      }
      catch (Exception e)
      {
         e.printStackTrace();
         log.error(e.getMessage());
         return new RetrunValue("unknown", false, this.getMessage("", Constants.ERROR_MESSAGE_10003))
               .returnString(true);
      }
      RetrunValue retrunValue = this.invoke(paramter);
      String returnString = retrunValue.returnString(paramter.isReturnJSON());
      return returnString;
   }

   private Paramter getParamter(AsipReqeustParamter reqeustParamter)
   {
      String sign = reqeustParamter.getParamter("sign");
      String headJson = reqeustParamter.getParamter("headJson");
      String dataJson = reqeustParamter.getParamter("dataJson");
      List<MultiPart> multiPartList = reqeustParamter.getMultiPartList();
      Paramter paramter = new Paramter(sign, headJson, dataJson);
      paramter.setMultiPartList(multiPartList);
      return paramter;
   }

   private RetrunValue invoke(Paramter paramter)
   {
      try
      {
         // 检测数据完整性
         if (!paramter.isValid())
         {
            return new RetrunValue(paramter.getServiceName(), false, this.getMessage("", Constants.ERROR_MESSAGE_10004));
         }
         Header header = paramter.getHeader();
         // 检测head
         if (!checkHeader(header))
         {
            return new RetrunValue(paramter.getServiceName(), false, this.getMessage("", Constants.ERROR_MESSAGE_10006));
         }
         //检测签名 
         if (!checkSign(paramter.getSign(), header))
         {
            return new RetrunValue(paramter.getServiceName(), false, this.getMessage("", Constants.ERROR_MESSAGE_10005));
         }
         // 调用ASIP服务
         String asipReturnXML = this.callAsip(header, paramter.getAsipXML());
         return new RetrunValue(paramter.getServiceName(), asipReturnXML);
      }
      catch (Exception e)
      {
         e.printStackTrace();
         log.error(e.getMessage());
         return new RetrunValue(paramter.getServiceName(), false, this.getMessage("", Constants.ERROR_MESSAGE_10001));
      }
   }

   private String getMessage(String localtion, String messageCode)
   {
      com.regaltec.asip.utils.PropertiesMapping conf = new com.regaltec.asip.utils.PropertiesMapping(
            "asipconf/kangaroo/message_zh_CN.properties");
      return conf.getProperty(messageCode, messageCode);
   }

   private boolean checkSign(String sign, Header header)
   {
      try
      {
         com.regaltec.asip.utils.PropertiesMapping conf = new com.regaltec.asip.utils.PropertiesMapping(
               "asipconf/kangaroo/appSecret.properties");
         String appSecret = conf.getProperty(header.getAppId(), "");
         if (StringUtils.isEmpty(appSecret))
            return true;
         return sign.equals(this.string2MD5(header.getAppId() + header.getTime() + appSecret));
      }
      catch (Exception e)
      {
         e.printStackTrace();
         log.error(e.getMessage());
         return false;
      }
   }

   private boolean checkHeader(Header header)
   {
      if (header.getAppId() == null)
         return false;
      if (header.getOrgId() == null)
         return false;
      if (header.getService_name() == null)
         return false;
      if (header.getTime() == null)
         return false;
      if (header.getUserName() == null)
         return false;
      return true;
   }

   private String string2MD5(String json)
   {
      MessageDigest md5 = null;
      try
      {
         md5 = MessageDigest.getInstance("MD5");
      }
      catch (Exception e)
      {
         e.printStackTrace();
         return "";
      }
      char[] charArray = json.toCharArray();
      byte[] byteArray = new byte[charArray.length];

      for (int i = 0; i < charArray.length; i++)
         byteArray[i] = (byte) charArray[i];
      byte[] md5Bytes = md5.digest(byteArray);
      StringBuffer hexValue = new StringBuffer();
      for (int i = 0; i < md5Bytes.length; i++)
      {
         int val = ((int) md5Bytes[i]) & 0xff;
         if (val < 16)
            hexValue.append("0");
         hexValue.append(Integer.toHexString(val));
      }
      return hexValue.toString();
   }

   private String callAsip(Header header, String xmljson)
   {
       if(header.getAppId().equalsIgnoreCase("sfiptestportal")){
           return xmljson;
       }
      return asipClient.call(header.getAppId(), xmljson, "");
   }
}
