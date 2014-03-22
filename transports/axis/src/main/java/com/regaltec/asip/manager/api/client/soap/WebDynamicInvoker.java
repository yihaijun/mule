/*
 * <p>����: �й�����ۺϵ��ϵͳ��4��</p>
 * <p>����: ...</p>
 * <p>��Ȩ: Copyright (c) 2010</p>
 * <p>��˾: ��Ѷ���ͨ�ż������޹�˾</p>
 * <p>��ַ��http://www.tisson.cn/
 */
package com.regaltec.asip.manager.api.client.soap;

import java.util.HashMap;
import java.util.Vector;

//import org.apache.axis.client.Service;
import javax.wsdl.Binding;
import javax.wsdl.Port;
import javax.wsdl.Service;
import javax.wsdl.Operation;

import org.apache.axis.wsdl.gen.Parser;
import org.apache.axis.wsdl.symbolTable.ServiceEntry;

/**
 * <p>����soap�ĸ����ࡣ</p>
 * <p>�������ڣ�2010-8-30 ����10:39:57</p>
 *
 * @author yihaijun
 */
public class WebDynamicInvoker {

    private org.apache.axis.wsdl.gen.Parser wsdlParser;
    @SuppressWarnings("unchecked")
    private java.util.Map services;

    public WebDynamicInvoker(java.lang.String wsdlURL, long connectTimeout) throws java.lang.Exception {
        wsdlParser = null;
        services = null;
        wsdlParser = new Parser();
        try {
            if (connectTimeout > 0) {
                wsdlParser.setTimeout(connectTimeout);
            }
            wsdlParser.run(wsdlURL);
        } catch (Exception e) {
            wsdlParser.run(wsdlURL + "?wsdl");
        }
        services = enumSymTabEntry(org.apache.axis.wsdl.symbolTable.ServiceEntry.class);
    }

    // Add by 2010.01.04
    @SuppressWarnings("unchecked")
    public java.util.Map<String, Object> invoke(java.lang.String serviceName, java.lang.String portName,
            java.lang.String operationName, java.util.Vector parameterValues) throws java.lang.Exception {
        return invoke(serviceName, portName, operationName, parameterValues, -1);
    }

    @SuppressWarnings("unchecked")
    public java.util.Map<String, Object> invoke(java.lang.String serviceName, java.lang.String portName,
            java.lang.String operationName, java.util.Vector parameterValues, int timeout) throws java.lang.Exception {
        java.lang.StringBuffer serviceNameBuf = new StringBuffer(serviceName);
        java.lang.StringBuffer portNameBuf = new StringBuffer(portName);
        java.util.Vector<Object> inputs = new Vector<Object>();
        java.lang.String returnName = null;
        org.apache.axis.wsdl.symbolTable.ServiceEntry serviceEntry = null;
        if (serviceNameBuf.length() == 0) {
            serviceNameBuf.append((String) services.keySet().iterator().next());
        }
        serviceEntry = (org.apache.axis.wsdl.symbolTable.ServiceEntry) services.get(serviceNameBuf.toString());
        Service service = serviceEntry.getService();
        org.apache.axis.client.Service clientService = new org.apache.axis.client.Service(wsdlParser, service
                .getQName());
        org.apache.axis.wsdl.symbolTable.BindingEntry bindingEntry = getBindingEntry(serviceNameBuf, portNameBuf);
        javax.xml.rpc.Call call = clientService.createCall(javax.xml.namespace.QName.valueOf(portNameBuf.toString()),
                javax.xml.namespace.QName.valueOf(operationName));
        if (timeout > 0) {
            ((org.apache.axis.client.Call) call).setTimeout(new Integer(timeout));
        }
        Operation o = getOperation(bindingEntry, operationName);
        org.apache.axis.wsdl.symbolTable.Parameters parameters = bindingEntry.getParameters(o);
        if (parameters.returnParam != null) {
            org.apache.axis.wsdl.toJava.Utils.getXSIType(parameters.returnParam);
            javax.xml.namespace.QName returnQName = parameters.returnParam.getQName();
            returnName = returnQName.getLocalPart();
        }
        int size = parameters.list.size();
        for (int i = 0; i < size; i++) {
            org.apache.axis.wsdl.symbolTable.Parameter p = (org.apache.axis.wsdl.symbolTable.Parameter) parameters.list
                    .get(i);
            switch (p.getMode()) {
            case 1: // '\001'
                inputs.add(getParamData((org.apache.axis.client.Call) call, p, (String) parameterValues.elementAt(i)));
                break;

            case 3: // '\003'
                inputs.add(getParamData((org.apache.axis.client.Call) call, p, (String) parameterValues.elementAt(i)));
                break;
            }
        }

        java.lang.Object ret = call.invoke(inputs.toArray());
        java.util.Map outputs = call.getOutputParams();
        java.util.HashMap<String, Object> map = new HashMap<String, Object>();
        if (ret != null && returnName != null) {
            map.put(returnName, ret);
        }
        if (outputs != null) {
            java.lang.String name;
            java.lang.Object value;
            for (java.util.Iterator i = outputs.keySet().iterator(); i.hasNext(); map.put(name, value)) {
                java.lang.Object obj = i.next();
                if (obj.getClass().getName().equals("java.lang.String"))
                    name = (java.lang.String) obj;
                else
                    name = ((javax.xml.namespace.QName) obj).getLocalPart();
                value = outputs.get(obj);
            }

        }
        return map;
    }

