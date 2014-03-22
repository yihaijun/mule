/**
 * AsipServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.regaltec.asip.transport.soap.stub.ida40;

public class AsipServiceLocator extends org.apache.axis.client.Service implements com.regaltec.asip.transport.soap.stub.ida40.AsipService {

    public AsipServiceLocator() {
    }


    public AsipServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public AsipServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for AsipServiceSOAP
    private java.lang.String AsipServiceSOAP_address = "http://132.228.169.145:80/AsipService";

    public java.lang.String getAsipServiceSOAPAddress() {
        return AsipServiceSOAP_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String AsipServiceSOAPWSDDServiceName = "AsipServiceSOAP";

    public java.lang.String getAsipServiceSOAPWSDDServiceName() {
        return AsipServiceSOAPWSDDServiceName;
    }

    public void setAsipServiceSOAPWSDDServiceName(java.lang.String name) {
        AsipServiceSOAPWSDDServiceName = name;
    }

    public com.regaltec.asip.transport.soap.stub.ida40.AsipServicePort getAsipServiceSOAP() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(AsipServiceSOAP_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getAsipServiceSOAP(endpoint);
    }

    public com.regaltec.asip.transport.soap.stub.ida40.AsipServicePort getAsipServiceSOAP(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            com.regaltec.asip.transport.soap.stub.ida40.AsipServiceSOAPStub _stub = new com.regaltec.asip.transport.soap.stub.ida40.AsipServiceSOAPStub(portAddress, this);
            _stub.setPortName(getAsipServiceSOAPWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setAsipServiceSOAPEndpointAddress(java.lang.String address) {
        AsipServiceSOAP_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (com.regaltec.asip.transport.soap.stub.ida40.AsipServicePort.class.isAssignableFrom(serviceEndpointInterface)) {
                com.regaltec.asip.transport.soap.stub.ida40.AsipServiceSOAPStub _stub = new com.regaltec.asip.transport.soap.stub.ida40.AsipServiceSOAPStub(new java.net.URL(AsipServiceSOAP_address), this);
                _stub.setPortName(getAsipServiceSOAPWSDDServiceName());
                return _stub;
            }
        }
        catch (java.lang.Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        java.lang.String inputPortName = portName.getLocalPart();
        if ("AsipServiceSOAP".equals(inputPortName)) {
            return getAsipServiceSOAP();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://www.regaltec.com.cn/ida40", "AsipService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://www.regaltec.com.cn/ida40", "AsipServiceSOAP"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("AsipServiceSOAP".equals(portName)) {
            setAsipServiceSOAPEndpointAddress(address);
        }
        else 
{ // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(javax.xml.namespace.QName portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}
