/**
 * 
 */
package com.alibaba.dubbo.bpm;

import javax.xml.soap.MessageFactory;

import com.alibaba.dubbo.bpm.api.Disposable;
import com.alibaba.dubbo.bpm.api.DubboBpmEvent;
import com.alibaba.dubbo.bpm.api.lifecycle.Initialisable;
import com.alibaba.dubbo.bpm.api.lifecycle.InitialisationException;


/**
 * @author yihaijun
 *
 */
public class ProcessComponent  {//implements Component, MuleContextAware, Lifecycle{
    // Created internally
    protected Process process;
    
    /** The underlying BPMS */
    protected BPMS bpms;

    /** The logical name of the process.  This is used to look up the running process instance from the BPMS. */
    private String name;
    
    /** The resource containing the process definition.  This will be used to deploy the process to the BPMS. */
    private String resource;

    /** This field will be used to correlate messages with processes. */
    private String processIdField;

//    @Override
    protected void doInitialise() throws InitialisationException
    {
//        if (bpms == null)
//        {
//            try
//            {
//                bpms = muleContext.getRegistry().lookupObject(BPMS.class);
//            }
//            catch (Exception e)
//            {
//                throw new InitialisationException(e, this);
//            }
//        }
        if (bpms == null)
        {
//            throw new InitialisationException(MessageFactory.createStaticMessage("The bpms property must be set for this component."), this);
          throw new InitialisationException("The bpms property must be set for this component.");
        }
        if (bpms instanceof Initialisable)
        {
            ((Initialisable) bpms).initialise();
        }
        
//        process = new Process(bpms, name, resource, flowConstruct, muleContext);
        process = new Process(bpms, name, resource);
        process.initialise();

        // Inject a callback so that the BPMS may generate messages within Mule.
        bpms.setMessageService(process);        
    }
    
//    @Override
    protected void doDispose()
    {
        if (bpms instanceof Disposable)
        {
            ((Disposable) bpms).dispose();
        }

        process.dispose();
        process = null;
    }

//    @Override
    public Object doInvoke(DubboBpmEvent event) throws Exception
    {
        return process.processAction(event);
    }

    protected Process getProcess()
    {
        return process;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public void setResource(String resource)
    {
        this.resource = resource;
    }

    public String getResource()
    {
        return resource;
    }
    
    public String getProcessIdField()
    {
        return processIdField;
    }

    public void setProcessIdField(String processIdField)
    {
        this.processIdField = processIdField;
    }

    public BPMS getBpms()
    {
        return bpms;
    }

    public void setBpms(BPMS bpms)
    {
        this.bpms = bpms;
    }

}
