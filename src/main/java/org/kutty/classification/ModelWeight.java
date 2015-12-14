package org.kutty.classification;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.kutty.constants.Constants;
import org.kutty.dbo.Benchmark;
import org.kutty.features.FeatureUtil;
import org.kutty.features.Post;
import org.kutty.utils.ClassificationUtils;

/** 
 * Calculates the accuracy of each model and saves it for later use
 * @author Senjuti Kundu
 */

public class ModelWeight {
	
	public int modelNumber;
	public String channelName;
	public String type;
	public String modelFilePath;
	public String heldSetPath;
	
	public ModelWeight() { 
		
	}
	
	/** 
	 * public constructor used to set the model number and channel name
	 * @param modelNumber Integer containing the model number
	 * @param channelName String containing the channel name
	 */
	public ModelWeight(int modelNumber,String channelName,String type) {  
		
		this.modelNumber = modelNumber;
		this.channelName = channelName;
		this.type = type;
		this.heldSetPath = this.channelName.toLowerCase().trim() + "/" + "held_out_set.txt"; 
		
		if (this.type.equalsIgnoreCase(Constants.SENTIMENT_TYPE)) {
			this.modelFilePath = this.channelName.toLowerCase().trim() + "/" + "sentiment_model_weights.txt";
			
		}
		else if(this.type.equalsIgnoreCase(Constants.SPAM_TYPE)) { 
			this.modelFilePath = this.channelName.toLowerCase().trim() + "/" + "spam_model_weights.txt";
		}
	}
	
	/** 
	 * Returns the weight associated with a particular model
	 * @return Double containing the model weight
	 * @throws IOException
	 */
	public Double getModelWeight()throws IOException { // Format of the file is ModelNum=Right,Wrong
		
		String temp = "";
		BufferedReader br;
		FileReader fr;
		double weight = 0.0;
		int index = -1;
		int model;
		
		fr = new FileReader(this.modelFilePath);
		br = new BufferedReader(fr);
		
		while((temp = br.readLine()) != null) { 
			
			index = temp.indexOf("=");
			if(index != -1) { 
				model = Integer.valueOf(temp.substring(0, index).trim());
				if (model == this.modelNumber) { 
					String [] weightArray = temp.substring(index+1).trim().split(",");
					int correct = Integer.valueOf(weightArray[0]);
					int wrong = Integer.valueOf(weightArray[1]);
					weight = (correct/(wrong+1));
					weight = Math.exp(weight);
					weight = Math.round(weight); // Test with Math.round()
				}
			}
		}
		
		br.close();
		fr.close();
		
		return weight;
	}
	
	public List<Benchmark> getBenchmarkList(String filenameHeldOutSet) { 
		
		List<Post> postList = new ArrayList<Post>();
		FeatureUtil.populateOtherChannelData(filenameHeldOutSet, postList);
		List<Benchmark> resultList = ClassificationUtils.getBenchmarkAdaptor(postList, this.type);
		
		return resultList;
	}
	
	public void calculateModelPrediction(List<Benchmark> resultList) { 
		
		String predictedLabel = ""; 
		
		if (this.type.equalsIgnoreCase(Constants.SENTIMENT_TYPE)) { 
			
			NaiveBayesSentiment naive = new NaiveBayesSentiment(this.modelNumber, this.channelName); 
			
			for (Benchmark benchmark : resultList) { 
				
				predictedLabel = ClassificationUtils.sanitizeString(naive.classifySentimentOtherChannels(benchmark.getContent()));
				benchmark.setPredictedLabel(predictedLabel);
			}
		}
		
		else if (this.type.equalsIgnoreCase(Constants.SPAM_TYPE)) { 
			
			NaiveBayesSpam naive = new NaiveBayesSpam(this.modelNumber, this.channelName);
			
			for (Benchmark benchmark : resultList) { 
				
				predictedLabel = ClassificationUtils.sanitizeString(naive.classifySpamOtherChannels(benchmark.getContent()));
				benchmark.setPredictedLabel(predictedLabel);
			}
		}
	}
	
	public void writeWeightToFile(List<Benchmark> resultList) throws IOException { 
		
		int correctLabels = getCorrectLabels(resultList);
		int incorrectLabels = resultList.size() - correctLabels;
		String modelParam = this.modelNumber + "=" + String.valueOf(correctLabels) + "," + String.valueOf(incorrectLabels);
		BufferedWriter bw;
		FileWriter fw;
		
		fw = new FileWriter(modelFilePath,true);
		bw = new BufferedWriter(fw);
		bw.write(modelParam);
		bw.newLine();
		bw.close();
		fw.close();
	}
	
	public void modelWeightCalculationPipeline() { 
		
		List<Benchmark> resultList = getBenchmarkList(this.heldSetPath); //TODO - Fill this up when ready
		calculateModelPrediction(resultList);
		try {
			writeWeightToFile(resultList);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void modelWeightCalculationPipelineAllModels(String channelName,String type) { 
		
		for (int i = 1; i <= Constants.MAX_MODEL_NUM; i++) { 
			
			new ModelWeight(i,channelName,type).modelWeightCalculationPipeline();
		}
	}
	/** 
	 * Returns the number of correct labels for a given result list
	 * @param resultList List<Benchmark> containing the result set
	 * @return Integer containing the number of correct labels
	 */
	public Integer getCorrectLabels(List<Benchmark> resultList) { 
		
		int correctCount = 0;
		
		for(Benchmark benchmark : resultList) { 
			
			if (benchmark.getActualLabel().equalsIgnoreCase(benchmark.getPredictedLabel())) {  
				
				correctCount++;
			}
		}
		
		return correctCount;
	}
	
	public static void main(String args[]) { 
		
		ModelWeight mw = new ModelWeight();
		mw.modelWeightCalculationPipelineAllModels("facebook", "spam");
	}
}
