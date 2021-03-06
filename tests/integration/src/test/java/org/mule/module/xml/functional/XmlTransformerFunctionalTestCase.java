/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.xml.functional;

import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.mule.module.client.MuleClient;

import org.custommonkey.xmlunit.XMLAssert;
import org.junit.Test;
import org.w3c.dom.Document;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class XmlTransformerFunctionalTestCase extends AbstractXmlFunctionalTestCase
{

    public static final String SIMPLE_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<parent><child name=\"poot\"/></parent>";
    public static final String CHILDLESS_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<parent/>";
    public static final String SERIALIZED = "<org.mule.module.xml.functional.XmlTransformerFunctionalTestCase_-Parent>\n" +
            "  <child/>\n" +
            "</org.mule.module.xml.functional.XmlTransformerFunctionalTestCase_-Parent>";

    @Override
    protected String getConfigResources()
    {
        return "org/mule/module/xml/xml-transformer-functional-test.xml";
    }

    protected MuleClient sendXml() throws MuleException
    {
        MuleClient client = new MuleClient(muleContext);
        client.dispatch("xml-in", SIMPLE_XML, null);
        return client;
    }

    protected MuleClient sendObject() throws MuleException
    {
        return sendObject("object-in");
    }

    protected MuleClient sendObject(String endpoint) throws MuleException
    {
        MuleClient client = new MuleClient(muleContext);
        client.dispatch(endpoint, new Parent(new Child()), null);
        return client;
    }

    @Test
    public void testXmlOut() throws Exception
    {
        String xml = (String) request(sendXml(), "xml-out", String.class);
        XMLAssert.assertXMLEqual(SIMPLE_XML, xml);
    }

    @Test
    public void testXmlDomOut() throws MuleException
    {
        Document dom = (Document) request(sendXml(), "xml-dom-out", Document.class);
        assertEquals("parent", dom.getDocumentElement().getLocalName());
    }

    @Test
    public void testXmlXsltOut() throws Exception
    {
        String xml = (String) request(sendXml(), "xml-xslt-out-string", String.class);
        XMLAssert.assertXMLEqual(CHILDLESS_XML, xml);
    }

    @Test
    public void testDomXmlOut() throws Exception
    {
        String xml = (String) request(sendXml(), "dom-xml-out", String.class);
        XMLAssert.assertXMLEqual(SIMPLE_XML, xml);
    }

    @Test
    public void testObjectOut() throws Exception
    {
        request(sendObject(), "object-out", Parent.class);
    }

    @Test
    public void testObjectXmlOut() throws Exception
    {
        String xml = (String) request(sendObject(), "object-xml-out", String.class);
        System.out.println(xml);
        XMLAssert.assertXMLEqual(SERIALIZED, xml);
    }

    // MULE-5038
    //@Test
    //public void testXmlObjectOut() throws MuleException
    //{
    //    request(sendObject(), "xml-object-out", Parent.class);
    //}

    @Test
    public void testXmlJxpathOut() throws Exception
    {
        String xml = (String) request(sendXml(), "xml-jxpath-out", String.class);
        assertEquals("1", xml);
    }

    protected Object request(MuleClient client, String endpoint, Class clazz) throws MuleException
    {
        MuleMessage message = client.request(endpoint, TIMEOUT);
        assertNotNull(message);
        assertNotNull(message.getPayload());
        assertTrue(message.getPayload().getClass().getName(), clazz.isAssignableFrom(message.getPayload().getClass()));
        return message.getPayload();
    }

    public static class Parent
    {
        private Child child;

        public Parent()
        {
            this(null);
        }

        public Parent(Child child)
        {
            setChild(child);
        }

        public Child getChild()
        {
            return child;
        }

        public void setChild(Child child)
        {
            this.child = child;
        }
    }

    public static class Child
    {
        // nothing here
    }

}
