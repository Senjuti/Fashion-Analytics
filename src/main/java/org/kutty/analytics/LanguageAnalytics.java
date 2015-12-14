package org.kutty.analytics;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.kutty.db.MongoBase;
import org.kutty.utils.DateConverter;
import org.kutty.utils.LanguageDetector;
import org.kutty.utils.PrintUtil;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

/** 
 * Profiles posts on the basis of their language of origin i.e. English, German French etc 
 * 
 * @author Senjuti Kundu
 * 
 */

public class LanguageAnalytics {

	public static Map<String,String> language_code_map = new HashMap<String,String>(); 
	public static Map<String, String> collection_names = new HashMap<String,String>();;
	public static List<String> channel_list = new ArrayList<String>(); 

	/** 
	 * Static block to initialize the language profiles and product collections
	 */ 
	
	static { 

		try {  
			
			loadLanguageCodes("language_map.txt",language_code_map);
			LanguageDetector.init("profiles"); 
			init("product_list.txt",collection_names); 
			
			channel_list.add("Twitter");
			channel_list.add("Facebook");
			channel_list.add("Reddit");
			channel_list.add("Instagram");
			channel_list.add("Youtube"); 
			
		} catch (IOException e) { 
			
			e.printStackTrace();
		}
	}

	/** 
	 * Utility function to initialize the list of fashion brands and their associated collection names
	 * @param filename String containing the filename from which the data is to be read
	 * @throws IOException
	 */ 

	public static void init(String filename,Map<String,String> name_map) throws IOException
	{ 
		BufferedReader br;
		FileReader fr;
		String alias;
		String collection_name;
		String s = ""; 
		int index; 

		fr = new FileReader(filename);
		br = new BufferedReader(fr);

		while((s = br.readLine()) != null) { 

			index = s.indexOf('=');

			if(index != -1) { 

				alias = s.substring(0,index);
				collection_name = s.substring(index+1,s.length());
				alias = alias.trim();
				collection_name = collection_name.trim();
				name_map.put(alias, collection_name); 
			}
		}

		br.close();
		fr.close();
	}   
	
	/** 
	 * Loads the language codes and corresponding language further use
	 * @param filename String containing the filename which contains the language profiles
	 * @param language Map<String,String> containing the mapping between language codes and names
	 * @throws IOException
	 */ 
	
	public static void loadLanguageCodes(String filename,Map<String,String> language) throws IOException { 

		BufferedReader br;
		FileReader fr;
		String temp = ""; 
		int index = -1; 
		String code = "";
		String name = ""; 

		fr = new FileReader(filename);
		br = new BufferedReader(fr);

		while((temp = br.readLine()) != null) { 

			index = temp.indexOf('\t');

			if (index != -1) { 

				code = temp.substring(0,index).trim();
				name = temp.substring(index+1).trim();
				language.put(code, name);
			}
		}

		br.close();
		fr.close();
	}
	
	/** 
	 * Given a text returns the language in which it was written
	 * @param s String containing the text
	 * @return String containing the language of the text
	 */ 
	
	public static String getLanguage(String s) { 

		String name = ""; 
		String code = LanguageDetector.detect(s);
		name = language_code_map.get(code); 
		
		return name;
	} 
	
	/** 
	 * For a given product and channel returns a mapping of the languages and their counts
	 * @param product String containing the product 
	 * @param channel String containing the channel of the given product
	 * @param from Date containing the starting date
	 * @param to Date containing the ending date
	 * @return Map<String,Integer> containing the mapping between the language and its count
	 */ 
	
