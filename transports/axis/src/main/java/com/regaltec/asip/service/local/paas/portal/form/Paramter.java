package com.regaltec.asip.service.local.paas.portal.form;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

import net.sf.json.JSONObject;
import nu.xom.Element;

import org.apache.commons.lang.StringUtils;
import org.mule.transport.http.multipart.MultiPartInputStream.MultiPart;

import sun.misc.BASE64Encoder;

import com.regaltec.asip.common.AsipLog4j;
import com.regaltec.asip.service.local.paas.portal.utils.XMLConverter;

public class Paramter implements Serializable
{
//   private AsipLog4j log = new AsipLog4j(this.getClass().getName());
   private static final long serialVersionUID = 1L;
   private String sign;
   private List<MultiPart> multiPartList;
   private JSONObject head;
   private JSONObject data;

   public Paramter()
   {
   }

   public Paramter(String sign, String xml)
   {
      this.setSign(sign);
      this.setXml(xml);
   }

   public List<MultiPart> getMultiPartList()
   {
      return multiPartList;
   }

   public void setMultiPartList(List<MultiPart> multiPartList)
   {
      this.multiPartList = multiPartList;
   }

   public Paramter(String sign, String headJson, String dataJson)
   {
      this.setSign(sign);
      if (!StringUtils.isEmpty(headJson))
         this.setHeadJson(headJson);
      if (!StringUtils.isEmpty(dataJson))
         this.setDataJson(dataJson);
      else
         this.data = new JSONObject();
   }

   public void setXml(String xml)
   {
      if (!StringUtils.isEmpty(xml))
      {
         JSONObject jo = XMLConverter.xml2Json(xml);
         this.head = jo.getJSONObject("head");
         this.data = jo.getJSONObject("data");
      }
   }

   public void setHeadJson(String headJson)
   {
      if (!StringUtils.isEmpty(headJson))
      {
         this.head = JSONObject.fromObject(headJson);
      }
   }

   public void setDataJson(String dataJson)
   {
      if (!StringUtils.isEmpty(dataJson))
      {
         this.data = JSONObject.fromObject(dataJson);
      }
   }

   /**
    * 验证是否包含 head和data数据域
    * @return
    */
   public boolean isValid()
   {
      if (this.head != null && this.data != null)
         return true;
      else
         return false;
   }

   public Header getHeader()
   {
      Header header = new Header();
      header.setAppId(this.head.optString("appid"));
      header.setFormat(this.head.optString("format", "json"));
      header.setOrgId(this.head.optString("orgid"));
      header.setService_name(this.head.optString("service_name"));
      header.setTime(this.head.optString("time"));
      header.setUserName(this.head.optString("username"));
      return header;
   }

   public String getSign()
   {
      return this.sign;
   }

   public void setSign(String sign)
   {
      this.sign = sign;
   }

   public String getServiceName()
   {
      if (this.head != null && this.head.containsKey("service_name"))
         return this.head.getString("service_name");
      else
         return "";
   }

   public boolean isReturnJSON()
   {
      if (this.head != null)
         return "json".equals(this.head.optString("format", "json").toLowerCase());
      else
         return true;
   }

   public String getAsipXML()
   {
      JSONObject head_info = new JSONObject();
      head_info.put("sender", this.head.optString("username"));
      head_info.put("service_name", this.head.optString("service_name"));
      head_info.put("receiver", "ASIP");
      head_info.put("msg_type", "request");
      head_info.put("time", org.mule.util.DateUtils.formatTimeStamp(new Date(), "yyyy-MM-dd HH:mm:ss"));
      head_info.put("simulate_flag", false);
      JSONObject jo = new JSONObject();
      jo.put("head", head_info);
      jo.put("data_info", this.data);
      Element root = XMLConverter.getXMLRootElement("service", jo);
      if (this.multiPartList != null && this.multiPartList.size() > 0)
      {
         Element fileList = new Element("fileList");
         BASE64Encoder encoder = new BASE64Encoder();
         for (MultiPart multiPart : this.multiPartList)
         {
            if (multiPart != null && multiPart.getSize() > 0)
            {
               try
               {
                  Element formName = new Element("formName");
                  formName.appendChild(multiPart.getName());
                  Element fileName = new Element("fileName");
                  fileName.appendChild(multiPart.getContentDispositionFilename());
                  Element size = new Element("size");
                  size.appendChild(multiPart.getSize() + "");
                  Element content = new Element("content");
                  content.appendChild(encoder.encode(multiPart2byte(multiPart)));
                  Element file = new Element("file");
                  file.appendChild(formName);
                  file.appendChild(fileName);
                  file.appendChild(size);
                  file.appendChild(content);
                  fileList.appendChild(file);
               }
               catch (Exception e)
               {
                  e.printStackTrace();
               }
            }
         }
         root.getChildElements("data_info").get(0).appendChild(fileList);
      }
      return XMLConverter.getXMLString(root);
   }

   private byte[] multiPart2byte(MultiPart multiPart) throws IOException
   {
      ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
      byte[] buff = new byte[100];
      int rc = 0;
      InputStream inStream = multiPart.getInputStream();
      while ((rc = inStream.read(buff, 0, 100)) > 0)
      {
         swapStream.write(buff, 0, rc);
      }
      byte[] in2b = swapStream.toByteArray();
      return in2b;
   }
}
