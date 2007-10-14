/**
 * BetafaceWebServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package de.offis.faint.detection.plugins.betaface.generated;

public class BetafaceWebServiceLocator extends org.apache.axis.client.Service implements de.offis.faint.detection.plugins.betaface.generated.BetafaceWebService {

/**
 * Betaface face detection and recognition service.
 */

    public BetafaceWebServiceLocator() {
    }


    public BetafaceWebServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public BetafaceWebServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for BetafaceWebServiceSoap12
    private java.lang.String BetafaceWebServiceSoap12_address = "http://www.betaface.com/webservice/service.asmx";

    public java.lang.String getBetafaceWebServiceSoap12Address() {
        return BetafaceWebServiceSoap12_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String BetafaceWebServiceSoap12WSDDServiceName = "BetafaceWebServiceSoap12";

    public java.lang.String getBetafaceWebServiceSoap12WSDDServiceName() {
        return BetafaceWebServiceSoap12WSDDServiceName;
    }

    public void setBetafaceWebServiceSoap12WSDDServiceName(java.lang.String name) {
        BetafaceWebServiceSoap12WSDDServiceName = name;
    }

    public de.offis.faint.detection.plugins.betaface.generated.BetafaceWebServiceSoap getBetafaceWebServiceSoap12() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(BetafaceWebServiceSoap12_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getBetafaceWebServiceSoap12(endpoint);
    }

    public de.offis.faint.detection.plugins.betaface.generated.BetafaceWebServiceSoap getBetafaceWebServiceSoap12(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            de.offis.faint.detection.plugins.betaface.generated.BetafaceWebServiceSoap12Stub _stub = new de.offis.faint.detection.plugins.betaface.generated.BetafaceWebServiceSoap12Stub(portAddress, this);
            _stub.setPortName(getBetafaceWebServiceSoap12WSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setBetafaceWebServiceSoap12EndpointAddress(java.lang.String address) {
        BetafaceWebServiceSoap12_address = address;
    }


    // Use to get a proxy class for BetafaceWebServiceSoap
    private java.lang.String BetafaceWebServiceSoap_address = "http://www.betaface.com/webservice/service.asmx";

    public java.lang.String getBetafaceWebServiceSoapAddress() {
        return BetafaceWebServiceSoap_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String BetafaceWebServiceSoapWSDDServiceName = "BetafaceWebServiceSoap";

    public java.lang.String getBetafaceWebServiceSoapWSDDServiceName() {
        return BetafaceWebServiceSoapWSDDServiceName;
    }

    public void setBetafaceWebServiceSoapWSDDServiceName(java.lang.String name) {
        BetafaceWebServiceSoapWSDDServiceName = name;
    }

    public de.offis.faint.detection.plugins.betaface.generated.BetafaceWebServiceSoap getBetafaceWebServiceSoap() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(BetafaceWebServiceSoap_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getBetafaceWebServiceSoap(endpoint);
    }

    public de.offis.faint.detection.plugins.betaface.generated.BetafaceWebServiceSoap getBetafaceWebServiceSoap(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            de.offis.faint.detection.plugins.betaface.generated.BetafaceWebServiceSoapStub _stub = new de.offis.faint.detection.plugins.betaface.generated.BetafaceWebServiceSoapStub(portAddress, this);
            _stub.setPortName(getBetafaceWebServiceSoapWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setBetafaceWebServiceSoapEndpointAddress(java.lang.String address) {
        BetafaceWebServiceSoap_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     * This service has multiple ports for a given interface;
     * the proxy implementation returned may be indeterminate.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (de.offis.faint.detection.plugins.betaface.generated.BetafaceWebServiceSoap.class.isAssignableFrom(serviceEndpointInterface)) {
                de.offis.faint.detection.plugins.betaface.generated.BetafaceWebServiceSoap12Stub _stub = new de.offis.faint.detection.plugins.betaface.generated.BetafaceWebServiceSoap12Stub(new java.net.URL(BetafaceWebServiceSoap12_address), this);
                _stub.setPortName(getBetafaceWebServiceSoap12WSDDServiceName());
                return _stub;
            }
            if (de.offis.faint.detection.plugins.betaface.generated.BetafaceWebServiceSoap.class.isAssignableFrom(serviceEndpointInterface)) {
                de.offis.faint.detection.plugins.betaface.generated.BetafaceWebServiceSoapStub _stub = new de.offis.faint.detection.plugins.betaface.generated.BetafaceWebServiceSoapStub(new java.net.URL(BetafaceWebServiceSoap_address), this);
                _stub.setPortName(getBetafaceWebServiceSoapWSDDServiceName());
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
        if ("BetafaceWebServiceSoap12".equals(inputPortName)) {
            return getBetafaceWebServiceSoap12();
        }
        else if ("BetafaceWebServiceSoap".equals(inputPortName)) {
            return getBetafaceWebServiceSoap();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://betaface.com/", "BetafaceWebService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://betaface.com/", "BetafaceWebServiceSoap12"));
            ports.add(new javax.xml.namespace.QName("http://betaface.com/", "BetafaceWebServiceSoap"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("BetafaceWebServiceSoap12".equals(portName)) {
            setBetafaceWebServiceSoap12EndpointAddress(address);
        }
        else 
if ("BetafaceWebServiceSoap".equals(portName)) {
            setBetafaceWebServiceSoapEndpointAddress(address);
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