	public static Map<String,Integer> getLanguageProductChannel(String product,String channel,Date from,Date to) {

		MongoBase mongo = null;
		BasicDBObject query;
		BasicDBObject fields;
		DBCollection collection;
		DBCursor cursor;
		DBObject temp;
		double from_date;
		double to_date;
		String collection_name = ""; 
		String text = ""; 
		String language = ""; 
		
		from_date = DateConverter.getJulianDate(from);
		to_date = DateConverter.getJulianDate(to);
		collection_name = collection_names.get(product.toLowerCase().trim()); 
		Map<String,Integer> language_count_map = new HashMap<String,Integer>(); 
		
		if (!(channel.equalsIgnoreCase("Reddit") || channel.equalsIgnoreCase("Instagram"))) { 

			query = new BasicDBObject("Channel",channel).
					append("TimeStamp", new BasicDBObject("$gte",from).append("$lte",to));
			fields = new BasicDBObject("Message",1).append("TimeStamp", 1); 

		} else { 

			if (channel.equalsIgnoreCase("Reddit")) { 

				query = new BasicDBObject("Channel",channel).
						append("TimeStamp", new BasicDBObject("$gte",from_date).append("$lte",to_date));

				fields = new BasicDBObject("Message",1).append("TimeStamp",1); 

			} else { 

				BasicDBList query_list = new BasicDBList();
				query_list = new BasicDBList();
				query_list.add(new BasicDBObject("Type", "tag"));
				query_list.add(new BasicDBObject("Type", "image"));
				query_list.add(new BasicDBObject("Type", "video"));

				query = new BasicDBObject("Channel",channel).append("$or",query_list).
						append("Timestamp", new BasicDBObject("$gte",from_date).append("$lte",to_date)); 

				fields = new BasicDBObject("CaptionText",1).append("Timestamp",1);
			}	
		}

		try {  

			mongo = new MongoBase();
			mongo.setCollection(collection_name);
			collection = mongo.getCollection(); 
			cursor = collection.find(query, fields); 

			while(cursor.hasNext()) { 

				temp = cursor.next(); 
				
				if (!channel.equalsIgnoreCase("Instagram")) { 
					
					text = (String) temp.get("Message");  
					
				} else { 
					
					text = (String) temp.get("CaptionText");
				}
				
				language = getLanguage(text);
				
				if (language_count_map.containsKey(language)) { 
					
					int count = language_count_map.get(language);
					language_count_map.put(language, count+1); 
					
				} else {  
					
					language_count_map.put(language,1);
				}
			} 

		} catch (Exception e) {  

			e.printStackTrace(); 

		} 

		return language_count_map;
	} 
	
	/** 
	 * For a given product returns the language profiles for all channels
	 * @param product String containing the product whose channels have to be mapped
	 * @param from Date containing the starting date
	 * @param to Date containing the ending date
	 * @return Map<String,Map<String,Integer>> containing the mapping between the product and language profile
	 */ 
	
	public static Map<String,Map<String,Integer>> getAllChannels(String product,Date from,Date to) { 
		
		Map<String,Map<String,Integer>> all_channel_map = new HashMap<String,Map<String,Integer>>();
		Map<String,Integer> channel_map; 
		
		for (String channel : channel_list) { 
			
			channel_map = getLanguageProductChannel(product, channel, from, to);
			all_channel_map.put(channel, channel_map);
		}
		
		return all_channel_map;
	}
	
	/** 
	 * Returns the language profile for all products of a given channel
	 * @param channel String containing the channel name
	 * @param from Date containing the starting date
	 * @param to Date containing the ending date
	 * @return Map<String,Map<String,Integer>> containing the mapping between the product and its language profile
	 */ 
	
	public static Map<String,Map<String,Integer>> getAllProducts(String channel,Date from,Date to) { 
		
		Map<String,Map<String,Integer>> all_product_map = new HashMap<String,Map<String,Integer>>();
		Map<String,Integer> product_map; 
		
		for (String product : collection_names.keySet()) { 
			
			product_map = getLanguageProductChannel(product, channel, from, to);
			all_product_map.put(product, product_map);
		}
		
		return all_product_map;
	}
	
	/** 
	 * Main functionality to test the functionality of the class
	 * @param args
	 */ 
	
	public static void main(String args[]) {  
		
		try {
			
			DateTime now = new DateTime();
			DateTime prev = now.minusYears(3);
			PrintUtil.printMap(getLanguageProductChannel("giveaway","Instagram",prev.toDate(),now.toDate()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
