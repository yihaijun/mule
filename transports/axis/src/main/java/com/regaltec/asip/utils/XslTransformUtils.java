package com.regaltec.asip.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
/**
 * 
 * <p>xsl命令行转换</p>
 * <p>创建日期：2010-10-22 下午02:49:58</p>
 *
 * @author 封加华
 */
public class XslTransformUtils {
    /**
     * 
     * <p>通过xsl转换xml文件并且输出在控制台</p>
     * @param xml xml文件路径
     * @param xsl xsl文件路径
     * @throws Exception 异常
     */
    @SuppressWarnings("unchecked")
    public static void transformXml(String xml, String xsl,java.util.Properties contextProperties) throws Exception {
        TransformerFactory tFactory = TransformerFactory.newInstance();
        Transformer transformer = tFactory.newTransformer(new StreamSource(xsl));
        // set transformation parameters
        if (contextProperties != null)
        {
            for (Iterator i = contextProperties.entrySet().iterator(); i.hasNext();)
            {
                Map.Entry parameter = (Entry) i.next();
                String key = (String) parameter.getKey();
                transformer.setParameter(key, contextProperties.getProperty(key));
            }
        }

        transformer.transform(new StreamSource(xml), new StreamResult(System.out));
    }

    
    /**
     * 
     * <p>通过xsl转换xml文件并且输出在控制台</p>
     * @param xml xml文件路径
     * @param xsl xsl文件路径
     * @throws Exception 异常
     */
    public static void transformXml(String xml, String xsl) throws Exception {
        TransformerFactory tFactory = TransformerFactory.newInstance();
        Transformer transformer = tFactory.newTransformer(new StreamSource(xsl));
        transformer.transform(new StreamSource(xml), new StreamResult(System.out));
    }
    /**
     * 
     * <p>从流中读取一行</p>
     * @param in 输入流
     * @return 一行字符
     * @throws Exception 异常
     */
    private static String readLine(InputStream in)throws Exception{
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        return reader.readLine();
    }
    /**
     * 
     * <p>main</p>
     * @param args 虚拟机参数
     * @throws Exception 异常
     */
    public static void main(String[] args)throws Exception {
        System.out.println("请输入xml文件路径：");
        String xml = readLine(System.in);
        System.out.println("请输入xsl文件路径：");
        String xsl = readLine(System.in);
        System.out.println("请输入properties文件路径：");
        String propertiesFn = readLine(System.in);
        long beginTime = System.currentTimeMillis();
        if(!propertiesFn.equals("")){
            java.util.Properties contextProperties = new java.util.Properties();
            File f = new File(propertiesFn,"");
            contextProperties.load(new FileInputStream(f));
            System.out.println(contextProperties);
            transformXml(xml,xsl,contextProperties);
        }else{
            transformXml(xml,xsl);
        }
        long endTime = System.currentTimeMillis();
        System.out.println("耗时"+ (endTime-beginTime)+ "ms");
    }
}
