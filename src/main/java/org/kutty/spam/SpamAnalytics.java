package org.kutty.spam;

import java.util.Date;

import org.kutty.constants.Constants;
import org.kutty.db.MongoBase;
import org.kutty.utils.DateConverter;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

/** 
 * Performs analytics on the spam detected for all channels and products
 * @author Senjuti Kundu
 */

public class SpamAnalytics {
	
	/** 
	 * Given a product and a channel returns the count in a given interval
	 * @param channel String containing the channel name
	 * @param brand String containing the brand name
	 * @param sentimentLabel String containing the sentiment label
	 * @param from Date containing the starting date
	 * @param to Date containing the ending date
	 * @return Integer containing the count of the sentiment
	 */
	
	public static int getSpamCountProductChannel(String channel,String brand,String spamLabel,Date from,Date to) {

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
						append("SpamLabel",spamLabel).
						append("TimeStamp", new BasicDBObject("$gte",from).append("$lte",to));
			} else { 

				double fromDate = DateConverter.getJulianDate(from);
				double toDate = DateConverter.getJulianDate(to);

				query = new BasicDBObject("Channel",channel).append("Product",brand).
						append("SpamLabel",spamLabel).
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
	 * Given a product returns the count of the spam label for all channels
	 * @param brand String containing the brand name
	 * @param spamLabel String containing the spam label
	 * @param from Date containing the starting date
	 * @param to Date containing the ending date
	 * @return Integer containing the count for the given product
	 */ 
	
	public static int getSpamCountProduct(String brand,String spamLabel,Date from,Date to) {

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
					append("SpamLabel",spamLabel).
					append("TimeStamp", new BasicDBObject("$gte",from).append("$lte",to));

			double fromDate = DateConverter.getJulianDate(from);
			double toDate = DateConverter.getJulianDate(to);

			queryOther = new BasicDBObject("Product",brand).
					append("SpamLabel",spamLabel).
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
	 * For a given channel returns the count of spam for all products
	 * @param channel String containing the channel name
	 * @param spamLabel String containing the spam label
	 * @param from Date containing the starting date
	 * @param to Date containing the ending date
	 * @return Integer containing the count of spam for all products of a given channel
	 */ 
	
	public static int getSpamCountChannel(String channel,String spamLabel,Date from,Date to) {

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
							append("SpamLabel",spamLabel).
							append("TimeStamp", new BasicDBObject("$gte",from).append("$lte",to));
				} else { 

					double fromDate = DateConverter.getJulianDate(from);
					double toDate = DateConverter.getJulianDate(to);

					query = new BasicDBObject("Channel",channel).append("Product",brand).
							append("SpamLabel",spamLabel).
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
}
