/**
 * AsipService_ServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.regaltec.asip.manager.api.client.soap.stub;

public class AsipService_ServiceLocator extends org.apache.axis.client.Service implements com.regaltec.asip.manager.api.client.soap.stub.AsipService_Service {

    public AsipService_ServiceLocator() {
    }


    public AsipService_ServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public AsipService_ServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for AsipService
    private java.lang.String AsipService_address = "http://132.228.169.175:8000/asip/services/AsipService";

    public java.lang.String getAsipServiceAddress() {
        return AsipService_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String AsipServiceWSDDServiceName = "AsipService";

    public java.lang.String getAsipServiceWSDDServiceName() {
        return AsipServiceWSDDServiceName;
    }

    public void setAsipServiceWSDDServiceName(java.lang.String name) {
        AsipServiceWSDDServiceName = name;
    }

    public com.regaltec.asip.manager.api.client.soap.stub.AsipService_PortType getAsipService() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(AsipService_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getAsipService(endpoint);
    }

    public com.regaltec.asip.manager.api.client.soap.stub.AsipService_PortType getAsipService(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            com.regaltec.asip.manager.api.client.soap.stub.AsipServiceSoapBindingStub _stub = new com.regaltec.asip.manager.api.client.soap.stub.AsipServiceSoapBindingStub(portAddress, this);
            _stub.setPortName(getAsipServiceWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setAsipServiceEndpointAddress(java.lang.String address) {
        AsipService_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (com.regaltec.asip.manager.api.client.soap.stub.AsipService_PortType.class.isAssignableFrom(serviceEndpointInterface)) {
                com.regaltec.asip.manager.api.client.soap.stub.AsipServiceSoapBindingStub _stub = new com.regaltec.asip.manager.api.client.soap.stub.AsipServiceSoapBindingStub(new java.net.URL(AsipService_address), this);
                _stub.setPortName(getAsipServiceWSDDServiceName());
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
        if ("AsipService".equals(inputPortName)) {
            return getAsipService();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://service.asip.regaltec.com/", "AsipService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://service.asip.regaltec.com/", "AsipService"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("AsipService".equals(portName)) {
            setAsipServiceEndpointAddress(address);
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
