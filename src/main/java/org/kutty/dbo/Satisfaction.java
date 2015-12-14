package org.kutty.dbo;

import java.util.Date;

/** 
 * Defines a satisfaction object to be inserted in the database
 * @author Senjuti Kundu
 *
 */
public class Satisfaction {

	private String channelName;
	private String brandName;
	private double frequencyFactor;
	private double reliabilityFactor;
	private double importanceFactor;
	private double frequencyWeight;
	private double reliabilityWeight;
	private Date timestamp;
	private String content;
	private double satisfactionScore;
	private double sentimentScore;
	private double otherDate; 
	
	/**
	 * @return the channelName
	 */ 
	
	public String getChannelName() { 
		
		return channelName;
	}
	
	/**
	 * @param channelName the channelName to set
	 */
	
	public void setChannelName(String channelName) { 
		
		this.channelName = channelName;
	} 
	
	/**
	 * @return the brandName
	 */ 
	
	public String getBrandName() { 
		
		return brandName;
	} 
	
	/**
	 * @param brandName the brandName to set
	 */ 
	
	public void setBrandName(String brandName) { 
		
		this.brandName = brandName;
	} 
	
	/**
	 * @return the frequencyFactor
	 */ 
	
	public double getFrequencyFactor() { 
		
		return frequencyFactor;
	} 
	
	/**
	 * @param frequencyFactor the frequencyFactor to set
	 */ 
	
	public void setFrequencyFactor(double frequencyFactor) { 
		
		this.frequencyFactor = frequencyFactor;
	} 
	
	/**
	 * @return the reliabilityFactor
	 */
	
	public double getReliabilityFactor() { 
		
		return reliabilityFactor;
	}
	
	/**
	 * @param reliabilityFactor the reliabilityFactor to set
	 */ 
	
	public void setReliabilityFactor(double reliabilityFactor) { 
		
		this.reliabilityFactor = reliabilityFactor;
	} 
	
	/**
	 * @return the importanceFactor
	 */ 
	
	public double getImportanceFactor() { 
		
		return importanceFactor;
	} 
	
	/**
	 * @param importanceFactor the importanceFactor to set
	 */
	
	public void setImportanceFactor(double importanceFactor) { 
		
		this.importanceFactor = importanceFactor;
	}
	
	/**
	 * @return the timestamp
	 */
	public Date getTimestamp() {
		return timestamp;
	} 
	
	/**
	 * @param timestamp the timestamp to set
	 */
	
	public void setTimestamp(Date timestamp) { 
		
		this.timestamp = timestamp;
	}
	
	/**
	 * @return the content
	 */
	
	public String getContent() { 
		
		return content;
	} 
	
	/**
	 * @param content the content to set
	 */
	
	public void setContent(String content) { 
		
		this.content = content;
	}
	
	/**
	 * @return the satisfactionScore
	 */
	
	public double getSatisfactionScore() { 
		
		return satisfactionScore;
	} 
	
	/**
	 * @param satisfactionScore the satisfactionScore to set
	 */
	
	public void setSatisfactionScore(double satisfactionScore) { 
		
		this.satisfactionScore = satisfactionScore;
	}
	
	/**
	 * @return the sentimentScore
	 */
	
	public double getSentimentScore() { 
		
		return sentimentScore;
	}
	
	/**
	 * @param sentimentScore the sentimentScore to set
	 */
	
	public void setSentimentScore(double sentimentScore) { 
		
		this.sentimentScore = sentimentScore;
	} 
	
	/**
	 * @return the otherDate
	 */ 
	
	public double getOtherDate() { 
		
		return otherDate;
	} 
	
	/**
	 * @param otherDate the otherDate to set
	 */
	
	public void setOtherDate(double otherDate) { 
		
		this.otherDate = otherDate;
	}

	/** 
	 * 
	 * @return the frequencyWeight
	 */ 
	
	public double getFrequencyWeight() { 
		
		return frequencyWeight;
	}

	/** 
	 * 
	 * @param frequencyWeight the frequencyWeight to set
	 */ 
	
	public void setFrequencyWeight(double frequencyWeight) { 
		
		this.frequencyWeight = frequencyWeight;
	}

	/** 
	 * 
	 * @return the reliabilityWeight
	 */ 
	
	public double getReliabilityWeight() { 
		
		return reliabilityWeight;
	}

	/** 
	 * 
	 * @param reliabilityWeight the reliabilityWeight to set
	 */ 
	
	public void setReliabilityWeight(double reliabilityWeight) { 
		
		this.reliabilityWeight = reliabilityWeight;
	}
}
