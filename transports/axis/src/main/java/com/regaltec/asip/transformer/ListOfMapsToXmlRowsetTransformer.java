package com.regaltec.asip.transformer;

import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.api.transformer.TransformerException;
import org.mule.api.transport.OutputHandler;
import org.mule.transformer.types.DataTypeFactory;
/**
 * 
 * <p>将jdbc transport默认结果集转成xml-rowset-row格式</p>
 * <p>创建日期：2010-10-12 下午03:04:17</p>
 *
 * @author 封加华
 */
public class ListOfMapsToXmlRowsetTransformer extends AsipTransformer {
    private Log logger = LogFactory.getLog(getClass());
    /**
     * 
     * 构造函数
     */
    @SuppressWarnings("deprecation")
    public ListOfMapsToXmlRowsetTransformer() {
        super();
        registerSourceType(Object.class);
        registerSourceType(byte[].class);
        registerSourceType(InputStream.class);
        registerSourceType(OutputHandler.class);
        // deliberately set the mime for this transformer to text plain so that other transformers
        // that serialize string types such as XML or JSON will not match this
        setReturnDataType(DataTypeFactory.TEXT_STRING);
    }
    /**
     * 
     * <p>功能的简单描述，参数、返回值及异常必须注明。</p>
     * @param rowMap 描述一行的map
     * @return xml string
     */
    @SuppressWarnings("unchecked")
    private String mapToXmlRow(Map rowMap) {
        StringBuffer xml = new StringBuffer();
        Set<Map.Entry> entrySet = rowMap.entrySet();
        xml.append("<row>");
        for (Map.Entry entry : entrySet) {
            xml.append("<").append(entry.getKey().toString().toLowerCase()).append(">").append(
                    entry.getValue() != null ? StringEscapeUtils.escapeXml(entry.getValue().toString()) : "").append("</").append(
                    entry.getKey().toString().toLowerCase()).append(">");
        }
        xml.append("</row>");
        return xml.toString();
    }
    /**
     * 
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public Object doTransform(Object src, String encoding) throws TransformerException {
        if (logger.isDebugEnabled()) {
            logger.debug("doTransform() begin...");
        }
        StringBuffer xml = new StringBuffer();
        xml.delete(0, xml.length());
        if(src==null){
            xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?><rowset totalRows=\"0\"></rowset>");
            if (logger.isDebugEnabled()) {
                logger.debug("转换后的结果：" + xml.toString());
                logger.debug("doTransform() return.");
            }
            return xml.toString();
        }
        if (logger.isDebugEnabled()) {
            logger.debug("转换前的数据："+src.toString());
        }
        if (src instanceof Collection) {
            List data = (List) src;
            xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            xml.append("<rowset totalRows=\""+data.size()+"\">");
            for (int i = 0; i < data.size(); i++) {
                Object item = data.get(i);
                if (item instanceof Map) {
                    xml.append(mapToXmlRow((Map) item));
                } else {
                    xml.append("List中的item类型不是java.util.Map,暂时无法进行转换");
                }
            }
            xml.append("</rowset>");
        } else if (src instanceof Map) {
            xml.append(mapToXmlRow((Map) src));
        }else{
            xml.append(src.toString());
        }
        if (logger.isDebugEnabled()) {
            logger.debug("转换后的结果：" + xml.toString());
            logger.debug("doTransform() return.");
        }
        return xml.toString();
    }
}
