
package com.regaltec.asip.transport.soap.cxf.ida40;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for call complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="call">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="serviceName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="serviceClass" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="parameterXML" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="contextName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "call", propOrder = {
    "serviceName",
    "serviceClass",
    "parameterXML",
    "contextName"
})
public class Call {

    @XmlElement(required = true)
    protected String serviceName;
    @XmlElement(required = true)
    protected String serviceClass;
    @XmlElement(required = true)
    protected String parameterXML;
    @XmlElement(required = true)
    protected String contextName;

    /**
     * Gets the value of the serviceName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getServiceName() {
        return serviceName;
    }

    /**
     * Sets the value of the serviceName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setServiceName(String value) {
        this.serviceName = value;
    }

    /**
     * Gets the value of the serviceClass property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getServiceClass() {
        return serviceClass;
    }

    /**
     * Sets the value of the serviceClass property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setServiceClass(String value) {
        this.serviceClass = value;
    }

    /**
     * Gets the value of the parameterXML property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getParameterXML() {
        return parameterXML;
    }

    /**
     * Sets the value of the parameterXML property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setParameterXML(String value) {
        this.parameterXML = value;
    }

    /**
     * Gets the value of the contextName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getContextName() {
        return contextName;
    }

    /**
     * Sets the value of the contextName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setContextName(String value) {
        this.contextName = value;
    }

}
