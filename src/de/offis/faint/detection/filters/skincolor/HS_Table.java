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

package de.offis.faint.detection.filters.skincolor;

import java.io.Serializable;
import java.util.BitSet;

/**
 * Compact and serializable class for the Hue Saturation Table
 */
public class HS_Table implements Serializable{
	private static final long serialVersionUID = 1009085682335324915L;
	
	BitSet[] values = new BitSet[256];
	
	public HS_Table()
	{
		for (int i = 0; i < values.length; i++){
			values[i] = new BitSet(256);
		}
	}
}
