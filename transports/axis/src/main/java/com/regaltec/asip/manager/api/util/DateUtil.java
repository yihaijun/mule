package com.regaltec.asip.manager.api.util;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.commons.lang.StringUtils;
/**
 * <p>日期工具类</p>
 * <p>2011-2-28 9:41:30</p>
 * @author 戈亮锋
 */
public class DateUtil {
    public static final String  DEFAULT_DATE_FORMATE = "yyyy-MM-dd HH:mm:ss";
    
    /**
     * <p>返回当前时间</p>
     */
    public static String now(){
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
//        return toStrFromDate(new Date());
    }
    
    /**
     * long型日期转化为字符串
     * @param longDate Long
     * @return String 
     */
    public static String toStrFromLongDate(Long longDate) {
        return toStrFromDate(new Date(longDate));
    }
    
    public static String toStrFromDate(Date date) {
            return toStrFromDate(date, null);
    }
    /**
     * Date转化为字符串
     * @param date
     * @param pattern
     * @return
     */
    public static String toStrFromDate(Date date, String pattern) {
        if (null == date) {
            return null;
        }
        if(StringUtils.isBlank(pattern)||StringUtils.isEmpty(pattern)){
        	pattern = DEFAULT_DATE_FORMATE;
        }
        String tmp = new SimpleDateFormat(pattern).format(date);
		return tmp;
    }


    public static Date toDateFromStr(String dateStr){
            return toDateFromStr(dateStr,null);
    }
    
   /**
    * 字符串转化为Date
    * @param dateStr
    * @param pattern
    * @return
    */
    public static Date toDateFromStr(String dateStr,String pattern){
    	if(StringUtils.isBlank(dateStr)){
    		return null;
    	}
        if(StringUtils.isBlank(pattern) || StringUtils.isEmpty(pattern)){
        	pattern = DEFAULT_DATE_FORMATE;
        }
        try {
            return org.mule.util.DateUtils.getDateFromString(dateStr,pattern);
        } catch (Exception e) {
            return new Date();
        }
    }
    public static void main(String[] args) {
		System.out.println("now="+now());
	}
}