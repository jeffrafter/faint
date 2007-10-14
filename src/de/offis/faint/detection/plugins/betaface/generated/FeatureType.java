/**
 * FeatureType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package de.offis.faint.detection.plugins.betaface.generated;

public class FeatureType implements java.io.Serializable {
    private java.lang.String _value_;
    private static java.util.HashMap _table_ = new java.util.HashMap();

    // Constructor
    protected FeatureType(java.lang.String value) {
        _value_ = value;
        _table_.put(_value_,this);
    }

    public static final java.lang.String _Unknown = "Unknown";
    public static final java.lang.String _Face = "Face";
    public static final java.lang.String _Eye_L = "Eye_L";
    public static final java.lang.String _Eye_R = "Eye_R";
    public static final java.lang.String _Eye_LCO = "Eye_LCO";
    public static final java.lang.String _Eye_RCO = "Eye_RCO";
    public static final java.lang.String _Eye_LCI = "Eye_LCI";
    public static final java.lang.String _Eye_RCI = "Eye_RCI";
    public static final java.lang.String _Mouth_LC = "Mouth_LC";
    public static final java.lang.String _Mouth_RC = "Mouth_RC";
    public static final java.lang.String _PRO_CHIN_EARCONN_L = "PRO_CHIN_EARCONN_L";
    public static final java.lang.String _PRO_CHIN_P1_L = "PRO_CHIN_P1_L";
    public static final java.lang.String _PRO_CHIN_P2_L = "PRO_CHIN_P2_L";
    public static final java.lang.String _PRO_CHIN_P3_L = "PRO_CHIN_P3_L";
    public static final java.lang.String _PRO_CHIN_P4_L = "PRO_CHIN_P4_L";
    public static final java.lang.String _PRO_CHIN_P5_L = "PRO_CHIN_P5_L";
    public static final java.lang.String _PRO_CHIN_B = "PRO_CHIN_B";
    public static final java.lang.String _PRO_CHIN_P5_R = "PRO_CHIN_P5_R";
    public static final java.lang.String _PRO_CHIN_P4_R = "PRO_CHIN_P4_R";
    public static final java.lang.String _PRO_CHIN_P3_R = "PRO_CHIN_P3_R";
    public static final java.lang.String _PRO_CHIN_P2_R = "PRO_CHIN_P2_R";
    public static final java.lang.String _PRO_CHIN_P1_R = "PRO_CHIN_P1_R";
    public static final java.lang.String _PRO_CHIN_EARCONN_R = "PRO_CHIN_EARCONN_R";
    public static final java.lang.String _PRO_TEMPLE_P4_R = "PRO_TEMPLE_P4_R";
    public static final java.lang.String _PRO_TEMPLE_P3_R = "PRO_TEMPLE_P3_R";
    public static final java.lang.String _PRO_TEMPLE_P2_R = "PRO_TEMPLE_P2_R";
    public static final java.lang.String _PRO_TEMPLE_P1_R = "PRO_TEMPLE_P1_R";
    public static final java.lang.String _PRO_TEMPLE_R = "PRO_TEMPLE_R";
    public static final java.lang.String _PRO_FOREHEAD_R = "PRO_FOREHEAD_R";
    public static final java.lang.String _PRO_FOREHEAD_P4 = "PRO_FOREHEAD_P4";
    public static final java.lang.String _PRO_FOREHEAD_P3 = "PRO_FOREHEAD_P3";
    public static final java.lang.String _PRO_FOREHEAD_M = "PRO_FOREHEAD_M";
    public static final java.lang.String _PRO_FOREHEAD_P2 = "PRO_FOREHEAD_P2";
    public static final java.lang.String _PRO_FOREHEAD_P1 = "PRO_FOREHEAD_P1";
    public static final java.lang.String _PRO_FOREHEAD_L = "PRO_FOREHEAD_L";
    public static final java.lang.String _PRO_TEMPLE_L = "PRO_TEMPLE_L";
    public static final java.lang.String _PRO_TEMPLE_P1_L = "PRO_TEMPLE_P1_L";
    public static final java.lang.String _PRO_TEMPLE_P2_L = "PRO_TEMPLE_P2_L";
    public static final java.lang.String _PRO_TEMPLE_P3_L = "PRO_TEMPLE_P3_L";
    public static final java.lang.String _PRO_TEMPLE_P4_L = "PRO_TEMPLE_P4_L";
    public static final java.lang.String _PRO_EYE_O_R = "PRO_EYE_O_R";
    public static final java.lang.String _PRO_EYE_BO_R = "PRO_EYE_BO_R";
    public static final java.lang.String _PRO_EYE_B_R = "PRO_EYE_B_R";
    public static final java.lang.String _PRO_EYE_BI_R = "PRO_EYE_BI_R";
    public static final java.lang.String _PRO_EYE_I_R = "PRO_EYE_I_R";
    public static final java.lang.String _PRO_EYE_TI_R = "PRO_EYE_TI_R";
    public static final java.lang.String _PRO_EYE_T_R = "PRO_EYE_T_R";
    public static final java.lang.String _PRO_EYE_TO_R = "PRO_EYE_TO_R";
    public static final java.lang.String _PRO_EYE_O_L = "PRO_EYE_O_L";
    public static final java.lang.String _PRO_EYE_TO_L = "PRO_EYE_TO_L";
    public static final java.lang.String _PRO_EYE_T_L = "PRO_EYE_T_L";
    public static final java.lang.String _PRO_EYE_TI_L = "PRO_EYE_TI_L";
    public static final java.lang.String _PRO_EYE_I_L = "PRO_EYE_I_L";
    public static final java.lang.String _PRO_EYE_BI_L = "PRO_EYE_BI_L";
    public static final java.lang.String _PRO_EYE_B_L = "PRO_EYE_B_L";
    public static final java.lang.String _PRO_EYE_BO_L = "PRO_EYE_BO_L";
    public static final java.lang.String _PRO_EYEBROW_I_R = "PRO_EYEBROW_I_R";
    public static final java.lang.String _PRO_EYEBROW_TI_R = "PRO_EYEBROW_TI_R";
    public static final java.lang.String _PRO_EYEBROW_T_R = "PRO_EYEBROW_T_R";
    public static final java.lang.String _PRO_EYEBROW_TO_R = "PRO_EYEBROW_TO_R";
    public static final java.lang.String _PRO_EYEBROW_O_R = "PRO_EYEBROW_O_R";
    public static final java.lang.String _PRO_EYEBROW_BI_R = "PRO_EYEBROW_BI_R";
    public static final java.lang.String _PRO_EYEBROW_B_R = "PRO_EYEBROW_B_R";
    public static final java.lang.String _PRO_EYEBROW_BO_R = "PRO_EYEBROW_BO_R";
    public static final java.lang.String _PRO_EYEBROW_I_L = "PRO_EYEBROW_I_L";
    public static final java.lang.String _PRO_EYEBROW_TI_L = "PRO_EYEBROW_TI_L";
    public static final java.lang.String _PRO_EYEBROW_T_L = "PRO_EYEBROW_T_L";
    public static final java.lang.String _PRO_EYEBROW_TO_L = "PRO_EYEBROW_TO_L";
    public static final java.lang.String _PRO_EYEBROW_O_L = "PRO_EYEBROW_O_L";
    public static final java.lang.String _PRO_EYEBROW_BO_L = "PRO_EYEBROW_BO_L";
    public static final java.lang.String _PRO_EYEBROW_B_L = "PRO_EYEBROW_B_L";
    public static final java.lang.String _PRO_EYEBROW_BI_L = "PRO_EYEBROW_BI_L";
    public static final java.lang.String _PRO_MOUTH_L = "PRO_MOUTH_L";
    public static final java.lang.String _PRO_MOUTH_TL = "PRO_MOUTH_TL";
    public static final java.lang.String _PRO_MOUTH_T = "PRO_MOUTH_T";
    public static final java.lang.String _PRO_MOUTH_TR = "PRO_MOUTH_TR";
    public static final java.lang.String _PRO_MOUTH_R = "PRO_MOUTH_R";
    public static final java.lang.String _PRO_MOUTH_BR = "PRO_MOUTH_BR";
    public static final java.lang.String _PRO_MOUTH_B = "PRO_MOUTH_B";
    public static final java.lang.String _PRO_MOUTH_BL = "PRO_MOUTH_BL";
    public static final java.lang.String _PRO_NOSE_T_L = "PRO_NOSE_T_L";
    public static final java.lang.String _PRO_NOSE_TI_NOSTRIL_L = "PRO_NOSE_TI_NOSTRIL_L";
    public static final java.lang.String _PRO_NOSE_TO_NOSTRIL_L = "PRO_NOSE_TO_NOSTRIL_L";
    public static final java.lang.String _PRO_NOSE_BO_NOSTRIL_L = "PRO_NOSE_BO_NOSTRIL_L";
    public static final java.lang.String _PRO_NOSE_B_NOSTRIL_L = "PRO_NOSE_B_NOSTRIL_L";
    public static final java.lang.String _PRO_NOSE_B = "PRO_NOSE_B";
    public static final java.lang.String _PRO_NOSE_B_NOSTRIL_R = "PRO_NOSE_B_NOSTRIL_R";
    public static final java.lang.String _PRO_NOSE_BO_NOSTRIL_R = "PRO_NOSE_BO_NOSTRIL_R";
    public static final java.lang.String _PRO_NOSE_TO_NOSTRIL_R = "PRO_NOSE_TO_NOSTRIL_R";
    public static final java.lang.String _PRO_NOSE_TI_NOSTRIL_R = "PRO_NOSE_TI_NOSTRIL_R";
    public static final java.lang.String _PRO_NOSE_T_R = "PRO_NOSE_T_R";
    public static final java.lang.String _PRO_EYE_IRIS_R = "PRO_EYE_IRIS_R";
    public static final java.lang.String _PRO_EYE_IRIS_L = "PRO_EYE_IRIS_L";
    public static final java.lang.String _PRO_NOSE_TIP = "PRO_NOSE_TIP";
    public static final java.lang.String _PRO_CHEEKBONE_L = "PRO_CHEEKBONE_L";
    public static final java.lang.String _PRO_CHEEKBONE_R = "PRO_CHEEKBONE_R";
    public static final FeatureType Unknown = new FeatureType(_Unknown);
    public static final FeatureType Face = new FeatureType(_Face);
    public static final FeatureType Eye_L = new FeatureType(_Eye_L);
    public static final FeatureType Eye_R = new FeatureType(_Eye_R);
    public static final FeatureType Eye_LCO = new FeatureType(_Eye_LCO);
    public static final FeatureType Eye_RCO = new FeatureType(_Eye_RCO);
    public static final FeatureType Eye_LCI = new FeatureType(_Eye_LCI);
    public static final FeatureType Eye_RCI = new FeatureType(_Eye_RCI);
    public static final FeatureType Mouth_LC = new FeatureType(_Mouth_LC);
    public static final FeatureType Mouth_RC = new FeatureType(_Mouth_RC);
    public static final FeatureType PRO_CHIN_EARCONN_L = new FeatureType(_PRO_CHIN_EARCONN_L);
    public static final FeatureType PRO_CHIN_P1_L = new FeatureType(_PRO_CHIN_P1_L);
    public static final FeatureType PRO_CHIN_P2_L = new FeatureType(_PRO_CHIN_P2_L);
    public static final FeatureType PRO_CHIN_P3_L = new FeatureType(_PRO_CHIN_P3_L);
    public static final FeatureType PRO_CHIN_P4_L = new FeatureType(_PRO_CHIN_P4_L);
    public static final FeatureType PRO_CHIN_P5_L = new FeatureType(_PRO_CHIN_P5_L);
    public static final FeatureType PRO_CHIN_B = new FeatureType(_PRO_CHIN_B);
    public static final FeatureType PRO_CHIN_P5_R = new FeatureType(_PRO_CHIN_P5_R);
    public static final FeatureType PRO_CHIN_P4_R = new FeatureType(_PRO_CHIN_P4_R);
    public static final FeatureType PRO_CHIN_P3_R = new FeatureType(_PRO_CHIN_P3_R);
    public static final FeatureType PRO_CHIN_P2_R = new FeatureType(_PRO_CHIN_P2_R);
    public static final FeatureType PRO_CHIN_P1_R = new FeatureType(_PRO_CHIN_P1_R);
    public static final FeatureType PRO_CHIN_EARCONN_R = new FeatureType(_PRO_CHIN_EARCONN_R);
    public static final FeatureType PRO_TEMPLE_P4_R = new FeatureType(_PRO_TEMPLE_P4_R);
    public static final FeatureType PRO_TEMPLE_P3_R = new FeatureType(_PRO_TEMPLE_P3_R);
    public static final FeatureType PRO_TEMPLE_P2_R = new FeatureType(_PRO_TEMPLE_P2_R);
    public static final FeatureType PRO_TEMPLE_P1_R = new FeatureType(_PRO_TEMPLE_P1_R);
    public static final FeatureType PRO_TEMPLE_R = new FeatureType(_PRO_TEMPLE_R);
    public static final FeatureType PRO_FOREHEAD_R = new FeatureType(_PRO_FOREHEAD_R);
    public static final FeatureType PRO_FOREHEAD_P4 = new FeatureType(_PRO_FOREHEAD_P4);
    public static final FeatureType PRO_FOREHEAD_P3 = new FeatureType(_PRO_FOREHEAD_P3);
    public static final FeatureType PRO_FOREHEAD_M = new FeatureType(_PRO_FOREHEAD_M);
    public static final FeatureType PRO_FOREHEAD_P2 = new FeatureType(_PRO_FOREHEAD_P2);
    public static final FeatureType PRO_FOREHEAD_P1 = new FeatureType(_PRO_FOREHEAD_P1);
    public static final FeatureType PRO_FOREHEAD_L = new FeatureType(_PRO_FOREHEAD_L);
    public static final FeatureType PRO_TEMPLE_L = new FeatureType(_PRO_TEMPLE_L);
    public static final FeatureType PRO_TEMPLE_P1_L = new FeatureType(_PRO_TEMPLE_P1_L);
    public static final FeatureType PRO_TEMPLE_P2_L = new FeatureType(_PRO_TEMPLE_P2_L);
    public static final FeatureType PRO_TEMPLE_P3_L = new FeatureType(_PRO_TEMPLE_P3_L);
    public static final FeatureType PRO_TEMPLE_P4_L = new FeatureType(_PRO_TEMPLE_P4_L);
    public static final FeatureType PRO_EYE_O_R = new FeatureType(_PRO_EYE_O_R);
    public static final FeatureType PRO_EYE_BO_R = new FeatureType(_PRO_EYE_BO_R);
    public static final FeatureType PRO_EYE_B_R = new FeatureType(_PRO_EYE_B_R);
    public static final FeatureType PRO_EYE_BI_R = new FeatureType(_PRO_EYE_BI_R);
    public static final FeatureType PRO_EYE_I_R = new FeatureType(_PRO_EYE_I_R);
    public static final FeatureType PRO_EYE_TI_R = new FeatureType(_PRO_EYE_TI_R);
    public static final FeatureType PRO_EYE_T_R = new FeatureType(_PRO_EYE_T_R);
    public static final FeatureType PRO_EYE_TO_R = new FeatureType(_PRO_EYE_TO_R);
    public static final FeatureType PRO_EYE_O_L = new FeatureType(_PRO_EYE_O_L);
    public static final FeatureType PRO_EYE_TO_L = new FeatureType(_PRO_EYE_TO_L);
    public static final FeatureType PRO_EYE_T_L = new FeatureType(_PRO_EYE_T_L);
    public static final FeatureType PRO_EYE_TI_L = new FeatureType(_PRO_EYE_TI_L);
    public static final FeatureType PRO_EYE_I_L = new FeatureType(_PRO_EYE_I_L);
    public static final FeatureType PRO_EYE_BI_L = new FeatureType(_PRO_EYE_BI_L);
    public static final FeatureType PRO_EYE_B_L = new FeatureType(_PRO_EYE_B_L);
    public static final FeatureType PRO_EYE_BO_L = new FeatureType(_PRO_EYE_BO_L);
    public static final FeatureType PRO_EYEBROW_I_R = new FeatureType(_PRO_EYEBROW_I_R);
    public static final FeatureType PRO_EYEBROW_TI_R = new FeatureType(_PRO_EYEBROW_TI_R);
    public static final FeatureType PRO_EYEBROW_T_R = new FeatureType(_PRO_EYEBROW_T_R);
    public static final FeatureType PRO_EYEBROW_TO_R = new FeatureType(_PRO_EYEBROW_TO_R);
    public static final FeatureType PRO_EYEBROW_O_R = new FeatureType(_PRO_EYEBROW_O_R);
    public static final FeatureType PRO_EYEBROW_BI_R = new FeatureType(_PRO_EYEBROW_BI_R);
    public static final FeatureType PRO_EYEBROW_B_R = new FeatureType(_PRO_EYEBROW_B_R);
    public static final FeatureType PRO_EYEBROW_BO_R = new FeatureType(_PRO_EYEBROW_BO_R);
    public static final FeatureType PRO_EYEBROW_I_L = new FeatureType(_PRO_EYEBROW_I_L);
    public static final FeatureType PRO_EYEBROW_TI_L = new FeatureType(_PRO_EYEBROW_TI_L);
    public static final FeatureType PRO_EYEBROW_T_L = new FeatureType(_PRO_EYEBROW_T_L);
    public static final FeatureType PRO_EYEBROW_TO_L = new FeatureType(_PRO_EYEBROW_TO_L);
    public static final FeatureType PRO_EYEBROW_O_L = new FeatureType(_PRO_EYEBROW_O_L);
    public static final FeatureType PRO_EYEBROW_BO_L = new FeatureType(_PRO_EYEBROW_BO_L);
    public static final FeatureType PRO_EYEBROW_B_L = new FeatureType(_PRO_EYEBROW_B_L);
    public static final FeatureType PRO_EYEBROW_BI_L = new FeatureType(_PRO_EYEBROW_BI_L);
    public static final FeatureType PRO_MOUTH_L = new FeatureType(_PRO_MOUTH_L);
    public static final FeatureType PRO_MOUTH_TL = new FeatureType(_PRO_MOUTH_TL);
    public static final FeatureType PRO_MOUTH_T = new FeatureType(_PRO_MOUTH_T);
    public static final FeatureType PRO_MOUTH_TR = new FeatureType(_PRO_MOUTH_TR);
    public static final FeatureType PRO_MOUTH_R = new FeatureType(_PRO_MOUTH_R);
    public static final FeatureType PRO_MOUTH_BR = new FeatureType(_PRO_MOUTH_BR);
    public static final FeatureType PRO_MOUTH_B = new FeatureType(_PRO_MOUTH_B);
    public static final FeatureType PRO_MOUTH_BL = new FeatureType(_PRO_MOUTH_BL);
    public static final FeatureType PRO_NOSE_T_L = new FeatureType(_PRO_NOSE_T_L);
    public static final FeatureType PRO_NOSE_TI_NOSTRIL_L = new FeatureType(_PRO_NOSE_TI_NOSTRIL_L);
    public static final FeatureType PRO_NOSE_TO_NOSTRIL_L = new FeatureType(_PRO_NOSE_TO_NOSTRIL_L);
    public static final FeatureType PRO_NOSE_BO_NOSTRIL_L = new FeatureType(_PRO_NOSE_BO_NOSTRIL_L);
    public static final FeatureType PRO_NOSE_B_NOSTRIL_L = new FeatureType(_PRO_NOSE_B_NOSTRIL_L);
    public static final FeatureType PRO_NOSE_B = new FeatureType(_PRO_NOSE_B);
    public static final FeatureType PRO_NOSE_B_NOSTRIL_R = new FeatureType(_PRO_NOSE_B_NOSTRIL_R);
    public static final FeatureType PRO_NOSE_BO_NOSTRIL_R = new FeatureType(_PRO_NOSE_BO_NOSTRIL_R);
    public static final FeatureType PRO_NOSE_TO_NOSTRIL_R = new FeatureType(_PRO_NOSE_TO_NOSTRIL_R);
    public static final FeatureType PRO_NOSE_TI_NOSTRIL_R = new FeatureType(_PRO_NOSE_TI_NOSTRIL_R);
    public static final FeatureType PRO_NOSE_T_R = new FeatureType(_PRO_NOSE_T_R);
    public static final FeatureType PRO_EYE_IRIS_R = new FeatureType(_PRO_EYE_IRIS_R);
    public static final FeatureType PRO_EYE_IRIS_L = new FeatureType(_PRO_EYE_IRIS_L);
    public static final FeatureType PRO_NOSE_TIP = new FeatureType(_PRO_NOSE_TIP);
    public static final FeatureType PRO_CHEEKBONE_L = new FeatureType(_PRO_CHEEKBONE_L);
    public static final FeatureType PRO_CHEEKBONE_R = new FeatureType(_PRO_CHEEKBONE_R);
    public java.lang.String getValue() { return _value_;}
    public static FeatureType fromValue(java.lang.String value)
          throws java.lang.IllegalArgumentException {
        FeatureType enumeration = (FeatureType)
            _table_.get(value);
        if (enumeration==null) throw new java.lang.IllegalArgumentException();
        return enumeration;
    }
    public static FeatureType fromString(java.lang.String value)
          throws java.lang.IllegalArgumentException {
        return fromValue(value);
    }
    public boolean equals(java.lang.Object obj) {return (obj == this);}
    public int hashCode() { return toString().hashCode();}
    public java.lang.String toString() { return _value_;}
    public java.lang.Object readResolve() throws java.io.ObjectStreamException { return fromValue(_value_);}
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new org.apache.axis.encoding.ser.EnumSerializer(
            _javaType, _xmlType);
    }
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new org.apache.axis.encoding.ser.EnumDeserializer(
            _javaType, _xmlType);
    }
    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(FeatureType.class);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://betaface.com/", "FeatureType"));
    }
    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

}
