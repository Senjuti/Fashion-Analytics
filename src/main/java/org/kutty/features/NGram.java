package org.kutty.features;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** 
 * Extracts the NGram features from a given post
 * @author Senjuti Kundu
 *
 */
public class NGram {

	public Map<String,Integer> label_count_map;
	public Map<String,Integer> spam_count_map;
	public Map<String,Double> real_giveaway_count_map;
	public Map<String,Double> fake_giveaway_count_map;
	public Map<String,Double> positive_count_map;
	public Map<String,Double> negative_count_map;
	public Map<String,Double> neutral_count_map;
	public Map<String,Double> spam_map;
	public Map<String,Double> ham_map;
	public Map<String,Double> real_tag_map;
	public Map<String,Double> fake_tag_map;
	public Map<String,Double> positive_tag_map;
	public Map<String,Double> negative_tag_map;
	public Map<String,Double> neutral_tag_map;
	public Map<String,Double> spam_tag_map;
	public Map<String,Double> ham_tag_map; 
	public static boolean ngramTagWrite = false; 
	public int NGRAM = 1;
	/** 
	 * public constructor to initialize the different count maps
	 */ 

	public NGram() { 

		real_giveaway_count_map = new HashMap<String,Double>();
		fake_giveaway_count_map = new HashMap<String,Double>();
		positive_count_map = new HashMap<String,Double>();
		negative_count_map = new HashMap<String,Double>();
		neutral_count_map = new HashMap<String,Double>();
		spam_map = new HashMap<String,Double>();
		ham_map = new HashMap<String,Double>();
		real_tag_map = new HashMap<String,Double>();
		fake_tag_map = new HashMap<String,Double>();
		positive_tag_map = new HashMap<String,Double>();
		negative_tag_map = new HashMap<String,Double>();
		neutral_tag_map = new HashMap<String,Double>();
		spam_tag_map = new HashMap<String,Double>();
		ham_tag_map = new HashMap<String,Double>();
	} 

	/** 
	 * Defines the pipeline for extraction of NGram features from the given dataset
	 * @param filename String containing the filename containing the data
	 * @param n Integer containing the value of N in Ngram
	 * @param channel String containing the channel name
	 * @param type String containing the type of data i.e. Sentiment or Giveaway
	 */ 

	public void NGramExtractionPipeline(String filename,int n,String channel,String type) { 

		List<Post> post_list = new ArrayList<Post>();
		FeatureUtil feat = new FeatureUtil();  
		this.NGRAM = n;
		
		if (channel.equalsIgnoreCase("Instagram") && type.equalsIgnoreCase("giveaway")) { 

			FeatureUtil.populateInstagramGiveawayData(filename, post_list);
			label_count_map = feat.giveaway_count_map;
		}  

		else if (channel.equalsIgnoreCase("Instagram") && type.equalsIgnoreCase("sentiment")) { 

			FeatureUtil.populateInstagramSentimentData(filename, post_list);
			label_count_map = feat.sentiment_count_map;
			spam_count_map = feat.spam_count_map;

		} else { 

			FeatureUtil.populateOtherChannelData(filename, post_list);
			label_count_map = feat.sentiment_count_map;
			spam_count_map = feat.spam_count_map;
		} 
		
		if (type.equalsIgnoreCase("spam")) { 
			
			spam_count_map.putAll(LabelCountUtil.getSpamLabelCount(post_list)); 
			
		} else if (type.equalsIgnoreCase("sentiment")) { 
			
			label_count_map.putAll(LabelCountUtil.getSentimentLabelCount(post_list));  
			
		} else { 
			
			label_count_map.putAll(LabelCountUtil.getGiveawayLabelCount(post_list)); 
		}

		for (Post p : post_list) { 

			String content = p.getContent();
			content = content.toLowerCase().trim();
			content = FeatureUtil.cleanString(content);
			content = FeatureUtil.removeStopWords(content);
			content = FeatureUtil.getStemPerWord(content);
			content = FeatureUtil.getNGram(content, n);
			p.setContent(content);
			getNGramCountMapSelector(p,post_list,type); 
			
			if (!ngramTagWrite && channel.equalsIgnoreCase("Instagram")) { 
				
				String tagset = p.getTagset();
				tagset = getTagCleanGram(tagset, 1);
				p.setTagset(tagset);
				getNGramTagMapSelector(p, post_list, type);
			}
		
		}
		
		writeGramToFileSelector(type); 
		
		if (!ngramTagWrite && channel.equalsIgnoreCase("Instagram")) { 
			
			writeTagGramToFileSelector(type);
			ngramTagWrite = true;
		}
	}
	
