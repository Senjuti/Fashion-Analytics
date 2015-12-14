package org.kutty.classification;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.kutty.constants.Constants;
import org.kutty.features.FeatureUtil;
import org.kutty.utils.ClassificationUtils;

/** 
 * Defines the Naive Bayes Classifier for Individual Ngrams
 * @author Senjuti Kundu
 */

public class NaiveBayes {

	public Map <String, Double> positive_map;
	public Map <String, Double> negative_map;
	public Map <String, Double> neutral_map;
	public Map <String,Double> spamMap;
	public Map <String,Double> hamMap;
	public Map<String,Double> spamPriorMap;
	public Map<String,Double> sentimentPriorMap;
	public String POSITIVE_FILENAME = "/positive_";
	public String NEGATIVE_FILENAME = "/negative_";
	public String NEUTRAL_FILENAME = "/neutral_";
	public String SPAM_FILENAME = "/spam_";
	public String HAM_FILENAME = "/ham_";
	public String CHANNEL_NAME = "";
	public String NGRAM_PREFIX = "gram";
	public int NGRAM_NUMBER;
	public String TRAIN_FILE;
	public String TYPE;

	/** 
	 * public constructor to initialize the model number and the channel name
	 * @param model_number Integer containing the model number
	 * @param channel_name String containing the channel name
	 */ 
	public NaiveBayes(String channelName,String type,int ngramNum) { 

		this.NGRAM_NUMBER = ngramNum;
		this.CHANNEL_NAME = channelName.toLowerCase().trim();
		this.TYPE = type; 
		
		if (this.CHANNEL_NAME.equalsIgnoreCase("Facebook")) {  
			
			this.TRAIN_FILE = Constants.FACEBOOK_TRAIN_FILE;
		} 
		
		else if (this.CHANNEL_NAME.equalsIgnoreCase("Twitter")) {  
			
			this.TRAIN_FILE = Constants.TWITTER_TRAIN_FILE;
		} 
		
		if (type.equalsIgnoreCase(Constants.SENTIMENT_TYPE)) { 

			POSITIVE_FILENAME = this.CHANNEL_NAME + "/" + this.NGRAM_NUMBER + "-"+ this.NGRAM_PREFIX + this.POSITIVE_FILENAME + this.NGRAM_NUMBER + ".txt";
			NEGATIVE_FILENAME = this.CHANNEL_NAME + "/" + this.NGRAM_NUMBER + "-"+ this.NGRAM_PREFIX + this.NEGATIVE_FILENAME + this.NGRAM_NUMBER +  ".txt";
			NEUTRAL_FILENAME = this.CHANNEL_NAME + "/" + this.NGRAM_NUMBER + "-"+ this.NGRAM_PREFIX + this.NEUTRAL_FILENAME + this.NGRAM_NUMBER + ".txt";

			positive_map = LoadModel.getTrainedModel(POSITIVE_FILENAME);
			negative_map = LoadModel.getTrainedModel(NEGATIVE_FILENAME);
			neutral_map = LoadModel.getTrainedModel(NEUTRAL_FILENAME);
			sentimentPriorMap = getPriorMapNGram(type, this.TRAIN_FILE);
		}

		else if (type.equalsIgnoreCase(Constants.SPAM_TYPE)) { 

			SPAM_FILENAME = this.CHANNEL_NAME + "/" + this.NGRAM_NUMBER + "-"+ this.NGRAM_PREFIX + this.SPAM_FILENAME + this.NGRAM_NUMBER + ".txt";
			HAM_FILENAME = this.CHANNEL_NAME + "/" + this.NGRAM_NUMBER + "-"+ this.NGRAM_PREFIX + this.HAM_FILENAME + this.NGRAM_NUMBER + ".txt";

			spamMap = LoadModel.getTrainedModel(SPAM_FILENAME);
			hamMap = LoadModel.getTrainedModel(HAM_FILENAME);
			spamPriorMap = getPriorMapNGram(type, this.TRAIN_FILE);
		}

	}
	
	/** 
	 * 
	 * @param type
	 * @param filename
	 * @return
	 */
	public static Map<String,Double> getPriorMapNGram(String type,String filename) { 
		
		if (type.equalsIgnoreCase(Constants.SENTIMENT_TYPE)) { 
			
			return Prior.generateLabelSentimentPriorMap(filename);
		}
		
		else if (type.equalsIgnoreCase(Constants.SPAM_TYPE)) { 
			
			return Prior.generateLabelSpamPriorMap(filename);
		}
		
		return null;
		
	} 
	
	/** 
	 * Given contents from other channels classifies them into a particular sentiment
	 * @param text String containing the content
	 * @return String containing the sentiment label (i.e. positive, negative or neutral)
	 */ 

