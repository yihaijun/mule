package com.regaltec.asip.service.local.paas.portal.form;

import java.io.Serializable;
import java.util.List;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.regaltec.asip.service.local.paas.portal.constants.Constants;
import com.regaltec.asip.service.local.paas.portal.utils.XMLConverter;

public class RetrunValue implements Serializable
{
   private static final long serialVersionUID = 1L;
   private String asipReturnXML;
   private String serviceName;
   private Boolean sucess = true;
   private String msg;

   private Element dataElement;

   public RetrunValue(String serviceName, Boolean sucess, String msg)
   {
      this.msg = msg;
      this.sucess = sucess;
      this.serviceName = serviceName;
   }

   public RetrunValue(String serviceName, String asipReturnXML)
   {
      this(serviceName, true, "");
      this.asipReturnXML = asipReturnXML;
   }

   public String returnString(boolean toJSON)
   {
      if (toJSON)
         return this.toJSON();
      else
         return this.toXML();
   }

   @SuppressWarnings("unchecked")
   private String toXML()
   {
      this.parseAsipXML();

      Document doc = DocumentHelper.createDocument();
      Element root_Element = doc.addElement("business-output");
      Element head_Element = root_Element.addElement("head");
      head_Element.addElement("servicename").setText(this.serviceName);
      head_Element.addElement("recvdata").setText(this.sucess.toString());
      head_Element.addElement("msg").setText(this.msg);
      if (this.sucess)
      {
         Element data_Element = root_Element.addElement("data");
         if (dataElement.isTextOnly())
            data_Element.add(DocumentHelper.createCDATA(dataElement.getText()));
         else
         {
            for (Element subElement : (List<Element>) dataElement.elements())
            {
               subElement.detach();
               data_Element.add(subElement);
            }
         }
      }
      return doc.asXML();
   }

   private String toJSON()
   {
      JSONObject jo = new JSONObject();
      try
      {
         this.parseAsipXML();
         jo.put("recvdata", this.sucess);
         jo.put("msg", this.msg);
         jo.put("servicename", this.serviceName);
         if (this.sucess)
         {
            if (dataElement.isTextOnly())
               jo.put("data", XMLConverter.xml2Json(dataElement.getText()));
            else
               jo.put("data", XMLConverter.xml2Json(dataElement.asXML()));
         }
      }
      catch (Exception e)
      {
         e.printStackTrace();
         jo.put("recvdata", false);
         jo.put("msg", Constants.ERROR_MESSAGE_10003);
      }
      return jo.toString();
   }

   /**
    * 解析asip返回的xml
    */
   private void parseAsipXML()
   {
      try
      {
         if (this.sucess && !StringUtils.isEmpty(this.asipReturnXML))
         {
            Document doc = DocumentHelper.parseText(asipReturnXML);
            String error_code = doc.getRootElement().element("head").element("error_code").getText();
            if ("ASIP-0000".equals(error_code))
            {
               this.sucess = true;
               this.msg = "";
            }
            else
            {
               this.sucess = false;
               this.msg = doc.getRootElement().element("head").element("error_info").getText();
            }
            if (this.sucess)
            {
               dataElement = doc.getRootElement().element("data_info");
            }
         }
         else
            this.sucess = false;
      }
      catch (Exception e)
      {
         e.printStackTrace();
         this.sucess = false;
         this.msg = Constants.ERROR_MESSAGE_10003;
      }
   }
}
