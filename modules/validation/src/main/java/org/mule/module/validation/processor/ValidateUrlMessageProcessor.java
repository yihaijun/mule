/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.module.validation.processor;

import org.mule.api.MuleEvent;
import org.mule.module.validation.Validator;

import org.apache.commons.validator.routines.UrlValidator;

/**
 * If the specified <code>url</code> is not a valid one throw an exception.
 */
public class ValidateUrlMessageProcessor extends AbstractValidationMessageProcessor
{

    /**
     * URL to validate
     */
    private String url;

    /**
     * Allow two slashes in the path component of the URL
     */
    private String allowTwoSlashes;

    /**
     * Allows all validly formatted schemes to pass validation instead of supplying a
     * set of valid schemes.
     */
    private String allowAllSchemes;

    /**
     * Allow local URLs, such as http://localhost/ or http://machine/
     */
    private String allowLocalUrls;

    /**
     * Enabling this options disallows any URL fragment
     */
    private String noFragments;

    @Override
    protected Validator getValidator(MuleEvent event) throws Exception
    {
        final String evaluatedUrl = (String) this.evaluateAndTransform(this.muleContext, event, String.class,
            null, this.url);

        final boolean evaluatedAllowTwoSlashes = (Boolean) this.evaluateAndTransform(this.muleContext, event,
            Boolean.class, null, this.allowTwoSlashes, false);

        final boolean evaluatedAllSchemes = (Boolean) this.evaluateAndTransform(this.muleContext, event,
            Boolean.class, null, this.allowAllSchemes, false);

        final boolean evaluatedAllowLocalURLs = (Boolean) this.evaluateAndTransform(this.muleContext, event,
            Boolean.class, null, this.allowLocalUrls, false);

        final boolean evaluatedNoFragments = (Boolean) this.evaluateAndTransform(this.muleContext, event,
            Boolean.class, null, this.noFragments, false);

        return new Validator()
        {
            @Override
            public boolean isValid(MuleEvent event)
            {
                long options = 0;

                if (evaluatedAllSchemes)
                {
                    options |= UrlValidator.ALLOW_ALL_SCHEMES;
                }
                if (evaluatedAllowTwoSlashes)
                {
                    options |= UrlValidator.ALLOW_2_SLASHES;
                }
                if (evaluatedAllowLocalURLs)
                {
                    options |= UrlValidator.ALLOW_LOCAL_URLS;
                }
                if (evaluatedNoFragments)
                {
                    options |= UrlValidator.NO_FRAGMENTS;
                }

                UrlValidator validator = new UrlValidator(options);

                return validator.isValid(evaluatedUrl);
            }

            @Override
            public String getErrorMessage(MuleEvent event)
            {
                return String.format("%s is not a valid url", evaluatedUrl);
            }
        };
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    public void setAllowTwoSlashes(String allowTwoSlashes)
    {
        this.allowTwoSlashes = allowTwoSlashes;
    }

    public void setAllowAllSchemes(String allowAllSchemes)
    {
        this.allowAllSchemes = allowAllSchemes;
    }

    public void setAllowLocalUrls(String allowLocalURLs)
    {
        this.allowLocalUrls = allowLocalURLs;
    }

    public void setNoFragments(String noFragments)
    {
        this.noFragments = noFragments;
    }
}
