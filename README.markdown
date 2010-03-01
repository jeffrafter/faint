Faint
=====
The Face Annotation Interface - Java toolkit for facial recognition using 
eigenfaces.

Background
----------
The Faint application was originally written by Malte Mathiszig as a Java 
application. Initially developed in the context of a Bachelor Thesis at the 
University of Oldenburg, faint has been integrated into several projects 
maintained by the OFFIS Institute for Information Technology. To attract a 
broader audience, the source code has been released under GNU General Public 
License (GPL) in October 2007 and hosted at http://faint.sourceforge.net/.

Why change?
-----------
The original sourceforge release was feature-rich. It included a photo browser
EXIF annotations, Image caches and a host of other features. These features
were powerful and made faint a valuable library. At the same time however, they
obscured the most valuable feature, its Eigenfaces implementation.

OpenCV face detection was written around the 1.0 version of the Windows DLL and
had not been ported. The Betafaces implementation seems to be no longer active.
Ultimately this left it unusable on platforms other than windows. 

Current Development
-------------------
This fork of Faint attempts to simplify and reduce the library to its core. I 
have left in the face database support and thumbnail caching as they make the
program useful by itself. Ultimately though it would be preferred to allow 
library users to implement their own storage mechanism.

This version of the library includes a JNI wrapper for OpenCV 2.0 and is 
targeted for Mac OSX. It expects a C++ build environment.

Installation and Usage
----------------------
You should build the OpenCV toolkit from source. Information on this can be 
found at http://opencv.willowgarage.com/wiki/Mac_OS_X_OpenCV_Port. This also 
contains the data for the default cascades.

Next get the source for this library

  git clone git://github.com/jeffrafter/faint.git
  
Once you have it, go ahead and build:

  cd faint
  ./build

This should build everything including the JNI wrapper. 

Now that you have it installed you can run the samples:

  cd samples
  ./detect
  ./train
  ./recognize
  
The detect application will utilize the OpenCV wrapper to execute the detection
pattern based on the included cascade file in the samples folder. This basically
tries to find things that match a large training set of objects (which happen
to be faces). It will print out the regions (x, y, width, height) it finds 
in the image.

You can use these to start training the system. Run the train application, 
pass it an image file, the region information and an annotation (basically the
person's name from the region). This adds the region to the thumbnail cache
and database and marks it as an image that should be used for training the 
eigenfaces algorithm.

Finally, you can submit an image and region to the recognize application and 
see if it matches any of the training annotations you entered. The results
will print out with the annotation names and their weights. The one with 
the highest weight generally wins.
  

Original License
----------------

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

Thanks
======
Special thanks to Malte Mathiszig for releasing an amazing application.