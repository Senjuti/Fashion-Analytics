package org.kutty.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.joda.time.DateTime;
import org.kutty.clean.Clean;
import org.kutty.db.MongoBase;

import com.cybozu.labs.langdetect.LangDetectException;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

/**
 * Base class for writing tweets/posts/feeds to file for labeling and further processing 
 * The need of this class arose in order to fulfill the need to provide a unified interface to 
 * access the varied content from across the different social channels and write them to the files.
 * 
 * @author Senjuti Kundu
 *
 */ 

public class CommitToFile {

	HashMap<String,String> collection_names = new HashMap<String,String>();
	HashMap<String,String> channel_names = new HashMap<String,String>(); 
	
	/** 
	 * public constructor to initialize the language profiles 
	 * @throws IOException
	 * @throws LangDetectException
	 */ 
	
	public CommitToFile() throws IOException, LangDetectException { 

		populateDB("collection_names.txt","channel_list.txt");
		LanguageDetector.init("profiles");
	}
	
	/** 
	 * TBD
	 */ 
	
	public void populateTrainDB() { 
		
		for(String channel : channel_names.values()) { 
			
			for(String collection : collection_names.values()) { 
				
				try {
					writeToFile(collection,channel);
				} catch (IOException | LangDetectException e) {
					e.printStackTrace();
				}
			}
		}
	} 
	
	/** 
	 * Populates the collection_names map with the product names and collection names
	 * @param product_list_filename String containing the product list filename
	 * @param channel_list_filename String containing the channel list filename
	 * @throws IOException
	 */ 
	
	public void populateDB(String product_list_filename,String channel_list_filename) throws IOException { 

		BufferedReader br;
		FileReader fr; 

		fr = new FileReader(product_list_filename);
		br = new BufferedReader(fr); 

		String alias;
		String value;
		String s = "";
		int index = -1; 

		while ((s = br.readLine()) != null) { 

			index = s.indexOf('=');

			if (index != -1) { 

				alias = s.substring(0, index);
				value = s.substring(index+1,s.length());

				collection_names.put(alias, value);
			}
		}

		br.close();
		fr.close(); 

		fr = new FileReader(channel_list_filename);
		br = new BufferedReader(fr);

		while ((s = br.readLine()) != null) { 

			index = s.indexOf('=');

			if (index != -1) { 

				alias = s.substring(0, index);
				value = s.substring(index+1,s.length());

				channel_names.put(alias, value);
			}
		}

		br.close();
		fr.close(); 
	} 
	
	public void writeToFile(String collection_name,String channel_name) throws IOException, LangDetectException { 

		MongoBase mongo = new MongoBase(); 
		FileWriter fw;
		BufferedWriter bw; 
		
		mongo.setCollection(collection_name);
		DBCollection collection;
		DBObject query;
		int count = 0;
		collection = mongo.getCollection();
		DateTime to = new DateTime();
		DateTime from = to.minusYears(13);
		query = new BasicDBObject("Channel",channel_name).
				append("TimeStamp", new BasicDBObject("$gte",from.toDate()).append("$lte", to.toDate()));
		
		@SuppressWarnings("unchecked")
		List<String> distinct_messages = collection.distinct("Message", query);
		String file_name = channel_name + "_train" + ".txt";
		fw = new FileWriter(file_name,true);
		bw = new BufferedWriter(fw);
		Object temp_obj; 

		for(String s:distinct_messages) { 

			temp_obj = s; 

			if (temp_obj != null && s.length() > 2) {  

				if (channel_name.equalsIgnoreCase("RSS")) { 

					s = Clean.cleanHTML(Clean.extractRSSText(s));
				}

				else if (channel_name.equalsIgnoreCase("Twitter")) { 

					s = Clean.removeLinksFromTweets(s);
				}

				s = Clean.removeNewLines(s);
				s = Clean.removeURL(s);
				LanguageDetector.init("profiles"); 

				if (!checkInFile(s,file_name) && LanguageDetector.detect(s).equalsIgnoreCase("en")) { 

					bw.write(s);
					System.out.println(s);
					bw.newLine();
					count++;
					if (count > 300) { 
						break;
					}
				}
			}
		}

		bw.close();
		fw.close();
	} 
	
