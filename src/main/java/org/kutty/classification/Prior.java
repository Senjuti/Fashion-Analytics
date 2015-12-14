package org.kutty.classification;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kutty.constants.Constants;
import org.kutty.features.FeatureUtil;
import org.kutty.features.LabelCountUtil;
import org.kutty.features.Post;

/** 
 * Calculates the prior probabilities of the classes and provides methods to interact with the same 
 * 
 * @author Senjuti Kundu
 */

public class Prior {

	/** 
	 * Given a Map of label counts returns the prior probability Map for a given label count map
	 * @param labelCountMap Map<String,Integer> containing the labels and their counts
	 * @return Map<String,Double> containing the mapping between the priors and the labels
	 */

	public static Map<String,Double> getPriorMap(Map<String,Integer> labelCountMap) { 

		double totalCount = 1.0; 
		double count; 
		Map<String,Double> labelPriorMap = new HashMap<String,Double>();

		for (Integer labelCount: labelCountMap.values()) {

			totalCount = totalCount + labelCount;
		}

		for (String label : labelCountMap.keySet()) { 

			count = labelCountMap.get(label);
			count = count/totalCount;
			labelPriorMap.put(label, count);
		}

		return labelPriorMap;
	}

	/** 
	 * Given a channel name and model number returns the label count map for sentiments
	 * @param channel String containing the channel name
	 * @param modelNumber Integer containing the model number
	 * @return Map<String,Integer> containing the mapping between the class label and its count
	 */

	public static Map<String,Integer> generateLabelSentimentCountMap(String channel,int modelNumber) { 

		channel = channel.toLowerCase().trim();
		String filename = channel + "/" + "split_" + modelNumber + ".txt";
		Map<String,Integer> labelCountMap;
		List<Post> postList = new ArrayList<Post>();

		FeatureUtil.populateOtherChannelData(filename, postList);
		labelCountMap = LabelCountUtil.getSentimentLabelCount(postList);

		return labelCountMap;
	} 
	
	/** 
	 * 
	 * @param filename
	 * @return
	 */
	public static Map<String,Integer> generateLabelSentimentCountMap(String filename) { 

		Map<String,Integer> labelCountMap;
		List<Post> postList = new ArrayList<Post>();

		FeatureUtil.populateOtherChannelData(filename, postList);
		labelCountMap = LabelCountUtil.getSentimentLabelCount(postList);

		return labelCountMap;
	} 
	
	/** 
	 * 
	 * @param filename
	 * @return
	 */
	public static Map<String,Double> generateLabelSentimentPriorMap(String filename) { 

		Map<String,Integer> labelCountMap;
		Map<String,Double> priorMap;
		List<Post> postList = new ArrayList<Post>();

		FeatureUtil.populateOtherChannelData(filename, postList);
		labelCountMap = LabelCountUtil.getSentimentLabelCount(postList);
		priorMap = getPriorMap(labelCountMap); 
		
		return priorMap;
	} 

	
	/** 
	 * Given a channel name and model number returns the label count map for spam labels
	 * @param channel String containing the channel name
	 * @param modelNumber Integer containing the model number
	 * @return Map<String,Integer> containing the mapping between the class label and its count
	 */

	public static Map<String,Integer> generateLabelSpamCountMap(String channel,int modelNumber) { 

		channel = channel.toLowerCase().trim();
		String filename = channel + "/" + "split_" + modelNumber + ".txt";
		Map<String,Integer> labelCountMap;
		List<Post> postList = new ArrayList<Post>();

		FeatureUtil.populateOtherChannelData(filename, postList);
		labelCountMap = LabelCountUtil.getSpamLabelCount(postList);

		return labelCountMap;
	} 
	
	/** 
	 * 
	 * @param filename
	 * @return
	 */
	public static Map<String,Integer> generateLabelSpamCountMap(String filename) { 

		Map<String,Integer> labelCountMap;
		List<Post> postList = new ArrayList<Post>();

		FeatureUtil.populateOtherChannelData(filename, postList);
		labelCountMap = LabelCountUtil.getSpamLabelCount(postList);

		return labelCountMap;
	} 
	
