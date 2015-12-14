package org.kutty.utils;

import java.util.ArrayList;
import java.util.List;

/** 
 * Defines a set of utility functions used for matrix operations 
 * 
 * @author Senjuti Kundu
 */
public class MatrixUtils {
	
	/** 
	 * Calculates the sum of the principal diagonal of a square matrix
	 * @param matrix Matrix whose diagonal sum is to be calculated
	 * @return Double containing the sum of the diagonal elements
	 */
	public static <T> double getDiagonalSum(T [][] matrix) { 
		
		double sum = 0.0;
		
		for (int i = 0; i < matrix.length; i++) {  
			
			sum = sum + (Double) matrix[i][i];
		}
		
		return sum;
	}
	
	/** 
	 * Returns the sum of a given row of the matrix (the numbering starts from 1)
	 * @param matrix Matrix whose row sum is to be calculated
	 * @param rowNum Integer containing the row number (starting with 1)
	 * @return Double containing the sum of the row
	 */
	public static <T> double getRowSum(T [][] matrix,int rowNum) { 
		
		double rowSum = 0.0;
		rowNum = rowNum-1;
		
		for (int i = 0; i < matrix[rowNum].length; i++) { 
			
			rowSum = rowSum + (Double) matrix[rowNum][i];
		}
		
		return rowSum;
	}
	
	/** 
	 * Returns the sum of a given column of the matrix (the numbering starts from 1)
	 * @param matrix Matrix whose column sum is to be calculated
	 * @param colNum Integer containing the column number (starting with 1)
	 * @return Double containing the sum of the column
	 */
	public static <T> double getColumnSum(T [][] matrix, int colNum) { 
		
		double colSum = 0.0;
		colNum = colNum-1;
		
		for (int i = 0; i < matrix.length; i++) {  
			
			colSum = colSum + (Double) matrix[i][colNum];
		}
		
		return colSum;
	}
	
	/** 
	 * Calculates the sum of all the elements in the matrix
	 * @param matrix Matrix whose sum is to be calculated
	 * @return Double containing the sum of the given matrix
	 */
	public static <T> double getMatrixSum(T [][] matrix) { 
		
		double sum = 0.0;
		
		for (int i = 0; i < matrix.length; i++) { 
			
			for (int j = 0; j < matrix[i].length; j++) { 
				
				sum = sum + (Double)matrix[i][j];
			}
		}
		
		return sum;
	}
	
	/** 
	 * Adds two given matrices (i.e. the matrices must be of the same order and compatible)
	 * @param first First matrix to add
	 * @param second Second matrix to be added
	 * @return A matrix containing the sum of the two
	 */
	public static <T> Double [][] addMatrix(T[][] first,T[][] second) { 
		
		Double [][] sum = new Double[first.length][second.length];
		
		for (int i = 0; i < first.length; i++) { 
			
			for (int j = 0; j < first[i].length; j++) { 
				
				sum[i][j] = (Double)first[i][j] + (Double)second[i][j];
			}
		}
		
		return sum;
	}
	
	/** 
	 * Given 2 vectors returns the hadamard product of them
	 * @param first First vector
	 * @param second Second vector
	 * @return Double containing the hadamard product
	 */
	public static <T> Double getHadamardProduct(T[] first, T[] second) { 
		
		double hadamard = 0.0; 
		
		for (int i = 0; i < first.length; i++) { 
			
			hadamard = hadamard + ((Double)first[i]*(Double)second[i]);
		}
		
		return hadamard;
	}
	
	/** 
	 * Given a vector multiplies it with a scalar 
	 * @param vector Double [] containing the vector
	 * @param scalar Scalar value which is to be multiplied with the vector
	 * @return Double[] containing the result of the multiplication of the scalar with vector
	 */
	public static <T, T1> Double[] multiplyByScalar(T[] vector, T1 scalar) { 
		
		Double [] v = (Double[]) vector.clone();
		Double scale = (Double)scalar;
		
		for (int i = 0; i < vector.length; i++) { 
			
			v[i] = v [i]*scale;
		}
		
		return v;
	}
	
	/** 
	 * Subtracts the two given vectors first from the second
	 * @param first Double[] containing the first vector
	 * @param second Double [] containing the second vector
	 * @return Double[] containing the difference of the two vectors
	 */
	public static <T> Double [] subtractVectors(T[] first, T[]second) { 
		
		Double [] difference = new Double[first.length];
		
		for (int i = 0; i < first.length; i++) { 
			
			difference[i] = (Double)first[i] - (Double)second[i];
		}
		
		return difference;
	}
	
	/** 
	 * Given two vectors adds them up and returns their sum
	 * @param first First vector which is to be added
	 * @param second Second vector which is added to the first
	 * @return Double[] containing the sum of the two vectors
	 */
	public static <T> Double[] addVectors(T[] first,T[] second) { 
		
		Double [] sum = new Double[first.length];
		
		for (int i = 0; i < first.length; i++) { 
			
			sum[i] = (Double)first[i] + (Double)second[i];
		}
		
		return sum;
	}
	
	/** 
	 * Returns the norm (i.e length) of a given vector
	 * @param vector Array containing the set of numbers
	 * @return Double containing the norm of the given vector
	 */
	public static <T> Double getNorm(T[] vector) { 
		
		Double norm = 0.0;
		for (int i = 0; i < vector.length; i++) { 
			
			norm = norm + (Double)vector[i];
		}
		
		norm = Math.sqrt(norm);
		
		return norm;
	}
	
	/** 
	 * 
	 * @param matrix
	 * @return
	 */
	public static <T> Double[] getMeanVector(T[][] matrix) { 
		
		Double meanVector [] = new Double[matrix[0].length];
		Double sum = 0.0;
		
		for (int i = 0; i < matrix[i].length; i++) {  
			
			sum = 0.0; 
			
			for (int j = 0; j < matrix.length; j++) { 
				
				sum = sum + (Double)matrix[j][i];
			}
			
			meanVector[i] = sum/matrix[i].length;
		}
		
		return meanVector;
	}
	
	/** 
	 * 
	 * @param matrix
	 * @return
	 */
	public static <T> Double[] getStdVector(T[][] matrix) { 
		
		Double stdVector [] = new Double[matrix[0].length];
		List<Double> numberList = new ArrayList<Double>();
		
		for (int i = 0; i < matrix[i].length; i++) {   
			
			numberList.clear(); 
			
			for (int j = 0; j < matrix.length; j++) { 
				
				numberList.add((Double)matrix[j][i]);
			}
			
			stdVector[i] = StatUtils.getStandardDeviation(numberList);
		}
		
		return stdVector;
	}
	
	/** 
	 * 
	 * @param matrix
	 */
	public static void initializeWithZeros(Double [][] matrix) { 
		
		for (int i = 0; i < matrix.length; i++) { 
			
			for (int j = 0; j < matrix[i].length; j++) { 
				
				matrix[i][j] = 0.0;
			}
		}
	}
}