	/** 
	 * Given a tagset space separates it and removes the stopwords
	 * @param tagset String containing the tagset
	 * @param n Integer containing the value of N for Ngram
	 * @return String containing the cleaned and separated tagset
	 */ 
	
	public static String getTagCleanGram(String tagset,int n) { 
		
		tagset = tagset.replace(","," ");
		tagset = tagset.toLowerCase().trim();
		tagset = FeatureUtil.cleanString(tagset);
		tagset = FeatureUtil.removeStopWords(tagset);
		tagset = FeatureUtil.getStemPerWord(tagset);
		tagset = FeatureUtil.getNGram(tagset,n);
		
		return tagset;
	} 
	
	/** 
	 * Given a post containing the tagset calculates the NGram count
	 * @param p Post containing the details of the tag item
	 * @param post_list List <Post> containing the set of posts
	 * @param ngram_map Map<String,Double> containing the mapping between the ngram and its probability
	 * @param type String containing the type of analysis to be made
	 */ 
	
	public void getTagGramCount(Post p,List<Post> post_list,Map<String,Double> ngram_map,String type) { 
		
		double count = 0;
		String tagset = p.getTagset();
		String ngram = "";
		String ngram_label = "";
		int index = -1; 

		if (type.equalsIgnoreCase("giveaway")) { 

			ngram_label = p.getGiveawayLabel();
		}

		if (type.equalsIgnoreCase("sentiment")) { 

			ngram_label = p.getSentimentLabel();
		}

		if (type.equalsIgnoreCase("spam")) { 

			ngram_label = p.getSpamLabel();
		}

		index = tagset.indexOf('|');  

		while(index != -1) { 

			ngram = tagset.substring(0, index).trim();
			tagset = tagset.substring(index+1);
			index = tagset.indexOf('|'); 
			
			if (index != -1 && !ngram_map.containsKey(ngram)) { 
				
				count = getNGramTagCountUtil(ngram, post_list, ngram_label, type);
				ngram_map.put(ngram,count); 
			}
		}
	} 
	
	/** 
	 * Given a post and post list selects the correct map type to pass onto the NGram count
	 * @param p Post post object containing the information for the given post
	 * @param post_list List<Post> containing the ArrayList of posts
	 * @param type String containing the type of analysis to be made
	 */ 
	
	public void getNGramTagMapSelector(Post p,List<Post> post_list,String type) {

		String ngram_label = "";

		if (type.equalsIgnoreCase("giveaway")) { 

			ngram_label = p.getGiveawayLabel();

			if (ngram_label != null && ngram_label.equalsIgnoreCase("real")) { 

				getTagGramCount(p, post_list, real_tag_map, type);
			}

			if (ngram_label != null && ngram_label.equalsIgnoreCase("fake")) { 

				getTagGramCount(p, post_list, fake_tag_map, type);
			}
		}

		if (type.equalsIgnoreCase("sentiment")) { 

			ngram_label = p.getSentimentLabel();

			if (ngram_label != null && ngram_label.equalsIgnoreCase("positive")) { 

				getTagGramCount(p, post_list, positive_tag_map, type);
			}

			if (ngram_label != null && ngram_label.equalsIgnoreCase("negative")) {

				getTagGramCount(p, post_list, negative_tag_map, type);
			}

			if (ngram_label != null && ngram_label.equalsIgnoreCase("neutral")) { 

				getTagGramCount(p, post_list, neutral_tag_map, type);
			}
		}

		if (type.equalsIgnoreCase("spam")) { 

			ngram_label = p.getSpamLabel();

			if (ngram_label != null && ngram_label.equalsIgnoreCase("spam")) { 

				getTagGramCount(p, post_list, spam_tag_map, type);
			}

			if (ngram_label != null && ngram_label.equalsIgnoreCase("ham")) { 

				getTagGramCount(p, post_list, ham_tag_map, type);
			}
		}
	}
	
