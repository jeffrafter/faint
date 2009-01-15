package de.offis.faint.detection.plugins.haar;

import java.io.Serializable;

public class Point implements Serializable{
	
	private static final long serialVersionUID = -2680006514028921644L;


	/**
	 * @param round
	 * @param round2
	 */
	public Point(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	/**
	 * 
	 */
	public Point() {
		x = 0;
		y = 0;
	}

	int x;
	int y;


	public void setCoords(int x, int y) {
		this.x = x;
		this.y = y;
	}

}
