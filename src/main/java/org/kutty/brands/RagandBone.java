package org.kutty.brands;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import org.kutty.fetch.FacebookFetch;
import org.kutty.fetch.TweetFetch;

import com.cybozu.labs.langdetect.LangDetectException;

import facebook4j.FacebookException;

/** 
 * Fetches posts relating to Rag&Bone from across different social channels like
 * Facebook and Twitter 
 * The post retrieval  code is fetched after every t minutes.  
 * 
 * @author Senjuti Kundu
 *
 */ 

public class RagandBone extends TimerTask {
	
	long delay;
	long period; 
	
	/** 
	 * public constructor to initialize the object with default values 
	 */ 
	
	public RagandBone()  { 
		
		delay = 5*1000;
		period = 30*60*100;
	} 
	
	/** 
	 * public constructor to initialize the object with default values
	 * @param delay long containing the delay
	 * @param period long containing the period
	 */ 
	
	public RagandBone(long delay,long period) { 
		
		this.delay = delay;
		this.period = period;
	}
	
	/** 
	 * Returns the delay in subsequent executions
	 * @return Long containing the delay milliseconds
	 */ 
	
	public long getDelay() { 
		
		return delay;
	}
	
	/** 
	 * Sets the delay in subsequent executions
	 * @param delay Long containing the delay in milliseconds
	 */
	
	public void setDelay(long delay) { 
		
		this.delay = delay;
	}
	
	/** 
	 * Returns the period of consequent executions in milliseconds 
	 * @return Long containing the period in milliseconds
	 */ 
	
	public long getPeriod() { 
		
		return period;
	}
	
	/** 
	 * Sets the period of the execution in milliseconds
	 * @param period Long containing the period of execution in milliseconds
	 */ 
	
	public void setPeriod(long period) { 
		
		this.period = period;
	}
	
	/** 
	 * Executes all the channels for a given product 
	 */ 
	
	public void executeAllChannels() { 
		
		FacebookFetch facebook; 
		
		TweetFetch tweet_1;
		TweetFetch tweet_2;
		TweetFetch tweet_3;
		TweetFetch tweet_4;
		TweetFetch tweet_5;  
		TweetFetch tweet_6; 
		
		try { 
			
			facebook = new FacebookFetch("RagandBone");
			facebook.start(); 
			
			tweet_1 = new TweetFetch("@rag_bone", "RagandBone");
			tweet_2 = new TweetFetch("#ragandbone", "RagandBone");
			tweet_3 = new TweetFetch("rag&bone :)", "RagandBone"); 
			tweet_4 = new TweetFetch("rag&bone :(", "RagandBone"); 
			tweet_5 = new TweetFetch("#rag&bone", "RagandBone");  
			tweet_6 = new TweetFetch("rag&bone jeans","RagandBone"); 
			
			tweet_1.start();
			tweet_2.start();
			tweet_3.start();
			tweet_4.start();
			tweet_5.start(); 
			tweet_6.start(); 
			 
		} catch (IOException | FacebookException | LangDetectException e) {
			
			e.printStackTrace();
		}
	}
	
	/** 
	 * Overridden run function to support multi-threading
	 */ 
	
	@Override
	public void run() {
		executeAllChannels();
	}
	
	public static void main(String args[]) { 
		
		Timer t = new Timer();
		
		t.scheduleAtFixedRate(new RagandBone(), 1000, 5*1000);
	}
}
