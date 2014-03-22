/*
 * <p>标题: 中国电信综合调度系统第4版</p>
 * <p>描述: ...</p>
 * <p>版权: Copyright (c) 2013</p>
 * <p>公司: 天讯瑞达通信技术有限公司</p>
 * <p>网址：http://www.tisson.cn/</p>
 */
package com.regaltec.asip.transformer;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.httpclient.Header;
import org.mule.api.MuleMessage;
import org.mule.api.transformer.TransformerException;
import org.mule.transport.http.multipart.MultiPartInputStream;

import com.regaltec.asip.service.AsipServiceParam;

/**
 * <p>概述该类作用，请详细描述。</p>
 * <p>创建日期：2013-5-24 上午09:05:42</p>
 *
 * @author yihaijun
 */
public class AppMessageToHttpRequest extends AsipTransformer {

    /* ******** P U B L I C F I E L D S ******** */

    /** No options specified. Value is zero. */
    public final static int NO_OPTIONS = 0;

    /** Specify encoding. */
    public final static int ENCODE = 1;

    /** Specify decoding. */
    public final static int DECODE = 0;

    /** Specify that data should be gzip-compressed. */
    public final static int GZIP = 2;

    /** Don't break lines when encoding (violates strict RtsBase64 specification) */
    public final static int DONT_BREAK_LINES = 8;

    /* ******** P R I V A T E F I E L D S ******** */

    /** Maximum line length (76) of RtsBase64 output. */
    private final static int MAX_LINE_LENGTH = 76;

    /** The equals sign (=) as a byte. */
    private final static byte EQUALS_SIGN = (byte) '=';

    /** The new line character (\n) as a byte. */
    private final static byte NEW_LINE = (byte) '\n';

    /** Preferred encoding. */
    private final static String PREFERRED_ENCODING = "UTF-8";

    /** The 64 valid RtsBase64 values. */
    private final static byte[] ALPHABET;

    private final static byte[] _NATIVE_ALPHABET = /*
                                                                                                     * May be something funny
                                                                                                     * like EBCDIC
                                                                                                     */
    { (byte) 'A', (byte) 'B', (byte) 'C', (byte) 'D', (byte) 'E', (byte) 'F', (byte) 'G', (byte) 'H', (byte) 'I', (byte) 'J', (byte) 'K', (byte) 'L', (byte) 'M', (byte) 'N', (byte) 'O', (byte) 'P', (byte) 'Q', (byte) 'R', (byte) 'S', (byte) 'T', (byte) 'U', (byte) 'V', (byte) 'W', (byte) 'X', (byte) 'Y', (byte) 'Z', (byte) 'a', (byte) 'b', (byte) 'c', (byte) 'd', (byte) 'e', (byte) 'f', (byte) 'g', (byte) 'h', (byte) 'i', (byte) 'j', (byte) 'k', (byte) 'l', (byte) 'm', (byte) 'n', (byte) 'o', (byte) 'p', (byte) 'q', (byte) 'r', (byte) 's', (byte) 't', (byte) 'u', (byte) 'v', (byte) 'w', (byte) 'x', (byte) 'y', (byte) 'z', (byte) '0', (byte) '1', (byte) '2', (byte) '3', (byte) '4', (byte) '5', (byte) '6', (byte) '7', (byte) '8', (byte) '9', (byte) '+', (byte) '/' };

    /** Determine which ALPHABET to use. */
    static {
            byte[] __bytes;
            try {
                    __bytes = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".getBytes(PREFERRED_ENCODING);
            } // end try
            catch (java.io.UnsupportedEncodingException use) {
                    __bytes = _NATIVE_ALPHABET; // Fall back to native encoding
            } // end catch
            ALPHABET = __bytes;
    } // end static

