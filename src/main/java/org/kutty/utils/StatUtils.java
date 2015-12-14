package org.kutty.utils;

import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

/** 
 * Defines Statistical Utilities commonly used
 * @author Senjuti Kundu
 */
public class StatUtils {
	
	/** 
	 * Returns the median of a list of elements
	 * @param sortList List<Double> containing the elements
	 * @return Double containing the value of the median
	 */
	public static Double getMedian(List<Double> sortList) { 
		
		Collections.sort(sortList);
		int n = sortList.size();
		
		if (n == 0) { 
			
			return 0.0;
		}
		
		if (n == 1) { 
			
			return sortList.get(0);
		}
		
		if (n%2 != 0) { 
			
			return sortList.get(n/2);
		} 
		
		return ((sortList.get(n/2) + sortList.get(n/2 - 1))/2.0);
	}
	
	/** 
	 * Returns the mean of a given list of elements
	 * @param list List<Double> containing the list of numbers
	 * @return Double containing the mean of the elements
	 */
	public static Double getMean(List<Double> list) { 
		
		Double sum = 0.0;
		
		for (Double element : list) { 
			
			sum += element;
		}
		
		return (sum/list.size());
	}
	
	/** 
	 * Calculates the variance of a given list of numbers
	 * @param list List<Double> containing the numbers whose variance is to be calculated
	 * @return Double containing the value of the variance
	 */
	public static Double getVariance(List<Double> list) { 
		
		Double var = 0.0;
		Double mean = getMean(list); 
		
		for (Double element : list) {  
			
			var += Math.pow((element-mean), 2.0);
		}
		
		return (var/list.size());
	}
	
	/** 
	 * Calculates the standard deviation of a given list of numbers
	 * @param list List<Double> containing the numbers whose standard deviation is to be calculated
	 * @return Double containing the value of the standard deviation
	 */
	public static Double getStandardDeviation(List<Double> list) { 
		
		Double std = getVariance(list);
		
		return Math.sqrt(std);
	}
	
	/** 
	 * Standardizes the data by subtracting the mean and dividing by the standard deviation
	 * @param list List<Double> containing the list of Numbers which are to be standardized
	 */
	public static void standardizeData(List<Double> list) { 
		
		Double mean;
		Double standardDeviation;
		Double temp; 
		
		mean = getMean(list);
		standardDeviation = getStandardDeviation(list);
		
		for(int i = 0; i < list.size(); i++) { 
			
			temp = list.get(i);
			temp = (temp - mean)/(standardDeviation + 1);
			list.set(i, temp);
		}
	}
	
	/** 
	 * Standardizes the data by subtracting the mean and dividing by the standard deviation
	 * @param list List<Double> containing the list of Numbers which are to be standardized
	 * @param mean Double containing the mean of the feature
	 * @param standardDeviation Double containing the standard deviation of the feature
	 */
	public static void standardizeData(List<Double> list,Double mean,Double standardDeviation) { 
		
		Double temp;  
		
		for(int i = 0; i < list.size(); i++) { 
			
			temp = list.get(i);
			temp = (temp - mean)/(standardDeviation + 1);
			list.set(i, temp);
		}
	}

	/** 
	 * Removes the outliers present in the list (considering that they following a normal distribution)
	 * @param list List<Double> containing the list of numbers
	 * @param mean Double containing the mean of the numbers
	 * @param std Double containing the standard deviation of the numbers
	 */
	public static void removeOutliers(List<Double> list,Double mean,Double std) { 
		
		Double lowerTail;
		Double upperTail;
		Double element; 
		
		lowerTail = mean - 3*std;
		upperTail = mean + 3*std;
		ListIterator<Double> listIterator = list.listIterator();
		
		while(listIterator.hasNext()) { 
			
			element = listIterator.next();
			
			if (element < lowerTail || element > upperTail) { 
				
				listIterator.remove();
			}
		}
	}
}
