/**
 * BetafaceFaceFeature.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package de.offis.faint.detection.plugins.betaface.generated;

public class BetafaceFaceFeature  extends de.offis.faint.detection.plugins.betaface.generated.BetafaceRect  implements java.io.Serializable {
    private de.offis.faint.detection.plugins.betaface.generated.FeatureType type;

    public BetafaceFaceFeature() {
    }

    public BetafaceFaceFeature(
           double x,
           double y,
           double width,
           double height,
           double angleDegree,
           de.offis.faint.detection.plugins.betaface.generated.FeatureType type) {
        super(
            x,
            y,
            width,
            height,
            angleDegree);
        this.type = type;
    }


    /**
     * Gets the type value for this BetafaceFaceFeature.
     * 
     * @return type
     */
    public de.offis.faint.detection.plugins.betaface.generated.FeatureType getType() {
        return type;
    }


    /**
     * Sets the type value for this BetafaceFaceFeature.
     * 
     * @param type
     */
    public void setType(de.offis.faint.detection.plugins.betaface.generated.FeatureType type) {
        this.type = type;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof BetafaceFaceFeature)) return false;
        BetafaceFaceFeature other = (BetafaceFaceFeature) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.type==null && other.getType()==null) || 
             (this.type!=null &&
              this.type.equals(other.getType())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = super.hashCode();
        if (getType() != null) {
            _hashCode += getType().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(BetafaceFaceFeature.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://betaface.com/", "BetafaceFaceFeature"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("type");
        elemField.setXmlName(new javax.xml.namespace.QName("http://betaface.com/", "Type"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://betaface.com/", "FeatureType"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}
