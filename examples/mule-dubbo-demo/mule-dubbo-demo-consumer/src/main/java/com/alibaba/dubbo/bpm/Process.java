/**
 * 
 */
package com.alibaba.dubbo.bpm;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alibaba.dubbo.bpm.api.DefaultDubboBpmMessage;
import com.alibaba.dubbo.bpm.api.Disposable;
import com.alibaba.dubbo.bpm.api.DubboBpmEvent;
import com.alibaba.dubbo.bpm.api.DubboBpmMessage;
import com.alibaba.dubbo.bpm.api.MessageExchangePattern;
import com.alibaba.dubbo.bpm.api.NullPayload;
import com.alibaba.dubbo.bpm.api.lifecycle.Initialisable;
import com.alibaba.dubbo.bpm.api.lifecycle.InitialisationException;

/**
 * @author yihaijun
 *
 */
/**
 * A business process definition.
 */
public class Process implements Initialisable, Disposable, MessageService
{
    /** The underlying BPMS */
    private final BPMS bpms;

    /** The logical name of the process.  This is used to look up the running process instance from the BPMS. */
    private final String name;

    /** The resource containing the process definition.  This will be used to deploy the process to the BPMS. */
    private final String resource;

    /** This field will be used to correlate messages with processes. */
    protected final String processIdField;

//    protected MuleContext muleContext;

    /** Needed for exception handling. */
//    private FlowConstruct flowConstruct;

    public static final String BPM_PROPERTY_PREFIX = "BPM_";
    public static final String PROPERTY_PREFIX = "DUBBO_"; // MuleProperties.PROPERTY_PREFIX
    
    public static final String PROPERTY_ENDPOINT = 
        PROPERTY_PREFIX + BPM_PROPERTY_PREFIX + "ENDPOINT";
    public static final String PROPERTY_PROCESS_TYPE = 
        PROPERTY_PREFIX + BPM_PROPERTY_PREFIX + "PROCESS_TYPE";
    public static final String PROPERTY_PROCESS_ID = 
        PROPERTY_PREFIX + BPM_PROPERTY_PREFIX + "PROCESS_ID";
    public static final String PROPERTY_ACTION = 
        PROPERTY_PREFIX + BPM_PROPERTY_PREFIX + "ACTION";
    public static final String PROPERTY_TRANSITION = 
        PROPERTY_PREFIX + BPM_PROPERTY_PREFIX + "TRANSITION";
    public static final String PROPERTY_PROCESS_STARTED = 
        PROPERTY_PREFIX + BPM_PROPERTY_PREFIX + "STARTED";
    
    public static final String ACTION_START = "start";
    public static final String ACTION_ADVANCE = "advance";
    public static final String ACTION_UPDATE = "update";
    public static final String ACTION_ABORT = "abort";
    
    public static final String PROCESS_VARIABLE_INCOMING = "incoming";
    public static final String PROCESS_VARIABLE_INCOMING_SOURCE = "incomingSource";
    public static final String PROCESS_VARIABLE_DATA = "data";

//    private final EndpointCache endpointCache;
    
    protected transient Log logger = LogFactory.getLog(getClass());

//    public Process(BPMS bpms, String name, String resource, FlowConstruct flowConstruct, MuleContext muleContext)
//    {
//        this(bpms, name, resource, null, flowConstruct, muleContext);
//    }
	  public Process(BPMS bpms, String name, String resource)
	  {
	      this(bpms, name, resource, null);
	  }

//    public Process(BPMS bpms, String name, String resource, String processIdField, FlowConstruct flowConstruct, MuleContext muleContext)
    public Process(BPMS bpms, String name, String resource, String processIdField)
    {
        this.bpms = bpms;
        this.name = name;
        this.resource = resource;
        this.processIdField = processIdField;
//        this.flowConstruct = flowConstruct;
//        this.muleContext = muleContext;
//        this.endpointCache = new SimpleEndpointCache(muleContext);
    }

    public void initialise() throws InitialisationException
    {
        try
        {
            bpms.deployProcess(resource);
        }
        catch (Exception e)
        {
            throw new InitialisationException(e, this);
        }
    }

	public void dispose()
	{
	    try
	    {
	        bpms.undeployProcess(resource);
	    }
	    catch (Exception e)
	    {
	        logger.warn(e.getMessage());
	    }
	}


    public String getProcessIdField()
    {
        return processIdField;
    }

    public BPMS getBpms()
    {
        return bpms;
    }

    public String getResource()
    {
        return resource;
    }

    public String getName()
    {
        return name;
    }

