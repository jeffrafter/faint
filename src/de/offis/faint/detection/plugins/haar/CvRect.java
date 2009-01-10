/**
 * 
 */
package de.offis.faint.detection.plugins.haar;

/**
 * Port of C struct as found in cxtypes.h
 * 
 * 	typedef struct CvRect
 * 	{
 *	  int x;
 *    int y;
 *    int width;
 *    int height;
 * }
 *
 * @author maltech
 *
 */
public class CvRect {
		
	int x;
    int y;
    int width;
    int height;
    
	public CvRect(int x, int y, int w, int h) {
		this.x = x;
		this.y = y;
		this.width = w;
		this.height = h;
	}

	/**
	 * 
	 */
	public CvRect() {}
}
