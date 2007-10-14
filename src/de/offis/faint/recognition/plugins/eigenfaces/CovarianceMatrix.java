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

package de.offis.faint.recognition.plugins.eigenfaces;

import java.util.ArrayList;
import java.util.Collections;

/**
 * This class is used to build up a covariance matrix and
 * extract the eigenvalues and eigenvectors of it. Portions
 * of the code are taken from the Java Matrix Package (JAMA),
 * a cooperative product of The MathWorks and the National
 * Institute of Standards and Technology (NIST).
 * 
 * @author maltech
 *
 */
public class CovarianceMatrix{
			
	private EigenValueAndVector[] eigenValueAndVectors;
	
	private double[][] smallMatrix;

	private int dimension;
	
	private double[] eig, e;
	
	
	/**
	 * Constructor that builds up the matrix and calculates
	 * the Eigenvalues and Vectors internally.
	 * 
	 * @param vectors
	 */
	public CovarianceMatrix(short[][] vectors){
		
		int vectorLength = vectors[0].length;
		dimension = vectors.length;
		
		// build "small" matrix
		// FIXME: if (vectors.length > lenght of single vector) then the small matrix will be the bigger one!
		smallMatrix = new double[dimension][dimension];
		for (int rowIndex = 0; rowIndex < smallMatrix.length; rowIndex++){
			
			for (int colIndex=0; colIndex < rowIndex+1; colIndex++){
				
				// calculate element
				smallMatrix[rowIndex][colIndex] = 0;
				for(int i = 0; i<vectorLength; i++){
					smallMatrix[rowIndex][colIndex] += vectors[rowIndex][i] * vectors[colIndex][i];
				}
				
				// set same value for mirrored element (matrix is symmetrical)
				smallMatrix[colIndex][rowIndex] = smallMatrix[rowIndex][colIndex];
			}
		}
		
		// tridiagonalize small matrix
		this.tridiagonalize();
		
		// perform QL algorithm to find eigenvectors of small matrix
		this.performQLalgorithm();
		
		/*
		 * Note: From here on the eigenvectors are stored in the COLUMNS of the small matrix!
		 * 
		 * Uncomment this example to print all elements of eigenvector e:
		 * 
		 * int e = 0;
		 * for (int j = 0; j < smallMatrix.length; j++)
		 *   System.out.println(smallMatrix[0][e]);
		 * 
		 */
		
		// build eigenvalues of big matrix
		ArrayList<EigenValueAndVector> tempList = new ArrayList<EigenValueAndVector>(dimension);
		for (int i = 0; i< dimension; i++){
			
			double[] eigenVector = new double[vectorLength];
			double length = 0;
			
			// calculate eigenvector
			for (int j = 0; j<vectorLength; j++){
				double value = 0;
				for (int k = 0; k < vectors.length; k++){
					value += (vectors[k][j]) * (smallMatrix[k][i]);
				}
				eigenVector[j] = value;
				length += value * value;
			}
			length = Math.sqrt(length);
			
			// normalize eigenvector (divide it by its length)
			for (int j = 0; j< eigenVector.length; j++){
				eigenVector[j]/=length;
			}
			
			// store results in list
			EigenValueAndVector evv = new EigenValueAndVector();
			evv.eigenVector = eigenVector;
			evv.eigenValue = eig[i]; // eigenvalues stays the same for big matrix
			tempList.add(evv);
		}
		

		/*
		 * Uncomment this to check if the eigenvalues are correct.
		 * 
		double[][] tempVectors = new double[tempList.size()][vectorLength];		
		for (int i = 0; i < vectorLength; i++){
			for (int j = 0; j< vectorLength; j++){
				
				double matrixElement = getBigMatrixElement(i, j, vectors);
				
				for (int k = 0; k < dimension; k++){
					tempVectors[k][i] +=  matrixElement * tempList.get(k).eigenVector[j];
				}
			}
		}
		for (int i = 0; i < tempList.size(); i++){			
			int element = -1;
			loop: for (int j = 0; j < tempList.size(); j++){
				if (tempVectors[i][j] != 0){
					element = j;
					System.err.println(eig[i] + " ?= "+tempVectors[i][element] / tempList.get(i).eigenVector[element]);
					break loop;
				}
			}
			tempList.get(i).eigenValue = tempVectors[i][element] / tempList.get(i).eigenVector[element];			
		}
		*/
		
		// Sort Eigenvectors by their eigenvalues
		Collections.sort(tempList);
		
		// Store Eigenvalues and corresponding Eigenvectors
		eigenValueAndVectors = new EigenValueAndVector[dimension];
		tempList.toArray(eigenValueAndVectors);
	}
	