	/** 
	 * 
	 * @param type
	 */ 
	
	public void writeTagGramToFileSelector(String type) { 

		if (type.equalsIgnoreCase("giveaway")) { 

			writeGramToFile("giveaway/tag_real_5.txt", real_tag_map);
			writeGramToFile("giveaway/tag_fake_5.txt", fake_tag_map);
		}

		if (type.equalsIgnoreCase("sentiment")) { 

			writeGramToFile("",positive_tag_map);
			writeGramToFile("",negative_tag_map);
			writeGramToFile("",neutral_tag_map);
		}

		if (type.equalsIgnoreCase("spam")) { 

			writeGramToFile("", spam_tag_map);
			writeGramToFile("", ham_tag_map);
		}
	} 
	
	/** 
	 * Utility function to calculate the probability of an ngram
	 * @param ngram String containing the ngram
	 * @param post_list List<Post> containing the ArrayList of posts
	 * @param ngram_label String containing the ngram_label
	 * @param type String containing the type of analysis which needs to be done
	 * @return Double containing the probability of the tag ngram
	 */ 
	
	public double getNGramTagCountUtil(String ngram,List<Post> post_list, String ngram_label,String type) { 

		double count = 0.0;
		String tag_set = ""; 
		String post_label = ""; 
		String spam_label = ""; 
		double label_count = 0.0; 

		if (ngram_label != null && (type.equalsIgnoreCase("giveaway") || type.equalsIgnoreCase("sentiment"))) { 

			label_count = label_count_map.get(ngram_label);
		} 

		if (ngram_label != null && type.equalsIgnoreCase("spam")) {  

			label_count = spam_count_map.get(ngram_label);
		} 

		for (Post temp : post_list) { 

			tag_set = temp.getTagset();

			if (type.equalsIgnoreCase("giveaway")) {  

				post_label = temp.getGiveawayLabel();
			}

			else if (type.equalsIgnoreCase("sentiment")) { 

				post_label = temp.getSentimentLabel();
			}

			else if (type.equalsIgnoreCase("spam")) { 

				spam_label = temp.getSpamLabel();
			}

			if (spam_label != null && post_label != null && (!spam_label.isEmpty() || !post_label.isEmpty())) {

				if (ngram_label != null && (ngram_label.equalsIgnoreCase(post_label) || ngram_label.equalsIgnoreCase(spam_label))) {

					count = count + getSubStringCount(ngram, tag_set);
				}
			}

			spam_label = "";
			post_label = "";
		}

		return ((count+1)/(label_count+1));
	}

	/** 
	 * Given a particular type of classification to be done selects the appropriate file
	 * @param type String containing the type of analysis to be made
	 */ 
	
	public void writeGramToFileSelector(String type) { 

		if (type.equalsIgnoreCase("giveaway")) { 

			writeGramToFile("giveaway/real_5.txt", real_giveaway_count_map);
			writeGramToFile("giveaway/fake_5.txt", fake_giveaway_count_map);
		}

		if (type.equalsIgnoreCase("sentiment")) { 

			writeGramToFile("facebook/7-gram/positive_7.txt",positive_count_map);
			writeGramToFile("facebook/7-gram/negative_7.txt",negative_count_map);
			writeGramToFile("facebook/7-gram/neutral_7.txt",neutral_count_map);
		}

		if (type.equalsIgnoreCase("spam")) { 

			writeGramToFile("facebook/1-gram/spam_1.txt", spam_map);
			writeGramToFile("facebook/1-gram/ham_1.txt", ham_map);
		}
	} 
	
	/** 
	 * Given a post and post list selects the correct map type to pass onto the NGram count
	 * @param p Post post object containing the information for the given post
	 * @param post_list List<Post> containing the ArrayList of posts
	 * @param type String containing the type of analysis to be made
	 */ 
	
