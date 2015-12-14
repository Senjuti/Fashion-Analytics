package org.kutty.benchmark;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kutty.dbo.Benchmark;
import org.kutty.dbo.ConfusionMatrix;
import org.kutty.utils.MatrixUtils;

/** 
 * Defines a set of utility functions for testing model performance
 * 
 * @author Senjuti Kundu
 */

public class PerformanceUtil {
	
	/** 
	 * Calculates the F1 score given the precision and recall
	 * @param precision Double containing the precision
	 * @param recall Double containing the recall
	 * @return Double containing the F1-score which is the harmonic mean of the two
	 */
	public static double f1Score(double precision,double recall) { 
		
		if (precision == 0 && recall == 0) { 
			
			return 0;
		}
		
		return (2*precision*recall)/(precision+recall);
	}
	
	/** 
	 * Calculates the micro F1 score of the given result set
	 * @param resultSet List<Benchmark> containing the result set
	 * @return Double containing the f1 score
	 */
	public static double getMicroF1Score(List<Benchmark> resultSet) { 
		
		double f1Score = 0.0;
		double microPrecision = 0.0;
		double microRecall = 0.0;
		
		microPrecision = getMicroPrecision(resultSet);
		microRecall = getMicroRecall(resultSet);
		f1Score = (2*microPrecision*microRecall)/(microPrecision + microRecall);
		
		return f1Score;
	}
	
	/** 
	 * Calculates the macro F1 score of the given result set
	 * @param resultSet List<Benchmark> containing the result set
	 * @return Double containing the macro F1 score
	 */
	public static double getMacroF1Score(List<Benchmark> resultSet) { 
		
		double f1Score = 0.0;
		double macroPrecision = 0.0;
		double macroRecall = 0.0;
		
		macroPrecision = getMacroPrecision(resultSet);
		macroRecall = getMacroRecall(resultSet);
		f1Score = (2*macroPrecision*macroRecall)/(macroPrecision + macroRecall);
		
		return f1Score;
	}
	
	/** 
	 * Calculates the f1-score per class 
	 * @param resultList List containing the benchmark objects (test set)
	 * @return Map<String,Double> containing the mapping between the class label and the f1-score
	 */
	public static Map<String,Double> getF1ScorePerClass(List<Benchmark> resultList) { 
		
		Map<String,Double> precisionMap = getPrecisionPerClass(resultList);
		Map<String,Double> recallMap = getRecallPerClass(resultList);
		Map<String,Double> f1Map = new HashMap<String,Double>();
		double precision;
		double recall;
		double f1Score; 
		
		for (String key : precisionMap.keySet()) { 
			
			precision = precisionMap.get(key);
			recall = recallMap.get(key);
			f1Score = 2*(precision*recall)/(precision+recall);
			
			f1Map.put(key, f1Score);
		}
		
		return f1Map;
	} 
	
	/** 
	 * Calculates the Area under the Curve for a binary classification problem
	 * @param specificity Double containing the specificity
	 * @param sensitivity Double containing the sensitivity
	 * @return Double containing the area under the curve
	 */
	public static double getAUC(double specificity,double sensitivity) {  
		
		return (specificity + sensitivity)/2.0;
	}
	
	/** 
	 * Calculates the value of the micro AUC (Area Under Curve) for binary classification
	 * @param resultSet List<Benchmark> containing the result set
	 * @return Double containing the value of the AUC
	 */
	public static double getMicroAUC(List<Benchmark> resultSet) { 
		
		double microSpecificity;
		double microSensitivity;
		
		microSpecificity = getMicroSpecificity(resultSet);
		microSensitivity = getMicroRecall(resultSet);
		
		return ((microSpecificity + microSensitivity)/2.0);
	}
	
	/** 
	 * Calculates the macro AUC for binary classification
	 * @param resultList List<Benchmark> containing the result set
	 * @return Double containing the value of the macro AUC
	 */
	public static double getMacroAUC(List<Benchmark> resultList) { 
		
		double macroSpecificity;
		double macroSensitivity;
		
		macroSpecificity = getMacroSpecificity(resultList);
		macroSensitivity = getMacroRecall(resultList);
		
		return ((macroSpecificity + macroSensitivity)/2.0);
	}
	
	/** 
	 * Returns the accuracy of a given test set
	 * @param testSet List of benchmark objects on which the accuracy has to be tested
	 * @return Double containing the accuracy of the result set
	 */
	public static double getAccuracy(List<Benchmark> testSet) { 
		
		double accuracy = 0.0;
		int total = testSet.size() + 1;
	
		for (Benchmark benchmark : testSet) { 
			
			if (benchmark.getActualLabel().equalsIgnoreCase(benchmark.getPredictedLabel())) { 
				accuracy = accuracy + 1;
				
			}
		}
		
		return (accuracy/total);
	}
	
