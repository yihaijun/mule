package com.regaltec.asip.transformer;

import java.net.URL;

import freemarker.cache.URLTemplateLoader;

/**
 * 
 * <p>ClassLoader模板加载器</p>
 * <p>创建日期：2011-5-11 下午03:38:21</p>
 *
 * @author 封加华
 */
public class ClassLoaderTemplateLoader extends URLTemplateLoader {
    private ClassLoader classLoader;
    private String path;

    public ClassLoaderTemplateLoader(ClassLoader classLoader, String path) {
        path = (path==null?"":path);
        setFields(classLoader, path);
    }

    private void setFields(ClassLoader classLoader, String path) {
        if (classLoader == null) {
            throw new IllegalArgumentException("classLoader == null");
        }
        if (path == null) {
            throw new IllegalArgumentException("path == null");
        }
        this.classLoader = classLoader;
        this.path = canonicalizePrefix(path);
    }

    @Override
    protected URL getURL(String name) {
        return classLoader.getResource(path+name);
    }

}
