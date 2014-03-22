/*
 * <p>标题: 中国电信综合调度系统第4版</p>
 * <p>描述: ...</p>
 * <p>版权: Copyright (c) 2011</p>
 * <p>公司: 天讯瑞达通信技术有限公司</p>
 * <p>网址：http://www.tisson.cn/
 */
package com.regaltec.asip.manager.api.util;

import java.io.IOException;
import org.apache.log4j.Logger;

/**
 * <p>字节处理类。</p>
 * <p>创建日期：2011-2-28 下午02:50:35</p>
 *
 * @author yihaijun
 */
public class ByteUtil {
    /**
     * 
     * <p>从一个字节数组中找一个字节数组。</p>
     * @param src
     * @param dest
     * @return 索引位置
     */
    private static int find(byte[] src, byte[] dest,int begin) {
        int srclen = src.length;
        int destlen = dest.length;
        int pos = 0;
        int index = begin;
        boolean findDest= false;
        int j = 0;
        while (index + destlen < srclen + 1) {
            j = index;
            findDest= true;
            while(j< index+destlen && j < srclen ){
                if(src[j] != dest[j-index] ){
                    findDest= false;
                    break;
                }
                j++;
            }
            if(findDest){
                return index;
            }
            index++;
        }
        return -1;
    }

    /**
     * 
     * <p>org.apache.commons.httpclient.ContentLengthInputStream读操作。</p>
     * @param src
     * @param len
     * @param out
     * @return 字节数组
     */
    public static byte[] readContentLengthInputStream(org.apache.commons.httpclient.ContentLengthInputStream src,int len,int timeout){
        byte []byteArry= new byte[len];
//        for(int i =0;i<len;i++){
//            byteArry[i]=0x38;
//        }
        int index = 0;
        try {
            long beginTime = System.currentTimeMillis();
            while(index < len && System.currentTimeMillis() - beginTime<timeout){
                byte []byteArry2= new byte[len-index];
//                for(int i =0;i<len -index;i++){
//                    byteArry[i]=0x38;
//                }
                int readLen = src.read(byteArry2);
                for(int i=0;i<readLen && index < len;i++){
                    byteArry[index]=byteArry2[i];
                    index ++;
                }
            }
            if (index < len) {
                Logger printlog = Logger.getLogger("com.regaltec.asip.script.PrintMessage.axis");
                printlog.error("read param failed:because " + index + "<" + len);
            }
            ((org.apache.commons.httpclient.ContentLengthInputStream)src).close();
        } catch (Exception e) {
//            e.printStackTrace();
            Logger printlog = Logger.getLogger("com.regaltec.asip.script.PrintMessage.axis");
            printlog.error("read param failed:because " + e.toString());
        }
        return byteArry;
    }

    /**
     * 
     * <p>从一个字节数组中删除一段。</p>
     * @param bytesContent 原字节数组
     * @param beginString  段开始字符串
     * @param endString    段结尾字符串
     * @return 新字节数组
     */
    public  static byte[] delteContentFromBuffer(byte[] bytesContent, String beginString, String endString) {
//        org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(ByteUtil.class.getName());
//        logger.error("bytesContent="+new String(bytesContent));
//        logger.error("beginString,endString:"+beginString+","+ endString);
        int begin = find(bytesContent, beginString.getBytes(),0);
        int end = find(bytesContent, endString.getBytes(),begin+1);
        if (begin < 0 || end < 0 || (begin + beginString.length() )> end) {
            return bytesContent;
        }

        int len = bytesContent.length;
        byte[] newByteArry = new byte[len - (end + endString.getBytes().length - begin)];
        for(int i=0;i<newByteArry.length;i++){
            newByteArry[i]=0x38;
        }
        int pos = 0;
        for (int i = 0; i < len && pos < newByteArry.length; i++ ) {
            if(i==begin){
                i=end + endString.getBytes().length;
            }
            newByteArry[pos] = bytesContent[i];
            pos++;
        }
//        logger.debug("begin="+begin+",end="+end+",len="+len+",newByteArry.length="+newByteArry.length+",pos="+pos);
//        logger.error("newByteArry="+new String(newByteArry));
        return newByteArry;
    }


    /**
     * 
     * <p>从一个字节数组中替换一段。</p>
     * @param bytesContent 原字节数组
     * @param beginString  原字符串
     * @param endString    目标字符串
     * @return 新字节数组
     */
    public  static byte[] replaceContentFromBuffer(byte[] bytesContent, String src, String dest) {
//        org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(ByteUtil.class.getName());
//        logger.error("bytesContent="+new String(bytesContent));
//        logger.error("beginString,endString:"+beginString+","+ endString);
        int begin = find(bytesContent, src.getBytes(),0);
        if (begin < 0) {
            return bytesContent;
        }

        int len = bytesContent.length;
        byte[] newByteArry = new byte[len - src.getBytes().length + dest.getBytes().length];
        for(int i=0;i<newByteArry.length;i++){
            newByteArry[i]=0x38;
        }
        int pos = 0;
        byte[] destByteArry=dest.getBytes(); 
        for (int i = 0; i < len && pos < newByteArry.length; i++ ) {
            if(i==begin){
                int j = 0;
                for(;j<destByteArry.length;j++){
                    newByteArry[pos] = destByteArry[j];
                    pos++;
                }
                for(;j<src.getBytes().length;j++){
                    pos++;
                }
                i+=src.getBytes().length-1;
                continue;
            }
            newByteArry[pos] = bytesContent[i];
            pos++;
        }
//        logger.debug("begin="+begin+",end="+end+",len="+len+",newByteArry.length="+newByteArry.length+",pos="+pos);
//        logger.error("newByteArry="+new String(newByteArry));
        return newByteArry;
    }

    /**
     * 
     * <p>从一个字节数组中替换两段。</p>
     * @param bytesContent 原字节数组
     * @param beginString  原字符串
     * @param endString    目标字符串
     * @return 新字节数组
     */
    public  static byte[] replaceDoubleContentFromBuffer(byte[] bytesContent, String src, String dest) {
//        org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(ByteUtil.class.getName());
//        logger.error("bytesContent="+new String(bytesContent));
//        logger.error("beginString,endString:"+beginString+","+ endString);
        int begin = find(bytesContent, src.getBytes(),0);
        int end = find(bytesContent, src.getBytes(),begin+1);
        if (begin < 0 || end < 0 || (begin + src.length() )> end) {
            return bytesContent;
        }

        int len = bytesContent.length;
        byte[] newByteArry = new byte[len - 2*(src.getBytes().length - dest.getBytes().length)];
        for(int i=0;i<newByteArry.length;i++){
            newByteArry[i]=0x38;
        }
        int pos = 0;
        byte[] destByteArry=dest.getBytes(); 
        for (int i = 0; i < len && pos < newByteArry.length; i++ ) {
            if(i==begin||i==end){
                int j = 0;
                for(;j<destByteArry.length;j++){
                    newByteArry[pos] = destByteArry[j];
                    pos++;
                }
                for(;j<src.getBytes().length;j++){
                    pos++;
                }
                i+=src.getBytes().length-1;
                continue;
            }
            newByteArry[pos] = bytesContent[i];
            pos++;
        }
//        logger.debug("begin="+begin+",end="+end+",len="+len+",newByteArry.length="+newByteArry.length+",pos="+pos);
//        logger.error("newByteArry="+new String(newByteArry));
        return newByteArry;
    }
}
