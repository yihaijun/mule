/*
 * <p>标题: 中国电信综合调度系统第4版</p>
 * <p>描述: ...</p>
 * <p>版权: Copyright (c) 2010</p>
 * <p>公司: 天讯瑞达通信技术有限公司</p>
 * <p>网址：http://www.tisson.cn/
 */
package com.regaltec.asip.manager.api.util;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.filefilter.RegexFileFilter;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * <p>ASIP文件操作工具类</p>
 * <p>创建日期：2010-11-10 下午06:19:17</p>
 * @author 戈亮锋
 */
public class AsipFileUtils {
    @SuppressWarnings("unused")
    private static Logger log = LoggerFactory.getLogger(AsipFileUtils.class.getName());

    /**
     * <p>列举某个目录下所有文件</p>
     * @param  filePath         String
     * @param  fileNamePattern  String   RegexFileFilter(如："\\w*-input.xml{1}$"表示-input.xml结束的文件)
     * @param  chosePattern     int 	 0:文件夹 和文件; 1:仅含文件夹    2：仅含文件
     * @return File[]
     */
    public static File[] enumerationFiles(String filePath,String fileNamePattern,int chosePattern){
        java.io.FilenameFilter fileNameFilter = null;
        if (StringUtils.isNotBlank(fileNamePattern)) {
            fileNameFilter = new RegexFileFilter(fileNamePattern);
        }
        int pattern = chosePattern;
        if (chosePattern != 0 && chosePattern != 1 && chosePattern != 2) {
            pattern = 0;
        }
        File fileSet = new File(filePath);
        File[] files = null;
        if (fileSet.exists() && fileSet.isDirectory()) {
               List<File> fileList = new ArrayList<File>();
               files = fileSet.listFiles(fileNameFilter);
               for (int i = 0; i < files.length; i++) {
                   File v = files[i];
                   if(pattern == 1) {
                       if (v.isDirectory()) {
                           fileList.add(v);
                       }
                   }else if(pattern == 2){
                       if (v.isFile()) {
                           fileList.add(v);
                       }                            
                   }else{
                       fileList.add(v);
                   }
               }               
               files = fileList.toArray(new File[0]);  
        }
        return files;
    }

    /**
     * <p>列举某个目录下的所有文件名称</p>
     * @param  filePath         String
     * @param  fileNamePattern  String    RegexFileFilter (如："\\w*-input.xml{1}$"表示-input.xml结束的文件)
     * @param  chosePattern     int 	  0:文件夹 和文件; 1:仅含文件夹    2：仅含文件
     * @return String[] 
     */
    public static String[] enumerationFileNames(String filePath,String fileNamePattern,int chosePattern){
        java.io.FilenameFilter fileNameFilter = null;
        if (StringUtils.isNotBlank(fileNamePattern)) {
            fileNameFilter = new RegexFileFilter(fileNamePattern);
        }  
        int pattern = chosePattern;
        if (chosePattern != 0 && chosePattern != 1 && chosePattern != 2) {
            pattern = 0;
        }
        File fileSet = new File(filePath);
        String[] fileNames = null;
        if (fileSet.exists() && fileSet.isDirectory()) {
            File[] files = fileSet.listFiles(fileNameFilter);
            if(files != null && files.length != 0){
                    List<String> strList = new ArrayList<String>();
                    for (int i = 0; i < files.length; i++) {
                        File v = files[i];
                        if(pattern == 1) {
                            if (v.isDirectory()) {
                                strList.add(v.getName());
                            }
                        }else if(pattern == 2){
                            if (v.isFile()) {
                                strList.add(v.getName());
                            }                            
                        }else{
                            strList.add(v.getName());
                        }
                    }
                    fileNames = strList.toArray(new String[0]);
            }
        }
        return fileNames;
    }

public static String readFiletoString(File file){
	if (file == null || !file.exists() || file.isDirectory()) {
		return "";
	}
        StringBuffer sb = new StringBuffer();
        BufferedReader reader = null;
        try {   
        	 reader = new BufferedReader(new FileReader(file));   
             String tempString = null;   
             while ((tempString = reader.readLine()) != null){   
                  sb.append(tempString);
             }   
         reader.close();   
        } catch (IOException e) {   
//            e.printStackTrace();   
        } finally {   
         if (reader != null){   
              try {   
                  reader.close();   
              } catch (IOException e1) {   
              }   
         }   
       }   
        return sb.toString();
}    

/**
 * 
 * @param fileName  含文件全路径
 * @return
 */
public static String readFiletoString(String fileName){
	return readFiletoString(new File(fileName));
}   
    

/*
 * 读取文件大小
 */
public static long getFileSizes(String fileName) {
    FileInputStream fis = null;
    try {
        File f = new File(fileName);
        long s = 0;
        if (f.exists()) {
            fis = new FileInputStream(f);
            s = fis.available();
        } else {
//            System.out.println("文件不存在");
        }
        return s;
    } catch (Exception e) {

    }finally{
        if (fis != null){
            try {
                fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    return 0;
}

/**
     * <p>功能的简单描述，参数、返回值及异常必须注明。</p>
     * @param args
     */
    public static void main(String[] args) throws Exception {
        String fileSeprator = System.getProperty("file.separator");
        String jarDir = System.getProperty("user.dir");
        jarDir = StringUtils.replace(jarDir, "lib"+fileSeprator+"user","asiptestresources");
        System.out.print("执行路径： "+jarDir+"\r\n");
        enumerationFileNames(jarDir,null,1);
    }

}
