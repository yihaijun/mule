package com.regaltec.asip.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * <p>属性文件映射</p>
 * <p>创建日期：2010-10-20 下午03:42:17</p>
 *
 * @author 封加华
 */
public class PropertiesMapping {
    protected Logger log = LoggerFactory.getLogger(this.getClass().getName());
    private static String muleHome = "";
    
    /**
     * 缓存
     */
    private static Map<String,Properties> PROPERTIES_CACHE = new HashMap<String,Properties>();
    private Properties properties;

    static {
        muleHome = System.getProperty("mule.home");
        if(muleHome==null || "".equals(muleHome)){
            muleHome = System.getenv("MULE_HOME");//取系统环境变量
        }

    }
    
    /**
     * 
     * 构造函数
     * @param filePath 属性文件相对于MULE_HOME的路径
     */
    public PropertiesMapping() {
    }
    /**
     * 
     * 构造函数
     * @param filePath 属性文件相对于MULE_HOME的路径
     */
    public PropertiesMapping(String filePath) {
        if(filePath==null){
            return;
        }
        filePath= filePath.replace("apps/predeal/inf.properties","asipconf/predeal/inf.properties" );
        filePath= filePath.replace("apps/sa/inf.properties","asipconf/sa/inf.properties" );
        filePath= filePath.replace("apps/open/inf.properties","asipconf/open/inf.properties" );
        if(log.isDebugEnabled()){
            log.debug(filePath);
        }
        properties = PROPERTIES_CACHE.get(filePath);
        //"/asipconf/predeal/app.properties"
        if(properties == null){
            loadPropertiesFile(filePath);
        }
    }

    public String loadPropertiesFile(String filePath) {
        FileInputStream fi = null;
        try {
            //取虚拟机系统属性，因为mule启动时会设置mule.home系统属性
            String basePath = muleHome;
            File f = new File(basePath,filePath);
            properties = new Properties(System.getProperties());
            
            fi = new FileInputStream(f);
            properties.load(fi);
            if(PROPERTIES_CACHE.size() > 100){
                log.warn("PROPERTIES_CACHE.size()="+PROPERTIES_CACHE.size()+">100");
            }
            PROPERTIES_CACHE.put(filePath, properties);
            return properties.toString();
        } catch (FileNotFoundException e) {
//            e.printStackTrace();
            return e.toString();
        } catch (IOException e) {
//            e.printStackTrace();
            return e.toString();
        }finally{
            if(fi != null){
                try {
                    fi.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
   }
    /**
     * 
     * <p>获取属性</p>
     * @param key The Key
     * @return value 
     */
    public String getProperty(String key) {
        if("".equals(key)||null==key){
            return "";
        }
        //properties.list(System.out);
        String value = properties.getProperty(key.trim(), "");
        //System.out.println("key="+key+",value="+value);
        return value;
    }
    
    /**
     * 
     * <p>获取属性，如果没有属性key，则返回默认值</p>
     * @param key key
     * @param defaultValue 默认值
     * @return 属性值
     */
    public String getProperty(String key,String defaultValue) {
        return properties.getProperty(key.trim(), defaultValue);
    }

    /**
     * @return the properties
     */
    public Properties getProperties() {
        return properties;
    }
}
