package org.kutty.features;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.kutty.clean.Clean;
import org.tartarus.snowball.ext.englishStemmer;

/** 
 * Utility class for pre-processing and extracting of features 
 * 
 * @author Senjuti Kundu
 * 
 */ 

public class FeatureUtil {

	public static List<String> stop_words = new ArrayList<String>();
	public static Map<String,String> spam_map = new HashMap<String,String>();
	public static Map<String,String> giveaway_map = new HashMap<String,String>();
	public static Map<String,String> sentiment_map = new HashMap<String,String>(); 
	public Map<String,Integer> spam_count_map = new HashMap<String,Integer>();
	public Map<String,Integer> sentiment_count_map = new HashMap<String,Integer>();
	public Map<String,Integer> giveaway_count_map = new HashMap<String,Integer>(); 

	public final static String stopword_filename = "stopwords.txt"; 
	public final static String spam_filename = "spam_label.txt";
	public final static String giveaway_filename = "giveaway_label.txt";
	public final static String sentiment_filename = "sentiment_label.txt";

	/** 
	 * Static block to load the stopwords list
	 */ 

	static { 

		loadStopList(stopword_filename);
		loadLabelMaps(giveaway_filename, giveaway_map);
		loadLabelMaps(sentiment_filename, sentiment_map);
		loadLabelMaps(spam_filename, spam_map);
	} 

	public FeatureUtil() { 

		initLabelCountMap(giveaway_map, giveaway_count_map);
		initLabelCountMap(sentiment_map, sentiment_count_map);
		initLabelCountMap(spam_map,spam_count_map);
	} 

	/** 
	 * Loads the stopword list in memory from a file
	 * @param filename String containing the stopwords list
	 */ 