	public void getNGramCountMapSelector(Post p,List<Post> post_list,String type) {

		String ngram_label = "";

		if (type.equalsIgnoreCase("giveaway")) { 

			ngram_label = p.getGiveawayLabel();

			if (ngram_label != null && ngram_label.equalsIgnoreCase("real")) { 

				getNGramCount(p, post_list, real_giveaway_count_map, type);
			}

			if (ngram_label != null && ngram_label.equalsIgnoreCase("fake")) { 

				getNGramCount(p, post_list, fake_giveaway_count_map, type);
			}
		}

		if (type.equalsIgnoreCase("sentiment")) { 

			ngram_label = p.getSentimentLabel();

			if (ngram_label != null && ngram_label.equalsIgnoreCase("positive")) { 

				getNGramCount(p, post_list, positive_count_map, type);
			}

			if (ngram_label != null && ngram_label.equalsIgnoreCase("negative")) {

				getNGramCount(p, post_list, negative_count_map, type);
			}

			if (ngram_label != null && ngram_label.equalsIgnoreCase("neutral")) { 

				getNGramCount(p, post_list, neutral_count_map, type);
			}
		}

		if (type.equalsIgnoreCase("spam")) { 

			ngram_label = p.getSpamLabel();

			if (ngram_label != null && ngram_label.equalsIgnoreCase("spam")) { 

				getNGramCount(p, post_list, spam_map, type);
			}

			if (ngram_label != null && ngram_label.equalsIgnoreCase("ham")) { 

				getNGramCount(p, post_list, ham_map, type);
			}
		}
	}

	/** 
	 * Returns the count of NGrams for a given NGram and a classification type
	 * @param p Post containing the post object along with the content and other things
	 * @param post_list List<Post> containing the posts for a given training set
	 * @param type Type of classification which needs to be done these include (Giveaway,Sentiment Analysis etc)
	 * @return Integer containing the count of integers
	 */ 

	public void getNGramCount(Post p,List<Post> post_list,Map<String,Double> ngram_map,String type) { 

		double count = 0;
		String sentence = p.getContent();
		String ngram = "";
		String ngram_label = "";
		int index = -1; 

		if (type.equalsIgnoreCase("giveaway")) { 

			ngram_label = p.getGiveawayLabel();
		}

		if (type.equalsIgnoreCase("sentiment")) { 

			ngram_label = p.getSentimentLabel();
		}

		if (type.equalsIgnoreCase("spam")) { 

			ngram_label = p.getSpamLabel();
		}

		index = sentence.indexOf('|');  

		while(index != -1) { 

			ngram = sentence.substring(0, index).trim();
			sentence = sentence.substring(index+1);
			index = sentence.indexOf('|');
			if (index != -1 && !ngram_map.containsKey(ngram)) {
				if (this.NGRAM == 1) {
					count = getNGramCountUtil(ngram, post_list, ngram_label,type); 
				} else {  
					count = getNGramCountUtilForNGreaterThanOne(ngram, post_list, ngram_label, type);
				}
				ngram_map.put(ngram,count); 
			}
		}
	}

	/** 
	 * Given an NGram counts its occurrence in dataset
	 * @param ngram String containing the ngram which is to be counted
	 * @param post_list List<Post> containing the entire post list
	 * @param ngram_label String containing the NGram label count
	 * @param type String containing the type of analysis to be made
	 * @return Integer containing count of the NGram in a given post list
	 */ 

	public double getNGramCountUtil(String ngram,List<Post> post_list, String ngram_label,String type) { 

		double count = 0.0;
		String sentence = ""; 
		String post_label = ""; 
		String spam_label = ""; 
		double label_count = 0.0; 

		if (ngram_label != null && (type.equalsIgnoreCase("giveaway") || type.equalsIgnoreCase("sentiment"))) { 

			label_count = label_count_map.get(ngram_label);
		} 

		if (ngram_label != null && type.equalsIgnoreCase("spam")) {  

			label_count = spam_count_map.get(ngram_label);
		} 

		for (Post temp : post_list) { 

			sentence = temp.getContent();

			if (type.equalsIgnoreCase("giveaway")) {  

				post_label = temp.getGiveawayLabel();
			}

			else if (type.equalsIgnoreCase("sentiment")) { 

				post_label = temp.getSentimentLabel();
			}

			else if (type.equalsIgnoreCase("spam")) { 

				spam_label = temp.getSpamLabel();
			}

			if (spam_label != null && post_label != null && (!spam_label.isEmpty() || !post_label.isEmpty())) {

				if (ngram_label != null && (ngram_label.equalsIgnoreCase(post_label) || ngram_label.equalsIgnoreCase(spam_label))) {

					count = count + getSubStringCount(ngram, sentence);
				}
			}

			spam_label = "";
			post_label = "";
		}
		
		return ((count+1)/(label_count+1));
	}
	
