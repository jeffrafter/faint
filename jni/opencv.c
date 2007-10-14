/*******************************************************************************
 * + -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- +
 * |                                                                         |
 *    faint - The Face Annotation Interface
 * |  Copyright (C) 2007  Malte Mathiszig                                    |
 * 
 * |  This program is free software: you can redistribute it and/or modify   |
 *    it under the terms of the GNU General Public License as published by
 * |  the Free Software Foundation, either version 3 of the License, or      |
 *    (at your option) any later version.                                     
 * |                                                                         |
 *    This program is distributed in the hope that it will be useful,
 * |  but WITHOUT ANY WARRANTY; without even the implied warranty of         |
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * |  GNU General Public License for more details.                           |
 * 
 * |  You should have received a copy of the GNU General Public License      |
 *    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * |                                                                         |
 * + -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- +
 *******************************************************************************/

#include "opencv.h"
#include <jni.h>
#include <stdio.h>
#include <windows.h>
#include <cv.h>
//#include <cvaux.h>
//#include <cxcore.h>

JNIEnv *env;
jclass class;
jobject caller;
CvHaarClassifierCascade *cascade;
char* lastCascade;
CvMemStorage* storage;
  	
/***
 * Performs face detection on a given file and returns an array of
 * regions as result.
 */
JNIEXPORT jobjectArray JNICALL Java_de_offis_faint_detection_plugins_opencv_OpenCVDetection_detectFacesJNI(JNIEnv *theEnv, jclass theClass, jstring fileName, jobject theCaller){
    
    printf("Preparing face detection...\n");

	// init global variables
  	caller = theCaller;
  	env = theEnv;
  	class = theClass;
  	
  	// load image
  	char* convertedFileName = jstringToWindows(env, fileName);
    IplImage *img = cvLoadImage(convertedFileName, 1 );
    free(convertedFileName);
 	int i = img->width;
  	//printf("%i",i);
  	
  	// init references for java class 'Region'
  	jclass regionClass = (*env)->FindClass(env, "de/offis/faint/model/Region");
  	jmethodID regionConstructor = (*env)->GetMethodID(env, regionClass, "<init>","(IIIIDLjava/lang/String;)V");
  	
  	// init cascade
  	loadCascade();
  	
  	// init storage
  	if (storage == NULL){
  		storage = cvCreateMemStorage(0);
  	}
  	
  	// resolve scale and minsize parameters
 	jmethodID jmidScale = (*env)->GetMethodID(env, class, "getScale","()F");
    if (jmidScale == NULL) printf("Error: Method getScale() not found!\n");
    jfloat scale = (*env)->CallFloatMethod(env,caller,jmidScale);
    
    jmethodID jmidSize = (*env)->GetMethodID(env, class, "getMinSize","()I");
    if (jmidSize == NULL) printf("Error: Method getMinSize() not found!\n");
    jint minSize = (*env)->CallIntMethod(env,caller,jmidSize);

  	// detect faces
  	printf("Running face detection... ");
  	CvSeq* faces = cvHaarDetectObjects(img, cascade, storage, scale, 2, CV_HAAR_DO_CANNY_PRUNING, cvSize(minSize,minSize));
  	printf("%i hits.\n", faces->total);	
  	
  	// prepare resulting array
  	jobjectArray result = NULL;
  	if (faces != NULL && faces->total > 0) {
  	result = (jobjectArray)(*env)->NewObjectArray(env, faces->total, regionClass, NULL);  	
    for( i = 0; i < (faces ? faces->total : 0); i++ )
        {
        	CvRect* r = (CvRect*)cvGetSeqElem( faces, i );
        	int w = r->width;
        	int h = r->height;
        	int x = r->x + (int)(w/2);
        	int y = r->y + (int)(h/2);
        	float a = 0.0;
        	
        	jobject region = (*env)->NewObject(env, regionClass, regionConstructor, x, y, w, h, a, fileName);
            (*env)->SetObjectArrayElement(env, result, i, region);
        }
  	}
  	
  	// cleanup
  	cvReleaseImage(&img);
  	// cvReleaseMemStorage(&storage);
  	
  	return result;
}

/**
 *  Loads the HaarClassifierCascade.
 */
void loadCascade(){
	
    // invoke getCascade() method to resolve cascade file name
    jmethodID jmid = (*env)->GetMethodID(env, class, "getCascade","()Ljava/lang/String;");
    if (jmid == NULL) printf("Error: Method getCascade() not found!\n");
    jstring filename = (jstring) (*env)->CallObjectMethod(env,caller,jmid);
    
    char* string = jstringToWindows(env, filename);
    
    if (cascade == NULL || strcmpi(string, lastCascade) != 0){
    
	    // init cascade    
		cascade = (CvHaarClassifierCascade*)cvLoad(string, 0, 0, 0 );
		if (cascade == NULL) printf("Error: Cascade == NULL!\n");
		else {
			printf(string);
			printf(" loaded!.\n");
		}
		//cleanup and remember cascade name
		if (lastCascade != NULL) free(lastCascade);
		lastCascade = string;
    }
}


/**
 * GetStringUTFChars() does not convert special characters
 * like "ä" correctly. This working solution for jstring
 * conversion is adapted from David Wendt / IBM.
 * 
 * http://www-128.ibm.com/developerworks/java/library/j-jninls/jninls.html
 * 
 */
char* jstringToWindows( JNIEnv* env, jstring jstr )
{
  int length = (*env)->GetStringLength( env, jstr );
  const jchar* jcstr = (*env)->GetStringChars( env, jstr, 0 );
  char* rtn = (char*)malloc( length*2+1 );
  int size = 0;
  size = WideCharToMultiByte( CP_ACP, 0, (LPCWSTR)jcstr, length, rtn,
                           (length*2+1), NULL, NULL );
  if( size <= 0 )
    return NULL;
  (*env)->ReleaseStringChars( env, jstr, jcstr );
  rtn[size] = 0;
  return rtn;
}