	/** 
	 * Writes the Instagram caption text from to a given file for a given collection
	 * @param filename String containing the filename where it is to be written
	 * @param collection_name String containing the collection name 
	 * @throws IOException
	 * @throws LangDetectException
	 */ 
	
	@SuppressWarnings("unused")
	public static void writeInstagramCaptionTextToFile(String filename,String collection_name) throws IOException, LangDetectException {

		MongoBase mongo = new MongoBase();
		mongo.setCollection(collection_name);
		DBObject query;
		DBObject fields;
		BufferedWriter bw;
		DBCursor cursor; 
		DBCollection collection;
		BasicDBList tagList;
		BasicDBObject temp;
		String longitude = "";
		String latitude = ""; 
		String tagset = "";
		String full_tag = ""; 
		
		FileWriter fw;
		File f; 

		f = new File(filename);

		if (!f.exists()) {  

			f.createNewFile();
		}

		fw = new FileWriter(filename,true);
		bw = new BufferedWriter(fw);
		int count = 0;
		query = new BasicDBObject("Channel","Instagram");
		fields = new BasicDBObject("CaptionText",1).append("TagSet",1).
					 append("Latitude",1).append("Longitude", 1); 
		
		collection = mongo.getCollection();
		
		@SuppressWarnings("unchecked")
		List<String> caption_list = collection.distinct("CaptionText"); 
		
		for(String text:caption_list) { 
			
			if (LanguageDetector.detect(text).equalsIgnoreCase("en")) {  
				
				query.put("CaptionText",text);
				tagList = (BasicDBList) collection.findOne(query,fields).get("TagSet");
				text = Clean.removeNewLines(text); 
				tagset = ListConverter.getCSVSet(tagList); 
				
				text = getXMLTaggedString(text,"CaptionText");
				tagset = getXMLTaggedString(tagset, "TagSet");
				
				full_tag = tagset + "\n" + text + "\n" + getXMLTaggedString("","SentimentLabel");
				full_tag = getXMLTaggedString(full_tag, "Tag");
				full_tag = full_tag + "\n";  
				
				System.out.println(full_tag); 
				
				bw.write(full_tag);
				bw.newLine();
				count++; 
				System.out.println("Count is : " + count);
				if (count > 700) { 
					break;
				}
			}	
		}

		bw.close();
		fw.close(); 		
	} 
	
	/** 
	 * Tags a given text with specified XML tags
	 * @param text String containing the text to be tagged
	 * @param tag_name String specifying the tag name
	 * @return String containing the tagged text
	 */ 
	
	public static String getXMLTaggedString(String text,String tag_name) { 
		
		String opening_tag = "";
		String closing_tag = "";
		String tagged_string = ""; 
		
		tag_name = tag_name.trim(); 
		
		opening_tag = "<" + tag_name + ">";
		closing_tag = "</" + tag_name + ">"; 
		
		tagged_string = opening_tag + "\n" + text + "\n" + closing_tag;
		
		return tagged_string;
	} 
	
	/** 
	 * Checks whether a given post already exists in a given file or not
	 * @param s String containing the post to check
	 * @param filename String containing the filename in which this has to be checked
	 * @return true if the post already exists false otherwise
	 * @throws IOException
	 */ 
	
	public boolean checkInFile(String s,String filename)throws IOException { 

		boolean exists = false;
		BufferedReader br = null;
		FileReader fr = null; 
		String str = ""; 

		try {  

			fr = new FileReader(filename);
			br = new BufferedReader(fr); 

		} catch(Exception e) { 

			e.printStackTrace();
		}  

		while((str = br.readLine()) != null) { 

			if (str.equalsIgnoreCase(s)) { 

				exists = true;
				break;
			}	
		} 

		br.close(); 
		fr.close(); 

		return exists;
	} 
	
	public static void main(String args[]) throws IOException, LangDetectException { 
			
		new CommitToFile().writeToFile("Forever21", "Facebook");
	}
}
