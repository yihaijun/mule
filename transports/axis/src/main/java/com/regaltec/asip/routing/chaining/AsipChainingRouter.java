/*
 * <p>标题: 中国电信综合调度系统第4版</p>
 * <p>描述: ...</p>
 * <p>版权: Copyright (c) 2010</p>
 * <p>公司: 天讯瑞达通信技术有限公司</p>
 * <p>网址：http://www.tisson.cn/
 */
package com.regaltec.asip.routing.chaining;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.mule.api.MuleEvent;
import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.mule.api.endpoint.EndpointException;
import org.mule.api.endpoint.EndpointURI;
import org.mule.api.endpoint.ImmutableEndpoint;
import org.mule.api.endpoint.OutboundEndpoint;
import org.mule.api.expression.ExpressionManager;
import org.mule.api.lifecycle.InitialisationException;
import org.mule.api.processor.MessageProcessor;
import org.mule.api.routing.CouldNotRouteOutboundMessageException;
import org.mule.api.routing.RoutePathNotFoundException;
import org.mule.api.routing.RoutingException;
import org.mule.api.routing.TransformingMatchable;
import org.mule.api.routing.filter.Filter;
import org.mule.api.transformer.Transformer;
import org.mule.config.i18n.CoreMessages;
import org.mule.endpoint.DynamicURIOutboundEndpoint;
import org.mule.endpoint.MuleEndpointURI;
import org.mule.routing.outbound.AbstractOutboundRouter;
import org.mule.transport.NullPayload;
import org.mule.util.TemplateParser;

/**
 * <p>概述该类作用，请详细描述。</p>
 * <p>创建日期：2010-12-24 下午06:28:03</p>
 *
 * @author yihaijun
 */
public class AsipChainingRouter extends AbstractOutboundRouter implements TransformingMatchable{
    protected ExpressionManager expressionManager;

    private List<Transformer> transformers = new LinkedList<Transformer>();

    private Filter filter;

    private boolean useTemplates = true;
    
    // We used Square templates as they can exist as part of an URI.
    private TemplateParser parser = TemplateParser.createSquareBracesStyleParser();

//    private String debugParam = "";
    private MuleEvent event = null;

    @Override
    public void initialise() throws InitialisationException
    {
        super.initialise();
        expressionManager = muleContext.getExpressionManager();
        if (routes == null || routes.size() == 0)
        {
            throw new InitialisationException(CoreMessages.objectIsNull("targets"), this);
        }
    }

    @Override
    public MuleEvent route(MuleEvent event) throws RoutingException {
        this.event = event;
        MuleMessage message = event.getMessage();

        MuleEvent resultToReturn = null;
        if (routes == null || routes.size() == 0) {
            throw new RoutePathNotFoundException(CoreMessages.noEndpointsForRouter(), event, null);
        }

        final int endpointsCount = routes.size();
        if (logger.isDebugEnabled()) {
            logger.debug("About to chain " + endpointsCount + " targets.");
        }

//        debugParam = "<DEBUGAsip>"+message.getInboundProperty("MULE_ORIGINATING_ENDPOINT")+"</DEBUGAsip></data_info>"; // 暂时性处理

        // need that ref for an error message
        MessageProcessor endpoint = null;
        try {
            MuleMessage intermediaryResult = message;
            String param = "";
            for (int i = 0; i < endpointsCount; i++) {
//                if (i == 0) {
//                    if (message.getPayload() instanceof String) {
//                        param = (String) message.getPayload();
//                        if (param.indexOf(debugParam) < 0) {
//                            //如果不按规定编写第一个调试outbound-endpoint,那第一个outbound-endpoint就被勿略了!
//                            continue;
//                        }
//                    }
//                }else if (i==1 && param.indexOf(debugParam)>0 ) {
//                    //param有值时可以肯定第一个outbound-endpoint已经路过,再包含debugParam,那第一个调试outbound-endpoint就已经处理过
//                    //如果不按规定编写第一个调试outbound-endpoint,那就返回component或inbound-endpoint直接过来的值了!
//                    break;
//                }
                endpoint = getRoute(i,event,intermediaryResult);

                // if it's not the last endpoint in the chain,
                // enforce the synchronous call, otherwise we lose response
                @SuppressWarnings("unused")
                boolean lastEndpointInChain = (i == endpointsCount - 1);

                if (logger.isDebugEnabled()) {
                    logger.debug("Sending Chained message '" + i + "': "
                            + (intermediaryResult == null ? "null" : intermediaryResult.toString()));
                }

                MuleEvent event1 = sendRequest(event, intermediaryResult, endpoint, true);
                resultToReturn = event1;
                MuleMessage localResult = event1 == null ? null : event1.getMessage();
                // Need to propagate correlation info and replyTo, because there
                // is no guarantee that an external system will preserve headers
                // (in fact most will not)
                if (localResult != null &&
                // null result can be wrapped in a NullPayload
                        localResult.getPayload() != NullPayload.getInstance() && intermediaryResult != null) {
                    processIntermediaryResult(localResult, intermediaryResult);

                    //Add by yihaijun at 2012-05-16
                    message.setPayload(localResult.getPayload());
                }
                intermediaryResult = localResult;

                if (logger.isDebugEnabled()) {
                    logger.debug("Received Chain result '" + i + "': "
                            + (intermediaryResult != null ? intermediaryResult.toString() : "null"));
                }

            }

        } catch (MuleException e) {
            throw new CouldNotRouteOutboundMessageException(event, endpoint, e);
        }
        return resultToReturn;
    }

