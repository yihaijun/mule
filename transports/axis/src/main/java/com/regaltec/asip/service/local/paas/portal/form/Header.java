package com.regaltec.asip.service.local.paas.portal.form;

public class Header
{
   private String time;
   private String userName;
   private String appId;
   private String format;
   private String orgId;
   private String appSecret;
   private String service_name;//配合ASIP一定要写成service_name

   public String getTime()
   {
      return time;
   }

   public void setTime(String time)
   {
      this.time = time;
   }

   public String getUserName()
   {
      return userName;
   }

   public void setUserName(String userName)
   {
      this.userName = userName;
   }

   public String getAppId()
   {
      return appId;
   }

   public void setAppId(String appId)
   {
      this.appId = appId;
   }

   public String getFormat()
   {
      return format;
   }

   public void setFormat(String format)
   {
      this.format = format;
   }

   public String getOrgId()
   {
      return orgId;
   }

   public void setOrgId(String orgId)
   {
      this.orgId = orgId;
   }

   public String getAppSecret()
   {
      return appSecret;
   }

   public void setAppSecret(String appSecret)
   {
      this.appSecret = appSecret;
   }

   public String getService_name()
   {
      return service_name;
   }

   public void setService_name(String service_name)
   {
      this.service_name = service_name;
   }

   public String toSingString()
   {
      // APP_ID+ACCESSS_TOKEN+TIME_STAMP+APP_SECRET
      return this.getAppId() + this.getTime() + this.getAppSecret();
   }
}
