package org.kutty.classification;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.kutty.constants.Constants;
import org.kutty.features.FeatureUtil;
import org.kutty.utils.ClassificationUtils;

/** 
 * Defines the class for spam detection across all channels
 * @author Senjuti Kundu
 */

public class NaiveBayesSpam {

	public Map <String, Double> spam_map;
	public Map <String, Double> ham_map;
	public String SPAM_FILENAME = "/spam_";
	public String HAM_FILENAME = "/ham_";
	public String CHANNEL_NAME = "";
	public int MODEL_NUMBER = 1;
	public int NGRAM_NUMBER;
	public String CLASS_LABEL;
	public double CLASS_PROB; 
	public double MODEL_WEIGHT;
	
	/** 
	 * public constructor to initialize the model number and channel name for spam detection
	 * @param model_number Integer containing the model number
	 * @param channel_name String containing the channel name
	 */ 

	public NaiveBayesSpam(int model_number,String channel_name) { 

		this.MODEL_NUMBER = model_number;
		this.CHANNEL_NAME = channel_name.toLowerCase().trim();
		SPAM_FILENAME = this.CHANNEL_NAME + this.SPAM_FILENAME + this.MODEL_NUMBER + ".txt";
		HAM_FILENAME = this.CHANNEL_NAME + this.HAM_FILENAME + this.MODEL_NUMBER + ".txt"; 
		ModelWeight modelWeight = new ModelWeight(MODEL_NUMBER,this.CHANNEL_NAME,Constants.SPAM_TYPE);
		
		try { 
			this.MODEL_WEIGHT = modelWeight.getModelWeight();
		} catch (IOException e) {
			e.printStackTrace();
		} 

		spam_map = LoadModel.getTrainedModel(SPAM_FILENAME);
		ham_map = LoadModel.getTrainedModel(HAM_FILENAME); 
	}

	/** 
	 * Given a text from channels other than Instagram determines their spamicity
	 * @param text String containing the content which is to be analyzed
	 * @return String containing the class label (spam or ham)
	 */ 
	
	public String classifySpamOtherChannels(String text) { 

		String processText = preProcessingPipelineForContent(text);
		double [] content_probability = new double[2];
		Map <String, Double> ngram_probabilty = new HashMap <String, Double>(); 
		Entry<String, Double> max_entry; 
		String ngramText;
		
		for (int i = 1; i <= 3; i++) { 

			ngramText = FeatureUtil.getNGram(processText, i);
			content_probability[0] = getProbability(ngramText, spam_map);
			content_probability[1] = getProbability(ngramText, ham_map);
			ClassificationUtils.convertToPercentage(content_probability);
			ngram_probabilty.putAll(getClassLabelAndConfidence(content_probability,i));
		} 

		max_entry = getMaxEntry(ngram_probabilty); 
		CLASS_LABEL = max_entry.getKey();
		CLASS_PROB = max_entry.getValue();
		NGRAM_NUMBER = ClassificationUtils.getNGramNumber(max_entry);

		return max_entry.getKey();
	} 
	
	/** 
	 * 
	 * @param text
	 * @return
	 */
	public String classifySpamWithPriors(String text) { 

		String processText = preProcessingPipelineForContent(text);
		double [] content_probability = new double[2];
		Map <String, Double> ngram_probabilty = new HashMap <String, Double>(); 
		Entry<String, Double> max_entry; 
		String ngramText; 
		
		for (int i = 1; i <= 3; i++) { 

			ngramText = FeatureUtil.getNGram(processText, i);
			content_probability[0] = getProbability(ngramText, spam_map);
			content_probability[1] = getProbability(ngramText, ham_map);
			multiplyWithPriors(content_probability);
			ClassificationUtils.convertToPercentage(content_probability);
			ngram_probabilty.putAll(getClassLabelAndConfidence(content_probability,i));
		} 

		max_entry = getMaxEntry(ngram_probabilty); 
		CLASS_LABEL = max_entry.getKey();
		CLASS_PROB = max_entry.getValue();
		NGRAM_NUMBER = ClassificationUtils.getNGramNumber(max_entry);

		return max_entry.getKey();
	} 
	
