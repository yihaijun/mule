/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.module.validation.transformer;

import org.mule.api.transformer.DiscoverableTransformer;
import org.mule.api.transformer.TransformerException;
import org.mule.module.validation.Locale;
import org.mule.transformer.AbstractTransformer;
import org.mule.transformer.types.DataTypeFactory;

public class LocaleEnumTransformer extends AbstractTransformer implements DiscoverableTransformer
{

    private int weighting = DiscoverableTransformer.DEFAULT_PRIORITY_WEIGHTING;

    public LocaleEnumTransformer()
    {
        registerSourceType(DataTypeFactory.create(String.class));
        setReturnDataType(DataTypeFactory.create(Locale.class));
        setName("LocaleEnumTransformer");
    }

    protected Object doTransform(Object src, String encoding) throws TransformerException
    {
        Locale result = null;
        result = Enum.valueOf(Locale.class, ((String) src));
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
