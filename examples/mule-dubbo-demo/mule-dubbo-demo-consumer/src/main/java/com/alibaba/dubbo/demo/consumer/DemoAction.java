/*
 * Copyright 1999-2011 Alibaba Group.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.dubbo.demo.consumer;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.jbpm.api.ProcessInstance;

import com.alibaba.dubbo.bpm.ProcessComponent;
import com.alibaba.dubbo.bpm.api.DubboBpmEvent;
import com.alibaba.dubbo.bpm.api.DubboBpmMessage;
import com.alibaba.dubbo.demo.DemoService;
import com.alibaba.dubbo.jbpm.Jbpm;

public class DemoAction {
    
    private DemoService demoService;
    private Jbpm jbpm;
    private ProcessComponent processComponent;

    public void setDemoService(DemoService demoService) {
        this.demoService = demoService;
    }


	/**
	 * @param jbpm the jbpm to set
	 */
	public void setJbpm(Jbpm jbpm) {
		this.jbpm = jbpm;
		try {
			jbpm.setName("fork-process.jpdl.xml");
			jbpm.deployProcess("fork-process.jpdl.xml");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param processComponent the processComponent to set
	 */
	public void setProcessComponent(ProcessComponent processComponent) {
		this.processComponent = processComponent;
	}

	private DubboBpmMessage call(String method,Object in){
		return null;
	}
	
	

	public void start() throws Exception {
		jbpm.initialise();
		DubboBpmEvent dubboBpmEvent = new DubboBpmEvent();
		processComponent.doInvoke(dubboBpmEvent);
		DubboBpmMessage response =  call("method",null);
        ProcessInstance process = (ProcessInstance) response.getPayload();
        String state = (String) jbpm.getState(process);
        process = (ProcessInstance) jbpm.lookupProcess(process.getId());
        // Start ServiceA
//      muleContext.getRegistry().lookupService("ServiceA").resume();
      Thread.sleep(2000);
        process = (ProcessInstance) jbpm.lookupProcess(process.getId());

        for (int i = 0; i < Integer.MAX_VALUE; i ++) {
            try {
            	String hello = demoService.sayHello("world" + i);
                System.out.println("[" + new SimpleDateFormat("HH:mm:ss").format(new Date()) + "] " + hello);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Thread.sleep(2000);
        }
	}

}