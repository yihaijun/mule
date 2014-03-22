/*
 * <p>标题: 中国电信综合调度系统第4版</p>
 * <p>描述: ...</p>
 * <p>版权: Copyright (c) 2010</p>
 * <p>公司: 天讯瑞达通信技术有限公司</p>
 * <p>网址：http://www.tisson.cn/
 */
package com.regaltec.asip.routing.filter;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.mule.api.MuleMessage;
import org.mule.api.routing.filter.Filter;
import org.mule.util.FileUtils;
import org.mule.util.PropertiesUtils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <p>文件列表过滤器</p>
 * <p>创建日期：2010-10-19 下午02:15:40</p>
 *
 * @author yihaijun
 */
public class FileListFilter implements Filter {
    private Log logger = LogFactory.getLog(getClass());

    private String fileNameRegex = "";
    private String flagFileName = "";
    private String validTime = "";

    private Pattern pattern = null;
    private long lastTimeInMillis = 0;
    private long validTimeValue = 0;

    /**
     * @return the validTime
     */
    public String getValidTime() {
        return validTime;
    }

    /**
     * @param validTime the validTime to set
     */
    public void setValidTime(String validTime) {
        if (logger.isDebugEnabled()) {
            logger.debug("setValidTime(" + validTime + ") begin...");
        }
        this.validTime = validTime;
        validTimeValue = Integer.parseInt(validTime);
    }

    /**
         * @return the lastTimeInMillis
         */
    public long getLastTimeInMillis() {
        return lastTimeInMillis;
    }

    /**
     * @param lastTimeInMillis the lastTimeInMillis to set
     */
    public void setLastTimeInMillis(long lastTimeInMillis) {
        if (logger.isDebugEnabled()) {
            logger.debug("setLastTimeInMillis(" + lastTimeInMillis + ") begin...");
        }
        this.lastTimeInMillis = lastTimeInMillis;
        Properties prop = null;
        OutputStream out = null;
        try {
            prop = PropertiesUtils.loadProperties(new URL(flagFileName));
            prop.setProperty("lastTime", "" + lastTimeInMillis);
            (new File(flagFileName)).delete();
            File f = FileUtils.newFile(flagFileName);
            out = new BufferedOutputStream(new FileOutputStream(f));
            prop.store(out, null);
            out.close();
        } catch (MalformedURLException e) {
        } catch (IOException e) {
        }finally{
            if(out != null){
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        
        if (logger.isDebugEnabled()) {
            logger.debug("setLastTimeInMillis(" + lastTimeInMillis + ") return.");
        }
    }

    /**
         * @return the flagFileName
         */
    public String getFlagFileName() {
        return flagFileName;
    }

    /**
     * @param flagFileName the flagFileName to set
     */
    public void setFlagFileName(String flagFileName) {
        if (logger.isDebugEnabled()) {
            logger.debug("setFlagFileName(" + flagFileName + ") begin.");
        }
        this.flagFileName = flagFileName;
        Properties prop = null;
        try {
            prop = PropertiesUtils.loadProperties(new URL(flagFileName));
            lastTimeInMillis = Integer.parseInt(prop.getProperty("lastTime"));
        } catch (MalformedURLException e) {
        } catch (IOException e) {
        }
        if (logger.isDebugEnabled()) {
            logger.debug("setFlagFileName(" + flagFileName + ") return.");
        }
    }

    /**
         * @return the fileNameRegex
         */
    public String getFileNameRegex() {
        return fileNameRegex;
    }

    /**
     * @param fileNameRegex the fileNameRegex to set
     */
    public void setFileNameRegex(String fileNameRegex) {
        if (fileNameRegex == null || fileNameRegex.equals("")) {
            return;
        }
        if (logger.isDebugEnabled()) {
            logger.debug("setFileNameRegex(" + fileNameRegex + ") begin.");
        }
        this.fileNameRegex = fileNameRegex;
        pattern = Pattern.compile(fileNameRegex);
    }

    /*
     * 不使用此方法
     */
    public boolean accept(MuleMessage arg0) {
        return false;
    }

    /*
     * 
     * 
     * /** <p>功能的简单描述，参数、返回值及异常必须注明。</p>
     * 
     * @param fileName： 文件名匹配正则表达式
     * 
     * @param timestamp: 时间戳
     * 
     * @return
     */
    public boolean accept(String fileName, java.util.Calendar timestamp) {
        if (logger.isDebugEnabled()) {
            logger.debug("accept(" + fileName + ") begin...");
        }

        if (fileName != null && !fileName.equals("") && fileNameRegex != null && !fileNameRegex.equals("")) {
            return accept(fileName);
        }
        if (timestamp == null) {
            if (logger.isDebugEnabled()) {
                logger.debug("accept(" + fileName + ",null ) return true");
            }
            return true;
        }
        long filetime = timestamp.getTimeInMillis();
        if (logger.isDebugEnabled()) {
            logger.debug("filetime = " + filetime + ",currentTimeMillis " + System.currentTimeMillis());
        }

        if (filetime < lastTimeInMillis) {
            if (logger.isDebugEnabled()) {
                logger.debug("accept(" + fileName + ") return false,lastTimeInMillis=" + lastTimeInMillis);
            }
            return false;
        }
        if (validTimeValue > 0 && System.currentTimeMillis() - (8 * 3600 * 1000) - filetime > validTimeValue) {
            if (logger.isDebugEnabled()) {
                logger.debug("accept(" + fileName + ") return false,validTimeValue=" + validTimeValue);
            }
            return false;
        }
        if (logger.isDebugEnabled()) {
            logger.debug("accept(" + fileName + ") return true");
        }
        return true;
    }

    private boolean accept(String fileName) {
        Matcher matcher = pattern.matcher(fileNameRegex);
        if (matcher.find()) {
            if (logger.isDebugEnabled()) {
                logger.debug("accept(" + fileName + ") return true.");
            }
            return true;
        }
        if (logger.isDebugEnabled()) {
            logger.debug("accept(" + fileName + ")  return false.");
        }
        return false;
    }
}
