/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.issues;

import org.mule.api.MuleEventContext;
import org.mule.api.lifecycle.Callable;
import org.mule.module.client.MuleClient;
import org.mule.tck.junit4.FunctionalTestCase;
import org.mule.tck.junit4.rule.DynamicPort;
import org.mule.transformer.simple.ObjectToString;

import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;


public class HttpReturnsJaxbObject5531TestCase extends FunctionalTestCase
{
    private static final String ZIP_RESPONSE =
        "<?xml version='1.0' encoding='utf-8'?><soap:Envelope xmlns:soap='http://schemas.xmlsoap.org/soap/envelope/' " +
            "xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' xmlns:xsd='http://www.w3.org/2001/XMLSchema'>" +
            "<soap:Body><GetCityWeatherByZIPResponse xmlns='http://ws.cdyne.com/WeatherWS/'><GetCityWeatherByZIPResult>" +
            "<Success>true</Success><ResponseText>City Found</ResponseText><State>GA</State><City>Roswell</City>" +
            "<WeatherStationCity>Marietta</WeatherStationCity><WeatherID>1</WeatherID><Description>Thunder Storms</Description>" +
            "<Temperature>79</Temperature><RelativeHumidity>57</RelativeHumidity><Wind>S8</Wind>" +
            "<Pressure>29.91R</Pressure><Visibility /><WindChill /><Remarks /></GetCityWeatherByZIPResult>" +
            "</GetCityWeatherByZIPResponse></soap:Body></soap:Envelope>";

    @Rule
    public DynamicPort dynamicPort = new DynamicPort("port1");

    @Override
    protected String getConfigResources()
    {
        return "org/mule/issues/http-returns-jaxb-object-mule-5531-test.xml";
    }

    @Test
    public void testGetWeather() throws Exception
    {
        String testUrl = "http://localhost:" + dynamicPort.getNumber() + "/test/weather";
        MuleClient client = new MuleClient(muleContext);
        Object response = client.send(testUrl, "hello", null);
        assertNotNull(response);
        String stringResponse = (String) new ObjectToString().transform(response, "UTF-8");
        assertTrue(stringResponse.contains("<Success>true</Success>"));
    }

    public static class WeatherReport implements Callable
    {
        public Object onCall(MuleEventContext eventContext) throws Exception
        {
            return ZIP_RESPONSE;
        }
    }
}
