package org.kutty.dbo;

import java.util.Map;

/** 
 * Defines a set of Performance metrics associated with a classifer
 * @author Senjuti Kundu
 */
public class PerformanceMetrics {

	private double accuracy;
	private double microPrecision;
	private double macroPrecision;
	private double microRecall;
	private double macroRecall;
	private double macroF1Score;
	private double microF1Score;
	private double microSpecificity;
	private double macroSpecificity;
	private Double [][] confusionMatrix;
	private double AUC;
	private Map<String,Double> precisionPerClass;
	private Map<String,Double> recallPerClass;
	private Map<String,Double> f1PerClass;
	private Map<String,Double> specificityPerClass; 
	
	/**
	 * 
	 * @return the accuracy
	 */
	public double getAccuracy() { 
		
		return accuracy;
	}
	
	/**
	 * 
	 * @param accuracy the accuracy to set
	 */
	public void setAccuracy(double accuracy) { 
		
		this.accuracy = accuracy;
	} 
	
	/** 
	 * 
	 * @return the microPrecision
	 */
	public double getMicroPrecision() { 
		
		return microPrecision;
	} 
	
	/** 
	 * 
	 * @param microPrecision the microPrecision to set
	 */
	public void setMicroPrecision(double microPrecision) { 
		
		this.microPrecision = microPrecision;
	} 
	
	/** 
	 * 
	 * @return the macroPrecision
	 */
	public double getMacroPrecision() { 
		
		return macroPrecision;
	} 
	
	/** 
	 * 
	 * @param macroPrecision the macroPrecision to set
	 */
	public void setMacroPrecision(double macroPrecision) { 
		
		this.macroPrecision = macroPrecision;
	} 
	
	/** 
	 * 
	 * @return the microRecall
	 */
	public double getMicroRecall() { 
		
		return microRecall;
	} 
	
	/** 
	 * 
	 * @param microRecall the microRecall to set
	 */
	public void setMicroRecall(double microRecall) { 
		
		this.microRecall = microRecall;
	} 
	
	/**
	 * 
	 * @return the macroRecall
	 */
	public double getMacroRecall() { 
		
		return macroRecall;
	} 
	
	/**
	 * 
	 * @param macroRecall the macroRecall to set
	 */
	public void setMacroRecall(double macroRecall) { 
		
		this.macroRecall = macroRecall;
	} 
	
	/** 
	 * 
	 * @return the macroF1Score
	 */
	public double getMacroF1Score() { 
		
		return macroF1Score;
	}
	
	/**
	 * 
	 * @param macroF1Score the macroF1Score to set
	 */
	public void setMacroF1Score(double macroF1Score) { 
		
		this.macroF1Score = macroF1Score;
	}
	
	/**
	 * 
	 * @return the microF1Score
	 */
	public double getMicroF1Score() { 
		
		return microF1Score;
	}
	
	/**
	 * 
	 * @param microF1Score the microF1Score to set
	 */
	public void setMicroF1Score(double microF1Score) {
		
		this.microF1Score = microF1Score;
	}
	
	/**
	 * 
	 * @return the confusionMatrix
	 */
	public Double[][] getConfusionMatrix() { 
		
		return confusionMatrix;
	}
	
	/**
	 * 
	 * @param confusionMatrix the confusionMatrix to set
	 */
	public void setConfusionMatrix(Double[][] confusionMatrix) { 
		
		this.confusionMatrix = confusionMatrix;
	}
	
	/**
	 * 
	 * @return the microSpecificity
	 */
	public double getMicroSpecificity() {
		return microSpecificity;
	}
	
	/**
	 * @param microSpecificity the microSpecificity to set
	 */
	public void setMicroSpecificity(double microSpecificity) {
		this.microSpecificity = microSpecificity;
	}
	
	/**
	 * @return the macroSpecificity
	 */
	public double getMacroSpecificity() {
		return macroSpecificity;
	}
	
	/**
	 * @param macroSpecificity the macroSpecificity to set
	 */
	public void setMacroSpecificity(double macroSpecificity) {
		this.macroSpecificity = macroSpecificity;
	}
	
	/**
	 * @return the aUC
	 */
	public double getAUC() {
		return AUC;
	}
	
	/**
	 * @param aUC the aUC to set
	 */
	public void setAUC(double aUC) {
		AUC = aUC;
	}
	
	/**
	 * @return the precisionPerClass
	 */
	public Map<String, Double> getPrecisionPerClass() {
		return precisionPerClass;
	}
	
	/**
	 * @param precisionPerClass the precisionPerClass to set
	 */
	public void setPrecisionPerClass(Map<String, Double> precisionPerClass) {
		this.precisionPerClass = precisionPerClass;
	}
	
	/**
	 * @return the recallPerClass
	 */
	public Map<String, Double> getRecallPerClass() {
		return recallPerClass;
	}
	
	/**
	 * @param recallPerClass the recallPerClass to set
	 */
	public void setRecallPerClass(Map<String, Double> recallPerClass) {
		this.recallPerClass = recallPerClass;
	}
	
	/**
	 * @return the f1PerClass
	 */
	public Map<String, Double> getF1PerClass() {
		return f1PerClass;
	}
	
	/**
	 * @param f1PerClass the f1PerClass to set
	 */
	public void setF1PerClass(Map<String, Double> f1PerClass) {
		
		this.f1PerClass = f1PerClass;
	}
	
	/**
	 * @return the specificityPerClass
	 */
	public Map<String, Double> getSpecificityPerClass() {
		
		return specificityPerClass;
	}
	
	/**
	 * @param specificityPerClass the specificityPerClass to set
	 */
	public void setSpecificityPerClass(Map<String, Double> specificityPerClass) {
		
		this.specificityPerClass = specificityPerClass;
	}
}
