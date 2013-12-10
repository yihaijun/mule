/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.module.validation.config;

import org.mule.config.spring.handlers.AbstractMuleNamespaceHandler;
import org.mule.config.spring.parsers.generic.ChildDefinitionParser;
import org.mule.module.validation.processor.ValidateAllMessageProcessor;
import org.mule.module.validation.processor.ValidateCreditCardNumberMessageProcessor;
import org.mule.module.validation.processor.ValidateDateMessageProcessor;
import org.mule.module.validation.processor.ValidateDomainMessageProcessor;
import org.mule.module.validation.processor.ValidateDoubleMessageProcessor;
import org.mule.module.validation.processor.ValidateEmailMessageProcessor;
import org.mule.module.validation.processor.ValidateFalseMessageProcessor;
import org.mule.module.validation.processor.ValidateFloatMessageProcessor;
import org.mule.module.validation.processor.ValidateISBN10MessageProcessor;
import org.mule.module.validation.processor.ValidateISBN13MessageProcessor;
import org.mule.module.validation.processor.ValidateIntegerMessageProcessor;
import org.mule.module.validation.processor.ValidateIpAddressMessageProcessor;
import org.mule.module.validation.processor.ValidateLengthMessageProcessor;
import org.mule.module.validation.processor.ValidateLongMessageProcessor;
import org.mule.module.validation.processor.ValidateNotEmptyMessageProcessor;
import org.mule.module.validation.processor.ValidateNotNullMessageProcessor;
import org.mule.module.validation.processor.ValidateNullMessageProcessor;
import org.mule.module.validation.processor.ValidatePercentageMessageProcessor;
import org.mule.module.validation.processor.ValidateShortMessageProcessor;
import org.mule.module.validation.processor.ValidateTimeMessageProcessor;
import org.mule.module.validation.processor.ValidateTopLevelDomainCountryMessageProcessor;
import org.mule.module.validation.processor.ValidateTopLevelDomainMessageProcessor;
import org.mule.module.validation.processor.ValidateTrueMessageProcessor;
import org.mule.module.validation.processor.ValidateUrlMessageProcessor;
import org.mule.module.validation.processor.ValidateUsingRegexMessageProcessor;

public class ValidationNamespaceHandler extends AbstractMuleNamespaceHandler
{

    public void init()
    {
        registerMuleBeanDefinitionParser("domain", new ValidationProcessorBeanDefinitionParser(
            ValidateDomainMessageProcessor.class));
        registerMuleBeanDefinitionParser("top-level-domain", new ValidationProcessorBeanDefinitionParser(
            ValidateTopLevelDomainMessageProcessor.class));
        registerMuleBeanDefinitionParser("top-level-domain-country",
            new ValidationProcessorBeanDefinitionParser(ValidateTopLevelDomainCountryMessageProcessor.class));
        registerMuleBeanDefinitionParser("credit-card-number", new ValidationProcessorBeanDefinitionParser(
            ValidateCreditCardNumberMessageProcessor.class));
        registerMuleBeanDefinitionParser("email", new ValidationProcessorBeanDefinitionParser(
            ValidateEmailMessageProcessor.class));
        registerMuleBeanDefinitionParser("ip-address", new ValidationProcessorBeanDefinitionParser(
            ValidateIpAddressMessageProcessor.class));
        registerMuleBeanDefinitionParser("percentage", new ValidationProcessorBeanDefinitionParser(
            ValidatePercentageMessageProcessor.class));
        registerMuleBeanDefinitionParser("isbn10", new ValidationProcessorBeanDefinitionParser(
            ValidateISBN10MessageProcessor.class));
        registerMuleBeanDefinitionParser("isbn13", new ValidationProcessorBeanDefinitionParser(
            ValidateISBN13MessageProcessor.class));
        registerMuleBeanDefinitionParser("url", new ValidationProcessorBeanDefinitionParser(
            ValidateUrlMessageProcessor.class));
        registerMuleBeanDefinitionParser("time", new ValidationProcessorBeanDefinitionParser(
            ValidateTimeMessageProcessor.class));
        registerMuleBeanDefinitionParser("date", new ValidationProcessorBeanDefinitionParser(
            ValidateDateMessageProcessor.class));
        registerMuleBeanDefinitionParser("using-regex", new ValidationProcessorBeanDefinitionParser(
            ValidateUsingRegexMessageProcessor.class));
        registerMuleBeanDefinitionParser("long", new ValidationProcessorBeanDefinitionParser(
            ValidateLongMessageProcessor.class));
        registerMuleBeanDefinitionParser("integer", new ValidationProcessorBeanDefinitionParser(
            ValidateIntegerMessageProcessor.class));
        registerMuleBeanDefinitionParser("float", new ValidationProcessorBeanDefinitionParser(
            ValidateFloatMessageProcessor.class));
        registerMuleBeanDefinitionParser("double", new ValidationProcessorBeanDefinitionParser(
            ValidateDoubleMessageProcessor.class));
        registerMuleBeanDefinitionParser("short", new ValidationProcessorBeanDefinitionParser(
            ValidateShortMessageProcessor.class));
        registerMuleBeanDefinitionParser("not-empty", new ValidationProcessorBeanDefinitionParser(
            ValidateNotEmptyMessageProcessor.class));
        registerMuleBeanDefinitionParser("length", new ValidationProcessorBeanDefinitionParser(
            ValidateLengthMessageProcessor.class));
        registerMuleBeanDefinitionParser("not-null", new ValidationProcessorBeanDefinitionParser(
            ValidateNotNullMessageProcessor.class));
        registerMuleBeanDefinitionParser("null", new ValidationProcessorBeanDefinitionParser(
            ValidateNullMessageProcessor.class));
        registerMuleBeanDefinitionParser("true", new ValidationProcessorBeanDefinitionParser(
            ValidateTrueMessageProcessor.class));
        registerMuleBeanDefinitionParser("false", new ValidationProcessorBeanDefinitionParser(
            ValidateFalseMessageProcessor.class));
        registerMuleBeanDefinitionParser("all", new ChildDefinitionParser("messageProcessors",
            ValidateAllMessageProcessor.class));

    }

}
