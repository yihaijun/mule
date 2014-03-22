package com.regaltec.asip.transformer;

import org.mule.api.transformer.TransformerException;
import org.mule.transformer.AbstractTransformer;
/**
 * 
 * <p>接口平台transformer扩展类，业务需要实现自定义转换器时继承该类，而不是去继承Mule相关的类。</p>
 * <p>创建日期：2010-10-12 下午03:18:44</p>
 *
 * @author 封加华
 */
public abstract class AsipTransformer extends AbstractTransformer {
    protected String extendedRules = "";
    protected java.util.Properties contextProperties;
    /**
     * 
     * {@inheritDoc}
     */
    @Override
    public abstract Object doTransform(Object srcData, String encoding) throws TransformerException;
    /**
     * @return the extendedRules
     */
    public String getExtendedRules() {
        return extendedRules;
    }
    /**
     * @param extendedRules the extendedRules to set
     */
    public void setExtendedRules(String extendedRules) {
        this.extendedRules = extendedRules;
    }
    /**
     * @return the contextProperties
     */
    public java.util.Properties getContextProperties() {
        return contextProperties;
    }
    /**
     * @param contextProperties the contextProperties to set
     */
    public void setContextProperties(java.util.Properties contextProperties) {
        this.contextProperties = contextProperties;
    }
}
