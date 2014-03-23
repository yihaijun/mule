/**
 * 
 */
package com.alibaba.dubbo.bpm.api.lifecycle;

/**
 * @author yihaijun
 *
 */
public class InitialisationException extends Exception{

	   /**
     * @param cause     the exception that cause this exception to be thrown
     * @param component the object that failed during a lifecycle method call
     */
    public InitialisationException(Throwable cause, Initialisable component)
    {
//        super(cause, component);
      super(cause);
    }

    public InitialisationException(String message,Throwable cause)
    {
//        super(cause, component);
      super(message,cause);
    }

    public InitialisationException(String message)
    {
//        super(cause, component);
      super(message);
    }
}