	/**
	 * Calculates elements of the "bigger" matrix - used for testing purpose only.
	 * 
	 * @param row
	 * @param col
	 * @param vectors
	 * @return
	 */
	@SuppressWarnings("unused")
	private double getBigMatrixElement(int row, int col, short[][] vectors){
		double result = 0;
		
		int vectorLength = vectors.length;
		
		for (int i = 0; i < vectorLength; i++){
			result += vectors[i][row] * vectors[i][col];
		}		
		
		return result;
	}
	
	/**
	 * Print method used for debugging.
	 */
	@SuppressWarnings("unused")
	private void print(){
		String whitespace = "                            ";
		for(int row = 0; row < smallMatrix.length; row++){
			for(int col = 0; col < smallMatrix.length; col++){
				String number = "" + smallMatrix[row][col];
				number = whitespace.substring(number.length()) + number;
				System.out.print(number);
			}
			System.out.println();
			System.out.println();
		}
	}

	
	/**
	 * Returns the Eigenvalue specified by a given number.
	 * The Values are sorted biggest-first.
	 * 
	 * @param number
	 * @return
	 */
	public double getEigenValue(int number){
		return this.eigenValueAndVectors[number].eigenValue;		
	}

	/**
	 * Returns the Eigenvector specified by a given number.
	 * The Eigenvectors are sorted by their Eigenvalues.
	 * 
	 * @param number
	 * @return
	 */
	public double[] getEigenVector(int number){
		return this.eigenValueAndVectors[number].eigenVector;
	}
	
	/**
	 *  Symmetric Householder reduction to tridiagonal form as
	 *  used in JAMA Package.
	 */
	private void tridiagonalize() {
		
		eig = new double[dimension];
		e = new double[dimension];
		
		//  This is derived from the Algol procedures tred2 by
		//  Bowdler, Martin, Reinsch, and Wilkinson, Handbook for
		//  Auto. Comp., Vol.ii-Linear Algebra, and the corresponding
		//  Fortran subroutine in EISPACK.
		
		
		for (int j = 0; j < dimension; j++) {
			eig[j] = smallMatrix[dimension-1][j];
		}
		
		// Householder reduction to tridiagonal form.
		
		for (int i = dimension-1; i > 0; i--) {
			
			// Scale to avoid under/overflow.
			
			double scale = 0.0;
			double h = 0.0;
			for (int k = 0; k < i; k++) {
				scale = scale + Math.abs(eig[k]);
			}
			if (scale == 0.0) {
				e[i] = eig[i-1];
				for (int j = 0; j < i; j++) {
					eig[j] = smallMatrix[i-1][j];
					smallMatrix[i][j] = 0.0;
					smallMatrix[j][i] = 0.0;
				}
			} else {
				
				// Generate Householder vector.
				
				for (int k = 0; k < i; k++) {
					eig[k] /= scale;
					h += eig[k] * eig[k];
				}
				double f = eig[i-1];
				double g = Math.sqrt(h);
				if (f > 0) {
					g = -g;
				}
				e[i] = scale * g;
				h = h - f * g;
				eig[i-1] = f - g;
				for (int j = 0; j < i; j++) {
					e[j] = 0.0;
				}
				
				// Apply similarity transformation to remaining columns.
				
				for (int j = 0; j < i; j++) {
					f = eig[j];
					smallMatrix[j][i] = f;
					g = e[j] + smallMatrix[j][j] * f;
					for (int k = j+1; k <= i-1; k++) {
						g += smallMatrix[k][j] * eig[k];
						e[k] += smallMatrix[k][j] * f;
					}
					e[j] = g;
				}
				f = 0.0;
				for (int j = 0; j < i; j++) {
					e[j] /= h;
					f += e[j] * eig[j];
				}
				double hh = f / (h + h);
				for (int j = 0; j < i; j++) {
					e[j] -= hh * eig[j];
				}
				for (int j = 0; j < i; j++) {
					f = eig[j];
					g = e[j];
					for (int k = j; k <= i-1; k++) {
						smallMatrix[k][j] -= (f * e[k] + g * eig[k]);
					}
					eig[j] = smallMatrix[i-1][j];
					smallMatrix[i][j] = 0.0;
				}
			}
			eig[i] = h;
		}
		
		// Accumulate transformations.
		
		for (int i = 0; i < dimension-1; i++) {
			smallMatrix[dimension-1][i] = smallMatrix[i][i];
			smallMatrix[i][i] = 1.0;
			double h = eig[i+1];
			if (h != 0.0) {
				for (int k = 0; k <= i; k++) {
					eig[k] = smallMatrix[k][i+1] / h;
				}
				for (int j = 0; j <= i; j++) {
					double g = 0.0;
					for (int k = 0; k <= i; k++) {
						g += smallMatrix[k][i+1] * smallMatrix[k][j];
					}
					for (int k = 0; k <= i; k++) {
						smallMatrix[k][j] -= g * eig[k];
					}
				}
			}
			for (int k = 0; k <= i; k++) {
				smallMatrix[k][i+1] = 0.0;
			}
		}
		for (int j = 0; j < dimension; j++) {
			eig[j] = smallMatrix[dimension-1][j];
			smallMatrix[dimension-1][j] = 0.0;
		}
		smallMatrix[dimension-1][dimension-1] = 1.0;
		e[0] = 0.0;
	} 
	
