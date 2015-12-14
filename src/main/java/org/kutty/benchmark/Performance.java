package org.kutty.benchmark;

import java.util.List;

import org.kutty.dbo.Benchmark;
import org.kutty.dbo.PerformanceMetrics;

/** 
 * Defines the performance pipeline for the classifiers to calculate different metrics 
 * 
 * @author Senjuti Kundu
 * 
 */
public class Performance {
	
	/** 
	 * Returns a set of minimum performance metrics (i.e. Accuracy and Confusion Matrix)
	 * @param resultSet Lists<Benchmark> containing the test set
	 * @return PerformanceMetrics object containing the set of minimum performance metrics
	 */
	public static PerformanceMetrics getMinimumMetrics(List<Benchmark> resultSet) { 
		
		PerformanceMetrics perfMetrics = new PerformanceMetrics();
		
		perfMetrics.setAccuracy(PerformanceUtil.getAccuracy(resultSet));
		perfMetrics.setConfusionMatrix(PerformanceUtil.getConfusionMatrix(resultSet).getMatrix());
		
		return perfMetrics;
	}
	
	/** 
	 * Returns the set of all Micro performance metrics for a given test set
	 * @param resultSet List<Benchmark> containing the test set
	 * @return PerformanceMetrics containing the micro metrics
	 */
	public static PerformanceMetrics getMicroMetrics(List<Benchmark> resultSet) { 
		
		PerformanceMetrics perfMetrics = new PerformanceMetrics();
		
		perfMetrics.setAccuracy(PerformanceUtil.getAccuracy(resultSet));
		perfMetrics.setConfusionMatrix(PerformanceUtil.getConfusionMatrix(resultSet).getMatrix());
		perfMetrics.setMicroPrecision(PerformanceUtil.getMicroPrecision(resultSet));
		perfMetrics.setMicroRecall(PerformanceUtil.getMicroRecall(resultSet));
		perfMetrics.setMicroSpecificity(PerformanceUtil.getMicroSpecificity(resultSet));
		perfMetrics.setMicroF1Score(PerformanceUtil.getMicroF1Score(resultSet));
		
		return perfMetrics;
	}
	
	/** 
	 * Returns a set of Macro performance metrics of a given result set
	 * @param resultSet List<Benchmark> containing the result set
	 * @return PerformanceMetrics object containing the macro metrics
	 */
	public static PerformanceMetrics getMacroMetrics(List<Benchmark> resultSet) { 
		
		PerformanceMetrics perfMetrics = new PerformanceMetrics();
		
		perfMetrics.setAccuracy(PerformanceUtil.getAccuracy(resultSet));
		perfMetrics.setConfusionMatrix(PerformanceUtil.getConfusionMatrix(resultSet).getMatrix());
		perfMetrics.setMacroPrecision(PerformanceUtil.getMacroPrecision(resultSet));
		perfMetrics.setMacroRecall(PerformanceUtil.getMacroRecall(resultSet));
		perfMetrics.setMacroSpecificity(PerformanceUtil.getMacroSpecificity(resultSet));
		perfMetrics.setMacroF1Score(PerformanceUtil.getMacroF1Score(resultSet));
		
		return perfMetrics;
	}
	
	/** 
	 * Calculates all the performance metrics for a given result list
	 * @param resultList List<Benchmark> containing the resultList
	 * @return PerformanceMetrics object containing the relevant metrics
	 */
	public static PerformanceMetrics getAllMetrics(List<Benchmark> resultList) { 
		
		PerformanceMetrics perfMetrics = new PerformanceMetrics();
		
		perfMetrics.setAccuracy(PerformanceUtil.getAccuracy(resultList));
		perfMetrics.setConfusionMatrix(PerformanceUtil.getConfusionMatrix(resultList).getMatrix());
		perfMetrics.setMacroF1Score(PerformanceUtil.getMacroF1Score(resultList));
		perfMetrics.setMacroPrecision(PerformanceUtil.getMacroPrecision(resultList));
		perfMetrics.setMacroRecall(PerformanceUtil.getMacroRecall(resultList));
		perfMetrics.setMacroSpecificity(PerformanceUtil.getMacroSpecificity(resultList));
		perfMetrics.setMicroF1Score(PerformanceUtil.getMicroF1Score(resultList));
		perfMetrics.setMicroPrecision(PerformanceUtil.getMicroPrecision(resultList));
		perfMetrics.setMicroRecall(PerformanceUtil.getMicroRecall(resultList));
		perfMetrics.setMicroSpecificity(PerformanceUtil.getMicroSpecificity(resultList));
		perfMetrics.setPrecisionPerClass(PerformanceUtil.getPrecisionPerClass(resultList));
		perfMetrics.setRecallPerClass(PerformanceUtil.getRecallPerClass(resultList));
		perfMetrics.setSpecificityPerClass(PerformanceUtil.getSpecificityPerClass(resultList));
		perfMetrics.setF1PerClass(PerformanceUtil.getF1ScorePerClass(resultList));
		
		if (perfMetrics.getPrecisionPerClass().size() == 2) { 
			
			perfMetrics.setAUC(PerformanceUtil.getMicroAUC(resultList));
		}
		
		return perfMetrics;
	}
}
