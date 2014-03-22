package com.regaltec.asip.manager.api.client.jms.vo;

import java.io.Serializable;

/**
 * <p>JMS Message定义</p>
 * <p>创建日期：2011-2-22 12:01:46</p>
 * @author 戈亮锋
 */
public class MsgBean implements Serializable{

	/**
	 * 消息类型：用于区分是各种操作类型
	 */
	private int msgType;
	private String msgResponse;
	private String context;	 //上下文(区分ASIP部署环境)
	private int timeOut; 	 //(单位:ms)
	
	public int getMsgType() {
		return msgType;
	}

	public void setMsgType(int msgType) {
		this.msgType = msgType;
	}

	public String getMsgResponse() {
		return msgResponse;
	}

	public void setMsgResponse(String msgResponse) {
		this.msgResponse = msgResponse;
	}
	
	public String getContext() {
		return context;
	}

	public void setContext(String context) {
		this.context = context;
	}
	
	public int getTimeOut() {
		return timeOut;
	}

	/**
	 * 
	 * @param timeOut int (单位:ms)
	 */
	public void setTimeOut(int timeOut) {
		this.timeOut = timeOut;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((context == null) ? 0 : context.hashCode());
		result = prime * result
				+ ((msgResponse == null) ? 0 : msgResponse.hashCode());
		result = prime * result + msgType;
		result = prime * result + timeOut;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MsgBean other = (MsgBean) obj;
		if (context == null) {
			if (other.context != null)
				return false;
		} else if (!context.equals(other.context))
			return false;
		if (msgResponse == null) {
			if (other.msgResponse != null)
				return false;
		} else if (!msgResponse.equals(other.msgResponse))
			return false;
		if (msgType != other.msgType)
			return false;
		if (timeOut != other.timeOut)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "[" 
		+"context="+(context == null ? "":context)+","
		+ "msgType=" + msgType +","
		+ "timeOut=" + timeOut +","
		+ "msgResponse="+(msgResponse == null ? "":msgResponse)
		+ "]";
	}

}