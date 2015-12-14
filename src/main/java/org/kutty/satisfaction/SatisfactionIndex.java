package org.kutty.satisfaction;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.joda.time.DateTime;
import org.kutty.constants.Constants;
import org.kutty.db.MongoBase;
import org.kutty.dbo.Satisfaction;
import org.kutty.utils.DateConverter;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

/** 
 * Calculates the Satisfaction Index of a given product and stores it in the database
 * @author Senjuti Kundu 
 * 
 */ 

public class SatisfactionIndex {

	private static double frequencyFactor;
	private static double frequencyWeight;
	private static double reliabilityFactor;
	private static double reliabilityWeight; 

	/** 
	 * Returns the label count for a given product and channel in a given interval
	 * @param channel String containing the channel name
	 * @param brand String containing the brand name
	 * @param type String containing the type (i.e. "sentiment" or "spam")
	 * @param label String containing the label
	 * @param from Date containing the starting date
	 * @param to Date containing the ending date
	 * @return Integer containing the count
	 */ 

	public static int getLabelCount(String channel,String brand,String type,String label,Date from,Date to) { 

		int count = 0;
		DBObject query = null;
		MongoBase mongo;
		DBCollection collection;
		double fromDate;
		double toDate; 

		try { 

			if (type.equalsIgnoreCase(Constants.SENTIMENT_TYPE)) { 

				if (!channel.equalsIgnoreCase("Instagram")) {

					query = new BasicDBObject("Channel",channel).append("Product",brand).
							append("SentimentLabel",label).
							append("TimeStamp", new BasicDBObject("$gte",from).append("$lte",to));

				} else { 

					fromDate = DateConverter.getJulianDate(from);
					toDate = DateConverter.getJulianDate(to); 

					query = new BasicDBObject("Channel",channel).append("Product",brand).
							append("SentimentLabel",label).
							append("OtherDate", new BasicDBObject("$gte",fromDate).append("$lte",toDate));
				}
			}  

			if (type.equalsIgnoreCase(Constants.SPAM_TYPE)) { 

				if (!channel.equalsIgnoreCase("Instagram")) {

					query = new BasicDBObject("Channel",channel).append("Product",brand).
							append("SpamLabel",label).
							append("TimeStamp", new BasicDBObject("$gte",from).append("$lte",to));

				} else { 

					fromDate = DateConverter.getJulianDate(from);
					toDate = DateConverter.getJulianDate(to); 

					query = new BasicDBObject("Channel",channel).append("Product",brand).
							append("SpamLabel",label).
							append("OtherDate", new BasicDBObject("$gte",fromDate).append("$lte",toDate));
				}

			}

			mongo = new MongoBase(); 
			mongo.setDB(Constants.ANALYTICS_DB);
			mongo.setCollection(brand);
			collection = mongo.getCollection();
			count = collection.find(query).size(); 

		} catch (Exception e) {  

			e.printStackTrace();
		} 

		return count; 
	}

	/** 
	 * Returns the count of post for a given channel and a given brand
	 * @param channel String containing the channel name
	 * @param brand String containing the brand name
	 * @param from Date containing the starting date
	 * @param to Date containing the ending date
	 * @return Integer containing the count of posts
	 */ 

	public static int getPostCount(String channel,String brand,Date from, Date to) { 

		int count = 0;

		DBObject query = null;
		MongoBase mongo;
		DBCollection collection;
		double fromDate;
		double toDate; 

		try { 

			if (!channel.equalsIgnoreCase("Instagram")) {

				query = new BasicDBObject("Channel",channel).append("Product",brand).
						append("TimeStamp", new BasicDBObject("$gte",from).append("$lte",to));

			} else { 

				fromDate = DateConverter.getJulianDate(from);
				toDate = DateConverter.getJulianDate(to); 

				query = new BasicDBObject("Channel",channel).append("Product",brand).
						append("OtherDate", new BasicDBObject("$gte",fromDate).append("$lte",toDate));
			}

			mongo = new MongoBase(); 
			mongo.setDB(Constants.ANALYTICS_DB);
			mongo.setCollection(brand);
			collection = mongo.getCollection();
			count = collection.find(query).size(); 

		} catch (Exception e) {  

			e.printStackTrace();
		}

		return count;
	}

	/** 
	 * Calculates the reliability factor for a given channel and a given brand
	 * @param channel String containing the channel name
	 * @param brand String containing the brand name
	 * @param from Date containing the starting date
	 * @param to Date containing the ending date
	 * @return Double containing the value of the factor
	 */ 