	/** 
	 * For a given result set returns the confusion matrix
	 * @param result List<Benchmark> containing the benchmark objects
	 * @return ConfusionMatrix object containing the necessary information
	 */
	public static ConfusionMatrix getConfusionMatrix(List<Benchmark> result) { 
		
		ConfusionMatrix confusionMatrix = getClassMappingUtil(result);
		Map<String,Integer> classMap = confusionMatrix.getClassMapping();
		Double matrix[][] = confusionMatrix.getMatrix();
		String predicted;
		String actual;
		
		for (Benchmark benchmark : result) {  
			
			predicted = benchmark.getPredictedLabel();
			actual = benchmark.getActualLabel();
			matrix[classMap.get(actual)][classMap.get(predicted)] += 1.0;
		}
		
		confusionMatrix.setMatrix(matrix);
		
		return confusionMatrix;
	}
	
	/** 
	 * Utility function to initialize the class mapping values
	 * @param resultList List<Benchmark> containing the benchmarked data
	 * @return ConfusionMatrix object with the values initialized
	 */
	public static ConfusionMatrix getClassMappingUtil(List<Benchmark> resultList) { 
		
		List<String> classLabel = new ArrayList<String>();
		Map<String,Integer> classMapping = new HashMap<String,Integer>();
		Double [][] matrix;
		ConfusionMatrix confusionMatrix = new ConfusionMatrix();
		
		for (Benchmark benchmark : resultList) { 
			
			if (!classLabel.contains(benchmark.getActualLabel())) {
				
				classLabel.add(benchmark.getActualLabel());
			}
		}
		
		for (int i = 0; i < classLabel.size(); i++) {  
			
			classMapping.put(classLabel.get(i), i);
		}
		
		matrix = new Double[classLabel.size()][classLabel.size()];
		MatrixUtils.initializeWithZeros(matrix);
		confusionMatrix.setClassLabelSet(classLabel);
		confusionMatrix.setClassMapping(classMapping);
		confusionMatrix.setMatrix(matrix);
		
		return confusionMatrix;
	}
	
	/** 
	 * Returns the micro precision value of a given test set
	 * @param resultList List<Benchmark> containing the Benchmark objects
	 * @return Double containing the micro-precision value
	 */
	public static double getMicroPrecision(List<Benchmark> resultList) { 
		
		double microPrecision = 0.0;
		double truePositives = 0.0;
		double predictedPositives = 1.0;
		
		ConfusionMatrix confusionMatrix = getConfusionMatrix(resultList);
		Double [][] matrix = confusionMatrix.getMatrix();
		
		for (int i = 0; i < matrix.length; i++) { 
			
			for (int j = 0;j < matrix[i].length; j++) { 
				
				if (i == j) { 
					
					truePositives += matrix[i][j];  
					
				} else {
					
					predictedPositives += matrix[j][i];
				}
			}
		}
		
		microPrecision = truePositives/predictedPositives;
		
		return microPrecision;
	}
	
	/** 
	 * Returns the Macro Precision value of a given result set
	 * @param resultList List<Benchmark> objects containing the test objects
	 * @return Double containing the value of the macro-precision
	 */
	public static double getMacroPrecision(List<Benchmark> resultList)  { 
		
		double macroPrecision = 0.0;
		Map<String,Double> precisionPerClass = getPrecisionPerClass(resultList);
		for (String key : precisionPerClass.keySet()) { 
			macroPrecision = macroPrecision + precisionPerClass.get(key);
		}
		
		return (macroPrecision/precisionPerClass.size());
	}
	
	/** 
	 * Returns the precision of the test set on each of the given classes
	 * @param resultList List<Benchmark> objects containing the test objects
	 * @return Map<String,Double> containing the mapping between the class label and its precision
	 */
	public static Map<String,Double> getPrecisionPerClass(List<Benchmark> resultList) { 
		
		ConfusionMatrix confusionMatrix = getConfusionMatrix(resultList);
		Map<String,Double> precisionMap = new HashMap<String,Double>();
		List<String> classLabels = confusionMatrix.getClassLabelSet();
		Double [][] matrix = confusionMatrix.getMatrix();
		double [] precisionPerClass = new double[classLabels.size()];
		double predictedPositive = 1.0; 
		
		for (int i = 0; i < matrix.length; i++) {  
			
			predictedPositive = 1.0;
			
			for (int j = 0; j < matrix[i].length; j++) { 
				
				if (i == j) {  
					
					precisionPerClass[i] = matrix[i][j];
				}
				
				predictedPositive = predictedPositive + matrix[j][i];
			}
			
			precisionPerClass[i] = precisionPerClass[i]/predictedPositive;
			precisionMap.put(classLabels.get(i), precisionPerClass[i]);
		}
		
		return precisionMap;
	}
	