    @SuppressWarnings("unchecked")
    public java.util.Vector<String> enumServiceNames() {
        java.util.Vector<String> v = new Vector<String>();
        java.lang.String s;
        for (java.util.Iterator i = services.keySet().iterator(); i.hasNext(); v.addElement(s))
            s = (java.lang.String) i.next();

        return v;
    }

    @SuppressWarnings("unchecked")
    public java.util.Vector<String> enumPortNames(java.lang.String serviceName) {
        java.util.Vector<String> v = new Vector<String>();
        org.apache.axis.wsdl.symbolTable.ServiceEntry serviceEntry = (org.apache.axis.wsdl.symbolTable.ServiceEntry) services
                .get(serviceName);
        java.util.Map ports = serviceEntry.getService().getPorts();
        java.lang.String s;
        for (java.util.Iterator i = ports.keySet().iterator(); i.hasNext(); v.addElement(s))
            s = (java.lang.String) i.next();

        return v;
    }

    @SuppressWarnings("unchecked")
    public java.util.Vector<String> enumOperationNames(java.lang.String serviceName, java.lang.String portName) {
        java.util.Vector<String> v = new Vector<String>();
        org.apache.axis.wsdl.symbolTable.BindingEntry entry = getBindingEntry(serviceName, portName);
        java.util.Set operations = entry.getOperations();
        java.lang.String s;
        for (java.util.Iterator i = operations.iterator(); i.hasNext(); v.addElement(s)) {
            Operation o = (Operation) i.next();
            s = o.getName();
        }

        return v;
    }

    public org.apache.axis.wsdl.symbolTable.Parameters enumParameters(java.lang.String serviceName,
            java.lang.String portName, java.lang.String operationName) {
        org.apache.axis.wsdl.symbolTable.BindingEntry entry = null;
        try {
            entry = getBindingEntry(serviceName, portName);
        } catch (Exception e) {
//            e.printStackTrace();
        }
        Operation o = getOperation(entry, operationName);
        org.apache.axis.wsdl.symbolTable.Parameters parameters = entry.getParameters(o);
        return parameters;
    }

    public java.lang.String getParameterModeString(org.apache.axis.wsdl.symbolTable.Parameter p) {
        java.lang.String ret = null;
        switch (p.getMode()) {
        case 1: // '\001'
            ret = "[IN]";
            break;

        case 3: // '\003'
            ret = "[IN, OUT]";
            break;

        case 2: // '\002'
            ret = "[OUT]";
            break;
        }
        return ret;
    }

