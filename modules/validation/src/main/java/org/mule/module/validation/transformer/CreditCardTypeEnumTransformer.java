/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.module.validation.transformer;

import org.mule.api.transformer.DiscoverableTransformer;
import org.mule.api.transformer.TransformerException;
import org.mule.module.validation.CreditCardType;
import org.mule.transformer.AbstractTransformer;
import org.mule.transformer.types.DataTypeFactory;

public class CreditCardTypeEnumTransformer extends AbstractTransformer implements DiscoverableTransformer
{

    private int weighting = DiscoverableTransformer.DEFAULT_PRIORITY_WEIGHTING;

    public CreditCardTypeEnumTransformer()
    {
        registerSourceType(DataTypeFactory.create(String.class));
        setReturnDataType(DataTypeFactory.create(CreditCardType.class));
        setName("CreditCardTypeEnumTransformer");
    }

    protected Object doTransform(Object src, String encoding) throws TransformerException
    {
        CreditCardType result = null;
        result = Enum.valueOf(CreditCardType.class, ((String) src));
        return result;
    }

    public int getPriorityWeighting()
    {
        return weighting;
    }

    public void setPriorityWeighting(int weighting)
    {
        this.weighting = weighting;
    }

}