	public static double getReliabilityFactor(String channel,String brand,Date from,Date to) { 

		double reliabitityFactor = 0.0; 
		double spread;
		double ham;
		double negative;
		double positive;
		double neutral;

		ham = getLabelCount(channel, brand,Constants.SPAM_TYPE,Constants.HAM_LABEL, from, to);
		negative = getLabelCount(channel, brand, Constants.SENTIMENT_TYPE,Constants.NEGATIVE_LABEL, from, to);
		positive = getLabelCount(channel, brand, Constants.SENTIMENT_TYPE, Constants.POSITIVE_LABEL, from, to);
		neutral = getLabelCount(channel, brand, Constants.SENTIMENT_TYPE,Constants.NEUTRAL_LABEL, from, to);

		spread = Math.abs(positive-negative) + Math.abs(positive-neutral) + Math.abs(neutral-negative) + 1;

		reliabitityFactor = ham/spread;

		return reliabitityFactor;
	}

	/** 
	 * Returns the frequency factor of a given channel and brand
	 * @param channel String containing the channel name
	 * @param brand String containing the brand name
	 * @param meanRetro Integer containing the number of retro days for which mean has to be calculated
	 * @param postRetro Integer containing the number of retro post count
	 * @return Double containing the value of the frequency factor
	 */ 

	public static double getFrequencyFactor(String channel,String brand, int meanRetro,int postRetro) { 

		double frequencyFactor = 0.0;
		double postRate;
		double meanRate;

		DateTime to = new DateTime();
		DateTime from = to.minusDays(postRetro);

		postRate = getPostCount(channel, brand, from.toDate(), to.toDate())/postRetro;
		from = to.minusDays(meanRetro);
		meanRate = getPostCount(channel, brand, from.toDate(), to.toDate())/meanRetro;

		frequencyFactor = (postRate/(meanRate+1));

		return frequencyFactor;
	} 

	/** 
	 * Gets the cumulative value for a given channel and brand for a field name
	 * @param channel String containing the channel name
	 * @param brand String containing the brand name
	 * @param fieldName String containing the field name whose cumulative value is to be calculated
	 * @param from Date containing the starting date
	 * @param to Date containing the ending date
	 * @return Double value containing the cumulative score
	 */ 

	public static double getCumulativeValue(String channel,String brand,String fieldName,Date from,Date to) { 

		MongoBase mongo;
		DBObject query;
		DBObject temp;
		DBCollection collection;
		DBCursor cursor;
		double sum = 0.0;

		try { 

			mongo = new MongoBase();
			mongo.setDB(Constants.ANALYTICS_DB);
			mongo.setCollection(Constants.SATISFACTION_COLLECTION); 

			if (!channel.equalsIgnoreCase("Instagram")) { 

				query = new BasicDBObject("Channel",channel).append("Product",brand).
						append("TimeStamp", new BasicDBObject("$gte",from).append("$lte",to)); 
			} else {  

				double dateFrom = DateConverter.getJulianDate(from);
				double dateTo = DateConverter.getJulianDate(to);

				query = new BasicDBObject("Channel",channel).append("Product",brand).
						append("OtherDate", new BasicDBObject("$gte",dateFrom).append("$lte",dateTo));
			}

			collection = mongo.getCollection(); 
			cursor = collection.find(query);

			while (cursor.hasNext()) { 

				temp = cursor.next();
				sum = sum + (double)temp.get(fieldName);
			}

		} catch (Exception e) { 

			e.printStackTrace();
		}

		return sum;
	}

	/** 
	 * Returns the ratio of two cumulative values
	 * @param channel String containing the channel name
	 * @param brand String containing the brand name
	 * @param fieldName String containing the field name
	 * @param fieldValue Object containing the field value
	 * @param from Date containing the starting date
	 * @param to Date containing the ending date
	 * @return Double value containing the ratio
	 */ 

	public static double getCumulativeRatio(String channel,String brand,String fieldName,Object fieldValue,Date from,Date to) { 

		MongoBase mongo;
		DBObject queryLessThanMean;
		DBObject queryGreaterThanMean;
		DBCollection collection;
		int	countLessThanMean = 0;
		int countGreaterThanMean = 0; 

		try { 

			mongo = new MongoBase();
			mongo.setDB(Constants.ANALYTICS_DB);
			mongo.setCollection(Constants.SATISFACTION_COLLECTION); 

			if (!channel.equalsIgnoreCase("Instagram")) { 

				queryLessThanMean = new BasicDBObject("Channel",channel).append("Product",brand).
						append("TimeStamp", new BasicDBObject("$gte",from).append("$lte",to)).
						append(fieldName,new BasicDBObject("$gt",fieldValue));

				queryGreaterThanMean = new BasicDBObject("Channel",channel).append("Product",brand).
						append("TimeStamp", new BasicDBObject("$gte",from).append("$lte",to)).
						append(fieldName,new BasicDBObject("$lt",fieldValue));
			} else { 

				double fromDate = DateConverter.getJulianDate(from);
				double toDate = DateConverter.getJulianDate(to); 

				queryLessThanMean = new BasicDBObject("Channel",channel).append("Product",brand).
						append("OtherDate", new BasicDBObject("$gte",fromDate).append("$lte",toDate)).
						append(fieldName,new BasicDBObject("$gt",fieldValue));

				queryGreaterThanMean = new BasicDBObject("Channel",channel).append("Product",brand).
						append("OtherDate", new BasicDBObject("$gte",fromDate).append("$lte",toDate)).
						append(fieldName,new BasicDBObject("$lt",fieldValue));
			}

			collection = mongo.getCollection();
			countLessThanMean = collection.find(queryLessThanMean).size();
			countGreaterThanMean = collection.find(queryGreaterThanMean).size();	

		} catch(Exception e) { 

			e.printStackTrace();
		}

		return (countLessThanMean/(countGreaterThanMean+1));
	} 

