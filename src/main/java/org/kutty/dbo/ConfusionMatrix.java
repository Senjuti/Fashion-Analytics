package org.kutty.dbo;

import java.util.List;
import java.util.Map;

/** 
 * Defines the confusion matrix which is to be used for benchmarking and other tests
 * @author Senjuti Kundu
 */
public class ConfusionMatrix {
	
	private List<String> classLabelSet;
	private Double matrix[][];
	private Map<String,Integer> classMapping;
	
	/**
	 * @return the classLabelSet
	 */
	public List<String> getClassLabelSet() {
		return classLabelSet;
	}
	
	/**
	 * @param classLabelSet the classLabelSet to set
	 */
	public void setClassLabelSet(List<String> classLabelSet) {
		this.classLabelSet = classLabelSet;
	}
	
	/**
	 * @return the matrix
	 */
	public Double [][] getMatrix() {
		return matrix;
	}
	
	/**
	 * @param matrix the matrix to set
	 */
	public void setMatrix(Double[][] matrix) { 
		
		this.matrix = matrix; 
		
		for (int i = 0; i < matrix.length; i++) {  
			
			for (int j = 0;j < matrix[i].length; j++) { 
				
				this.matrix[i][j] = matrix[i][j];
			}
		}
	}

	/**
	 * @return the classMapping
	 */
	public Map<String, Integer> getClassMapping() {
		return classMapping;
	}

	/**
	 * @param classMapping the classMapping to set
	 */
	public void setClassMapping(Map<String, Integer> classMapping) {
		this.classMapping = classMapping;
	}
	
	/**
	 * Prints the confusion Matrix to the console in a pretty fashion
	 */
	public void prettyPrint()  {
		
		System.out.println("\t");
		
		for (int i = 0; i < classLabelSet.size(); i++) { 
			
			System.out.print(classLabelSet.get(i) + "\t");
		}
		
		System.out.println("------(Predicted)------");
		
		for (int i = 0; i < matrix.length; i++) {   
			
			System.out.print(classLabelSet.get(i) + "\t"); 
			
			for (int j = 0; j < matrix[i].length; j++) { 
				
				System.out.println(matrix[i][j] + "\t");
			}
			
			System.out.println();
		}
	}
}
