/*
 * <p>标题: 中国电信综合调度系统第4版</p>
 * <p>描述: ...</p>
 * <p>版权: Copyright (c) 2013</p>
 * <p>公司: 天讯瑞达通信技术有限公司</p>
 * <p>网址：http://www.tisson.cn/</p>
 */
package com.regaltec.asip.routing;

import java.util.List;

import mx4j.log.Logger;

import org.jdom.Element;
import org.mule.api.MuleEvent;
import org.mule.api.MuleException;
import org.mule.api.routing.RoutingException;
import org.mule.api.transformer.Transformer;
import org.mule.config.i18n.CoreMessages;
import org.mule.expression.ExpressionConfig;
import org.mule.expression.transformers.ExpressionArgument;
import org.mule.expression.transformers.ExpressionTransformer;
import org.mule.module.scripting.component.Scriptable;
import org.mule.module.scripting.transformer.ScriptTransformer;

import com.regaltec.asip.common.AsipLog4j;
import com.regaltec.asip.transformer.AsipTransformer;

/**
 * <p>概述该类作用，请详细描述。</p>
 * <p>创建日期：2013-4-14 上午02:28:26</p>
 *
 * @author yihaijun
 */
public class AsipDynamicRouteTransformer {
    private AsipLog4j logger = new AsipLog4j(this.getClass().getName());
    private Element transformerElement = null;

    public AsipDynamicRouteTransformer(Element transformerElement) {
        this.transformerElement = transformerElement;
    }

    public MuleEvent transform(MuleEvent event) throws MuleException {
        if (transformerElement.getName().equals("expression-transformer")) {
            ExpressionTransformer transformer = new ExpressionTransformer();
            List expressionArgumentElementList = transformerElement.getChildren();
            for (int i = 0; i < expressionArgumentElementList.size(); i++) {
                Element element = (Element) expressionArgumentElementList.get(i);
                if (element.getAttributeValue("expression") != null) {
                    String expression = element.getAttributeValue("expression");
                    String evaluator = element.getAttributeValue("evaluator");
                    String customEvaluator = element.getAttributeValue("customEvaluator");
                    String expressionPrefix = element.getAttributeValue("expressionPrefix");
                    String expressionPostfix = element.getAttributeValue("expressionPostfix");

                    ExpressionConfig expressionConfig =
                            new ExpressionConfig(expression, evaluator, customEvaluator, expressionPrefix,
                                    expressionPostfix);
                    ExpressionArgument expressionArgument = new ExpressionArgument();
                    expressionArgument.setMuleContext(event.getMuleContext());
                    expressionArgument.setExpressionConfig(expressionConfig);
                    transformer.addArgument(expressionArgument);
                }
            }
            transformer.setMuleContext(event.getMuleContext());
            transformer.initialise();
            return transformer.process(event);
        }
        if (transformerElement.getName().equals("custom-transformer")) {
            String className = transformerElement.getAttributeValue("class");
            AsipTransformer asipTransformer = null;
            try {
                asipTransformer = (AsipTransformer) Class.forName(className).newInstance();
            } catch (Exception e) {
                e.printStackTrace();
                // throw new org.mule.api.transformer.TransformerException(event.getMessage(), e.getCause());
                throw new RoutingException(CoreMessages.failedToCreate(className), event, null, e);
            }
            asipTransformer.setMuleContext(event.getMuleContext());
            java.util.Properties contextProperties = new java.util.Properties();
            List propertyElementList = transformerElement.getChildren();
            for (int i = 0; i < propertyElementList.size(); i++) {
                Element element = (Element) propertyElementList.get(i);
                String keyName = element.getAttributeValue("name");
                String keyValue = element.getAttributeValue("value");
                contextProperties.put(keyName, keyValue);
            }
            String extendedRules = transformerElement.getText();
            asipTransformer.setContextProperties(contextProperties);
            asipTransformer.setExtendedRules(extendedRules);
            asipTransformer.initialise();
            return asipTransformer.process(event);
        }

        if (transformerElement.getName().equals("transformer") && transformerElement.getNamespacePrefix().equals("")) {
            String beanName = transformerElement.getAttributeValue("ref");
            Transformer transformer = (Transformer) event.getMuleContext().getRegistry().lookupTransformer(beanName);
            if (transformer == null) {
                logger.error("lookupTransformer(" + beanName + ") return null");
                return event;
            }
            return transformer.process(event);
        }
        if ((transformerElement.getName().equals("script:transformer"))
                || (transformerElement.getName().equals("transformer") && transformerElement.getNamespacePrefix()
                        .startsWith("script"))) {
            Scriptable scriptable = new Scriptable(event.getMuleContext());
            Element scriptElement = (Element) transformerElement.getChildren().get(0);
            String scriptEngineName = scriptElement.getAttributeValue("engine");
            String scriptText = scriptElement.getText();
            scriptable.setScriptEngineName(scriptEngineName);
            scriptable.setScriptText(scriptText);
            scriptable.initialise();
            ScriptTransformer transformer = new ScriptTransformer();
            transformer.setScript(scriptable);
            transformer.setMuleContext(event.getMuleContext());
            transformer.initialise();
            return transformer.process(event);
        }
        return event;
    }
}