	/** 
	 * Returns the reliability weight for a given channel and brand during a specified period
	 * @param channel String containing the channel name
	 * @param brand String containing the brand name
	 * @param from Date containing the starting date
	 * @param to Date containing the ending date
	 * @return Double containing the reliability weight
	 */ 

	public static double getReliabilityWeight(String channel,String brand,Date from,Date to) { 

		double meanReliablity = 0;
		double reliabilityRatio = 0;
		meanReliablity = getCumulativeValue(channel, brand,"ReliabilityFactor",from,to);
		meanReliablity = meanReliablity/Constants.MEAN_DAYS;
		reliabilityRatio = getCumulativeRatio(channel, brand,"ReliabilityFactor",meanReliablity,from, to);
		if (reliabilityRatio == 0) {
			reliabilityRatio = 1.0;
		}
		return reliabilityRatio;
	}

	/** 
	 * Returns the frequency weight for a given channel and given brand in a given interval
	 * @param channel String containing the channel name
	 * @param brand String containing the brand name
	 * @param from Date containing the starting date
	 * @param to Date containing the ending date
	 * @return Double containing the frequency weight
	 */ 

	public static double getFrequencyWeight(String channel,String brand,Date from,Date to) { 

		double meanFrequency = 0;
		double frequencyRatio = 0;
		meanFrequency = getCumulativeValue(channel, brand,"FrequencyFactor",from,to);
		meanFrequency = meanFrequency/Constants.MEAN_DAYS;
		frequencyRatio = getCumulativeRatio(channel, brand,"FrequencyFactor",meanFrequency,from, to);
		if (frequencyRatio == 0) { 
			frequencyRatio = 1;
		}
		return frequencyRatio;
	}

	/** 
	 * Given a channel and a brand returns the satisfaction index associated with it 
	 * @param channel String containing the channel name
	 * @param brand String containing the brand name
	 * @return Double containing the satisfaction index value
	 */ 

	public static double getSatisfactionIndex(String channel,String brand) { 

		DateTime to = new DateTime();
		DateTime from = to.minusDays(Constants.DAYS);
		reliabilityFactor = getReliabilityFactor(channel, brand, from.toDate(), to.toDate());
		frequencyFactor = getFrequencyFactor(channel, brand, Constants.MEAN_DAYS,Constants.DAYS);
		from = to.minusDays(Constants.MEAN_DAYS);  
		reliabilityWeight = getReliabilityWeight(channel, brand, from.toDate(), to.toDate());
		frequencyWeight = getFrequencyWeight(channel, brand, from.toDate(), to.toDate()); 

		return (reliabilityWeight*reliabilityFactor + frequencyWeight*frequencyFactor);
	}

	/** 
	 * Returns the set of DBObjects containing the sentiment data
	 * @param channel String containing the channel name
	 * @param brand String containing the brand name
	 * @return Set<DBObject> containing the sentiment objects
	 */

	public static Set<DBObject> getSentimentData(String channel,String brand,Date from,Date to) { 

		MongoBase mongo;
		DBCollection collection;
		DBObject temp;
		DBObject query;
		DBCursor cursor;
		BasicDBList queryList;
		Set<DBObject> sentiSet = new HashSet<DBObject>();
		try {
			mongo = new MongoBase();
			mongo.setDB(Constants.ANALYTICS_DB);
			mongo.setCollection(brand);
			queryList = new BasicDBList();
			queryList.add(new BasicDBObject("SentimentLabel",Constants.POSITIVE_LABEL));
			queryList.add(new BasicDBObject("SentimentLabel",Constants.NEGATIVE_LABEL));
			queryList.add(new BasicDBObject("SentimentLabel",Constants.NEUTRAL_LABEL)); 

			query = new BasicDBObject("Channel",channel).append("Product",brand).append("$or",queryList).
					append("TimeStamp", new BasicDBObject("$gte",from).append("$lte", to));

			collection = mongo.getCollection();
			cursor = collection.find(query);
			while(cursor.hasNext()) { 
				temp = cursor.next();
				sentiSet.add(temp);
			}
		} catch(Exception e) { 
			e.printStackTrace();
		}

		return sentiSet;
	}

