package com.regaltec.asip.transformer;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

import org.apache.cxf.common.util.StringUtils;
import org.mule.api.MuleContext;
import org.mule.api.transformer.TransformerException;
import org.mule.transformer.types.DataTypeFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * 
 * <p>接口平台Ftl(freemarker模板)转换器，入参只接受Map类型</p>
 * <p>创建日期：2011-5-11 下午05:26:12</p>
 *
 * @author 封加华
 */
public class AsipFtlTransformer extends AsipTransformer {
    private static Logger logger = LoggerFactory.getLogger(AsipFtlTransformer.class);
    private static Configuration templateFacotry = new Configuration();
    private String templateFile;

    public AsipFtlTransformer() {
        super();
        this.registerSourceType(DataTypeFactory.create(Map.class));
        this.setReturnDataType(DataTypeFactory.create(String.class));
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object doTransform(Object srcData, String encoding) throws TransformerException {
        initTemplateFacotry(muleContext);
        if (!(srcData instanceof Map)) {
            throw new IllegalArgumentException("AsipFtlTransformer only accept Map types");
        }
        Map<String, Object> dataModel = (Map<String, Object>) srcData;

        long startTime = System.currentTimeMillis();
        String content = null;
        Template template;
        try {
            template = templateFacotry.getTemplate(getFileName(templateFile));

            StringWriter writer = new StringWriter();
            template.process(dataModel, writer);
            StringBuffer buffer = writer.getBuffer();
            if (buffer != null) {
                content = buffer.toString();
            }
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("", e);
        } catch (TemplateException e) {
            e.printStackTrace();
            logger.error("模板解析异常", e);
        }
        long endTime = System.currentTimeMillis();
        
        if(logger.isDebugEnabled()){
            logger.debug("Template[" + templateFile + "] process time:" + (endTime - startTime) + "(ms)");
        }
        return content;
    }

    private void initTemplateFacotry(MuleContext muleContext) {
        if (StringUtils.isEmpty(this.getTemplateFile())) {
            throw new IllegalArgumentException("templateFile can't be empty");
        }
        int mpos = this.getTemplateFile().lastIndexOf("/");
        if (mpos == -1) {
            throw new IllegalArgumentException("templateFile format is not correct,/ does not occur.");
        } else {
            templateFacotry.setTemplateLoader(new ClassLoaderTemplateLoader(muleContext!=null?muleContext.getExecutionClassLoader():this.getClass().getClassLoader(), this
                    .getFileParent(getTemplateFile())));
        }
    }

    private String getFileParent(String path) {
        String name = "";
        int pos = path.lastIndexOf("/");
        if (pos != -1) {
            path.substring(0, pos + 1);
        }
        return name;
    }

    private String getFileName(String path) {
        String name = path;
        int pos = path.lastIndexOf("/");
        if (pos != -1) {
            path.substring(pos + 1, path.length());
        }
        return name;
    }

    /**
     * @return the templateFile
     */
    public String getTemplateFile() {
        return templateFile;
    }

    /**
     * @param templateFile the templateFile to set
     */
    public void setTemplateFile(String templateFile) {
        this.templateFile = templateFile;
    }
}