    /**
     * Translates a RtsBase64 value to either its 6-bit reconstruction value or
     * a negative number indicating some other meaning.
     */
    private final static byte[] DECODABET = { -9, -9, -9, -9, -9, -9, -9, -9, -9, // Decimal
                                                                                                                                                                    // 0 -
                                                                                                                                                                    // 8
                    -5, -5, // Whitespace: Tab and Linefeed
                    -9, -9, // Decimal 11 - 12
                    -5, // Whitespace: Carriage Return
                    -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, // Decimal 14 -
                                                                                                                            // 26
                    -9, -9, -9, -9, -9, // Decimal 27 - 31
                    -5, // Whitespace: Space
                    -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, // Decimal 33 - 42
                    62, // Plus sign at decimal 43
                    -9, -9, -9, // Decimal 44 - 46
                    63, // Slash at decimal 47
                    52, 53, 54, 55, 56, 57, 58, 59, 60, 61, // Numbers zero through nine
                    -9, -9, -9, // Decimal 58 - 60
                    -1, // Equals sign at decimal 61
                    -9, -9, -9, // Decimal 62 - 64
                    0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, // Letters 'A'
                                                                                                                    // through 'N'
                    14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, // Letters 'O'
                                                                                                                    // through 'Z'
                    -9, -9, -9, -9, -9, -9, // Decimal 91 - 96
                    26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, // Letters 'a'
                                                                                                                            // through 'm'
                    39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, // Letters 'n'
                                                                                                                            // through 'z'
                    -9, -9, -9, -9 // Decimal 123 - 126
    /*
     * ,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9, // Decimal 127 - 139
     * -9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9, // Decimal 140 - 152
     * -9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9, // Decimal 153 - 165
     * -9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9, // Decimal 166 - 178
     * -9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9, // Decimal 179 - 191
     * -9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9, // Decimal 192 - 204
     * -9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9, // Decimal 205 - 217
     * -9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9, // Decimal 218 - 230
     * -9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9, // Decimal 231 - 243
     * -9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9 // Decimal 244 - 255
     */
    };

    // I think I end up not using the BAD_ENCODING indicator.
    // private final static byte BAD_ENCODING = -9; // Indicates error in
    // encoding
    private final static byte WHITE_SPACE_ENC = -5; // Indicates white space in
                                                                                                    // encoding

    private final static byte EQUALS_SIGN_ENC = -1; // Indicates equals sign in
                                                                                                    // encoding

    public final static String MULTIPART_BOUNDARY = "asipBOUNDARYbkTYZNF_jY-9nzpN_UtLz3lxGR";
    public static Header[] HEADERS = new Header[] { new Header("Content-Type", "multipart/form-data; boundary="
            + MULTIPART_BOUNDARY) };
    Header[] HEADERS1 =
            new Header[] { new Header("Content-Type", "multipart/form-data; boundary=" + MULTIPART_BOUNDARY) };

