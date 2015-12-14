package org.kutty.dbo;

import java.util.Date;
import java.util.Set;

/** 
 * Defines a spam object for insertion in the database
 * @author Senjuti Kundu
 */

public class Spam {
	
	private String product;
	private String author;
	private Date timestamp;
	private String channel;
	private String spamLabel;
	private String content;
	private Double spamScore;
	private Double otherDate;
	private Set<Update> updateSet;
	
	public Spam() { 
		
		spamScore = 0.0;
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
	
	public String getSpamLabel() { 
		
		return spamLabel;
	}
	
	/** 
	 * 
	 * @param sentimentLabel
	 */ 
	
	public void setSpamLabel(String sentimentLabel) {  
		
		this.spamLabel = sentimentLabel;
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
	
	public Double getSpamScore() { 
		
		return spamScore;
	}
	
	/** 
	 * 
	 * @param sentimentScore
	 */ 
	
	public void setSpamScore(Double sentimentScore) { 
		
		this.spamScore = sentimentScore;
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
	 * @return the updateSet
	 */
	public Set<Update> getUpdateSet() {
		
		return updateSet;
	}

	/**
	 * @param updateSet the updateSet to set
	 */
	public void setUpdateSet(Set<Update> updateSet) {
		
		this.updateSet = updateSet;
	}
}
