package com.regaltec.asip.manager.api.util;
/**
 * <p>Object工具类</p>
 * <p>创建日期：2011-3-4 17:08:53</p>
 * @author 戈亮锋
 */
public class ObjectUtil {

	/**
	 * 对象是否为数组
	 * @param obj
	 * @return boolean 
	 */
	public static boolean isArray(Object obj){
		if(obj == null ){
			return false;
		}
		if(obj.getClass().isArray()){
			return true;
		}
		return false;
	}
}