    public DubboBpmMessage generateMessage(String endpoint,
			Object payload, Map messageProperties,
			MessageExchangePattern mep) throws Exception {
      DubboBpmMessage message;
      if (payload instanceof DubboBpmMessage)
      {
          message = (DubboBpmMessage) payload;
      }
      else
      {
//          message = new DefaultDubboBpmMessage(payload, muleContext);
    	  message = new DefaultDubboBpmMessage(payload);
      }
      message.getMessageProperties().putAll(messageProperties);

      // Use an endpoint cache to prevent memory leaks (see MULE-5422)
//      OutboundEndpoint ep = endpointCache.getOutboundEndpoint(endpoint, exchangePattern, null);
//      DefaultMuleEvent event = new DefaultMuleEvent(message, ep, new DefaultMuleSession(flowConstruct, muleContext));
//      RequestContext.setEvent(event);
//
//      // Set correlation properties in SESSION scope so that they get propagated to response messages.
//      if (messageProperties.get(PROPERTY_PROCESS_TYPE) != null)
//      {
//          event.getMessage().setSessionProperty(PROPERTY_PROCESS_TYPE, messageProperties.get(PROPERTY_PROCESS_TYPE));
//      }
//      if (messageProperties.get(PROPERTY_PROCESS_ID) != null)
//      {
//          event.getMessage().setSessionProperty(PROPERTY_PROCESS_ID, messageProperties.get(PROPERTY_PROCESS_ID));
//      }
//      
      DubboBpmEvent resultEvent = new DubboBpmEvent();//= ep.process(event);
      resultEvent.setDubboBpmMessage(message);
      
      DubboBpmMessage response = null;
      if (resultEvent != null)
      {
          response = resultEvent.getDubboBpmMessage();
//          if (response.getExceptionPayload() != null)
//          {
//              throw new DispatchException(MessageFactory.createStaticMessage("Unable to send or route message"), event, ep, response.getExceptionPayload().getRootException());
//          }
      }        
      return response;
  }
	
//  protected Object processAction(MuleEvent event) throws Exception
  protected Object processAction(DubboBpmEvent event) throws Exception{
    // An object representing the new state of the process
    Object process;

    // Create a map of process variables based on the message properties.
    Map processVariables = new HashMap();
    if (event != null)
    {
    	processVariables.putAll(event.getDubboBpmMessage().getProcessVariables());

    	Object payload = event.getDubboBpmMessage().getPayload();
        if (payload != null )
        {
            // Store the message's payload as a process variable.
            processVariables.put(PROCESS_VARIABLE_INCOMING, payload);

            // Store the endpoint on which the message was received as a process variable.
            String originatingEndpoint = event.getDubboBpmMessage().getOriginatingEndpoint();
            if (StringUtils.isNotEmpty(originatingEndpoint))
            {
                processVariables.put(PROCESS_VARIABLE_INCOMING_SOURCE, originatingEndpoint);
            }
        }
    }

    String processIdField = getProcessIdField();
    if (StringUtils.isEmpty(processIdField))
    {
        processIdField = PROPERTY_PROCESS_ID;
    }

    Object processId;
    processId = event.getDubboBpmMessage().getProcessVariables().get(processIdField);
    processVariables.remove(processIdField);

    // Default action is "advance"
    String action = ACTION_ADVANCE;
    if(event.getDubboBpmMessage().getProcessVariables().get(PROPERTY_ACTION) != null){
	    action = event.getDubboBpmMessage().getProcessVariables().get(PROPERTY_ACTION).toString();
	    if(action == null || action.equals("")){
	    	action = ACTION_ADVANCE;
	    }
    }
    processVariables.remove(PROPERTY_ACTION);

    Object transition = event.getDubboBpmMessage().getProcessVariables().get(PROPERTY_TRANSITION);
    processVariables.remove(PROPERTY_TRANSITION);

    // //////////////////////////////////////////////////////////////////////

    logger.debug("Message received: payload = " + event.getDubboBpmMessage().getPayload().getClass().getName() + " processType = " + name + " processId = " + processId + " action = " + action);
    
    // Start a new process.
    if (processId == null || action.equals(ACTION_START))
    {
        process = getBpms().startProcess(name, transition, processVariables);
        if ((process != null) && logger.isInfoEnabled())
        {
            logger.info("New process started, ID = " + getBpms().getId(process));
        }
    }

    // Don't advance the process, just update the process variables.
    else if (action.equals(ACTION_UPDATE))
    {
        if (processId != null)
        {
            process = getBpms().updateProcess(processId, processVariables);
            if ((process != null) && logger.isInfoEnabled())
            {
                logger.info("Process variables updated, ID = " + getBpms().getId(process));
            }
        }
        else
        {
            throw new IllegalArgumentException("Process ID is missing, cannot update process.");
        }
    }

    // Abort the running process (end abnormally).
    else if (action.equals(ACTION_ABORT))
    {
        if (processId != null)
        {
            getBpms().abortProcess(processId);
            process = NullPayload.getInstance();
            logger.info("Process aborted, ID = " + processId);
        }
        else
        {
            throw new IllegalArgumentException("Process ID is missing, cannot abort process.");
        }
    }

    // Advance the already-running process one step.
    else
    {
        if (processId != null)
        {
            process = getBpms().advanceProcess(processId, transition, processVariables);
            if ((process != null) && logger.isInfoEnabled())
            {
                logger.info("Process advanced, ID = " + getBpms().getId(process)
                                + ", new state = " + getBpms().getState(process));
            }
        }
        else
        {
            throw new IllegalArgumentException("Process ID is missing, cannot advance process.");
        }
    }

    return process;
  }
}