    @Override
    public Object transform(Object src, String enc) throws TransformerException
    {
        MuleMessage msg = null;
        if (!(src instanceof MuleMessage)){
            logger.info("!(src instanceof MuleMessage)");
            return src;
        }
        msg = (MuleMessage)src;
        if (!(msg.getPayload() instanceof AsipServiceParam)) {
            logger.info("!(msg.getPayload() instanceof AsipServiceParam)");
            return msg;
        }
        AsipServiceParam param = (AsipServiceParam) msg.getPayload();
        org.w3c.dom.Node fileList = null;
        try {
            fileList = org.mule.module.xml.util.XMLUtils.selectOne("/service/data_info/fileList", param.getParamNode());
        } catch (XPathExpressionException e1) {
            e1.printStackTrace();
        }
        if (fileList == null) {
            if(logger.isDebugEnabled()){
                logger.debug("fileList == null");
            }
            return msg;
        }
        msg.setOutboundProperty("Content-Type","multipart/form-data; boundary="+MULTIPART_BOUNDARY);
        org.w3c.dom.NodeList fileNodeList = fileList.getChildNodes();
        int index = fileNodeList.getLength();
        for (; index > 0; index--) {
            org.w3c.dom.Node file = fileNodeList.item(index - 1);
            try {
                String formName = org.mule.module.xml.util.XMLUtils.selectValue("formName", file);
                String fileName = org.mule.module.xml.util.XMLUtils.selectValue("fileName", file);
                String content = org.mule.module.xml.util.XMLUtils.selectValue("content", file);
                if(formName == null || formName.equals("")){
                    formName = "null";
                }
                if(fileName == null || fileName.equals("")){
                    fileName = "null";
                }
                if(content == null || content.equals("")){
                    content = "null";
                }
                byte[] contentBytes = decode(content);
    
                ByteDataSource byteDataSource = new ByteDataSource(contentBytes,formName);
                DataHandler dataHandler = new DataHandler(byteDataSource);
                msg.addOutboundAttachment(fileName,dataHandler);
            } catch (XPathExpressionException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return msg;
    }

    
    public class ByteDataSource implements DataSource
    {
        protected byte[] content;
        protected String contentType = "text/plain";
        protected String name = "StringDataSource";

        public ByteDataSource(byte[] payload)
        {
            super();
            content = payload;
        }

        public ByteDataSource(byte[] payload, String name)
        {
            super();
            content = payload;
            this.name = name;
        }

        public ByteDataSource(byte[] content, String name, String contentType)
        {
            this.content = content;
            this.contentType = contentType;
            this.name = name;
        }

        public InputStream getInputStream() throws IOException
        {
            return new ByteArrayInputStream(content);
        }

        public OutputStream getOutputStream()
        {
            throw new UnsupportedOperationException("Read-only javax.activation.DataSource");
        }

        public String getContentType()
        {
            return contentType;
        }

        public String getName()
        {
            return name;
        }
    }

    //
        // <fileList><file><formName>file</formName><fileName>ListVOResponse.java</fileName><size>1705</size><content>
        //
        // HttpRequest request = null;
        // String filename =
        // com.regaltec.asip.manager.api.util.SystemUtil.getEnvValueByName("MULE_HOME")
        // + "/apps/ida30/classes/com/regaltec/asip/ida40/ida30/jt/province_predeal/http/crmCustInfoQry-2-request.ftl";
        // FileDataSource ds = new FileDataSource(new java.io.File(filename).getAbsoluteFile());
        // DataHandler dataHandler = new DataHandler(ds);
        //
        // try {
        //
        //
        // RequestLine requestLine = RequestLine.parseLine("POST /portal/multigateway.p?sign=66666666666666 HTTP/1.1");
        // // InputStream stream = new ByteArrayInputStream(MULTIPART_MESSAGE.getBytes());
        // java.io.InputStream stream = dataHandler.getInputStream();
        // // String MULTIPART_BOUNDARY = "------------------------------2eab2c5d5c7e";
        // // Header[] headers = new Header[]{new Header("Content-Type", "multipart/form-data; boundary=" +
        // // MULTIPART_BOUNDARY.substring(2))};
        // Header[] headers = com.regaltec.asip.transformer.AppMessageToHttpRequest.HEADERS;
        // HttpRequest httpRequest = new HttpRequest(requestLine, headers, stream, "utf-8");
        // msg.setPayload(httpRequest.getBody());
        // } catch (HttpException e) {
        // e.printStackTrace();
        // } catch (IOException e) {
        // e.printStackTrace();
        // }
        //
        // return request;

        // <script:transformer>
//         <script:script engine="groovy">
//         <![CDATA[
//         /*
//         private static final String MULTIPART_MESSAGE = "--" + MULTIPART_BOUNDARY + "\r\n"
//         + "Content-Disposition: form-data; name=\"payload\"; filename=\"payload\"\r\n"
//         + "Content-Type: application/octet-stream\r\n\r\n" +
//         "part payload\r\n\r\n" +
//         "--" + MULTIPART_BOUNDARY + "\r\n"
//         + "Content-Disposition: form-data; name=\"two\"; filename=\"two\"\r\n"
//         + "Content-Type: application/octet-stream\r\n\r\n" + "part two\r\n\r\n" +
//         "--" + MULTIPART_BOUNDARY + "--\r\n\r\n";
//         */
//         import javax.activation.DataHandler;
//         import javax.activation.FileDataSource;
//         import org.apache.commons.httpclient.Header;
//         import org.mule.transport.http.RequestLine;
//         import org.mule.transport.http.HttpRequest;
//        
//         org.mule.api.MuleMessage msg = new org.mule.DefaultMuleMessage(null, message.getMuleContext());
//         //String filename =
//         com.regaltec.asip.manager.api.util.SystemUtil.getEnvValueByName("MULE_HOME")+"/apps/ida30/classes/com/regaltec/asip/ida40/ida30/jt/province_predeal/http/crmCustInfoQry-2-request.ftl";
//         String filename = "D:/temp/test1.txt";
//         System.out.println("filename="+filename);
//         String MULTIPART_BOUNDARY = "asipenm1MTgTbkTYZNF_jY-9nzpN_UtLz3lxGR";
//        
//         FileDataSource ds = new FileDataSource(new java.io.File(filename).getAbsoluteFile());
//         msg.setOutboundProperty("Content-Type","multipart/form-data; boundary="+MULTIPART_BOUNDARY);
//         DataHandler dataHandler = new DataHandler(ds);
//         msg.addOutboundAttachment("testattachments",dataHandler );
//         System.out.println("dataHandler.getContentType()="+ dataHandler.getContentType());
//         System.out.println("dataHandler.getName()="+ dataHandler.getName());
//        
//         filename = "D:/temp/test2.txt";
//         FileDataSource ds2 = new FileDataSource(new java.io.File(filename).getAbsoluteFile());
//         DataHandler dataHandler2 = new DataHandler(ds2);
//         msg.addOutboundAttachment("testattachments2",dataHandler2 );
//         System.out.println("dataHandler2.getContentType()="+ dataHandler2.getContentType());
//         System.out.println("dataHandler2.getName()="+ dataHandler2.getName());
//        
//         RequestLine requestLine = RequestLine.parseLine("POST /portal/multigateway.p?sign=66666666666666 HTTP/1.1");
//         //InputStream stream = new ByteArrayInputStream(MULTIPART_MESSAGE.getBytes());
//         java.io.InputStream stream = dataHandler.getInputStream();
//         //Header[] headers = new Header[]{new Header("Content-Type", "multipart/form-data; boundary=" +
//         MULTIPART_BOUNDARY.substring(2))};
//         //Header[] headers = new Header[]{new Header("Content-Type", "multipart/form-data; boundary=" +
//         MULTIPART_BOUNDARY)};
//         Header[] headers = com.regaltec.asip.transformer.AppMessageToHttpRequest.HEADERS;
//         HttpRequest httpRequest = new HttpRequest(requestLine, headers, stream, "utf-8");
//         System.out.println("httpRequest.getContentType() = " +httpRequest.getContentType());
//         System.out.println("httpRequest.getBodyString() = " +httpRequest.getBodyString());
//         msg.setPayload(httpRequest.getBody());
//         return msg;
//         ]]>
//         </script:script>
//         </script:transformer>

//    public static byte[] decode(String source) {// 对字节数组字符串进行Base64解码并生成文件
//        if (source == null || "".equals(source)) // 图像数据为空
//            return "".getBytes();
//        BASE64Decoder decoder = new BASE64Decoder();
//        try {
//            // Base64解码
//            byte[] b = decoder.decodeBuffer(source);
//            for (int i = 0; i < b.length; ++i) {
//                if (b[i] < 0) {// 调整异常数据
//                    b[i] += 256;
//                }
//            }
//            return b;
//        } catch (Exception e) {
//            return source.getBytes();
//        }
//    }

    
    /**
     * Decodes four bytes from array <var>source</var> and writes the resulting
     * bytes (up to three of them) to <var>destination</var>. The source and
     * destination arrays can be manipulated anywhere along their length by
     * specifying <var>srcOffset</var> and <var>destOffset</var>. This method
     * does not check to make sure your arrays are large enough to accomodate
     * <var>srcOffset</var> + 4 for the <var>source</var> array or
     * <var>destOffset</var> + 3 for the <var>destination</var> array. This
     * method returns the actual number of bytes that were converted from the
     * RtsBase64 encoding.
     * 
     * 
     * @param source
     *            the array to convert
     * @param srcOffset
     *            the index where conversion begins
     * @param destination
     *            the array to hold the conversion
     * @param destOffset
     *            the index where output will be put
     * @return the number of decoded bytes converted
     * @since 1.3
     */
    private static int decode4to3(byte[] source, int srcOffset, byte[] destination, int destOffset) {
            // Example: Dk==
            if (source[srcOffset + 2] == EQUALS_SIGN) {
                    // Two ways to do the same thing. Don't know which way I like best.
                    // int outBuff = ( ( DECODABET[ source[ srcOffset ] ] << 24 ) >>> 6
                    // )
                    // | ( ( DECODABET[ source[ srcOffset + 1] ] << 24 ) >>> 12 );
                    int outBuff = ((DECODABET[source[srcOffset]] & 0xFF) << 18) | ((DECODABET[source[srcOffset + 1]] & 0xFF) << 12);

                    destination[destOffset] = (byte) (outBuff >>> 16);
                    return 1;
            }

            // Example: DkL=
            else if (source[srcOffset + 3] == EQUALS_SIGN) {
                    // Two ways to do the same thing. Don't know which way I like best.
                    // int outBuff = ( ( DECODABET[ source[ srcOffset ] ] << 24 ) >>> 6
                    // )
                    // | ( ( DECODABET[ source[ srcOffset + 1 ] ] << 24 ) >>> 12 )
                    // | ( ( DECODABET[ source[ srcOffset + 2 ] ] << 24 ) >>> 18 );
                    int outBuff = ((DECODABET[source[srcOffset]] & 0xFF) << 18) | ((DECODABET[source[srcOffset + 1]] & 0xFF) << 12) | ((DECODABET[source[srcOffset + 2]] & 0xFF) << 6);

                    destination[destOffset] = (byte) (outBuff >>> 16);
                    destination[destOffset + 1] = (byte) (outBuff >>> 8);
                    return 2;
            }

            // Example: DkLE
            else {
                    try {
                            // Two ways to do the same thing. Don't know which way I like
                            // best.
                            // int outBuff = ( ( DECODABET[ source[ srcOffset ] ] << 24 )
                            // >>> 6 )
                            // | ( ( DECODABET[ source[ srcOffset + 1 ] ] << 24 ) >>> 12 )
                            // | ( ( DECODABET[ source[ srcOffset + 2 ] ] << 24 ) >>> 18 )
                            // | ( ( DECODABET[ source[ srcOffset + 3 ] ] << 24 ) >>> 24 );
                            int outBuff = ((DECODABET[source[srcOffset]] & 0xFF) << 18) | ((DECODABET[source[srcOffset + 1]] & 0xFF) << 12) | ((DECODABET[source[srcOffset + 2]] & 0xFF) << 6) | ((DECODABET[source[srcOffset + 3]] & 0xFF));

                            destination[destOffset] = (byte) (outBuff >> 16);
                            destination[destOffset + 1] = (byte) (outBuff >> 8);
                            destination[destOffset + 2] = (byte) (outBuff);

                            return 3;
                    } catch (Exception e) {
                            System.out.println("" + source[srcOffset] + ": " + (DECODABET[source[srcOffset]]));
                            System.out.println("" + source[srcOffset + 1] + ": " + (DECODABET[source[srcOffset + 1]]));
                            System.out.println("" + source[srcOffset + 2] + ": " + (DECODABET[source[srcOffset + 2]]));
                            System.out.println("" + source[srcOffset + 3] + ": " + (DECODABET[source[srcOffset + 3]]));
                            return -1;
                    } // e nd catch
            }
    } // end decodeToBytes

    /**
     * Very low-level access to decoding ASCII characters in the form of a byte
     * array. Does not support automatically gunzipping or any other "fancy"
     * features.
     * 
     * @param source
     *            The RtsBase64 encoded data
     * @param off
     *            The offset of where to begin decoding
     * @param len
     *            The length of characters to decode
     * @return decoded data
     * @since 1.3
     */
    public static byte[] decode(byte[] source, int off, int len) {
            int len34 = len * 3 / 4;
            byte[] outBuff = new byte[len34]; // Upper limit on size of output
            int outBuffPosn = 0;

            byte[] b4 = new byte[4];
            int b4Posn = 0;
            int i = 0;
            byte sbiCrop = 0;
            byte sbiDecode = 0;
            for (i = off; i < off + len; i++) {
                    sbiCrop = (byte) (source[i] & 0x7f); // Only the low seven bits
                    sbiDecode = DECODABET[sbiCrop];

                    if (sbiDecode >= WHITE_SPACE_ENC) // White space, Equals sign or
                                                                                            // better
                    {
                            if (sbiDecode >= EQUALS_SIGN_ENC) {
                                    b4[b4Posn++] = sbiCrop;
                                    if (b4Posn > 3) {
                                            outBuffPosn += decode4to3(b4, 0, outBuff, outBuffPosn);
                                            b4Posn = 0;

                                            // If that was the equals sign, break out of 'for' loop
                                            if (sbiCrop == EQUALS_SIGN)
                                                    break;
                                    } // end if: quartet built

                            } // end if: equals sign or better

                    } // end if: white space, equals sign or better
                    else {
                            System.err.println("Bad RtsBase64 input character at " + i + ": " + source[i] + "(decimal)");
                            return null;
                    } // end else:
            } // each input character

            byte[] out = new byte[outBuffPosn];
            System.arraycopy(outBuff, 0, out, 0, outBuffPosn);
            return out;
    } // end decode

    /**
     * Decodes data from RtsBase64 notation, automatically detecting
     * gzip-compressed data and decompressing it.
     * 
     * @param s
     *            the string to decode
     * @return the decoded data
     * @since 1.4
     */
    public static byte[] decode(String s) {
            byte[] bytes;
            try {
                    bytes = s.getBytes(PREFERRED_ENCODING);
            } // end try
            catch (java.io.UnsupportedEncodingException uee) {
                    bytes = s.getBytes();
            } // end catch
            // </change>

            // Decode
            bytes = decode(bytes, 0, bytes.length);

            // Check to see if it's gzip-compressed
            // GZIP Magic Two-Byte Number: 0x8b1f (35615)
            if (bytes != null && bytes.length >= 4) {

                    int head = ((int) bytes[0] & 0xff) | ((bytes[1] << 8) & 0xff00);
                    if (java.util.zip.GZIPInputStream.GZIP_MAGIC == head) {
                            java.io.ByteArrayInputStream bais = null;
                            java.util.zip.GZIPInputStream gzis = null;
                            java.io.ByteArrayOutputStream baos = null;
                            byte[] buffer = new byte[2048];
                            int length = 0;

                            try {
                                    baos = new java.io.ByteArrayOutputStream();
                                    bais = new java.io.ByteArrayInputStream(bytes);
                                    gzis = new java.util.zip.GZIPInputStream(bais);

                                    while ((length = gzis.read(buffer)) >= 0) {
                                            baos.write(buffer, 0, length);
                                    } // end while: reading input

                                    // No error? Get new bytes.
                                    bytes = baos.toByteArray();

                            } // end try
                            catch (java.io.IOException e) {
                                    // Just return originally-decoded bytes
                            } // end catch
                            finally {
                                    try {
                                            baos.close();
                                    } catch (Exception e) {
                                    }
                                    try {
                                            gzis.close();
                                    } catch (Exception e) {
                                    }
                                    try {
                                            bais.close();
                                    } catch (Exception e) {
                                    }
                            } // end finally

                    } // end if: gzipped
            } // end if: bytes.length >= 2

            return bytes;
    } // end decode

    @Override
    public Object doTransform(Object srcData, String encoding) throws TransformerException {
        return null;
    }
}