	/** 
	 * Returns the recall of the test set on each of the given classes
	 * @param resultList List<Benchmark> objects containing the test objects
	 * @return Map<String,Double> containing the mapping between the class label and its recall
	 */
	public static Map<String,Double> getRecallPerClass(List<Benchmark> resultList) { 
		
		ConfusionMatrix confusionMatrix = getConfusionMatrix(resultList);
		Map<String,Double> recallMap = new HashMap<String,Double>();
		List<String> classLabels = confusionMatrix.getClassLabelSet();
		Double [][] matrix = confusionMatrix.getMatrix();
		double [] recallPerClass = new double[classLabels.size()];
		double actualPositive = 1.0; 
		
		for (int i = 0; i < matrix.length; i++) {  
			
			actualPositive = 1.0;
			
			for (int j = 0; j < matrix[i].length; j++) { 
				
				if (i == j) { 
					
					recallPerClass[i] = matrix[i][j];
				}
				
				actualPositive = actualPositive + matrix[i][j];
			}
			
			recallPerClass[i] = recallPerClass[i]/actualPositive;
			recallMap.put(classLabels.get(i), recallPerClass[i]);
		}
		
		return recallMap;
	}
	
	/** 
	 * Returns the Macro Recall value of a given result set
	 * @param resultList List<Benchmark> objects containing the test objects
	 * @return Double containing the value of the macro-recall
	 */
	public static double getMacroRecall(List<Benchmark> resultList) {  
		
		double macroRecall = 0.0;
		Map<String,Double> recallMap = getRecallPerClass(resultList); 
		
		for (String key : recallMap.keySet()) {  
			
			macroRecall = macroRecall + recallMap.get(key);
		}
		
		return (macroRecall/recallMap.size());
	}
	
	/** 
	 * Returns the micro recall value of a given test set
	 * @param resultList List<Benchmark> containing the Benchmark objects
	 * @return Double containing the micro-recall value
	 */
	public static double getMicroRecall(List<Benchmark> resultList) { 
		
		double microRecall = 0.0;
		double truePositives = 0.0;
		double actualPositives = 1.0;
		
		ConfusionMatrix confusionMatrix = getConfusionMatrix(resultList);
		Double [][] matrix = confusionMatrix.getMatrix();
		
		for (int i = 0; i < matrix.length; i++) { 
			
			for (int j = 0;j < matrix[i].length; j++) { 
				
				if (i == j) { 
					
					truePositives += matrix[i][j]; 
					
				} else {  
				
					actualPositives += matrix[i][j];
				}
			}
		}
		
		microRecall = truePositives/actualPositives;
		
		return microRecall;
	}
	
	/** 
	 * Returns the value of Specificity for a given list of test objects
	 * @param resultList List<Benchmark> containing the result set
	 * @return Double containing the value of specificity
	 */
	public static double getMicroSpecificity(List<Benchmark> resultList) { 
		
		double microSpecificity = 0.0;
		double trueNegatives = 0.0;
		double actualNegatives = 0.0;
		
		ConfusionMatrix confusionMatrix = getConfusionMatrix(resultList);
		Double [][] matrix = confusionMatrix.getMatrix();
		double diagonalSum = MatrixUtils.getDiagonalSum(matrix);
		double matrixSum =  MatrixUtils.getMatrixSum(matrix);
		
		for (int i = 0; i < matrix.length; i++) { 
			
			trueNegatives = trueNegatives + (diagonalSum-matrix[i][i]);
			actualNegatives = actualNegatives + (matrixSum - MatrixUtils.getRowSum(matrix, i+1));
		}
		
		microSpecificity = trueNegatives/actualNegatives;
		
		return microSpecificity;
	}
	
	/** 
	 * Returns the value of specificity per class 
	 * @param resultList List<Benchmark> containing the set of test objects
	 * @return Map<String,Double> containing the class label and specificity
	 */
	public static Map<String,Double> getSpecificityPerClass(List<Benchmark> resultList) { 
		
		Map<String,Double> specificMap = new HashMap<String,Double>();
		double trueNegative;
		double actualNegative;
		double specificity; 
		
		ConfusionMatrix confusionMatrix = getConfusionMatrix(resultList);
		Double [][] matrix = confusionMatrix.getMatrix();
		double diagonalSum = MatrixUtils.getDiagonalSum(matrix);
		double matrixSum =  MatrixUtils.getMatrixSum(matrix);
		List<String> classLabels = confusionMatrix.getClassLabelSet();
		
		for (int i = 0; i < classLabels.size(); i++) { 
			
			trueNegative = diagonalSum - matrix[i][i];
			actualNegative = matrixSum - MatrixUtils.getRowSum(matrix, i+1);
			specificity = trueNegative/actualNegative;
			
			specificMap.put(classLabels.get(i), specificity);
		}
		
		return specificMap;
	}
	
	/** 
	 * Returns the MacroSpecificity of a given result set
	 * @param resultList List<Benchmark> containing the resultSet
	 * @return Double containing the value of the specificity
	 */
	public static double getMacroSpecificity(List<Benchmark> resultList) { 
		
		double macroSpecificity = 0.0;
		Map<String,Double> perClassSpecific = getSpecificityPerClass(resultList);
		
		for (String key : perClassSpecific.keySet()) { 
			
			macroSpecificity = macroSpecificity + perClassSpecific.get(key);
		}
		
		return (macroSpecificity/perClassSpecific.size());
	}
}
