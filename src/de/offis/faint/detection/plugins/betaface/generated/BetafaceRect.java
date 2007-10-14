/**
 * BetafaceRect.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package de.offis.faint.detection.plugins.betaface.generated;

public class BetafaceRect  implements java.io.Serializable {
    private double x;

    private double y;

    private double width;

    private double height;

    private double angleDegree;

    public BetafaceRect() {
    }

    public BetafaceRect(
           double x,
           double y,
           double width,
           double height,
           double angleDegree) {
           this.x = x;
           this.y = y;
           this.width = width;
           this.height = height;
           this.angleDegree = angleDegree;
    }


    /**
     * Gets the x value for this BetafaceRect.
     * 
     * @return x
     */
    public double getX() {
        return x;
    }


    /**
     * Sets the x value for this BetafaceRect.
     * 
     * @param x
     */
    public void setX(double x) {
        this.x = x;
    }


    /**
     * Gets the y value for this BetafaceRect.
     * 
     * @return y
     */
    public double getY() {
        return y;
    }


    /**
     * Sets the y value for this BetafaceRect.
     * 
     * @param y
     */
    public void setY(double y) {
        this.y = y;
    }


    /**
     * Gets the width value for this BetafaceRect.
     * 
     * @return width
     */
    public double getWidth() {
        return width;
    }


    /**
     * Sets the width value for this BetafaceRect.
     * 
     * @param width
     */
    public void setWidth(double width) {
        this.width = width;
    }


    /**
     * Gets the height value for this BetafaceRect.
     * 
     * @return height
     */
    public double getHeight() {
        return height;
    }


    /**
     * Sets the height value for this BetafaceRect.
     * 
     * @param height
     */
    public void setHeight(double height) {
        this.height = height;
    }


    /**
     * Gets the angleDegree value for this BetafaceRect.
     * 
     * @return angleDegree
     */
    public double getAngleDegree() {
        return angleDegree;
    }


    /**
     * Sets the angleDegree value for this BetafaceRect.
     * 
     * @param angleDegree
     */
    public void setAngleDegree(double angleDegree) {
        this.angleDegree = angleDegree;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof BetafaceRect)) return false;
        BetafaceRect other = (BetafaceRect) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this.x == other.getX() &&
            this.y == other.getY() &&
            this.width == other.getWidth() &&
            this.height == other.getHeight() &&
            this.angleDegree == other.getAngleDegree();
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        _hashCode += new Double(getX()).hashCode();
        _hashCode += new Double(getY()).hashCode();
        _hashCode += new Double(getWidth()).hashCode();
        _hashCode += new Double(getHeight()).hashCode();
        _hashCode += new Double(getAngleDegree()).hashCode();
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(BetafaceRect.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://betaface.com/", "BetafaceRect"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("x");
        elemField.setXmlName(new javax.xml.namespace.QName("http://betaface.com/", "X"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "double"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("y");
        elemField.setXmlName(new javax.xml.namespace.QName("http://betaface.com/", "Y"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "double"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("width");
        elemField.setXmlName(new javax.xml.namespace.QName("http://betaface.com/", "Width"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "double"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("height");
        elemField.setXmlName(new javax.xml.namespace.QName("http://betaface.com/", "Height"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "double"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("angleDegree");
        elemField.setXmlName(new javax.xml.namespace.QName("http://betaface.com/", "AngleDegree"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "double"));
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
