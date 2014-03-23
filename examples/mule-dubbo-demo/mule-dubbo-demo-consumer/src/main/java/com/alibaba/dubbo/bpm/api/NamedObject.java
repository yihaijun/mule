/**
 * 
 */
package com.alibaba.dubbo.bpm.api;

/**
 * @author yihaijun
 *
 */
/**
 * Adds {@link #getName} and {@link #setName} methods to an object
 */
public interface NamedObject
{
    /**
     * Sets the name of the object
     * @param name the name of the object
     */
    void setName(String name);

    /**
     * Gets the name of the object
     * @return the name of the object
     */
    String getName();
}