	/** 
	 * 
	 * @param filename
	 * @return
	 */
	public static Map<String,Double> generateLabelSpamPriorMap(String filename) { 
	
		Map<String,Integer> labelCountMap;
		Map<String,Double> spamPriorMap;
		List<Post> postList = new ArrayList<Post>();

		FeatureUtil.populateOtherChannelData(filename, postList);
		labelCountMap = LabelCountUtil.getSpamLabelCount(postList);
		spamPriorMap = getPriorMap(labelCountMap); 
		
		return spamPriorMap;

	}
	
	/** 
	 * Writes the sentiment prior probabilities to an external text file
	 * @param filename String containing the filename
	 * @param priorMap Map<String,Double> containing the prior probability map
	 * @param modelNumber Integer containing the model number
	 * @throws IOException
	 */

	public static void writeSentimentPriorsToFile(String filename,Map<String,Double> priorMap,int modelNumber) throws IOException { 

		BufferedWriter bw;
		FileWriter fw;
		File f = new File(filename);
		String listPrior;

		if (!f.exists()) { 
			f.createNewFile();
		}

		fw = new FileWriter(f,true);
		bw = new BufferedWriter(fw); //Format of file is : modelNo = positive,negative,neutral

		listPrior = priorMap.get(Constants.POSITIVE_LABEL) + "," + priorMap.get(Constants.NEGATIVE_LABEL) 
				+ "," + priorMap.get(Constants.NEUTRAL_LABEL);
		listPrior = modelNumber + "=" + listPrior;
		bw.write(listPrior);
		bw.newLine();

		bw.close();
		fw.close();

	}

	/** 
	 * Writes the spam priors to a given external file
	 * @param filename String containing the filename
	 * @param priorMap Map<String,Double> containing the prior probability map
	 * @param modelNumber Integer containing the modelNumber
	 * @throws IOException
	 */

	public static void writeSpamPriorsToFile(String filename,Map<String,Double> priorMap,int modelNumber) throws IOException { 

		BufferedWriter bw;
		FileWriter fw;
		File f = new File(filename);
		String listPrior;

		if (!f.exists()) { 
			f.createNewFile();
		}

		fw = new FileWriter(f,true);
		bw = new BufferedWriter(fw); //Format of file is : modelNo = spam,ham

		listPrior = priorMap.get(Constants.SPAM_LABEL) + "," + priorMap.get(Constants.HAM_LABEL); 
		listPrior = modelNumber + "=" + listPrior;
		bw.write(listPrior);
		bw.newLine();

		bw.close();
		fw.close();

	}


	/** 
	 * Returns a Map containing the sentiment label prior distribution 
	 * @param filename String containing the filename
	 * @param modelNumber Integer containing the model number
	 * @return Map<String,Double> containing the mapping between sentiment label and its prior probability
	 * @throws IOException
	 */

	public static Map<String,Double> getSentimentLabelPriors(String filename,int modelNumber) throws IOException { 

		BufferedReader br;
		FileReader fr;
		File f;
		Map<String,Double> sentimentLabelMap;
		String line;
		int model;
		int index;

		f = new File(filename); 
		index = -1;
		sentimentLabelMap = new HashMap<String,Double>(); 

		if (f.exists()) {  

			fr = new FileReader(f);
			br = new BufferedReader(fr); 

			while((line = br.readLine())!=null) { 
				index = line.indexOf('=');
				if(index != -1) { 
					model = Integer.valueOf(line.substring(0, index).trim());
					if (model == modelNumber) { 
						String [] labels = line.substring(index+1).trim().split(",");
						sentimentLabelMap.put(Constants.POSITIVE_LABEL,Double.valueOf(labels[0]));
						sentimentLabelMap.put(Constants.NEGATIVE_LABEL,Double.valueOf(labels[1]));
						sentimentLabelMap.put(Constants.NEUTRAL_LABEL,Double.valueOf(labels[2]));
						break;
					}
				}
			}

			br.close();
			fr.close();
		}

		return sentimentLabelMap;
	}

