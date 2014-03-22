package com.regaltec.asip.manager.api.util;
import java.text.DecimalFormat;
import org.apache.commons.lang.StringUtils;
/**
 * <p>String操作工具类</p>
 * <p>创建日期：2010-11-10 下午06:19:17</p>
 * @author 戈亮锋
 */
public class StringUtil {

    /**
     * 功能描述：按格式将Dobule转换为字符串<br/>
     * @param    source ： 是待转换的字符串<br/>
     *           pattern:  模式字符串。如"0.00"表示输出2位小数;"0.0"表示输出1位小数;其它依此类推。  
     * @return 
     */             
    public static  String toStringFromDouble(double source,String pattern) {
         if (StringUtils.isBlank(pattern) || StringUtils.isEmpty(pattern)) {
              pattern = "0.0";   //设置默认为输出一位小数点
          }
          DecimalFormat df = new DecimalFormat();
          df.applyPattern(pattern);
          return String.valueOf(df.format(source));
    }
   
     public static  String toStringFromDouble(double source) {
         return toStringFromDouble(source,null);
     }
     
}