	public String classify(String text) { 

		String processText = preProcessingPipelineForContent(text);

		double [] content_probability = new double[3]; 
		double [] spam_probability = new double[2];  
		Map <String, Double> ngram_probabilty = new HashMap <String, Double>(); 
		Entry<String, Double> max_entry; 
		processText = FeatureUtil.getNGram(processText, this.NGRAM_NUMBER); 
		
		if (this.TYPE.equalsIgnoreCase(Constants.SENTIMENT_TYPE)) { 
			
			content_probability[0] = getProbability(processText, positive_map);
			content_probability[1] = getProbability(processText, negative_map);
			content_probability[2] = getProbability(processText, neutral_map); 
		}
		
		else if (this.TYPE.equalsIgnoreCase(Constants.SPAM_TYPE)) { 
			
			spam_probability[0] = getProbability(processText, spamMap);
			spam_probability[1] = getProbability(processText, hamMap);
		}
		
		ClassificationUtils.convertToPercentage(content_probability);  
		
		if (this.TYPE.equalsIgnoreCase(Constants.SENTIMENT_TYPE)) { 
			
			ngram_probabilty.putAll(getClassLabelAndConfidence(content_probability)); 
		}
		
		else if (this.TYPE.equalsIgnoreCase(Constants.SPAM_TYPE)) {  
			
			ngram_probabilty.putAll(getClassLabelAndConfidenceSpam(spam_probability));
		} 
		
		max_entry = getMaxEntry(ngram_probabilty);

		return max_entry.getKey();
	} 
	
	public String classifyWithPrior(String text) { 

		String processText = preProcessingPipelineForContent(text);

		double [] content_probability = new double[3]; 
		double [] spam_probability = new double[2];  
		Map <String, Double> ngram_probabilty = new HashMap <String, Double>(); 
		Entry<String, Double> max_entry; 
		processText = FeatureUtil.getNGram(processText, this.NGRAM_NUMBER); 
		
		if (this.TYPE.equalsIgnoreCase(Constants.SENTIMENT_TYPE)) { 
			
			content_probability[0] = getProbability(processText, positive_map)*sentimentPriorMap.get(Constants.POSITIVE_LABEL);
			content_probability[1] = getProbability(processText, negative_map)*sentimentPriorMap.get(Constants.NEGATIVE_LABEL);
			content_probability[2] = getProbability(processText, neutral_map)*sentimentPriorMap.get(Constants.NEUTRAL_LABEL); 
		}
		
		else if (this.TYPE.equalsIgnoreCase(Constants.SPAM_TYPE)) { 
			
			spam_probability[0] = getProbability(processText, spamMap)*spamPriorMap.get(Constants.SPAM_LABEL);
			spam_probability[1] = getProbability(processText, hamMap)*spamPriorMap.get(Constants.HAM_LABEL);
		}
		
		ClassificationUtils.convertToPercentage(content_probability);  
		
		if (this.TYPE.equalsIgnoreCase(Constants.SENTIMENT_TYPE)) { 
			
			ngram_probabilty.putAll(getClassLabelAndConfidence(content_probability)); 
		}
		
		else if (this.TYPE.equalsIgnoreCase(Constants.SPAM_TYPE)) {  
			
			ngram_probabilty.putAll(getClassLabelAndConfidenceSpam(spam_probability));
		} 
		
		max_entry = getMaxEntry(ngram_probabilty);

		return max_entry.getKey();
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
	 * For a given model returns the most probable class along with model_number and ngram model
	 * @param a  Array containing the class probabilities
	 * @return Map <String, Double> containing the class label and its associated probability
	 */ 

	public Map<String, Double> getClassLabelAndConfidence(double a[]) { 

		double max = Double.MIN_VALUE; 
		int index = 0; 
		Map <String, Double> max_pair = new HashMap <String, Double>(); 
		
		if (a[0] == a[1] && a[0] == a[2] ) {  
			
			max_pair.put("neutral", a[2]);
			
			return max_pair;
		} 
		
		for (int i = 0; i < a.length; i++) { 

			if (a[i] > max) { 

				max = a[i];
				index = i;
			}
		}

		if (index == 0) { 

			max_pair.put("positive", max); 

		} else if (index == 1) { 

			max_pair.put("negative", max); 

		} else if (index == 2) { 

			max_pair.put("neutral" , max);
		}

		return max_pair;
	}
	
	/** 
	 * Given the probabilities of being in a given class returns the most plausible class label
	 * @param a Double array containing the probabilities of the class labels
	 * @return Map <String, Double> containing the class label appended with ngram_model and model number
	 */ 

	public Map<String, Double> getClassLabelAndConfidenceSpam(double a[]) { 

		double max = Double.MIN_VALUE; 
		int index = 0; 
		Map <String, Double> max_pair = new HashMap <String, Double>(); 
		
		if (a[0] == a[1]) { 
			
			max_pair.put("ham", a[0]); 
			
			return max_pair;
		}
		
		for (int i = 0; i < a.length; i++) { 

			if (a[i] > max) { 

				max = a[i];
				index = i;
			}
		}

		if (index == 0) { 

			max_pair.put("spam", max); 

		} else { 

			max_pair.put("ham", max);
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
	 * Returns the probability of a given text in a given ngram language model
	 * @param tagset String containing the text to be classified
	 * @param ngram_map Map <String, Double> containing the ngram map
	 * @return double containing the probability value
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

	/** 
	 * Main function to test the functionality of the given class
	 * @param args
	 */ 

	public static void main(String args[]) { 

		String text = "Check out LEVIS SIZE 10 LONG STRAIGHT LEG 505 JEANS EXCELLENT CONDITION@LOOK@ #Levis #StraightLeg";
		System.out.println(new NaiveBayes("twitter", "sentiment", 3).classifyWithPrior(text));

	}
}
