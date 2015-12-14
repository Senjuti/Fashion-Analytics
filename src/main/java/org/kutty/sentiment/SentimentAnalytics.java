package org.kutty.sentiment;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.kutty.constants.Constants;
import org.kutty.db.MongoBase;
import org.kutty.utils.DateConverter;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

/** 
 * Performs analytics on the sentiment database for all products and channels
 * 
 * @author Senjuti Kundu
 */ 

public class SentimentAnalytics {
	
	/** 
	 * Given a product and a channel returns the count in a given interval
	 * @param channel String containing the channel name
	 * @param brand String containing the brand name
	 * @param sentimentLabel String containing the sentiment label
	 * @param from Date containing the starting date
	 * @param to Date containing the ending date
	 * @return Integer containing the count of the sentiment
	 */
	
	public static int getSentimentCountProductChannel(String channel,String brand,String sentimentLabel,Date from,Date to) {

		int count = 0;
		DBCollection collection;
		DBObject query;
		MongoBase mongo;

		try { 

			mongo = new MongoBase();
			mongo.setDB(Constants.ANALYTICS_DB);
			mongo.setCollection(brand);

			if (!channel.equalsIgnoreCase("Instagram")) { 

				query = new BasicDBObject("Channel",channel).append("Product",brand).
						append("SentimentLabel",sentimentLabel).
						append("TimeStamp", new BasicDBObject("$gte",from).append("$lte",to));
			} else { 

				double fromDate = DateConverter.getJulianDate(from);
				double toDate = DateConverter.getJulianDate(to);

				query = new BasicDBObject("Channel",channel).append("Product",brand).
						append("SentimentLabel",sentimentLabel).
						append("OtherDate", new BasicDBObject("$gte",fromDate).append("$lte",toDate));
			}

			collection = mongo.getCollection();
			count = collection.find(query).size(); 

		} catch (Exception e) {  

			e.printStackTrace();
		}

		return count;
	}
	
	/** 
	 * Given a product returns the count of the sentiment label for all channels
	 * @param brand String containing the brand name
	 * @param sentimentLabel String containing the sentiment label
	 * @param from Date containing the starting date
	 * @param to Date containing the ending date
	 * @return Integer containing the count for the given product
	 */ 
	
	public static int getSentimentCountProduct(String brand,String sentimentLabel,Date from,Date to) {

		int count = 0;
		DBCollection collection;
		DBObject query;
		DBObject queryOther;
		MongoBase mongo;

		try { 

			mongo = new MongoBase();
			mongo.setDB(Constants.ANALYTICS_DB);
			mongo.setCollection(brand);

			query = new BasicDBObject("Product",brand).
					append("SentimentLabel",sentimentLabel).
					append("TimeStamp", new BasicDBObject("$gte",from).append("$lte",to));

			double fromDate = DateConverter.getJulianDate(from);
			double toDate = DateConverter.getJulianDate(to);

			queryOther = new BasicDBObject("Product",brand).
					append("SentimentLabel",sentimentLabel).
					append("OtherDate", new BasicDBObject("$gte",fromDate).append("$lte",toDate));


			collection = mongo.getCollection();
			count = collection.find(query).size(); 
			count = count + collection.find(queryOther).size();

		} catch (Exception e) {  

			e.printStackTrace();
		}

		return count;
	}
	
	/** 
	 * For a given channel returns the count of sentiment for all products
	 * @param channel String containing the channel name
	 * @param sentimentLabel String containing the sentiment label
	 * @param from Date containing the starting date
	 * @param to Date containing the ending date
	 * @return Integer containing the count of sentiments for all products of a given channel
	 */ 
	
	public static int getSentimentCountChannel(String channel,String sentimentLabel,Date from,Date to) {

		int count = 0;
		DBCollection collection;
		DBObject query;
		MongoBase mongo;

		try { 

			mongo = new MongoBase();
			mongo.setDB(Constants.ANALYTICS_DB);
			for (String brand : Constants.brandNames) { 
				
				mongo.setCollection(brand);

				if (!channel.equalsIgnoreCase("Instagram")) { 

					query = new BasicDBObject("Channel",channel).append("Product",brand).
							append("SentimentLabel",sentimentLabel).
							append("TimeStamp", new BasicDBObject("$gte",from).append("$lte",to));
				} else { 

					double fromDate = DateConverter.getJulianDate(from);
					double toDate = DateConverter.getJulianDate(to);

					query = new BasicDBObject("Channel",channel).append("Product",brand).
							append("SentimentLabel",sentimentLabel).
							append("OtherDate", new BasicDBObject("$gte",fromDate).append("$lte",toDate));
				}

				collection = mongo.getCollection();
				count = count + collection.find(query).size(); 
			} 
			
		} catch (Exception e) {  

			e.printStackTrace();
		}

		return count;
	}
	
	/** 
	 * Returns all the posts made by a given author for a given brand and channel during a given period 
	 * @param author String containing the author name
	 * @param channel String containing the channel name
	 * @param brand String containing the brand name
	 * @param sentimentLabel String containing the sentiment label
	 * @param from Date containing the starting date
	 * @param to Date containing the ending date
	 * @return Set<String> containing the set of messages
	 */
	
	public static Set<String> getAuthorMap(String author,String channel,String brand,String sentimentLabel,Date from,Date to) { 
		
		MongoBase mongo;
		Set<String> authorSet = new HashSet<String>();
		DBObject query;
		DBCursor cursor; 
		DBCollection collection; 
		
		try { 
			
			mongo = new MongoBase();
			mongo.setDB(Constants.ANALYTICS_DB);
			mongo.setCollection(brand); 
			
			if (!channel.equalsIgnoreCase("Instagram")) { 

				query = new BasicDBObject("Channel",channel).append("Product",brand).
						append("SentimentLabel",sentimentLabel).append("Author",author).
						append("TimeStamp", new BasicDBObject("$gte",from).append("$lte",to));
			} else { 

				double fromDate = DateConverter.getJulianDate(from);
				double toDate = DateConverter.getJulianDate(to);

				query = new BasicDBObject("Channel",channel).append("Product",brand).
						append("SentimentLabel",sentimentLabel).append("Author",author).
						append("OtherDate", new BasicDBObject("$gte",fromDate).append("$lte",toDate));
			} 
			
			collection = mongo.getCollection();
			cursor = collection.find(query);
			
			while(cursor.hasNext()) { 
				
				authorSet.add((String) cursor.next().get("Message"));
			}
		} catch (Exception e) { 
			e.printStackTrace();
		}
		
		return authorSet;
	}
}
