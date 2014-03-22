/*
 * $Id: HttpMultipartMuleMessageFactory.java 23032 2011-09-26 20:40:42Z svacas $
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.transport.http;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import javax.activation.DataHandler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.DefaultMuleMessage;
import org.mule.api.MuleContext;
import org.mule.message.ds.StringDataSource;
import org.mule.transport.http.multipart.MultiPartInputStream;
import org.mule.transport.http.multipart.Part;
import org.mule.transport.http.multipart.PartDataSource;

public class HttpMultipartMuleMessageFactory extends HttpMuleMessageFactory
{
    public final static String PARTNAMEMIDAPPENDSYMBOL = "-ASIPPARTNAMEMIDSYMBOL-";

    private Collection<Part> parts;

    public HttpMultipartMuleMessageFactory(MuleContext context)
    {
        super(context);
    }

    @Override
    protected Object extractPayloadFromHttpRequest(HttpRequest httpRequest) throws IOException
    {
        Object body = null;

        if (httpRequest.getContentType().contains("multipart/form-data"))
        {
            MultiPartInputStream in = new MultiPartInputStream(httpRequest.getBody(), httpRequest.getContentType(), null);

            // We need to store this so that the headers for the part can be read
            parts = in.getParts();
            for (Part part : parts)
            {
                if (part.getName().equals("payload"))
                {
                    body = part.getInputStream();
//                    break;
                }
            }
            if (body == null)
            {
//                throw new IllegalArgumentException("no part named \"payload\" found");
                StringDataSource  stringDataSource  = new StringDataSource("\n" ,"payload");
                body = stringDataSource.getInputStream();
            }
        }
        else
        {
            body = super.extractPayloadFromHttpRequest(httpRequest);
        }

        return body;
    }

    @Override
    protected void addAttachments(DefaultMuleMessage message, Object transportMessage) throws Exception
    {
        Log log = LogFactory.getLog(this.getClass());
        int index = 0;
        if (parts != null)
        {
            try
            {
                for (Part part : parts)
                {
                    if (!part.getName().equals("payload"))
                    {
                        message.addInboundAttachment(index + PARTNAMEMIDAPPENDSYMBOL + part.getName(), new DataHandler(new PartDataSource(part)));
                        index++;
                    }
                }
            }
            finally
            {
                // Attachments are the last thing to get processed
                parts.clear();
                parts = null;
            }
        }
    }

    @Override
    protected void convertMultiPartHeaders(Map<String, Object> headers)
    {
        if (parts != null)
        {
            for (Part part : parts)
            {
                if (part.getName().equals("payload"))
                {
                    for (String name : part.getHeaderNames())
                    {
                        headers.put(name, part.getHeader(name));
                    }
                    break;
                }
            }

        }

    }

}