	/**
	 * Symmetric tridiagonal QL algorithm as used in JAMA Package.
	 */
	private void performQLalgorithm () {
		
		//  This is derived from the Algol procedures tql2, by
		//  Bowdler, Martin, Reinsch, and Wilkinson, Handbook for
		//  Auto. Comp., Vol.ii-Linear Algebra, and the corresponding
		//  Fortran subroutine in EISPACK.
		
		for (int i = 1; i < dimension; i++) {
			e[i-1] = e[i];
		}
		e[dimension-1] = 0.0;
		
		double f = 0.0;
		double tst1 = 0.0;
		double eps = Math.pow(2.0,-52.0);
		for (int l = 0; l < dimension; l++) {
			
			// Find small subdiagonal element
			
			tst1 = Math.max(tst1,Math.abs(eig[l]) + Math.abs(e[l]));
			int m = l;
			while (m < dimension) {
				if (Math.abs(e[m]) <= eps*tst1) {
					break;
				}
				m++;
			}
			
			// If m == l, d[l] is an eigenvalue,
			// otherwise, iterate.
			
			if (m > l) {
				int iter = 0;
				do {
					iter = iter + 1;  // (Could check iteration count here.)
					
					// Compute implicit shift
					
					double g = eig[l];
					double p = (eig[l+1] - g) / (2.0 * e[l]);
					double r = hypot(p,1.0);
					if (p < 0) {
						r = -r;
					}
					eig[l] = e[l] / (p + r);
					eig[l+1] = e[l] * (p + r);
					double dl1 = eig[l+1];
					double h = g - eig[l];
					for (int i = l+2; i < dimension; i++) {
						eig[i] -= h;
					}
					f = f + h;
					
					// Implicit QL transformation.
					
					p = eig[m];
					double c = 1.0;
					double c2 = c;
					double c3 = c;
					double el1 = e[l+1];
					double s = 0.0;
					double s2 = 0.0;
					for (int i = m-1; i >= l; i--) {
						c3 = c2;
						c2 = c;
						s2 = s;
						g = c * e[i];
						h = c * p;
						r = hypot(p,e[i]);
						e[i+1] = s * r;
						s = e[i] / r;
						c = p / r;
						p = c * eig[i] - s * g;
						eig[i+1] = h + s * (c * g + s * eig[i]);
						
						// Accumulate transformation.
						
						for (int k = 0; k < dimension; k++) {
							h = smallMatrix[k][i+1];
							smallMatrix[k][i+1] = s * smallMatrix[k][i] + c * h;
							smallMatrix[k][i] = c * smallMatrix[k][i] - s * h;
						}
					}
					p = -s * s2 * c3 * el1 * e[l] / dl1;
					e[l] = s * p;
					eig[l] = c * p;
					
					// Check for convergence.
					
				} while (Math.abs(e[l]) > eps*tst1);
			}
			eig[l] = eig[l] + f;
			e[l] = 0.0;
		}
		
		// Sort eigenvalues and corresponding vectors.
		
		for (int i = 0; i < dimension-1; i++) {
			int k = i;
			double p = eig[i];
			for (int j = i+1; j < dimension; j++) {
				if (eig[j] < p) {
					k = j;
					p = eig[j];
				}
			}
			if (k != i) {
				eig[k] = eig[i];
				eig[i] = p;
				for (int j = 0; j < dimension; j++) {
					p = smallMatrix[j][i];
					smallMatrix[j][i] = smallMatrix[j][k];
					smallMatrix[j][k] = p;
				}
			}
		}
	}
	
	/**
	 * Sqrt(a^2 + b^2) without under/overflow as used in JAMA Package.
	 */
	private static double hypot(double a, double b) {
		
		double r;
		
		if (Math.abs(a) > Math.abs(b)) {
			r = b/a;
			r = Math.abs(a)*Math.sqrt(1+r*r);
		} else if (b != 0) {
			r = a/b;
			r = Math.abs(b)*Math.sqrt(1+r*r);
		} else {
			r = 0.0;
		}
		return r;
	}
	
	/**
	 * Sortable structure holding eigenvalues and their
	 * corresponding eigenvectors.
	 */
	private static class EigenValueAndVector implements Comparable {
		private double eigenValue;
		private double[] eigenVector;
		
		/* (non-Javadoc)
		 * @see java.lang.Comparable#compareTo(T)
		 */
		public int compareTo(Object o) {
			EigenValueAndVector that = (EigenValueAndVector) o;
			if (this.eigenValue < that.eigenValue) return 1;
			if (this.eigenValue > that.eigenValue) return -1;
			return 0;
		}
	}
}
