package com.alibaba.dubbo.bpm.api;

import java.util.Map;

/**
 * 
 */

/**
 * @author yihaijun
 * 
 */
public abstract interface DubboBpmMessage extends java.io.Serializable {
	public Object getPayload();

	public Map getMessageProperties();

	/**
	 * @return the method
	 */
	public String getMethod();
	
	/**
	 * @param method the method to set
	 */
	public void setMethod(String method);
	
	/**
	 * @return the processIdField
	 */
	public String getProcessIdField();
	
	/**
	 * @param processIdField the processIdField to set
	 */
	public void setProcessIdField(String processIdField);
	/**
	 * @return the processVariables
	 */
	public Map getProcessVariables();
	/**
	 * @param processVariables the processVariables to set
	 */
	public void setProcessVariables(Map processVariables);
	/**
	 * @return the originatingEndpoint
	 */
	public String getOriginatingEndpoint();
	
	/**
	 * @param originatingEndpoint the originatingEndpoint to set
	 */
	public void setOriginatingEndpoint(String originatingEndpoint);
	/**
	 * @return the action
	 */
	public String getAction();
	/**
	 * @param action the action to set
	 */
	public void setAction(String action);
}
