package org.kutty.classification;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.kutty.constants.Constants;
import org.kutty.features.FeatureUtil;
import org.kutty.utils.ClassificationUtils;

/** 
 * Defines the pipeline for sentiment analysis of posts from all channels
 * @author Senjuti Kundu
 */ 

public class NaiveBayesSentiment {

	public Map <String, Double> positive_map;
	public Map <String, Double> negative_map;
	public Map <String, Double> neutral_map;
	public String POSITIVE_FILENAME = "/positive_";
	public String NEGATIVE_FILENAME = "/negative_";
	public String NEUTRAL_FILENAME = "/neutral_";
	public String CHANNEL_NAME = "twitter";
	public int MODEL_NUMBER = 1;
	public int NGRAM_NUMBER;
	public String CLASS_LABEL;
	public double CLASS_PROB;
	public double MODEL_WEIGHT; 

	/** 
	 * public constructor to initialize the model number and the channel name
	 * @param model_number Integer containing the model number
	 * @param channel_name String containing the channel name
	 */ 

	public NaiveBayesSentiment(int model_number,String channel_name) { 

		this.MODEL_NUMBER = model_number;
		this.CHANNEL_NAME = channel_name.toLowerCase().trim();
		POSITIVE_FILENAME = this.CHANNEL_NAME + this.POSITIVE_FILENAME + this.MODEL_NUMBER + ".txt";
		NEGATIVE_FILENAME = this.CHANNEL_NAME + this.NEGATIVE_FILENAME + this.MODEL_NUMBER + ".txt";
		NEUTRAL_FILENAME = this.CHANNEL_NAME + this.NEUTRAL_FILENAME + this.MODEL_NUMBER + ".txt"; 
		ModelWeight modelWeight = new ModelWeight(MODEL_NUMBER,this.CHANNEL_NAME,Constants.SENTIMENT_TYPE);

		try { 
			this.MODEL_WEIGHT = modelWeight.getModelWeight();
		} catch (IOException e) {
			e.printStackTrace();
		}

		positive_map = LoadModel.getTrainedModel(POSITIVE_FILENAME);
		negative_map = LoadModel.getTrainedModel(NEGATIVE_FILENAME);
		neutral_map = LoadModel.getTrainedModel(NEUTRAL_FILENAME); 

	}


	/** 
	 * Given contents from other channels classifies them into a particular sentiment
	 * @param text String containing the content
	 * @return String containing the sentiment label (i.e. positive, negative or neutral)
	 */ 

	public String classifySentimentOtherChannels(String text) { 

		String processText = preProcessingPipelineForContent(text);
		String ngramText;
		double [] content_probability = new double[3]; 
		Map <String, Double> ngram_probabilty = new HashMap <String, Double>(); 
		Entry<String, Double> max_entry; 

		for (int i = 1; i <= 5; i++) { 

			ngramText = FeatureUtil.getNGram(processText, i);
			content_probability[0] = getProbability(ngramText, positive_map);
			content_probability[1] = getProbability(ngramText, negative_map);
			content_probability[2] = getProbability(ngramText, neutral_map);
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
	 * Given contents from other channels classifies them into a particular sentiment
	 * @param text String containing the content
	 * @return String containing the sentiment label (i.e. positive, negative or neutral)
	 */ 

	public String classifySentimentWithPrior(String text) { 

		String processText = preProcessingPipelineForContent(text);
		String ngramText;
		double [] content_probability = new double[3]; 
		Map <String, Double> ngram_probabilty = new HashMap <String, Double>(); 
		Entry<String, Double> max_entry; 

		for (int i = 1; i <= 5; i++) { 

			ngramText = FeatureUtil.getNGram(processText, i);
			content_probability[0] = getProbability(ngramText, positive_map);
			content_probability[1] = getProbability(ngramText, negative_map);
			content_probability[2] = getProbability(ngramText, neutral_map);
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
	public void multiplyWithPriors(double[] probabilityArray) { 
		
		String filename = this.CHANNEL_NAME + "/" + Constants.SENTIMENT_PRIOR_FILE;
		Map<String,Double> sentimentPriorMap; 
		
		try { 
			
			sentimentPriorMap = Prior.getSentimentLabelPriors(filename, this.MODEL_NUMBER); 
		
			probabilityArray[0] = probabilityArray[0]*sentimentPriorMap.get(Constants.POSITIVE_LABEL);
			probabilityArray[1] = probabilityArray[1]*sentimentPriorMap.get(Constants.NEGATIVE_LABEL);
			probabilityArray[2] = probabilityArray[2]*sentimentPriorMap.get(Constants.NEUTRAL_LABEL); 
			
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
	 * For a given model returns the most probable class along with model_number and ngram model
	 * @param a  Array containing the class probabilities
	 * @param ngram_model Integer containing the ngram language model number (i.e. 1,2,3)
	 * @return Map <String, Double> containing the class label and its associated probability
	 */ 

	public Map<String, Double> getClassLabelAndConfidence(double a[],int ngram_model) { 

		double max = Double.MIN_VALUE; 
		int index = 0; 
		Map <String, Double> max_pair = new HashMap <String, Double>(); 

		for (int i = 0; i < a.length; i++) { 

			if (a[i] > max) { 

				max = a[i];
				index = i;
			}
		}

		if (index == 0) { 

			max_pair.put("positive_" + ngram_model + "_" + this.MODEL_NUMBER, max); 

		} else if (index == 1) { 

			max_pair.put("negative_" + ngram_model + "_" + this.MODEL_NUMBER, max); 

		} else if (index == 2) { 

			max_pair.put("neutral_" + ngram_model + "_" + this.MODEL_NUMBER, max);
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
	 * Given a tagset space separates it and removes the stopwords
	 * @param tagset String containing the tagset
	 * @param n Integer containing the value of N for Ngram
	 * @return String containing the cleaned and separated tagset
	 */ 

	public static String preProcessingPipelineForTag(String tagset) { 

		tagset = tagset.replace(","," ");
		tagset = tagset.toLowerCase();
		tagset = FeatureUtil.cleanString(tagset);
		tagset = FeatureUtil.removeStopWords(tagset);
		tagset = FeatureUtil.getStemPerWord(tagset);

		return tagset;
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

		String text = "#Burberry & #TrueReligion What A Wonderful Combination!!! #JustAnotherDay #WaddupTwitter";
		System.out.println(new NaiveBayesSentiment(1,"facebook").classifySentimentOtherChannels(text));

	}

}
