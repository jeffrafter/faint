/**
 * BetafaceWebServiceSoap.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package de.offis.faint.detection.plugins.betaface.generated;

public interface BetafaceWebServiceSoap extends java.rmi.Remote {

    /**
     * (Detection) This method returns an array of all faces found
     * on the input image. Orig members contains original source image coordinates,
     * Crop members contains feature coordinates on the cropped image. Detection
     * score may be used to determine which of the faces found have better
     * quality, if you interested in only one face per image select one with
     * highest score.
     */
    public de.offis.faint.detection.plugins.betaface.generated.BetafaceDetectedFace[] findFaces(java.lang.String licenseKey, byte[] srcImage, int maxImageWidthPix, int maxImageHeightPix, double minFaceSizeOnImage, int minFaceSizePix, int faceWidth, int faceHeight, double eyesDistance, double eyeLineHeight) throws java.rmi.RemoteException;

    /**
     * (Detection) This method returns an array of all faces found
     * on the input image. Orig members contains original source image coordinates,
     * Crop members contains feature coordinates on the cropped image. Detection
     * score may be used to determine which of the faces found have better
     * quality, if you interested in only one face per image select one with
     * highest score.
     */
    public de.offis.faint.detection.plugins.betaface.generated.BetafaceDetectedFace[] findFaces2(java.lang.String licenseKey, byte[] srcImage, int maxImageWidthPix, int maxImageHeightPix, double minFaceSizeOnImage, int minFaceSizePix, double dDetectionAngle, double dAngleTolerance, int faceWidth, int faceHeight, double eyesDistance, double eyeLineHeight) throws java.rmi.RemoteException;

    /**
     * (Recognition) This method adds a new person to the database.
     * iGender can be 0 - unknown, 1 - man, 2 - woman. Return a person ID
     * in the database.
     */
    public int newPerson(java.lang.String licenseKey, int iGender, java.lang.String strDescription) throws java.rmi.RemoteException;

    /**
     * (Recognition) This method adds a new face to the database.
     * Return a face ID in the database.
     */
    public int newFace(java.lang.String licenseKey, de.offis.faint.detection.plugins.betaface.generated.BetafaceDetectedFace face) throws java.rmi.RemoteException;

    /**
     * (Recognition) This method adds a new face to the database and
     * associate it to specified person ID.
     */
    public int newFaceOfPerson(java.lang.String licenseKey, de.offis.faint.detection.plugins.betaface.generated.BetafaceDetectedFace face, int iPesonID) throws java.rmi.RemoteException;

    /**
     * (Recognition) This method delete each face key with specified
     * ID from your database.
     */
    public boolean deleteFacesByIDs(java.lang.String licenseKey, int[] iFaceIDs) throws java.rmi.RemoteException;

    /**
     * (Recognition) This method will delete all face keys from your
     * database.
     */
    public boolean deleteAllFaces(java.lang.String licenseKey) throws java.rmi.RemoteException;

    /**
     * (Recognition) This method create face key from detected face.
     * This key can be used later to search database.
     */
    public byte[] createFaceKey(java.lang.String licenseKey, de.offis.faint.detection.plugins.betaface.generated.BetafaceDetectedFace face) throws java.rmi.RemoteException;

    /**
     * (Recognition) This method returns an array of persons IDs'
     * which faces are most similar to the specified face key. In Similarities
     * method returns an array of similarity values for each match.
     */
    public int[] compareFaceKeyToFacesDB(java.lang.String licenseKey, byte[] faceKey, int nMatches, de.offis.faint.detection.plugins.betaface.generated.ArrayOfDoubleHolder similarities) throws java.rmi.RemoteException;

    /**
     * (Recognition) This method returns an array of persons IDs'
     * which faces are most similar to the specified face ID. In Similarities
     * method returns an array of similarity values for each match.
     */
    public int[] compareFaceIDToFacesDB(java.lang.String licenseKey, int iFaceID, int nMatches, de.offis.faint.detection.plugins.betaface.generated.ArrayOfDoubleHolder similarities) throws java.rmi.RemoteException;

    /**
     * (Morphing) This method will morph 2 faces with given transition
     * koefficient.
     */
    public de.offis.faint.detection.plugins.betaface.generated.BetafaceImage morphFaces(java.lang.String licenseKey, de.offis.faint.detection.plugins.betaface.generated.BetafaceDetectedFace srcFace, de.offis.faint.detection.plugins.betaface.generated.BetafaceDetectedFace dstFace, double dTransitionKoeff) throws java.rmi.RemoteException;
}
