package org.kutty.features;

/** 
 * Defines a Post entity for further processing
 * @author Senjuti Kundu
 */ 

public class Post {
	
	private String content;
	private String tagset;
	private String sentimentLabel;
	private String spamLabel;
	private String giveawayLabel;
	
	/** 
	 * Overloaded public constructor to initialize the post object with default values 
	 */ 
	
	public Post() { 
		
		content = "";
		tagset = "";
		sentimentLabel = "";
		spamLabel = "";
		giveawayLabel = "";
	} 
	
	/** 
	 * Returns the content of a given a post 
	 * @return String containing the content
	 */ 
	
	public String getContent() { 
		
		return content;
	}
	
	/** 
	 * Sets the content of a given String
	 * @param content String containing the content
	 */  
	
	public void setContent(String content) { 
		
		this.content = content;
	}
	
	/** 
	 * Sets the tagset of a given post
	 * @return String containing the tagset
	 */ 
	
	public String getTagset() { 
		
		return tagset;
	}
	
	/** 
	 * Sets the tagset of a given post (incase of Instagram only)
	 * @param tagset String containing the tagset
	 */ 
	
	public void setTagset(String tagset) { 
		
		this.tagset = tagset;
	}
	
	/** 
	 * Returns the sentiment label of a given post
	 * @return String containing the sentiment label of a post
	 */ 
	
	public String getSentimentLabel() { 
		
		return sentimentLabel;
	} 
	
	/** 
	 * Sets the sentiment label of a given post
	 * @param sentimentLabel String containing the sentiment label
	 */ 
	
	public void setSentimentLabel(String sentimentLabel) { 
		
		this.sentimentLabel = sentimentLabel;
	} 
	
	/** 
	 * Returns the spam label of a given post
	 * @return String containing the spam label
	 */ 
	
	public String getSpamLabel() { 
		
		return spamLabel;
	} 
	
	/** 
	 * Sets the spam label of a given post
	 * @param spamLabel String containing the spam label
	 */ 
	
	public void setSpamLabel(String spamLabel) { 
		
		this.spamLabel = spamLabel;
	} 
	
	/** 
	 * Returns the Giveaway label of a given post
	 * @return String containing the Giveaway label
	 */ 
	
	public String getGiveawayLabel() { 
		
		return giveawayLabel;
	} 
	
	/** 
	 * Sets the Giveaway label of a given post
	 * @param giveawayLabel String containing the giveaway label
	 */ 
	
	public void setGiveawayLabel(String giveawayLabel) { 
		
		this.giveawayLabel = giveawayLabel;
	}
	
	/** 
	 * Prints the attributes of the post object to the console 
	 */
	
	public void printPost() { 
		
		System.out.println("--------------------------------------------------------");
		System.out.println("Content : " + this.content);
		System.out.println("Giveaway : " + this.giveawayLabel);
		System.out.println("Sentiment Label : " + this.sentimentLabel);
		System.out.println("Spam Label : " + this.spamLabel);
		System.out.println("Tag Set : " + this.tagset);
		System.out.println("---------------------------------------------------------");
	}
}
