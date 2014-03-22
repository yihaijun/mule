package com.regaltec.asip.transformer;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.mule.api.MuleEvent;
import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.mule.api.transformer.TransformerException;
import org.mule.api.transport.OutputHandler;
import org.mule.module.xml.util.XMLUtils;
import org.mule.transformer.types.DataTypeFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * <p>xslt转换器</p>
 * <p>创建日期：2010-10-30 下午03:51:35</p>
 *
 * @author 封加华
 */
public class AsipXsltTransformer extends AsipTransformer {
    private static Logger logger = LoggerFactory.getLogger(AsipXsltTransformer.class);
    private final XMLInputFactory factory = XMLInputFactory.newInstance();
    private String xslFile;

    /**
     * 
     * 构造函数
     */
    @SuppressWarnings("deprecation")
    public AsipXsltTransformer() {
        super();
        registerSourceType(Object.class);
        registerSourceType(byte[].class);
        registerSourceType(InputStream.class);
        registerSourceType(OutputHandler.class);
        // deliberately set the mime for this transformer to text plain so that other transformers
        // that serialize string types such as XML or JSON will not match this
        setReturnDataType(DataTypeFactory.TEXT_STRING);
    }

    @Override
    public MuleEvent process(MuleEvent event) throws MuleException {
        MuleMessage message = event.getMessage();
        if (contextProperties == null) {
            contextProperties = new Properties();
        }
        Set<String> sessionKey = message.getSessionPropertyNames();
        for (Iterator<String> it = sessionKey.iterator(); it.hasNext();) {
            String key = it.next();
            Object value = message.getSessionProperty(key);
            if (!(value instanceof String)) {
                continue;
            }
            contextProperties.put(key, value);
        }
        Set<String> outboundKey = message.getOutboundPropertyNames();
        for (Iterator<String> it = outboundKey.iterator(); it.hasNext();) {
            String key = it.next();
            Object value = message.getOutboundProperty(key);
            if (!(value instanceof String)) {
                continue;
            }
            contextProperties.put(key, value);
        }
        return super.process(event);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object doTransform(Object srcData, String encoding) throws TransformerException {
        try {
            long beginTime = System.currentTimeMillis();
            if (srcData == null) {
                srcData = "";
            }
            if (logger.isDebugEnabled()) {
                logger.debug("onCall() begin...");
            }
            InputStream xslt = null;
            if (xslFile == null || xslFile.equals("")) {
                if (contextProperties != null && contextProperties.get("xslFile") != null) {
                    xslFile = (String) contextProperties.get("xslFile");
                } else {
                }
            }
            try {
                // String xsltString = IOUtils.getResourceAsString(xslFile, getClass());
                if (xslFile == null) {
                    xslt = new ByteArrayInputStream(extendedRules.getBytes());
                } else {
                    xslt = muleContext.getExecutionClassLoader().getResourceAsStream(xslFile);
                }
            } catch (Exception e1) {
                // e1.printStackTrace();
                try {
                    logger.error("getResourceAsString(" + xslFile + ") Exception:" + e1.toString() + "("
                            + muleContext.getExecutionClassLoader().getResource(xslFile).toString() + ")");
                } catch (Exception e) {
                    // e.printStackTrace();
                    logger.error("getResourceAsString(" + xslFile + ") Exception:" + e1.toString());
                }
            }
            // StreamSource xslStream = new StreamSource(new StringReader(xslt));
            StreamSource xslStream = new StreamSource(xslt);
            if (logger.isDebugEnabled()) {
                logger.debug("构造xsl的stream：" + (xslStream == null));
                logger.debug("转换前的数据：" + srcData.toString());
            }
            if (srcData.equals("") || !isXml(srcData.toString())) {
                srcData =
                        "<original>" + org.apache.commons.lang.StringEscapeUtils.escapeXml(srcData.toString())
                                + "</original>";
            }
            TransformerFactory tFactory = TransformerFactory.newInstance();
            Transformer transformer = null;
            Object returnObject = null;
            StringWriter writer = new StringWriter();
            try {
                // transformer = tFactory.newTransformer(new StreamSource(xslStream));
                logger.debug("factory instanceof default:" + tFactory.getClass().getName());
                // if (System.getProperty("asip.transformer.xslt.caching","false").equalsIgnoreCase("true")) {
                // if (tFactory instanceof com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl) {
                // System.setProperty("javax.xml.transform.TransformerFactory",
                // "com.regaltec.asip.transformer.xslt.XalanCachingTransformerFactory");
                // tFactory = TransformerFactory.newInstance();
                // } else if (tFactory instanceof net.sf.saxon.TransformerFactoryImpl) {
                // System.setProperty("javax.xml.transform.TransformerFactory",
                // "com.regaltec.asip.transformer.xslt.SaxonCachingTransformerFactory");
                // tFactory = TransformerFactory.newInstance();
                // } else if (tFactory instanceof org.apache.xalan.processor.TransformerFactoryImpl) {
                // System.setProperty("javax.xml.transform.TransformerFactory",
                // "com.regaltec.asip.transformer.xslt.ApacheXalanProcessorCachingTransformerFactory");
                // tFactory = TransformerFactory.newInstance();
                // }else{
                // }
                // }
                logger.debug("factory instanceof actual:" + tFactory.getClass().getName());
                transformer = tFactory.newTransformer(xslStream);
                if (transformer == null) {
                    logger.error("transformer==null?");
                }
                if (contextProperties != null) {
                    for (Iterator<?> i = contextProperties.entrySet().iterator(); i.hasNext();) {
                        Map.Entry parameter = (Entry) i.next();
                        String key = (String) parameter.getKey();
                        transformer.setParameter(key, contextProperties.getProperty(key));
                    }
                }
                if (srcData.toString().length() >= 5 * 1024 * 1024 / 10) {
                    logger.warn("Do not query so much records or such a big message package("
                            + (srcData.toString().length() / 1024) + "k>=0.5M)");
                }
                StreamResult streamResult = new StreamResult(writer);
                StreamSource streamSource = new StreamSource(new StringReader(srcData.toString()));
                transformer.transform(streamSource, streamResult);
            } catch (TransformerConfigurationException e) {
                throw (TransformerException) e.getException();
            } catch (javax.xml.transform.TransformerException e) {
                throw (TransformerException) e.getException();
            }
            returnObject = writer.getBuffer().toString();
            long endTime = System.currentTimeMillis();
            if (logger.isDebugEnabled()) {
                logger.debug("转换用时" + (endTime - beginTime) + "ms,结果：" + returnObject);
            }
            return returnObject;
        } catch (TransformerFactoryConfigurationError e) {
            throw new TransformerException(this, e);
        } catch (Exception e) {
            e.printStackTrace();
            throw new TransformerException(this, e);
        }finally{
            
        }
    }

    private boolean isXml(String s) {
        XMLStreamReader parser = null;
        try {
            parser = XMLUtils.toXMLStreamReader(factory, s);
            if (parser == null) {
                return false;
            }

            while (parser.next() != XMLStreamConstants.END_DOCUMENT) {
                // meep meep!
            }

            return true;
        } catch (XMLStreamException ex) {
            return false;
        } finally {
            if (parser != null) {
                try {
                    parser.close();
                } catch (XMLStreamException ignored) {
                    // bummer
                }
            }
        }

    }

    /**
     * @return the xslFile
     */
    public String getXslFile() {
        return xslFile;
    }

    /**
     * @param xslFile the xslFile to set
     */
    public void setXslFile(String xslFile) {
        this.xslFile = xslFile;
    }
}
