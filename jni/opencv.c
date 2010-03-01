#include "opencv.h"
#include <jni.h>
#include <cv.h>
#include <highgui.h>
#include <sys/types.h>
#include <sys/ipc.h>
#include <sys/shm.h>
#include <sys/mman.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <unistd.h>

/*
 * Class:     faint_OpenCV
 * Method:    detectFaces
 * Signature: (Ljava/lang/String;)[Lfaint/Region;
 */
JNIEXPORT jobjectArray JNICALL Java_faint_OpenCV_detectFaces
  (JNIEnv * env, jobject jobj, jstring image) {
  
  int i;

  // Get the path to the image as a c-string and load it
  jboolean iscopy;
  const char *mimage = (*env).GetStringUTFChars(image, &iscopy);
  IplImage* iplImage = (IplImage*) cvLoadImage(mimage,1);
  (*env).ReleaseStringUTFChars(image, mimage);      
      
  // Load the cascade (would be faster if this was cached)
  CvHaarClassifierCascade *cascade = (CvHaarClassifierCascade*)cvLoad("./haarcascade_frontalface_alt.xml", 0, 0, 0);
    
  // Detect the objects (simplify min size and scale)
  CvMemStorage* storage = cvCreateMemStorage(0);
  CvSeq* faces = cvHaarDetectObjects(iplImage, cascade, storage, 1.1, 2, CV_HAAR_DO_CANNY_PRUNING, cvSize(0,0));
   
  // Prepare resulting array
  jclass regionClass = (*env).FindClass("faint/Region");
  jmethodID regionConstructor = (*env).GetMethodID(regionClass, "<init>","(IIIIDLjava/lang/String;)V");
  jobjectArray result = NULL;
  if (faces != NULL && faces->total > 0) {
    printf("%i hits.\n", faces->total);  
    result = (jobjectArray)(*env).NewObjectArray(faces->total, regionClass, NULL);    
    for( i = 0; i < (faces ? faces->total : 0); i++ ) {
      CvRect* r = (CvRect*)cvGetSeqElem( faces, i );
      int w = r->width;
      int h = r->height;
      int x = r->x;
      int y = r->y;
      float a = 0.0;
      
      printf("Region: %i, %i, %i, %i \n", x, y, w, h);  
      jobject region = (*env).NewObject(regionClass, regionConstructor, x, y, w, h, a, image);
        (*env).SetObjectArrayElement(result, i, region);
    }
  }
    
  // Clean up  
  cvReleaseImage(&iplImage);
 
  return result;
}
