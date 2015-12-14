package org.kutty.benchmark;

import java.util.ArrayList;
import java.util.List;

import org.kutty.classification.EnsembleMachineSentiment;
import org.kutty.classification.EnsembleMachineSpam;
import org.kutty.classification.NaiveBayes;
import org.kutty.constants.Constants;
import org.kutty.dbo.Benchmark;
import org.kutty.dbo.PerformanceMetrics;
import org.kutty.features.FeatureUtil;
import org.kutty.features.Post;
import org.kutty.utils.PrintUtil;

/** 
 * Defines the class for testing the classification of the trained models on unseen data 
 * Based on metrics like Precision,Recall,Accuracy and F1-score 
 * 
 * @author Senjuti Kundu
 *
 */
public class PerformanceTest {

	public String TYPE;
	public String CHANNEL;
	public String FILE_PATH;  
	
	/** 
	 * 
	 * @param channel
	 * @param type
	 * @param testFilePath
	 */
	public PerformanceTest(String channel,String type,String testFilePath) { 

		this.FILE_PATH = testFilePath;
		this.CHANNEL = channel;
		this.TYPE = type;
	}
	
	/** 
	 * 
	 * @param testFilePath
	 * @return
	 */
	public List<Post> getPostList(String testFilePath) { 

		List<Post> postList = new ArrayList<Post>();
		FeatureUtil.populateOtherChannelData(testFilePath, postList);

		return postList;
	}
	
	/** 
	 * 
	 * @param postList
	 * @return
	 */
	public List<Benchmark> getBenchmarkObjects(List<Post> postList) { 

		List<Benchmark> benchmarkList = new ArrayList<Benchmark>(); 
		Benchmark bench; 

		for (Post p : postList) {  

			bench =  new Benchmark();

			if(this.TYPE.equalsIgnoreCase(Constants.SENTIMENT_TYPE)) { 

				if (p.getSentimentLabel() != null && !(p.getSentimentLabel().equalsIgnoreCase("null"))) { 

					bench.setActualLabel(p.getSentimentLabel());
					bench.setContent(p.getContent());
					bench.setType(this.TYPE);
					benchmarkList.add(bench);
				}
			} 

			else if (this.TYPE.equalsIgnoreCase(Constants.SPAM_TYPE)) { 

				bench.setActualLabel(p.getSpamLabel());
				bench.setContent(p.getContent());
				bench.setType(this.TYPE);
				benchmarkList.add(bench);
			}
		} 

		return benchmarkList;
	}
	
	/** 
	 * 
	 * @param resultList
	 * @param ngramNum
	 */
	public void setPredictedLabelIndividualNGram(List<Benchmark> resultList,int ngramNum) { 

		NaiveBayes naiveBayes = new NaiveBayes(this.CHANNEL, this.TYPE, ngramNum);
		String predictedLabel; 

		for(Benchmark bench : resultList) { 

			predictedLabel = naiveBayes.classify(bench.getContent());
			bench.setPredictedLabel(predictedLabel);
		}
	}
	
	/** 
	 * 
	 * @param resultList
	 * @param ngramNum
	 */
	public void setPredictedLabelIndividualNGramWithPrior(List<Benchmark> resultList,int ngramNum) { 

		NaiveBayes naiveBayes = new NaiveBayes(this.CHANNEL, this.TYPE, ngramNum);
		String predictedLabel; 

		for(Benchmark bench : resultList) { 

			predictedLabel = naiveBayes.classifyWithPrior(bench.getContent());
			bench.setPredictedLabel(predictedLabel);
		}
	}
	
	/** 
	 * 
	 */
	public void performanceMetricPipeline() { 

		PerformanceMetrics performance = null;
		List<Post> postList = getPostList(this.FILE_PATH);
		List<Benchmark> resultList = getBenchmarkObjects(postList);

		for (int i = 1 ; i <= 7; i++) {

			setPredictedLabelIndividualNGram(resultList, i);
			performance = Performance.getMacroMetrics(resultList); 

			System.out.println("----------------------------------------------------------------\n");
			System.out.println("Accuracy for N = " + i + " : " + performance.getAccuracy()*100);
			System.out.println("Confusion Matrix : ");
			PrintUtil.printMatrix(performance.getConfusionMatrix());
			System.out.println("Precision : " + performance.getMacroPrecision()*100);
			System.out.println("Recall : " + performance.getMacroRecall()*100);
			System.out.println("F1 Score : " + performance.getMacroF1Score()*100);
			System.out.println("Specificity : " + performance.getMacroSpecificity()*100);
			System.out.println("----------------------------------------------------------------\n");

		}
	}
	
