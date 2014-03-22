/*
 * <p>标题: 中国电信综合调度系统第4版</p>
 * <p>描述: ...</p>
 * <p>版权: Copyright (c) 2010</p>
 * <p>公司: 天讯瑞达通信技术有限公司</p>
 * <p>网址：http://www.tisson.cn/
 */
package com.regaltec.asip.manager.api.util;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.mule.module.xml.util.XMLUtils;

/**
 * <p>概述该类作用，请详细描述。</p>
 * <p>创建日期：2010-12-27 下午12:37:13</p>
 *
 * @author yihaijun
 */
public class XmlParser {
    private final XMLInputFactory factory = XMLInputFactory.newInstance();

    public  boolean isXml(String s){
        XMLStreamReader parser = null;
        try
        {
            parser = XMLUtils.toXMLStreamReader(factory, s);
            if (parser == null)
            {
                return false;
            }

            while (parser.next() != XMLStreamConstants.END_DOCUMENT)
            {
                // meep meep!
            }

            return true;
        }
        catch (XMLStreamException ex)
        {
            return false;
        }
        finally
        {
            if (parser != null)
            {
                try
                {
                    parser.close();
                }
                catch (XMLStreamException ignored)
                {
                    // bummer
                }
            }
        }
        
    }
}
