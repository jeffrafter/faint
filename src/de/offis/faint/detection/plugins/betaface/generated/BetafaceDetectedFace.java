/**
 * BetafaceDetectedFace.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package de.offis.faint.detection.plugins.betaface.generated;

public class BetafaceDetectedFace  implements java.io.Serializable {
    private double detectionScore;

    private de.offis.faint.detection.plugins.betaface.generated.BetafaceImage faceImage;

    private de.offis.faint.detection.plugins.betaface.generated.BetafaceFaceFeature[] features;

    private de.offis.faint.detection.plugins.betaface.generated.BetafaceFaceFeature[] origFeatures;

    public BetafaceDetectedFace() {
    }

    public BetafaceDetectedFace(
           double detectionScore,
           de.offis.faint.detection.plugins.betaface.generated.BetafaceImage faceImage,
           de.offis.faint.detection.plugins.betaface.generated.BetafaceFaceFeature[] features,
           de.offis.faint.detection.plugins.betaface.generated.BetafaceFaceFeature[] origFeatures) {
           this.detectionScore = detectionScore;
           this.faceImage = faceImage;
           this.features = features;
           this.origFeatures = origFeatures;
    }


    /**
     * Gets the detectionScore value for this BetafaceDetectedFace.
     * 
     * @return detectionScore
     */
    public double getDetectionScore() {
        return detectionScore;
    }


    /**
     * Sets the detectionScore value for this BetafaceDetectedFace.
     * 
     * @param detectionScore
     */
    public void setDetectionScore(double detectionScore) {
        this.detectionScore = detectionScore;
    }


    /**
     * Gets the faceImage value for this BetafaceDetectedFace.
     * 
     * @return faceImage
     */
    public de.offis.faint.detection.plugins.betaface.generated.BetafaceImage getFaceImage() {
        return faceImage;
    }


    /**
     * Sets the faceImage value for this BetafaceDetectedFace.
     * 
     * @param faceImage
     */
    public void setFaceImage(de.offis.faint.detection.plugins.betaface.generated.BetafaceImage faceImage) {
        this.faceImage = faceImage;
    }


    /**
     * Gets the features value for this BetafaceDetectedFace.
     * 
     * @return features
     */
    public de.offis.faint.detection.plugins.betaface.generated.BetafaceFaceFeature[] getFeatures() {
        return features;
    }


    /**
     * Sets the features value for this BetafaceDetectedFace.
     * 
     * @param features
     */
    public void setFeatures(de.offis.faint.detection.plugins.betaface.generated.BetafaceFaceFeature[] features) {
        this.features = features;
    }


    /**
     * Gets the origFeatures value for this BetafaceDetectedFace.
     * 
     * @return origFeatures
     */
    public de.offis.faint.detection.plugins.betaface.generated.BetafaceFaceFeature[] getOrigFeatures() {
        return origFeatures;
    }


    /**
     * Sets the origFeatures value for this BetafaceDetectedFace.
     * 
     * @param origFeatures
     */
    public void setOrigFeatures(de.offis.faint.detection.plugins.betaface.generated.BetafaceFaceFeature[] origFeatures) {
        this.origFeatures = origFeatures;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof BetafaceDetectedFace)) return false;
        BetafaceDetectedFace other = (BetafaceDetectedFace) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this.detectionScore == other.getDetectionScore() &&
            ((this.faceImage==null && other.getFaceImage()==null) || 
             (this.faceImage!=null &&
              this.faceImage.equals(other.getFaceImage()))) &&
            ((this.features==null && other.getFeatures()==null) || 
             (this.features!=null &&
              java.util.Arrays.equals(this.features, other.getFeatures()))) &&
            ((this.origFeatures==null && other.getOrigFeatures()==null) || 
             (this.origFeatures!=null &&
              java.util.Arrays.equals(this.origFeatures, other.getOrigFeatures())));
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
        _hashCode += new Double(getDetectionScore()).hashCode();
        if (getFaceImage() != null) {
            _hashCode += getFaceImage().hashCode();
        }
        if (getFeatures() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getFeatures());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getFeatures(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getOrigFeatures() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getOrigFeatures());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getOrigFeatures(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(BetafaceDetectedFace.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://betaface.com/", "BetafaceDetectedFace"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("detectionScore");
        elemField.setXmlName(new javax.xml.namespace.QName("http://betaface.com/", "DetectionScore"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "double"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("faceImage");
        elemField.setXmlName(new javax.xml.namespace.QName("http://betaface.com/", "FaceImage"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://betaface.com/", "BetafaceImage"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("features");
        elemField.setXmlName(new javax.xml.namespace.QName("http://betaface.com/", "Features"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://betaface.com/", "BetafaceFaceFeature"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://betaface.com/", "BetafaceFaceFeature"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("origFeatures");
        elemField.setXmlName(new javax.xml.namespace.QName("http://betaface.com/", "OrigFeatures"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://betaface.com/", "BetafaceFaceFeature"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://betaface.com/", "BetafaceFaceFeature"));
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
