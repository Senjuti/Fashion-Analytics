package org.kutty.satisfaction;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.joda.time.DateTime;
import org.kutty.constants.Constants;
import org.kutty.db.MongoBase;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

/** 
 * Performs satisfaction analytics on the satisfaction collection 
 * @author Senjuti Kundu
 */

public class SatisfactionAnalytics {

	/** 
	 * Calculates the satisfaction score on a given channel for a given product
	 * @param channel String containing the channel name
	 * @param brand String containing the brand name
	 * @param from Date containing the starting date
	 * @param to Date containing the ending date
	 * @return Double containing the satisfaction of the given channel and product
	 */

	public static double getSatisfactionScoreProductChannel(String channel,String brand,Date from,Date to) { 

		MongoBase mongo;
		DBObject query;
		DBCollection collection;
		DBCursor cursor;
		DBObject temp;
		double score = 0.0;
		int size = 0; 

		try { 

			mongo = new MongoBase();
			mongo.setDB(Constants.ANALYTICS_DB);
			mongo.setCollection(Constants.SATISFACTION_COLLECTION); 

			query = new BasicDBObject("Channel",channel).append("Product",brand).
					append("TimeStamp", new BasicDBObject("$gte",from).append("$lte",to)).
					append("SatisfactionScore", new BasicDBObject("$gte", 0)); 

			collection = mongo.getCollection();
			cursor = collection.find(query); 
			size = cursor.size(); 

			while(cursor.hasNext()) { 

				temp = cursor.next();
				score = score + (double)temp.get("SatisfactionScore");
			}
		} catch (Exception e) { 
			e.printStackTrace();
		}

		return (score/(size+1));
	}

	/** 
	 * Returns the satisfaction score for a given brand across all channels
	 * @param brand String containing the brand name
	 * @param from Date containing the starting date
	 * @param to Date containing the ending date
	 * @return Double containing the satisfaction score for the product in the given interval
	 */

	public static double getSatisfactionScoreProduct(String brand,Date from,Date to) { 

		double score = 0.0;
		double channelScore = 0.0;

		try { 

			for (String channel : Constants.channelNames) {  
				
				channelScore = channelScore + getSatisfactionScoreProductChannel(channel, brand, from, to);
			} 

			score = (channelScore/Constants.channelNames.length); 

		} catch (Exception e) {  

			e.printStackTrace();
		}

		return score;
	}

	/**
	 * Calculates the satisfaction index of all products in a given time interval 
	 * @param from Date containing the starting date
	 * @param to Date containing the ending date
	 * @return Map<String,Double> containing the mapping between the brand name and satisfaction score
	 */

	public static Map<String,Double> getSatisfactionAllChannels(Date from,Date to) { 

		Map<String,Double> satisfactionMap = new HashMap<String,Double>();
		for (String brand : Constants.brandNames) { 
			satisfactionMap.put(brand, getSatisfactionScoreProduct(brand, from, to));
		}

		return satisfactionMap;
	}

	public static void writeSatisfactionToFile(String filename,Date from,Date to) { 

		BufferedWriter bw;
		FileWriter fw;
		Map<String,Double> productScores = getSatisfactionAllChannels(from, to);
		String formattedOutput; 

		try { 

			fw = new FileWriter(filename);
			bw = new BufferedWriter(fw);

			for (String key : productScores.keySet()) { 

				formattedOutput = key + "=" + productScores.get(key);
				bw.write(formattedOutput);
				bw.newLine();
			} 
			
			bw.close();
			fw.close();
			
		} catch (Exception e) {  

			e.printStackTrace();
		}
	}

	public static Map<String,Double> getSatisfactionAllProducts(String filename) { 

		Map<String,Double> productSatMap = new HashMap<String,Double>();
		FileReader fr;
		BufferedReader br;
		String temp;
		String product;
		double score;
		int index = -1; 

		try { 
			
			fr = new FileReader(filename);
			br = new BufferedReader(fr);

			while((temp = br.readLine()) != null) { 

				index = temp.indexOf("="); 
				
				if (index != -1) {  
					
					product = temp.substring(0, index);
					score = Double.valueOf(temp.substring(index+1));
					productSatMap.put(product, score);
				}
			}

			br.close();
			fr.close();
			
		} catch(Exception e) {  
			
			e.printStackTrace();

		}  
		
		return productSatMap;
	} 
	
	public static void main(String args[]) { 

		DateTime to = new DateTime();
		DateTime from = to.minusMonths(3);
		//writeSatisfactionToFile("Satisfaction.txt", from.toDate(), to.toDate());
		System.out.println(getSatisfactionAllChannels(from.toDate(), to.toDate()));

	}
}