	/** 
	 * Defines the satisfaction pipeline for a given channel and a brand
	 * @param channel String containing the channel name
	 * @param brand String containing the brand name
	 */

	public static void satisfactionPipeline(String channel,String brand,Date from,Date to) { 

		MongoBase mongo;
		Set<DBObject> sentiData = new HashSet<DBObject>();
		Satisfaction satIndex;
		double neutral;
		String sentimentLabel = ""; 
		neutral = getLabelCount(channel, brand, Constants.SENTIMENT_TYPE,Constants.NEUTRAL_LABEL, from, to);
		double satScore; 

		try {

			mongo = new MongoBase();
			mongo.setDB(Constants.ANALYTICS_DB);
			mongo.setCollection(Constants.SATISFACTION_COLLECTION);
			sentiData = getSentimentData(channel, brand,from,to); 

			for(DBObject temp : sentiData) { 

				satIndex = new Satisfaction();
				satIndex.setBrandName(brand);
				satIndex.setChannelName(channel);
				satIndex.setContent((String) temp.get("Message"));
				satIndex.setSentimentScore(0); 
				sentimentLabel = (String) temp.get("SentimentLabel");
				satScore = getSatisfactionIndex(channel, brand)*Math.exp(0.0); 
				satScore = scale(satScore); 
				satIndex.setTimestamp((Date) temp.get("TimeStamp")); 

				if (sentimentLabel.equalsIgnoreCase(Constants.POSITIVE_LABEL)) { 

					satIndex.setSatisfactionScore(satScore);
				} 

				else if (sentimentLabel.equalsIgnoreCase(Constants.NEGATIVE_LABEL)) { 

					satIndex.setSatisfactionScore(-1.0*satScore);
				}

				else if (sentimentLabel.equalsIgnoreCase(Constants.NEUTRAL_LABEL)) {  

					satIndex.setSatisfactionScore(2.0*(satScore/(neutral+1)));
				} 

				satIndex.setFrequencyFactor(frequencyFactor);
				satIndex.setFrequencyWeight(frequencyWeight);
				satIndex.setReliabilityFactor(reliabilityFactor);
				satIndex.setReliabilityWeight(reliabilityWeight);
				System.out.println(satScore);
				mongo.putInDB(satIndex);
			} 

		} catch(Exception e) {  

			e.printStackTrace();
		}
	}

	/** 
	 * Calculates the the satisfaction for all channels given a product and time interval
	 * @param product String containing the product name
	 * @param from Date containing the starting date
	 * @param to Date containing the ending date
	 */

	public static void satisfactionAllChannels(String product,Date from,Date to) { 

		for (String channel : Constants.channelNames) { 
			satisfactionPipeline(channel, product,from,to);
		}
	}

	/** 
	 * Calculates the satisfaction of all products for a given channel during a given time period
	 * @param channel String containing the channel name
	 * @param from Date containing the starting date
	 * @param to Date containing the ending date
	 */

	public static void satisfactionAllProducts(String channel,Date from,Date to) { 

		for (String brand : Constants.brandNames) { 
			satisfactionPipeline(channel, brand, from, to);
		}
	}

	/** 
	 * Calculates and stores the satisfaction index of all products and channels in a given interval
	 * @param from Date containing the starting date
	 * @param to Date containing the ending date
	 */ 

	public static void satisfactionAllProductsAndChannels(Date from,Date to) { 

		for (String brand : Constants.brandNames) { 

			for (String channel : Constants.channelNames) { 

				satisfactionPipeline(channel, brand,from, to);
			}
		}
	}

	/** 
	 * Scales satisfaction score in a range of 0 - 100
	 * @param satisfactionScore Satisfaction Score which is to be scaled
	 * @return scaled value of Satisfaction Score
	 */ 

	public static double scale(double satisfactionScore) { 

		double oldMax = Constants.OLD_MAX;
		double oldMin = Constants.OLD_MIN;
		double newMax = Constants.NEW_MAX;
		double newMin = Constants.NEW_MIN; 

		if (satisfactionScore > oldMax) {  

			satisfactionScore = oldMax-2;
		} 

		if (satisfactionScore < oldMin) { 

			satisfactionScore = oldMin;
		}

		double scaledValue = (newMax - newMin)/(oldMax-oldMin)*(satisfactionScore - oldMin) + newMin;

		return scaledValue;
	} 

	public static void main(String args[]) { 

		DateTime to = new DateTime();
		DateTime from = to.minusMonths(3);

		satisfactionAllProductsAndChannels(from.toDate(), to.toDate());
	}
}
