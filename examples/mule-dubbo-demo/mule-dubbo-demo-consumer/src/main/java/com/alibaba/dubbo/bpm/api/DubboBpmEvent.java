/**
 * 
 */
package com.alibaba.dubbo.bpm.api;

import java.util.HashMap;
import java.util.Map;

/**
 * @author yihaijun
 *
 */
public class DubboBpmEvent {
	private String method = "";
	private Object param  = "";
	private String processIdField = "";
	private Map processVariables = new HashMap();
	private String originatingEndpoint = "";
	private String action = "";
	
	/**
	 * @return the method
	 */
	public String getMethod() {
		return method;
	}
	/**
	 * @param method the method to set
	 */
	public void setMethod(String method) {
		this.method = method;
	}
	/**
	 * @return the param
	 */
	public Object getParam() {
		return param;
	}
	/**
	 * @param param the param to set
	 */
	public void setParam(Object param) {
		this.param = param;
	}
	/**
	 * @return the processIdField
	 */
	public String getProcessIdField() {
		return processIdField;
	}
	/**
	 * @param processIdField the processIdField to set
	 */
	public void setProcessIdField(String processIdField) {
		this.processIdField = processIdField;
	}
	/**
	 * @return the processVariables
	 */
	public Map getProcessVariables() {
		return processVariables;
	}
	/**
	 * @param processVariables the processVariables to set
	 */
	public void setProcessVariables(Map processVariables) {
		this.processVariables = processVariables;
	}
	/**
	 * @return the originatingEndpoint
	 */
	public String getOriginatingEndpoint() {
		return originatingEndpoint;
	}
	/**
	 * @param originatingEndpoint the originatingEndpoint to set
	 */
	public void setOriginatingEndpoint(String originatingEndpoint) {
		this.originatingEndpoint = originatingEndpoint;
	}
	/**
	 * @return the action
	 */
	public String getAction() {
		return action;
	}
	/**
	 * @param action the action to set
	 */
	public void setAction(String action) {
		this.action = action;
	}

}