	public double getNGramCountUtilForNGreaterThanOne(String ngram,List<Post> post_list, String ngram_label,String type) { 

		double count = 0.0;
		String sentence = ""; 
		String post_label = ""; 
		String spam_label = ""; 
		double n_minusone_count = 0.0;
		String minusOneGram = getNMinusOneGram(ngram.trim());

		for (Post temp : post_list) { 

			sentence = temp.getContent();

			if (type.equalsIgnoreCase("giveaway")) {  

				post_label = temp.getGiveawayLabel();
			}

			else if (type.equalsIgnoreCase("sentiment")) { 

				post_label = temp.getSentimentLabel();
			}

			else if (type.equalsIgnoreCase("spam")) { 

				spam_label = temp.getSpamLabel();
			}

			if (spam_label != null && post_label != null && (!spam_label.isEmpty() || !post_label.isEmpty())) {

				if (ngram_label != null && (ngram_label.equalsIgnoreCase(post_label) || ngram_label.equalsIgnoreCase(spam_label))) {

					count = count + getSubStringCount(ngram, sentence);
					n_minusone_count = n_minusone_count + getSubStringCount(minusOneGram, sentence);
				}
			}

			spam_label = "";
			post_label = "";
		}
		
		return ((count+1)/(n_minusone_count+1));
	}
	
	public String getNMinusOneGram(String ngram) { 
		
		String minusOne = "";
		int index = -1;
		
		index = ngram.lastIndexOf(" ");
		
		if (index != -1) { 
			
			minusOne = ngram.substring(0, index);
			minusOne = minusOne.trim();
		}
		
		return minusOne;
	}

	/** 
	 * Given an NGram and a sentence returns the count of the ngram in the sentence
	 * @param ngram String containing the ngram
	 * @param sentence String containing the sentence
	 * @return Integer containing the count of the NGram
	 */ 

	public static int getSubStringCount(String ngram,String sentence) {  

		int count = 0;
		int index = -1;

		index = sentence.indexOf(ngram);

		while(index != -1) { 

			count++;
			sentence = sentence.substring(index+ngram.length());
			index = sentence.indexOf(ngram);
		}

		return count;
	}

	/** 
	 * Given a map of NGram probabilities writes them to a file
	 * @param filename String containing the filename
	 * @param ngram_map_count Map<String,Double> containing ngram probabilities
	 */ 

	public void writeGramToFile(String filename,Map<String,Double> ngram_map_count) { 

		BufferedWriter bw;
		FileWriter fw;
		File f;
		double count;
		String transform = ""; 

		f = new File(filename); 

		if (!f.exists()) { 

			try {
				f.createNewFile();
			} catch (Exception e) { 
				e.printStackTrace();
			}
		}

		try { 

			fw = new FileWriter(filename,true);
			bw = new BufferedWriter(fw); 

			for (String s : ngram_map_count.keySet()) { 

				count = ngram_map_count.get(s);
				transform = getTransformedString(s);
				transform = transform + "=" + String.valueOf(count);
				bw.write(transform);
				bw.newLine();
			}

			bw.close();
			fw.close();

		} catch(Exception e) { 
			e.printStackTrace();
		}
	}

	/** 
	 * Given an ngram encloses it with a pair of braces
	 * @param ngram String containing the NGram
	 * @return String with the enclosing braces
	 */ 

	public static String getTransformedString(String ngram) { 

		String temp = ngram.trim();
		temp = "(" + ngram + ")";

		return temp;
	}

	/** 
	 * Main function to test the overall functionality of the class 
	 * @param args
	 */ 

	public static void main(String args[]) {  
		
	}
}