	/** 
	 * 
	 */
	public void performanceMetricPipelineWithPrior() { 

		PerformanceMetrics performance = null;
		List<Post> postList = getPostList(this.FILE_PATH);
		List<Benchmark> resultList = getBenchmarkObjects(postList);

		for (int i = 1 ; i <= 7; i++) {

			setPredictedLabelIndividualNGramWithPrior(resultList, i);
			performance = Performance.getMacroMetrics(resultList); 

			System.out.println("----------------------------------------------------------------\n");
			System.out.println("Accuracy for N = " + i + " : " + performance.getAccuracy()*100);
			System.out.println("Confusion Matrix : ");
			PrintUtil.printMatrix(performance.getConfusionMatrix());
			System.out.println("Precision : " + performance.getMacroPrecision()*100);
			System.out.println("Recall : " + performance.getMacroRecall()*100);
			System.out.println("F1 Score : " + performance.getMacroF1Score()*100);
			System.out.println("Specificity : " + performance.getMacroSpecificity()*100);
			System.out.println("----------------------------------------------------------------\n");

		}
	} 
	
	/** 
	 * 
	 */
	public void performanceEnsemblePipeline() { 

		PerformanceMetrics performance = null;
		List<Post> postList = getPostList(this.FILE_PATH);
		List<Benchmark> resultList = getBenchmarkObjects(postList);
		String predictedLabel; 
		EnsembleMachineSentiment ems = null;
		EnsembleMachineSpam ensembleSpam = null; 

		if (this.TYPE.equalsIgnoreCase(Constants.SENTIMENT_TYPE)) { 

			ems = new EnsembleMachineSentiment(CHANNEL, Constants.MAX_MODEL_NUM); 
			
			for(Benchmark bench : resultList) { 

				predictedLabel = ems.organizeAndActEnsemble(bench.getContent());
				bench.setPredictedLabel(predictedLabel);
			}
		}
		
		else if (this.TYPE.equalsIgnoreCase(Constants.SPAM_TYPE)) { 

			ensembleSpam = new EnsembleMachineSpam(CHANNEL, Constants.MAX_MODEL_NUM);
			
			for (Benchmark bench : resultList) { 
				
				predictedLabel = ensembleSpam.organizeAndActEnsemble(bench.getContent());
				bench.setPredictedLabel(predictedLabel);
			}
		}

		performance = Performance.getMacroMetrics(resultList);
		
		System.out.println("----------------------------------------------------------------\n");
		System.out.println("Accuracy for Ensemble Model" + " : " + performance.getAccuracy()*100);
		System.out.println("Confusion Matrix : ");
		PrintUtil.printMatrix(performance.getConfusionMatrix());
		System.out.println("Precision : " + performance.getMacroPrecision()*100);
		System.out.println("Recall : " + performance.getMacroRecall()*100);
		System.out.println("F1 Score : " + performance.getMacroF1Score()*100);
		System.out.println("Specificity : " + performance.getMacroSpecificity()*100);
		System.out.println("----------------------------------------------------------------\n");
		
	}
	
	/** 
	 * 
	 */
	public void performanceEnsemblePipelineWithPrior() { 

		PerformanceMetrics performance = null;
		List<Post> postList = getPostList(this.FILE_PATH);
		List<Benchmark> resultList = getBenchmarkObjects(postList);
		String predictedLabel; 
		EnsembleMachineSentiment ems = null;
		EnsembleMachineSpam ensembleSpam = null; 

		if (this.TYPE.equalsIgnoreCase(Constants.SENTIMENT_TYPE)) { 

			ems = new EnsembleMachineSentiment(CHANNEL, Constants.MAX_MODEL_NUM); 
			
			for(Benchmark bench : resultList) { 

				predictedLabel = ems.organizeAndActEnsembleWithPriors(bench.getContent());
				bench.setPredictedLabel(predictedLabel);
			}
		}
		
		else if (this.TYPE.equalsIgnoreCase(Constants.SPAM_TYPE)) { 

			ensembleSpam = new EnsembleMachineSpam(CHANNEL, Constants.MAX_MODEL_NUM);
			
			for (Benchmark bench : resultList) { 
				
				predictedLabel = ensembleSpam.organizeAndActEnsembleWithPriors(bench.getContent());
				bench.setPredictedLabel(predictedLabel);
			}
		}

		performance = Performance.getMacroMetrics(resultList);
		
		System.out.println("----------------------------------------------------------------\n");
		System.out.println("Accuracy for Ensemble Model" + " : " + performance.getAccuracy()*100);
		System.out.println("Confusion Matrix : ");
		PrintUtil.printMatrix(performance.getConfusionMatrix());
		System.out.println("Precision : " + performance.getMacroPrecision()*100);
		System.out.println("Recall : " + performance.getMacroRecall()*100);
		System.out.println("F1 Score : " + performance.getMacroF1Score()*100);
		System.out.println("Specificity : " + performance.getMacroSpecificity()*100);
		System.out.println("----------------------------------------------------------------\n");
		

	}

	public static void main(String args[]) { 

		PerformanceTest pt = new PerformanceTest("twitter", "sentiment", "twitter_test_annotated.txt");
		//pt.performanceEnsemblePipeline();
		//pt.performanceEnsemblePipelineWithPrior();
		pt.performanceMetricPipelineWithPrior();
		//System.out.println(p.getAccuracy());
		//PrintUtil.printMatrix(p.getConfusionMatrix());
	}
}
