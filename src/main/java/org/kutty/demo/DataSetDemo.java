package org.kutty.demo;

import java.io.IOException;

import org.kutty.benchmark.PerformanceTest;
import org.kutty.classification.Prior;
import org.kutty.satisfaction.SatisfactionAnalytics;
import org.kutty.utils.PrintUtil;

/** 
 * Prints the distribution of the dataset on screen for demo purpose
 * @author Senjuti Kundu
 *
 */
public class DataSetDemo {
	
	public static void main(String args[]) throws IOException { 
		
		System.out.println("*************** Distribution of Twitter Train Data ******************\n");
		PrintUtil.printMap(Prior.generateLabelSentimentCountMap(("Twitter_train.txt")));
		PrintUtil.printMap(Prior.generateLabelSpamCountMap(("Twitter_train.txt")));
		System.out.println();
		System.out.println("************* Distribution of Facebook Train Data *******************\n");
		PrintUtil.printMap(Prior.generateLabelSentimentCountMap(("Facebook_train.txt")));
		PrintUtil.printMap(Prior.generateLabelSpamCountMap("Facebook_train.txt"));
		System.out.println();
		System.out.println("************** Distribution of Facebook Test Data ********************\n");
		PrintUtil.printMap(Prior.generateLabelSentimentCountMap("Facebook_test_annotated.txt"));
		PrintUtil.printMap(Prior.generateLabelSpamCountMap("Facebook_test_annotated.txt")); 
		System.out.println("************** Distribution of Twitter Test Data ********************\n");
		PrintUtil.printMap(Prior.generateLabelSentimentCountMap("Twitter_test_annotated.txt"));
		PrintUtil.printMap(Prior.generateLabelSpamCountMap("Twitter_test_annotated.txt"));
		
		PerformanceTest facebooktestSentiment = new PerformanceTest("facebook", "sentiment", "Facebook_test_annotated.txt");
		System.out.println("\n************** Performance of Facebook For Sentiment Analysis Without Priors ************\n");
		facebooktestSentiment.performanceMetricPipeline();
		System.out.println("\n************** Performance of Facebook For Sentiment Analysis With Prior Probability ***********\n");
		facebooktestSentiment.performanceMetricPipelineWithPrior();
		PerformanceTest facebooktestSpam = new PerformanceTest("facebook", "spam", "Facebook_test_annotated.txt");
		System.out.println("\n************** Performance of Facebook For Spam Detection Without Priors ************\n");
		facebooktestSpam.performanceMetricPipeline();
		System.out.println("\n************** Performance of Facebook For Spam Detect With Prior Probability ***********\n");
		facebooktestSpam.performanceMetricPipelineWithPrior();
		System.out.println("\n*************** Sentiment Ensemble Model of Facebook Without Prior Probability *********************\n");
		facebooktestSentiment.performanceEnsemblePipeline(); 
		System.out.println("\n*************** Sentiment Ensemble Model of Facebook With Prior Probability *********************\n");
		facebooktestSentiment.performanceEnsemblePipelineWithPrior();
		System.out.println("\n************************ Spam Ensemble Model of Facebook Without Prior Probability *****************\n");
		facebooktestSpam.performanceEnsemblePipeline();
		System.out.println("\n************************ Spam Ensemble Model of Facebook With Prior Probability *****************\n");
		facebooktestSpam.performanceEnsemblePipelineWithPrior();
		
		PerformanceTest twittertestSentiment = new PerformanceTest("twitter", "sentiment", "Twitter_test_annotated.txt");
		System.out.println("\n************** Performance of Twitter For Sentiment Analysis Without Priors ************\n");
		twittertestSentiment.performanceMetricPipeline();
		System.out.println("\n************** Performance of Twitter For Sentiment Analysis With Prior Probability ***********\n");
		twittertestSentiment.performanceMetricPipelineWithPrior();
		PerformanceTest twittertestSpam = new PerformanceTest("twitter", "spam", "Twitter_test_annotated.txt");
		System.out.println("\n************** Performance of Twitter For Spam Detection Without Priors ************\n");
		twittertestSpam.performanceMetricPipeline();
		System.out.println("\n************** Performance of Twitter For Spam Detection With Prior Probability ***********\n");
		twittertestSpam.performanceMetricPipelineWithPrior();
		System.out.println("\n*************** Sentiment Ensemble Model of Twitter Without Prior Probability *********************\n");
		twittertestSentiment.performanceEnsemblePipeline(); 
		System.out.println("\n*************** Sentiment Ensemble Model of Twitter With Prior Probability *********************\n");
		twittertestSentiment.performanceEnsemblePipelineWithPrior();
		System.out.println("\n************************ Spam Ensemble Model of Twitter Without Prior Probability *****************\n");
		twittertestSpam.performanceEnsemblePipeline();
		System.out.println("\n************************ Spam Ensemble Model of Twitter With Prior Probability *****************\n");
		twittertestSpam.performanceEnsemblePipelineWithPrior();
		System.out.println("\n******************* Satisfaction Index of Brands Between the Period of August 2015 to November 2015 ***************\n");
		PrintUtil.printMap(SatisfactionAnalytics.getSatisfactionAllProducts("Satisfaction.txt")); 
	}
}
