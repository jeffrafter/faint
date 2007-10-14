/**
 * BetafaceWebService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package de.offis.faint.detection.plugins.betaface.generated;

public interface BetafaceWebService extends javax.xml.rpc.Service {

/**
 * Betaface face detection and recognition service.
 */
    public java.lang.String getBetafaceWebServiceSoap12Address();

    public de.offis.faint.detection.plugins.betaface.generated.BetafaceWebServiceSoap getBetafaceWebServiceSoap12() throws javax.xml.rpc.ServiceException;

    public de.offis.faint.detection.plugins.betaface.generated.BetafaceWebServiceSoap getBetafaceWebServiceSoap12(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
    public java.lang.String getBetafaceWebServiceSoapAddress();

    public de.offis.faint.detection.plugins.betaface.generated.BetafaceWebServiceSoap getBetafaceWebServiceSoap() throws javax.xml.rpc.ServiceException;

    public de.offis.faint.detection.plugins.betaface.generated.BetafaceWebServiceSoap getBetafaceWebServiceSoap(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}
