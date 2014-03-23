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
	private DubboBpmMessage dubboBpmMessage;

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