	/** 
	 * 
	 * @param probabilityArray
	 */
	public void multiplyWithPriors(double probabilityArray[]) { 
		
		Map<String,Double> spamPriorMap;
		String filename = this.CHANNEL_NAME + "/" + Constants.SPAM_PRIOR_FILE; 
		
		try {
			
			spamPriorMap = Prior.getSpamLabelPriors(filename, this.MODEL_NUMBER); 
			
			probabilityArray[0] = probabilityArray[0]*spamPriorMap.get(Constants.SPAM_LABEL);
			probabilityArray[1] = probabilityArray[1]*spamPriorMap.get(Constants.HAM_LABEL);  
			
		} catch (IOException e) { 
			
			e.printStackTrace();
		}
	}
	
	/** 
	 * Returns the map entry which has the maximum value
	 * @param ngram_output HashMap<String,Double> containing the value of the scores and labels for each class
	 * @return Entry<String,Double> which has the maximum value
	 */ 

	public static Entry<String,Double> getMaxEntry(Map<String,Double> ngram_output) { 

		Entry <String,Double> maxentry = null;

		for(Map.Entry<String, Double> temp: ngram_output.entrySet()) { 

			if (maxentry == null || temp.getValue() > maxentry.getValue()) {  

				maxentry = temp;
			}
		}

		return maxentry;
	}

	/** 
	 * Given the probabilities of being in a given class returns the most plausible class label
	 * @param a Double array containing the probabilities of the class labels
	 * @param ngram_model Integer containing the ngram model number
	 * @return Map <String, Double> containing the class label appended with ngram_model and model number
	 */ 

	public Map<String, Double> getClassLabelAndConfidence(double a[],int ngram_model) { 

		double max = Double.MIN_VALUE; 
		int index = 0; 
		Map <String, Double> max_pair = new HashMap <String, Double>(); 
		
		if (a[0] == a[1]) {  
			
			max_pair.put("spam_" + ngram_model + "_" + this.MODEL_NUMBER, max);
			
			return max_pair;
		}
		
		for (int i = 0; i < a.length; i++) { 

			if (a[i] > max) { 

				max = a[i];
				index = i;
			}
		}

		if (index == 0) { 

			max_pair.put("spam_" + ngram_model + "_" + this.MODEL_NUMBER, max); 

		} else { 

			max_pair.put("ham_" + ngram_model + "_" + this.MODEL_NUMBER, max);
		}

		return max_pair;
	}

	/** 
	 * Given a string cleans it up for necessary pre-processing
	 * @param content String containing the content to be pre-processed
	 * @return String which has been sanitized
	 */ 

	public static String preProcessingPipelineForContent(String content) { 

		content = content.toLowerCase().trim();
		content = FeatureUtil.cleanString(content);
		content = FeatureUtil.removeStopWords(content);
		content = FeatureUtil.getStemPerWord(content);

		return content;
	}

	/** 
	 * Returns the probability of a given label for a given class
	 * @param tagset String containing the text which is to be evaluated
	 * @param ngram_map Map <String, Double> containing the class label and its probability
	 * @return Double containing the probability of the class label
	 */ 
	public double getProbability(String tagset,Map<String,Double> ngram_map) { 

		double count = 1.0;
		double temp_count; 

		int index = -1;
		String ngram;
		int previous_position = 0; 

		index = tagset.indexOf('|');

		while(index != -1) { 

			ngram = tagset.substring(previous_position, index);
			ngram = ngram.trim();
			ngram = getTransformedString(ngram); 

			if (ngram_map.containsKey(ngram) && ngram_map.get(ngram) <= 1.0) {  

				temp_count = ngram_map.get(ngram); 

			} else { 

				temp_count = 0.01;
			}

			count = count * temp_count;
			previous_position = index+1;
			index = tagset.indexOf('|',previous_position);
		} 

		return count;
	}

	/** 
	 * Given an ngram encloses it with a pair of braces
	 * @param ngram String containing the NGram
	 * @return String with the enclosing braces
	 */ 

	public static String getTransformedString(String ngram) { 

		String temp = ngram.trim();
		temp = "(" + ngram + ")";

		return temp;
	}
}
