JAVAPATH	= C:\Programme\Java\jdk1.5.0_07
OPENCVPATH	= C:\\Programme\\OpenCV
FLAGS		= -Wall -mno-cygwin -shared -Wl,--add-stdcall-alias
INCLUDES  	= -I"$(JAVAPATH)\include" \
			  -I"$(JAVAPATH)\include\win32" \
			  -I"$(OPENCVPATH)\cv\include" \
              -I"$(OPENCVPATH)\cvaux\include" \
              -I"$(OPENCVPATH)\cxcore\include" \
              -I"$(OPENCVPATH)\cv\include" \
              
LIBS        = $(OPENCVPATH)\\lib\\cv.lib \
              $(OPENCVPATH)\\lib\\highgui.lib \
              $(OPENCVPATH)\\lib\\cxcore.lib

all : opencv.dll

opencv.h :
	javah -jni -classpath ../bin -o opencv.h de.offis.faint.detection.plugins.opencv.OpenCVDetection
	
opencv.dll : opencv.h
	gcc  $(FLAGS) $(INCLUDES) opencv.c -o opencv.dll $(LIBS)
	mv opencv.dll ../src/de/offis/faint/detection/plugins/opencv

clean :
	rm opencv.h