	/** 
	 * Returns a Map containing the spam label prior distribution 
	 * @param filename String containing the filename
	 * @param modelNumber Integer containing the model number
	 * @return Map<String,Double> containing the mapping between spam label and its prior probability
	 * @throws IOException
	 */

	public static Map<String,Double> getSpamLabelPriors(String filename,int modelNumber) throws IOException { 

		BufferedReader br;
		FileReader fr;
		File f;
		Map<String,Double> spamLabelMap;
		String line;
		int model;
		int index;

		f = new File(filename); 
		index = -1;
		spamLabelMap = new HashMap<String,Double>(); 

		if (f.exists()) {  

			fr = new FileReader(f);
			br = new BufferedReader(fr); 

			while((line = br.readLine())!=null) { 
				index = line.indexOf('=');
				if(index != -1) { 
					model = Integer.valueOf(line.substring(0, index).trim());
					if (model == modelNumber) { 
						String [] labels = line.substring(index+1).trim().split(",");
						spamLabelMap.put(Constants.SPAM_LABEL,Double.valueOf(labels[0]));
						spamLabelMap.put(Constants.HAM_LABEL,Double.valueOf(labels[1]));
						break;
					}
				}
			}

			br.close();
			fr.close();
		}

		return spamLabelMap;
	}


	/** 
	 * Defines the pipeline for sentiment prior calculation
	 * @param channel String containing the channel name
	 * @param modelNumber Integer containing the model number
	 * @param fileToWrite String containing the file to which the data is to be written
	 */

	public static void sentimentPriorPipeline(String channel,int modelNumber,String fileToWrite) { 

		Map<String,Integer> labelCountMap = generateLabelSentimentCountMap(channel, modelNumber);
		Map<String,Double> sentimentPriorMap = getPriorMap(labelCountMap);
		try {
			writeSentimentPriorsToFile(fileToWrite, sentimentPriorMap, modelNumber);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/** 
	 * Writes the prior probabilities for all models to a given file
	 * @param channel String containing the channel name
	 * @param fileToWrite String containing the file to which data is written
	 * @param maxModelNumber Integer containing the maximum model number
	 */

	public static void sentimentPriorAllModels(String channel,int maxModelNum,String fileToWrite) { 

		for (int i = 1; i <= maxModelNum; i++) { 
			sentimentPriorPipeline(channel,i,fileToWrite);
		}
	}

	/** 
	 * Defines the pipeline for spam prior calculation
	 * @param channel String containing the channel name
	 * @param modelNumber Integer containing the model number
	 * @param fileToWrite String containing the file to which the data is to be written
	 */

	public static void spamPriorPipeline(String channel,int modelNumber,String fileToWrite) { 

		Map<String,Integer> labelCountMap = generateLabelSpamCountMap(channel, modelNumber);
		Map<String,Double> spamPriorMap = getPriorMap(labelCountMap);
		try {
			writeSpamPriorsToFile(fileToWrite, spamPriorMap, modelNumber);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/** 
	 * Writes the prior probabilities for all models to a given file
	 * @param channel String containing the channel name
	 * @param fileToWrite String containing the file to which data is written
	 * @param maxModelNumber Integer containing the maximum model number
	 */

	public static void spamPriorAllModels(String channel,int maxModelNumber,String fileToWrite) { 

		for (int i = 1; i <= maxModelNumber; i++) { 
			spamPriorPipeline(channel,i,fileToWrite);
		}
	} 

	public static void main(String args[]) throws IOException { 

		System.out.println(generateLabelSentimentCountMap("Facebook_test_annotated.txt"));
		sentimentPriorAllModels("facebook", 5, "facebook/" + Constants.SENTIMENT_PRIOR_FILE);
		spamPriorAllModels("facebook", 5, "facebook/" + Constants.SPAM_PRIOR_FILE);
	}
}
