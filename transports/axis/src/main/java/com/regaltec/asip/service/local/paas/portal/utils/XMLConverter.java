package com.regaltec.asip.service.local.paas.portal.utils;

import net.sf.json.JSON;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import nu.xom.Element;

public class XMLConverter
{
   static XMLSerializer xmlSerializer = new XMLSerializer();
   static
   {
      xmlSerializer.setTypeHintsEnabled(false);
      xmlSerializer.setForceTopLevelObject(false);
      xmlSerializer.setSkipWhitespace(false);
      xmlSerializer.setSkipNamespaces(true);
   }

   public static Element getXMLRootElement(String rootName, JSON json)
   {
      xmlSerializer.setRootName(rootName);
      return xmlSerializer.getXMLRootElement(json);
   }

   public static String getXMLString(Element root)
   {
      return xmlSerializer.writeXML(root, null);
   }

   public static String json2Xml(String rootName, String jsonStr)
   {
      JSON json = JSONSerializer.toJSON(jsonStr);
      return json2Xml(rootName, json);
   }

   public static String json2Xml(String rootName, JSON json)
   {
      xmlSerializer.setRootName(rootName);
      return xmlSerializer.write(json);
   }

   public static JSONObject xml2Json(String xml)
   {
      JSONObject jo = null;
      try
      {
         jo = (JSONObject) xmlSerializer.read(xml);
      }
      catch (Exception e)
      {
         try
         {
            jo = JSONObject.fromObject(xml);
         }
         catch (Exception e1)
         {
            jo = new JSONObject();
            jo.put("data", xml);
         }
      }
      return jo;
   }
}
