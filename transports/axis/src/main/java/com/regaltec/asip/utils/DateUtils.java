package com.regaltec.asip.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 
 * <p>日期工具类</p>
 * <p>创建日期：2010-10-19 下午01:01:17</p>
 *
 * @author 封加华
 */
public class DateUtils {
    /**
     * 
     * <p>将字符日期从指定格式转换到另一种字符日期</p>
     * @param inDateStr 需要转换日期串
     * @param inPattern 需要转换的日期串的格式
     * @param outPattern 转换后输出的格式
     * @return 转成字符日期成outPattern模式的字符日期
     */
    public static String convert(String inDateStr, String inPattern, String outPattern) {
        if (inDateStr == null || inPattern == null || outPattern == null) {
            return null;
        }
        try {
            Date tmp = new SimpleDateFormat(inPattern).parse(inDateStr);
            return new SimpleDateFormat(outPattern).format(tmp);
        } catch (ParseException e) {
            return inDateStr;
        }
    }

    /**
     * 
     * <p>获取指定样式的当前系统时间</p>
     * @param outPattern 日期样式
     * @return 当前系统时间
     */
    public static String now(String outPattern) {
        return new SimpleDateFormat(outPattern).format(new Date());
    }

    /**
     * 
     * <p>获取当前系统时间</p>
     * @return 当前系统时间，默认格式为：yyyy-MM-dd HH:mm:ss
     */
    public static String now() {
        return now("yyyy-MM-dd HH:mm:ss");
    }
    
    /**
	 * 
	 * <p>
	 * 格式化日期
	 * </p>
	 * 
	 * @param d
	 *            日期
	 * @param outPattern
	 *            格式化样式
	 * @return 格式化后的日期字符
	 */
	public static String format(Date d, String outPattern) {
		return new SimpleDateFormat(outPattern).format(d);
	}
	
	/**
	 *将指定格式的字符日期转成java.util.Date
	 *@param dateStr 字符日期
	 *@param inPattern 输入格式
	 *@return 返回java.util.Date 
	 */
	public static Date parse(String dateStr,String inPattern)throws ParseException{
		return new SimpleDateFormat(inPattern).parse(dateStr);
	}
	
	/**
	 * 在指定格式的字符日期上增加分钟，并且以指定格式返回
	 * @param inDateStr 字符日期
	 * @param inPattern 字符日期格式
	 * @param outPattern 返回字符日期的格式
	 * @param minute 追加的分钟数
	 * @return 返回增加分钟之后的outPattern格式字符日期
	 * @throws ParseException 解析日期异常
	 */
	@SuppressWarnings("deprecation")
	public static String addMinute(String inDateStr, String inPattern,
			String outPattern, int minute) throws ParseException {
		Date d = new SimpleDateFormat(inPattern).parse(inDateStr);
		d.setMinutes(d.getMinutes() + minute);
		return format(d,outPattern);
	}
	
	public static String addMinute(String inDateStr, String inPattern,
			String outPattern, String minute) throws ParseException,NumberFormatException {
		Date d = new SimpleDateFormat(inPattern).parse(inDateStr);
		d.setMinutes(d.getMinutes() + Integer.valueOf(minute));
		return format(d,outPattern);
	}
    
    /**
     * 
     * <p>获取字符串表达的时间</p>
     * @param date  表达时间的字符串
     * @return 字符串表达的时间
     */
    public static Date getDateFromString(String date){
        try {
            return org.mule.util.DateUtils.getDateFromString(date, "yyyy-MM-dd HH:mm:ss");
        } catch (Exception e) {
            return new Date();
        }
    }
    

    /**
     * 
     * <p>格式化表达时间的字符串</p>
     * @param date  表达时间的字符串
     * @return 表达时间的字符串
     */
    public static String formatDateString(String date){
                Date tmp = getDateFromString(date);
        try {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(tmp);
        } catch (Exception e) {
            return now();
        }
    }
}
