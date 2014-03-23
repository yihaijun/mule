/**
 * 
 */
package com.alibaba.dubbo.bpm.api;

import java.io.ObjectStreamException;
import java.io.Serializable;

/**
 * @author yihaijun
 *
 */
//@Immutable
public final class NullPayload implements Serializable
{
    /**
     * Serial version
     */
    private static final long serialVersionUID = 3530905899811505080L;

    private static class NullPayloadHolder
    {
        private static final NullPayload instance = new NullPayload();
    }

    public static NullPayload getInstance()
    {
        return NullPayloadHolder.instance;
    }

    private NullPayload()
    {
        super();
    }

    private Object readResolve() throws ObjectStreamException
    {
        return NullPayloadHolder.instance;
    }

    @Override
    public boolean equals(Object obj)
    {
        return obj instanceof NullPayload;
    }

    @Override
    public int hashCode ()
    {
        return 1; // random, 0 is taken by VoidResult
    }

    @Override
    public String toString()
    {
        return "{NullPayload}";
    }

}