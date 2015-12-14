package org.kutty.features;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** 
 * Utility class to find out label counts of classes in the dataset 
 * 
 * @author Senjuti Kundu
 */ 

public class LabelCountUtil {
	
	/** 
	 * Returns a map of Spam Label count for a given post list
	 * @param post_list List<Post> containing the post list
	 * @return Map<String,Integer> containing the spam label (i.e. Ham/Spam) and its count
	 */ 
	
	public static Map<String,Integer> getSpamLabelCount(List<Post> post_list) { 

		int count = 0;
		Map<String,Integer> spam_label_map = new HashMap<String,Integer>(); 
		String label = ""; 

		for(Post p : post_list) { 

			if (p.getSpamLabel() != null && !p.getSpamLabel().isEmpty()) { 

				label = p.getSpamLabel(); 

				if (spam_label_map.containsKey(label)) { 

					count = spam_label_map.get(label);
					spam_label_map.put(label, count+1); 

				} else {  

					spam_label_map.put(label, 1);
				}
			}
		}

		return spam_label_map;
	}
	
	/** 
	 * Returns a map of sentiment label count for a given post list
	 * @param post_list List<Post> containing the list of posts
	 * @return Map<String,Integer> containing the mapping between the sentiment label and its count
	 */ 
	
	public static Map<String,Integer> getSentimentLabelCount(List<Post> post_list) { 

		int count = 0;
		Map<String,Integer> sentiment_label_map = new HashMap<String,Integer>(); 
		String label = ""; 

		for(Post p : post_list) { 

			if (p.getSentimentLabel() != null && !p.getSentimentLabel().isEmpty()) { 

				label = p.getSentimentLabel(); 

				if (sentiment_label_map.containsKey(label)) { 

					count = sentiment_label_map.get(label);
					sentiment_label_map.put(label, count+1); 

				} else {  

					sentiment_label_map.put(label, 1);
				}
			}
		}

		return sentiment_label_map;
	} 
	
	/** 
	 * For a given post list extracts the giveaway labels (i.e. real or fake)
	 * @param post_list List<Post> containing the list of posts
	 * @return Map<String,Integer> containing the mapping between giveawway label and its count
	 */ 
	
	public static Map<String,Integer> getGiveawayLabelCount(List<Post> post_list) { 

		int count = 0;
		Map<String,Integer> giveaway_label_map = new HashMap<String,Integer>(); 
		String label = ""; 

		for(Post p : post_list) { 

			if (p.getGiveawayLabel() != null && !p.getGiveawayLabel().isEmpty()) { 

				label = p.getGiveawayLabel(); 

				if (giveaway_label_map.containsKey(label)) { 

					count = giveaway_label_map.get(label);
					giveaway_label_map.put(label, count+1); 

				} else {  

					giveaway_label_map.put(label, 1);
				}
			}
		}

		return giveaway_label_map;
	} 
}
