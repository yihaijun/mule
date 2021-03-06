/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.alibaba.dubbo.jbpm;

import org.jbpm.jpdl.internal.activity.JpdlBinding;
import org.jbpm.jpdl.internal.model.JpdlProcessDefinition;
import org.jbpm.jpdl.internal.xml.JpdlParser;
import org.jbpm.pvm.internal.model.ActivityImpl;
import org.jbpm.pvm.internal.util.XmlUtil;
import org.jbpm.pvm.internal.xml.Parse;
import org.w3c.dom.Element;

public class DubboJbpmReceiveBinding extends JpdlBinding
{
    public DubboJbpmReceiveBinding()
    {
        super("dubbo-receive");
    }

    public Object parseJpdl(Element element, Parse parse, JpdlParser parser)
    {
        DubboJbpmReceiveActivity activity;
        
        JpdlProcessDefinition processDefinition = parse.contextStackFind(JpdlProcessDefinition.class);        
        if (processDefinition.getInitial() == null) 
        {
            processDefinition.setInitial(parse.contextStackFind(ActivityImpl.class));          
            activity = new DubboJbpmReceiveActivity(true);
        } 
        else
        {
            activity = new DubboJbpmReceiveActivity(false);
        }

        if (element.hasAttribute("var"))
            activity.setVariableName(XmlUtil.attribute(element, "var", parse));
        
        if (element.hasAttribute("endpoint"))
            activity.setEndpoint(XmlUtil.attribute(element, "endpoint", parse));
        
        if (element.hasAttribute("type"))
            activity.setPayloadClass(XmlUtil.attribute(element, "type", parse));

        return activity;
    }
}