    private org.apache.axis.wsdl.symbolTable.BindingEntry getBindingEntry(java.lang.String serviceName,
            java.lang.String portName) {
        java.lang.StringBuffer serviceNameBuf = new StringBuffer(serviceName);
        java.lang.StringBuffer portNameBuf = new StringBuffer(portName);
        return getBindingEntry(serviceNameBuf, portNameBuf);
    }

    private org.apache.axis.wsdl.symbolTable.BindingEntry getBindingEntry(java.lang.StringBuffer serviceName,
            java.lang.StringBuffer portName) {
        if (serviceName.length() == 0) {
            if (services.keySet() == null || services.keySet().isEmpty() || services.keySet().size() < 0) {
                return null;
            }
            serviceName.append((String) services.keySet().iterator().next());
        }
        org.apache.axis.wsdl.symbolTable.ServiceEntry serviceEntry = null;
        serviceEntry = (org.apache.axis.wsdl.symbolTable.ServiceEntry) services.get(serviceName.toString());
        if (portName.length() == 0) {
            if (serviceEntry.getService() == null || serviceEntry.getService().getPorts() == null
                    || serviceEntry.getService().getPorts().size() <= 0) {
                return null;
            }
            portName.append((String) serviceEntry.getService().getPorts().keySet().iterator().next());
        }
        Port port = serviceEntry.getService().getPort(portName.toString());
        Binding binding = port.getBinding();
        org.apache.axis.wsdl.symbolTable.SymbolTable table = wsdlParser.getSymbolTable();
        return table.getBindingEntry(binding.getQName());
    }

    @SuppressWarnings("unchecked")
    private Operation getOperation(org.apache.axis.wsdl.symbolTable.BindingEntry entry, java.lang.String operationName) {
        java.util.Set operations = entry.getOperations();
        for (java.util.Iterator i = operations.iterator(); i.hasNext();) {
            Operation o = (Operation) i.next();
            if (operationName.equals(o.getName()))
                return o;
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    private java.util.Map<String, Object> enumSymTabEntry(java.lang.Class<ServiceEntry> cls) {
        java.util.HashMap<String, Object> ret = new HashMap<String, Object>();
        java.util.HashMap<String, Object> map = wsdlParser.getSymbolTable().getHashMap();
        for (java.util.Iterator iterator = map.entrySet().iterator(); iterator.hasNext();) {
            java.util.Map.Entry entry = (java.util.Map.Entry) iterator.next();
            javax.xml.namespace.QName key = (javax.xml.namespace.QName) entry.getKey();
            java.util.Vector v = (java.util.Vector) entry.getValue();
            int size = v.size();
            for (int i = 0; i < size; i++) {
                org.apache.axis.wsdl.symbolTable.SymTabEntry symTabEntry = (org.apache.axis.wsdl.symbolTable.SymTabEntry) v
                        .elementAt(i);
                if (cls.isInstance(symTabEntry))
                    ret.put(key.getLocalPart(), symTabEntry);
            }

        }

        return ret;
    }

    private java.lang.Object getParamData(org.apache.axis.client.Call c, org.apache.axis.wsdl.symbolTable.Parameter p,
            String arg) throws java.lang.Exception {
        javax.xml.namespace.QName paramType = org.apache.axis.wsdl.toJava.Utils.getXSIType(p);
        org.apache.axis.wsdl.symbolTable.TypeEntry type = p.getType();
        if ((type instanceof org.apache.axis.wsdl.symbolTable.BaseType)
                && ((org.apache.axis.wsdl.symbolTable.BaseType) type).isBaseType()) {
            javax.xml.rpc.encoding.DeserializerFactory factory = c.getTypeMapping().getDeserializer(paramType);
            javax.xml.rpc.encoding.Deserializer deserializer = factory.getDeserializerAs("Axis SAX Mechanism");
            if (deserializer instanceof org.apache.axis.encoding.ser.SimpleDeserializer)
                return ((org.apache.axis.encoding.ser.SimpleDeserializer) deserializer).makeValue(arg);
        }
        throw new RuntimeException("not know how to convert '" + arg + "' into " + c);
    }
}