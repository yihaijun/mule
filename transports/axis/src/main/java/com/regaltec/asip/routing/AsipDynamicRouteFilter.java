/*
 * <p>标题: 中国电信综合调度系统第4版</p>
 * <p>描述: ...</p>
 * <p>版权: Copyright (c) 2013</p>
 * <p>公司: 天讯瑞达通信技术有限公司</p>
 * <p>网址：http://www.tisson.cn/</p>
 */
package com.regaltec.asip.routing;

import java.io.CharArrayReader;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.mule.api.MuleEvent;
import org.mule.module.xml.filters.JaxenFilter;
import org.mule.routing.filters.ExpressionFilter;

import com.regaltec.asip.common.AsipLog4j;

/**
 * <p>概述该类作用，请详细描述。</p>
 * <p>创建日期：2013-4-11 下午07:35:42</p>
 *
 * @author yihaijun
 */
public class AsipDynamicRouteFilter {
    private AsipLog4j logger = new AsipLog4j(this.getClass().getName());
    private Element filterElement = null;

    public AsipDynamicRouteFilter(String filter) {
        SAXBuilder sb = null;
        sb = new SAXBuilder();
        try {
            Document document = sb.build(new CharArrayReader(filter.toCharArray()));
            filterElement = document.getRootElement();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public AsipDynamicRouteFilter(Element filterElement) {
        this.filterElement = filterElement;
    }

    public boolean accept(MuleEvent event) {
        if (filterElement == null) {
            if (logger.isDebugEnabled()) {
                logger.debug("return false.because paramNode == null");
            }
            return false;
        }
        if (filterElement.getName().equals("not-filter")) {
            return !(new AsipDynamicRouteFilter((Element) filterElement.getChildren().get(0)).accept(event));
        } else if (filterElement.getName().equals("and-filter")) {
            List childNodes = filterElement.getChildren();
            int len = childNodes.size();
            for (int i = 0; i < len; i++) {
                if (!new AsipDynamicRouteFilter((Element) filterElement.getChildren().get(i)).accept(event)) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("and failed!");
                    }
                    return false;
                }
            }
            return true;
        } else if (filterElement.getName().equals("or-filter")) {
            List childNodes = filterElement.getChildren();
            int len = childNodes.size();
            for (int i = 0; i < len; i++) {
                if (new AsipDynamicRouteFilter((Element) filterElement.getChildren().get(i)).accept(event)) {
                    return true;
                }
            }
            if (logger.isDebugEnabled()) {
                logger.info("or failed!");
            }
            return false;
        } else if (filterElement.getName().equals("expression-filter")) {
            ExpressionFilter expressionFilter = new ExpressionFilter();
            expressionFilter.setEvaluator(filterElement.getAttributeValue("evaluator"));
            expressionFilter.setExpression(filterElement.getAttributeValue("expression"));
            expressionFilter.setMuleContext(event.getMuleContext());
            boolean ret = expressionFilter.accept(event.getMessage());
            if (logger.isDebugEnabled()) {
                logger.debug(filterElement.getAttributeValue("expression") + "===" + ret);
            }
            return ret;
        } else if (filterElement.getName().equals("jaxen-filter") && filterElement.getNamespacePrefix().equals("xm")) {
            JaxenFilter jaxenFilter = new JaxenFilter();
            jaxenFilter.setExpectedValue(filterElement.getAttributeValue("expectedValue"));
            jaxenFilter.setPattern(filterElement.getAttributeValue("pattern"));
            jaxenFilter.setMuleContext(event.getMuleContext());
            boolean ret = jaxenFilter.accept(event.getMessage());
            if (logger.isDebugEnabled()) {
                logger.debug(filterElement.getAttributeValue("pattern") + "===" + ret);
            }
            return ret;
        } else if (filterElement.getName().equals("custom-filter")) {
        }else{
            logger.info("filterElement.getName()="+filterElement.getName());
        }
        return false;
    }
}
