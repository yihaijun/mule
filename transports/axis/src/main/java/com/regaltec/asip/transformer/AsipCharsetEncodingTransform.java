/*
 * <p>标题: 中国电信综合调度系统第4版</p>
 * <p>描述: ...</p>
 * <p>版权: Copyright (c) 2010</p>
 * <p>公司: 天讯瑞达通信技术有限公司</p>
 * <p>网址：http://www.tisson.cn/
 */
package com.regaltec.asip.transformer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.api.transformer.TransformerException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;

import org.mule.util.CharSetUtils;

/**
 * <p>字符集编码转换类。</p>
 * <p>创建日期：2010-12-14 上午10:06:25</p>
 *
 * @author yihaijun
 */
public class AsipCharsetEncodingTransform extends AsipTransformer {
    private String inputEncoding = "utf-8";
    private String outputEncoding = "utf-8";
    private Log logger = LogFactory.getLog(getClass());

    // private String[] charsetNames = { "US-ASCII", "ISO-8859-1", "UTF-8", "UTF-16BE", "UTF-16LE", "UTF-16"};

    /**
     * @return the inputEncoding
     */
    public synchronized String getInputEncoding() {
        return inputEncoding;
    }

    /**
     * @param inputEncoding the inputEncoding to set
     */
    public synchronized void setInputEncoding(String inputEncoding) {
        if (inputEncoding.equalsIgnoreCase("utf-8") || inputEncoding.equalsIgnoreCase("Unicode")
                || inputEncoding.equalsIgnoreCase("gb2312") || inputEncoding.equalsIgnoreCase("gbk")) {
            this.inputEncoding = inputEncoding;
        }
    }

    /**
     * @return the outputEncoding
     */
    public synchronized String getOutputEncoding() {
        return outputEncoding;
    }

    /**
     * @param outputEncoding the outputEncoding to set
     */
    public synchronized void setOutputEncoding(String outputEncoding) {
        if (outputEncoding.equalsIgnoreCase("utf-8") || outputEncoding.equalsIgnoreCase("Unicode")
                || outputEncoding.equalsIgnoreCase("gb2312") || outputEncoding.equalsIgnoreCase("gbk")) {
            this.outputEncoding = outputEncoding;
        }
    }

    @Override
    public Object doTransform(Object srcData, String encoding) throws TransformerException {
        if (logger.isDebugEnabled()) {
            logger.debug("doTransform() begin...");
            logger.debug("defaultCharsetName=" + CharSetUtils.defaultCharsetName() + ",inputEncoding=" + inputEncoding
                    + ",outputEncoding=" + outputEncoding);
            logger.debug("srcData=" + srcData.toString());
        }
        if (inputEncoding.equalsIgnoreCase(outputEncoding)) {
            if (logger.isDebugEnabled()) {
                logger.debug("doTransform() return [" + srcData + "]");
            }
            return srcData; // 还需斟酌
        }

        if (inputEncoding.equalsIgnoreCase("utf-8")) {

        } else if (inputEncoding.equalsIgnoreCase("gb2312") || inputEncoding.equalsIgnoreCase("gbk")) {

        } else {

        }

        try {
            CharsetDecoder cdInput = Charset.forName(inputEncoding.toUpperCase()).newDecoder();
            ByteBuffer bbInput = null;
            if (srcData instanceof byte[]) {
                bbInput = ByteBuffer.wrap((byte[]) srcData);
            } else {
                bbInput = ByteBuffer.wrap(srcData.toString().getBytes());
            }
            CharBuffer cbInput = cdInput.decode(bbInput);

            if (outputEncoding.equalsIgnoreCase("utf-8")) {

            } else if (outputEncoding.equalsIgnoreCase("gb2312") || outputEncoding.equalsIgnoreCase("gbk")) {

            } else {

            }

            CharsetEncoder ceOutput = Charset.forName(outputEncoding.toUpperCase()).newEncoder();
            ByteBuffer bbOutput = ceOutput.encode(cbInput);

            CharsetDecoder cdOutput = Charset.forName(outputEncoding.toUpperCase()).newDecoder();
            CharBuffer cbOutput = cdOutput.decode(bbOutput);
            return cbOutput.toString();
        } catch (CharacterCodingException e) {
//            e.printStackTrace();
        }

        if (logger.isDebugEnabled()) {
            logger.debug("doTransform() return [" + srcData + "]");
        }
        return srcData; // 还需斟酌
    }
}
