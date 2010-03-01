FLAGS	= -dynamiclib
INCLUDES = -I/System/Library/Frameworks/JavaVM.framework/Headers -I/usr/local/include/opencv
LIBS = -L/usr/local/lib -lcxcore -lcv -lhighgui -lcvaux -lml -framework JavaVM
							
all : libopencv.jnilib

opencv.h :
	javah -jni -classpath ../bin -o opencv.h faint.OpenCV
	
libopencv.jnilib : opencv.h
	g++ -o libopencv.jnilib $(FLAGS) $(INCLUDES) $(LIBS) opencv.c
	mv libopencv.jnilib ../bin

clean :
	rm opencv.h