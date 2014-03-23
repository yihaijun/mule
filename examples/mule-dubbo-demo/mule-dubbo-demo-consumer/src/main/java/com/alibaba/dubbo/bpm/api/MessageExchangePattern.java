/**
 * 
 */
package com.alibaba.dubbo.bpm.api;

/**
 * @author yihaijun
 *
 */
public enum MessageExchangePattern
{    
    ONE_WAY
    {
        @Override
        public boolean hasResponse()
        {
            return false;
        }
    }, 
    
    REQUEST_RESPONSE
    {
        @Override
        public boolean hasResponse()
        {
            return true;
        }
    }; 
    
    public abstract boolean hasResponse();

    public static MessageExchangePattern fromSyncFlag(boolean sync)
    {
        if (sync)
        {
            return REQUEST_RESPONSE;
        }
        else
        {
            return ONE_WAY;
        }
    }

    public static MessageExchangePattern fromString(String string)
    {
        String mepString = string.toUpperCase().replace('-', '_');
        return MessageExchangePattern.valueOf(mepString);
    }
}
