/**
 * 
 */
package com.alibaba.dubbo.bpm.api;


/**
 * @author yihaijun
 *
 */
public class DubboBpmEvent {
	private DubboBpmMessage dubboBpmMessage = new DefaultDubboBpmMessage("");

	/**
	 * @return the dubboBpmMessage
	 */
	public DubboBpmMessage getDubboBpmMessage() {
		return dubboBpmMessage;
	}

	/**
	 * @param dubboBpmMessage the dubboBpmMessage to set
	 */
	public void setDubboBpmMessage(DubboBpmMessage dubboBpmMessage) {
		this.dubboBpmMessage = dubboBpmMessage;
	} 

}