	public static void loadStopList(String filename) { 

		BufferedReader br;
		FileReader fr; 
		String s; 

		try {
			fr = new FileReader(filename);
			br = new BufferedReader(fr); 

			while((s = br.readLine()) != null) { 

				stop_words.add(s);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}  

	/** 
	 * Sanitizes a given string i.e. removes all the punctuation and other noise from it
	 * @param s String which is to be cleansed
	 * @return String which is hopefully sanitized
	 */ 

	public static String cleanString(String s) { 

		String clean = "";

		clean = Clean.cleanHTML(s);
		clean = Clean.removeURL(clean);
		clean = Clean.removePunctuationAndJunk(clean);
		clean = Clean.removeDigits(clean);
		clean = Clean.removeDays(clean);
		clean = Clean.removeMonths(clean);
		
		return clean;
	} 

	/** 
	 * Initializes the label count map with zero values
	 * @param label_map Map<T1,T2> containing the class labels
	 * @param label_count_map Map<T2,Integer> containing the count of each label
	 */ 

	public static <T1,T2> void initLabelCountMap(Map<T1,T2> label_map,Map<T2,Integer> label_count_map) {

		for (T2 temp : label_map.values()) { 
			label_count_map.put(temp,0);
		}
	}

	/** 
	 * Loads the class label maps for a given filename
	 * @param filename String containing the filename
	 * @param label_map Map<T1,T2> containing the mapping between the class label and class code
	 */ 

	@SuppressWarnings("unchecked")
	public static <T1, T2> void loadLabelMaps(String filename,Map<T1,T2> label_map) { 

		BufferedReader br;
		FileReader fr;
		String temp = ""; 
		int index; 
		T1 alias;
		T2 label; 

		try { 

			fr = new FileReader(filename);
			br = new BufferedReader(fr);

			while((temp = br.readLine()) != null) { 

				index = -1;
				index = temp.indexOf('=');

				if (index != -1) { 

					alias = (T1) temp.substring(0,index).trim();
					label = (T2) temp.substring(index+1).trim();

					label_map.put(alias, label);
				}
			}
		} catch (Exception e) { 
			e.printStackTrace();
		}

	} 

	/** 
	 * Checks whether a given word is a stopword or not
	 * @param s String whose stopword criteria has to be checked
	 * @return true if the given word is a stopword false otherwise
	 * 
	 */ 

	public static boolean isStopWord(String s) { 

		if(stop_words.contains(s)) {
			return true;
		}
		return false;
	}

	/** 
	 * Given a sentence removes all the stopwords from it
	 * @param s String containing the sentence
	 * @return String sans the stopwords
	 * 
	 */ 

	public static String removeStopWords(String s) { 

		String words[] = s.split(" ");
		String removed = ""; 

		for (int i = 0; i < words.length; i++) { 

			if (!isStopWord(words[i].toLowerCase().trim())) { 

				removed = removed + words[i].toLowerCase().trim() + " ";
			}
		}

		return removed.trim();
	}

	/** 
	 * Returns a sentence where each word is stemmed i.e reduced to its root word
	 * @param s Sentence to be stemmed
	 * @return A sentence where all the given words are stemmed
	 */ 

	public static String getStemPerWord(String s) { 

		StringTokenizer word_token;
		String stem_sentence = ""; 
		String temp = "";
		word_token = new StringTokenizer(s," "); 

		while (word_token.hasMoreTokens()) { 

			temp = word_token.nextToken();
			temp = getStem(temp);
			stem_sentence = stem_sentence + temp + " ";
		}

		return stem_sentence;
	} 

	/** 
	 * Stems a given word (i.e. Reduces it to its root word)
	 * @param s Word which is to be stemmed
	 * @return the Stemmed word
	 */ 

	public static String getStem(String s) {

		englishStemmer eng = new englishStemmer();
		eng.setCurrent(s);

		if (eng.stem()) { 

			return eng.getCurrent();
		}

		return s;
	}

	/** 
	 * Utility function to transform a given string into its NGram representation
	 * @param s String s which is to be transformed
	 * @param n Integer specifying the value of the Ngram
	 * @return String with the NGrams concatenated by a "|"
	 */ 

	public static String getNGram(String s,int n) { 

		if (n > s.length()) { 

			return "";
		}

		n = n-1;
		StringTokenizer word_token = new StringTokenizer(s," ");
		String [] word_array = new String[word_token.countTokens()];
		int count = 0;
		String n_gram_string = ""; 

		while(word_token.hasMoreTokens()) { 

			word_array[count++] = word_token.nextToken();
		} 

		for (int i = 0; i < count-n; i++) { 

			for (int j = i; j <= i+n && (i+n) < count; j++) { 

				n_gram_string = n_gram_string + word_array[j] + " ";
			}

			n_gram_string = n_gram_string + "|";
		}

		return n_gram_string;
	}

	/** 
	 * Given the data containing the Instagram posts returns a string of the data
	 * @param filename String containing the filename
	 * @return String containing the entire data of the file
	 */ 

	public static String loadInstagramData(String filename) { 

		BufferedReader br;
		FileReader fr;
		String data = ""; 
		String temp = ""; 

		try { 

			fr = new FileReader(filename);
			br = new BufferedReader(fr);

			while((temp = br.readLine()) != null) { 

				data = data + temp;
			} 

			br.close();
			fr.close();

		} catch (Exception e) { 

			e.printStackTrace();
		} 

		data = Clean.removeNewLines(data);

		return data;
	}

	/** 
	 * Given a tag name returns the content of the tag
	 * @param s String containing the text
	 * @param tagname String containing the tagname
	 * @return String containing the content of the tag
	 */ 

	public static String getTagContent(String s,String tagname) { 

		String start_tag = "<" + tagname + ">";
		String end_tag = "</" + tagname + ">";
		String tag_content = ""; 

		int start_index = -1;
		int end_index = -1;

		start_index = s.indexOf(start_tag);
		end_index = s.indexOf(end_tag); 

		if (start_index != -1 && end_index != -1 && start_index < s.length()-1) { 

			start_index = s.indexOf('>',start_index+1);
			end_index = s.indexOf('<', end_index-1);

			if (start_index != -1 && end_index != -1 && start_index < s.length()-1) { 

				tag_content = s.substring(start_index+1, end_index);
			}
		}

		return tag_content;
	} 

	/** 
	 * Given the Instagram data file loads it in a list of posts
	 * @param filename String containing the filename
	 * @param post_list List<Post> containing the list of posts
	 */ 

	public static void populateInstagramGiveawayData(String filename,List<Post> post_list) { 

		int start_tag = -1;
		int end_tag = -1;
		String content = loadInstagramData(filename);
		String caption = "";
		String tagset = ""; 
		String class_label = ""; 
		end_tag = content.indexOf("</Tag>"); 
		Post post; 

		while(end_tag != -1) { 

			start_tag = content.indexOf("<Tag>");

			if (start_tag != -1) { 

				tagset = getTagContent(content, "TagSet");
				caption = getTagContent(content, "CaptionText");
				class_label = getTagContent(content, "ClassLabel"); 
				class_label = giveaway_map.get(class_label); 

				post = new Post();
				post.setContent(caption);
				post.setGiveawayLabel(class_label);
				post.setTagset(tagset);
				post_list.add(post);
			}

			end_tag = content.indexOf('>',end_tag+1);

			if (end_tag != -1) { 
				content = content.substring(end_tag+1);
			}

			end_tag = content.indexOf("</Tag>");
		}	
	} 

	/** 
	 * Loads Instagram data from external file and populates the List of posts
	 * @param filename String containing the filename which has data
	 * @param post_list List<Post> containing the list of posts
	 */ 

	public static void populateInstagramSentimentData(String filename,List<Post> post_list) { 

		int start_tag = -1;
		int end_tag = -1;
		String content = loadInstagramData(filename);
		String caption = "";
		String tagset = ""; 
		String class_label = ""; 
		end_tag = content.indexOf("</Tag>");
		String [] labels;
		String spam_label = "";
		Post post; 

		while(end_tag != -1) { 

			start_tag = content.indexOf("<Tag>");

			if (start_tag != -1) { 

				tagset = getTagContent(content, "TagSet");
				caption = getTagContent(content, "CaptionText");
				class_label = getTagContent(content, "SentimentLabel"); 
				labels = class_label.split(",");
				spam_label = spam_map.get(labels[0].trim());
				class_label = sentiment_map.get(labels[1].trim()); 

				post = new Post();
				post.setContent(caption);
				post.setSentimentLabel(class_label);
				post.setSpamLabel(spam_label);
				post.setTagset(tagset);

				post_list.add(post);
			}

			end_tag = content.indexOf('>',end_tag+1);

			if (end_tag != -1) {  

				content = content.substring(end_tag+1);
			}

			end_tag = content.indexOf("</Tag>");
		}	
	} 

	/** 
	 * Given a sentence for other channels returns the class label set
	 * @param sentence String containing the sentence from which the data is retrieved
	 * @return String containing the tag labels
	 */ 

	public static String getTagLabelOtherChannel(String sentence) { 

		int start_index = -1;
		int end_index = -1; 

		String taglabels = "";

		end_index = sentence.indexOf('>');
		start_index = sentence.indexOf('<'); 

		if (start_index != -1 && end_index != -1) { 

			taglabels = sentence.substring(start_index+1, end_index);
		}

		return taglabels;
	} 

	/** 
	 * Given a string returns the sentence/content
	 * @param sentence String containing the sentence
	 * @return String which contains only the content
	 */ 

	public static String getSentence(String sentence) { 

		int index = -1;
		String content = "";
		index = sentence.indexOf('>'); 

		if (index != -1) { 

			content = sentence.substring(index+1);
		}

		return content.trim();
	} 

	/** 
	 * Populates the post list with data from channels other than Instagram
	 * @param filename String containing the filename from which the data has to be read
	 * @param post_list List<Post> containing the information about the posts
	 */ 

	public static void populateOtherChannelData(String filename,List<Post> post_list) { 

		BufferedReader br;
		FileReader fr;
		String temp = ""; 
		String [] array;
		Post post; 
		
		try { 

			fr = new FileReader(filename);
			br = new BufferedReader(fr); 
			
			while((temp = br.readLine()) != null) { 
				
				String taglabels = getTagLabelOtherChannel(temp);
				array = taglabels.trim().split(",");
				String spam_label = spam_map.get(array[0].trim());
				String sentiment_label = sentiment_map.get(array[1].trim());
				String content = getSentence(temp); 

				post = new Post();
				post.setContent(content);
				post.setSpamLabel(spam_label);
				post.setSentimentLabel(sentiment_label);
				
				post_list.add(post);

			} 

		} catch (Exception e) { 

			e.printStackTrace();
		}
	} 

	public static void main(String args[]) { 

		List<Post> post_list = new ArrayList<Post>();
		//populateInstagramGiveawayData("giveaway/split_5.txt", post_list);
		//System.out.println(post_list);
		//giveaway_count_map.putAll(LabelCountUtil.getGiveawayLabelCount(post_list));
		//System.out.println(giveaway_count_map);
		populateOtherChannelData("Twitter_test_annotated.txt", post_list);
	}
}
