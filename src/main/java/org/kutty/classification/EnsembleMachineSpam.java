package org.kutty.classification;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.kutty.dbo.Update;
import org.kutty.utils.ClassificationUtils;

/** 
 * Defines the ensemble machine for Spam classification
 * @author Senjuti Kundu
 */

public class EnsembleMachineSpam {
	
	public Set<Update> ALL_UPDATES;
	public Set<Update> CORRECT_UPDATES;
	public NaiveBayesSpam ensemble[]; 
	public int NUM_OF_MODELS; 
	
	public EnsembleMachineSpam(String channelName,int n) { 
		
		this.ensemble = new NaiveBayesSpam[n]; 
		this.NUM_OF_MODELS = n; 
		
		for (int i = 0; i < this.NUM_OF_MODELS; i++) { 
			
			this.ensemble[i] = new NaiveBayesSpam(i+1, channelName);
		}
	}
	
	/** 
	 * Aggregates different models to get a ensemble average of the result
	 * @param text String containing the text
	 * @param n Integer containing the number of models to be loaded (max 5 now)
	 * @return String containing the class label (i.e. spam or ham)
	 */ 
	
	public String organizeAndActEnsemble(String text) { 
		
		List<String> result_list = new ArrayList<String>(); 
		String result = ""; 
		
		for (int i = 0; i < this.NUM_OF_MODELS; i++) { 
			
			result = ensemble[i].classifySpamOtherChannels(text);
			result = sanitizeString(result);
			result_list.addAll(ClassificationUtils.getAugmentedLabelList(result,(int) ensemble[i].MODEL_WEIGHT));
		}
		
		result = getMaxLabel(result_list);
		ALL_UPDATES = getUpdateObjects(ensemble);
		CORRECT_UPDATES = getCorrectUpdate(ALL_UPDATES, result);
		
		return result;
	}
	
	/** 
	 * Classifies the text taking into account the priors
	 * @param text String containing the text to be classified
	 * @return String containing the class label
	 */
	public String organizeAndActEnsembleWithPriors(String text) { 
		
		List<String> result_list = new ArrayList<String>(); 
		String result = ""; 
		
		for (int i = 0; i < this.NUM_OF_MODELS; i++) { 
			
			result = ensemble[i].classifySpamWithPriors(text);
			result = sanitizeString(result);
			result_list.addAll(ClassificationUtils.getAugmentedLabelList(result,(int) ensemble[i].MODEL_WEIGHT));
		}
		
		result = getMaxLabel(result_list);
		ALL_UPDATES = getUpdateObjects(ensemble);
		CORRECT_UPDATES = getCorrectUpdate(ALL_UPDATES, result);
		
		return result;
	}
	
	/** 
	 * For each ensemble model converts it into an update object
	 * @param ensemble Array containing the ensemble objects
	 * @return Set<Update> containing the set of ensemble models
	 */
	public Set<Update> getUpdateObjects(NaiveBayesSpam ensemble[]) { 
		
		Update update;
		Set<Update> updateSet = new HashSet<Update>();
		
		for (int i = 0; i < ensemble.length; i++) { 
			
			update = new Update();
			update.setClassLabel(sanitizeString(ensemble[i].CLASS_LABEL));
			update.setModelNum(ensemble[i].MODEL_NUMBER);
			update.setNgramNum(ensemble[i].NGRAM_NUMBER);
			update.setProbPercent(ensemble[i].CLASS_PROB);
			updateSet.add(update);
		}
		
		return updateSet;
	}
	
	/** 
	 * Returns the set of update objects matching the correct class label
	 * @param updateSet Set<Update> containing all the update object
	 * @param result String containing the correct class label
	 * @return Set<Update> containing only the update objects with the correct class label
	 */ 
	
	public Set<Update> getCorrectUpdate(Set<Update> updateSet, String result) { 
		
		Set<Update> reducedSet = new HashSet<Update>();
		for (Update temp : updateSet) { 
			if (temp.getClassLabel().equalsIgnoreCase(result)) { 
				reducedSet.add(temp);
			}
		}
		
		return reducedSet;
	}
	
	/** 
	 * Given the output of different classifiers finds the most frequently occuring class
	 * @param result_list List containing the results
	 * @return Class label of the most frequently occuring class
	 */ 
	
	public static <T> T getMaxLabel(List<T> result_list) { 
		
		int count = 0;
		int max_count = Integer.MIN_VALUE; 
		T temp; 
		T max_label = null; 
		
		for (int i = 0; i < result_list.size(); i++) {  
			
			temp = result_list.get(i); 
			count = 0; 
			
			for (int j = 0; j < result_list.size(); j++) { 
				
				if (result_list.get(j) == temp) { 
					count++;
				}
			}
			
			if (count > max_count) { 
				max_count = count;
				max_label = temp;
			}
		}
		
		return max_label;
	}
	
	/** 
	 * Usually the string is cleaned as the class label is appended with the model number and ngram number
	 * for example fake_ngram_number_model_number
	 * @param s String containing the label to be sanitized
	 * @return String sans the appendages
	 */ 
	
	public static String sanitizeString(String s) { 
		
		String clean = s;
		int index = -1;
		
		index = s.indexOf('_');
		
		if (index != -1) { 
			
			clean = s.substring(0, index).trim();
		}
		
		return clean;
	}
	
	public void clearUpdates() { 
		
		ALL_UPDATES.clear();
		CORRECT_UPDATES.clear();
	}
}