    /**
     * Process intermediary result of invocation. The method will be invoked
     * <strong>only</strong> if both local and intermediary results are available
     * (not null).
     * <p/>
     * Overriding methods must call <code>super(localResult, intermediaryResult)</code>,
     * unless they are modifying the correlation workflow (if you know what that means,
     * you know what you are doing and when to do it).
     * <p/>
     * Default implementation propagates
     * the following properties:
     * <ul>
     * <li>correlationId
     * <li>correlationSequence
     * <li>correlationGroupSize
     * <li>replyTo
     * </ul>
     * @param localResult result of the last endpoint invocation
     * @param intermediaryResult the message travelling across the targets
     */
    protected void processIntermediaryResult(MuleMessage localResult, MuleMessage intermediaryResult)
    {
        localResult.setCorrelationId(intermediaryResult.getCorrelationId());
        localResult.setCorrelationSequence(intermediaryResult.getCorrelationSequence());
        localResult.setCorrelationGroupSize(intermediaryResult.getCorrelationGroupSize());
        localResult.setReplyTo(intermediaryResult.getReplyTo());
    }

    public Filter getFilter()
    {
        return filter;
    }

    public void setFilter(Filter filter)
    {
        this.filter = filter;
    }

    public boolean isMatch(MuleMessage message) throws RoutingException
    {
        if (getFilter() == null)
        {
            return true;
        }
        
        try
        {
            message.applyTransformers(null, transformers);
        }
        catch (MuleException e)
        {
            throw new RoutingException(CoreMessages.transformFailedBeforeFilter(), event, 
                routes.get(0), e);
        }
        
        boolean match = getFilter().accept(message); 
        return  match;
    }

    public List<Transformer> getTransformers()
    {
        return transformers;
    }

    public void setTransformers(List<Transformer> transformers)
    {
        this.transformers = transformers;
    }

    @Override
    public void addRoute(MessageProcessor target) throws MuleException
    {
        if (!useTemplates)
        {
            if (target instanceof ImmutableEndpoint)
            {
                ImmutableEndpoint endpoint = (ImmutableEndpoint) target;
                if (parser.isContainsTemplate(endpoint.getEndpointURI().toString()))
                {
                    useTemplates = true;
                }
            }
        }
        super.addRoute(target);
    }

    /**
     * Will Return the target at the given index and will resolve any template tags
     * on the Endpoint URI if necessary
     * 
     * @param index the index of the endpoint to get
     * @param message the current message. This is required if template matching is
     *            being used
     * @return the endpoint at the index, with any template tags resolved
     * @throws CouldNotRouteOutboundMessageException if the template causs the
     *             endpoint to become illegal or malformed
     */
    @SuppressWarnings("unchecked")
    public MessageProcessor getRoute(int index, MuleEvent event,MuleMessage message)
        throws CouldNotRouteOutboundMessageException
    {
        if (!useTemplates)
        {
            return routes.get(index);
        }
        else
        {
            MessageProcessor mp = routes.get(index);
            if (!(mp instanceof ImmutableEndpoint))
            {
                return routes.get(index);
            }
            OutboundEndpoint ep = (OutboundEndpoint) mp;
            String uri = ep.getAddress();

            if (logger.isDebugEnabled())
            {
                logger.debug("Uri before parsing is: " + uri);
            }

            Map props = new HashMap();
            // Also add the endpoint properties so that users can set fallback values
            // when the property is not set on the event
            props.putAll(ep.getProperties());
            for (String propertyKey : message.getOutboundPropertyNames())
            {
                Object value = message.getOutboundProperty(propertyKey);
                props.put(propertyKey, value);
            }

            propagateMagicProperties(message, message);

            if (!parser.isContainsTemplate(uri))
            {
                logger.debug("Uri does not contain template(s)");
                return ep;
            }
            else
            {

                String newUriString = parser.parse(props, uri);
                if (parser.isContainsTemplate(newUriString))
                {
                    newUriString = this.getMuleContext().getExpressionManager().parse(newUriString, message, true);
                }
                if (logger.isDebugEnabled())
                {
                    logger.debug("Uri after parsing is: " + uri);
                }
                try
                {
                    EndpointURI newUri = new MuleEndpointURI(newUriString, muleContext);
                    EndpointURI endpointURI = ep.getEndpointURI();
                    if (endpointURI != null && !newUri.getScheme().equalsIgnoreCase(endpointURI.getScheme()))
                    {
                        throw new CouldNotRouteOutboundMessageException(
                            CoreMessages.schemeCannotChangeForRouter(ep.getEndpointURI().getScheme(),
                                newUri.getScheme()), event, ep);
                    }

                    return new DynamicURIOutboundEndpoint(ep, newUri);
                }
                catch (EndpointException e)
                {
                    throw new CouldNotRouteOutboundMessageException(
                        CoreMessages.templateCausedMalformedEndpoint(uri, newUriString), event, ep, e);
                }
            }
        }
    }

    public boolean isUseTemplates()
    {
        return useTemplates;
    }

    public void setUseTemplates(boolean useTemplates)
    {
        this.useTemplates = useTemplates;
    }
    
    public boolean isTransformBeforeMatch()
    {
        return !transformers.isEmpty();
    }
}
