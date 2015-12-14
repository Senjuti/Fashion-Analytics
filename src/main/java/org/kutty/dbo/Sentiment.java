package org.kutty.dbo;

import java.util.Date;
import java.util.Set;

/** 
 * Defines a sentiment object to be inserted in the database
 * @author Senjuti Kundu
 *
 */
public class Sentiment {
	
	private String product;
	private String author;
	private Date timestamp;
	private String channel;
	private String sentimentLabel;
	private String content;
	private Double sentimentScore;
	private Double otherDate; 
	private Set<Update> updateModels;
	
	public Sentiment() { 
		
		sentimentScore = 0.0;
	}
	
	/**
	 * @return
	 */
	public String getProduct() { 
		
		return product;
	} 
	
	/** 
	 * 
	 * @param product
	 */ 
	
	public void setProduct(String product) { 
		
		this.product = product;
	}
	
		
	/** 
	 * 
	 * @return
	 */ 
	
	public String getAuthor() {
		
		return author;
	}
	
	/** 
	 * 
	 * @param author
	 */ 
	
	public void setAuthor(String author) {
		
		this.author = author;
	}
	
	/** 
	 * 
	 * @return
	 */ 
	
	public Date getTimestamp() { 
		
		return timestamp;
	}
	
	/** 
	 * 
	 * @param timestamp
	 */ 
	
	public void setTimestamp(Date timestamp) { 
		
		this.timestamp = timestamp;
	}
	
	/** 
	 * 
	 * @return
	 */ 
	
	public String getChannel() { 
		
		return channel;
	}
	
	/** 
	 * 
	 * @param channel
	 */ 
	
	public void setChannel(String channel) { 
		
		this.channel = channel;
	}
	
	/** 
	 * 
	 * @return
	 */ 
	
	public String getSentimentLabel() { 
		
		return sentimentLabel;
	}
	
	/** 
	 * 
	 * @param sentimentLabel
	 */ 
	
	public void setSentimentLabel(String sentimentLabel) {  
		
		this.sentimentLabel = sentimentLabel;
	}
	
	/** 
	 * 
	 * @return
	 */ 
	
	public String getContent() { 
		
		return content;
	}
	
	/** 
	 * 
	 * @param content
	 */ 
	
	public void setContent(String content) { 
		
		this.content = content;
	}
	
	/** 
	 * 
	 * @return
	 */ 
	
	public Double getSentimentScore() { 
		
		return sentimentScore;
	}
	
	/** 
	 * 
	 * @param sentimentScore
	 */ 
	
	public void setSentimentScore(Double sentimentScore) { 
		
		this.sentimentScore = sentimentScore;
	}
	
	/** 
	 * 
	 * @return
	 */ 
	
	public Double getOtherDate() { 
		
		return otherDate;
	}
	
	/** 
	 * 
	 * @param otherDate
	 */
	
	public void setOtherDate(Double otherDate) { 
		
		this.otherDate = otherDate;
	}

	/**
	 * @return the updateModels
	 */
	public Set<Update> getUpdateModels() {
		return updateModels;
	}

	/**
	 * @param updateModels the updateModels to set
	 */
	public void setUpdateModels(Set<Update> updateModels) {
		this.updateModels = updateModels;
	}
}
