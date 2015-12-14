package org.kutty.sentiment;

import java.net.UnknownHostException;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.joda.time.DateTime;
import org.kutty.classification.EnsembleMachineSentiment;
import org.kutty.classification.EnsembleMachineSpam;
import org.kutty.constants.Constants;
import org.kutty.db.MongoBase;
import org.kutty.dbo.Sentiment;
import org.kutty.utils.DateConverter;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

/** 
 * Detects the sentiment of a given post (i.e. Positive, Negative, Neutral)
 * @author Senjuti Kundu
 *
 */

public class SentimentDetector {

	public static String [] channelNames = {"Twitter","Facebook"};
	public static String [] brandNames = {"Forever21","FreePeople","Guess","HandM","Levis","Mango",
		"RagandBone","SevenForAllMankind","TrueReligion"};


	/** 
	 * Defines the pipeline for sentiment detection for all posts of a given channel and product
	 * @param brandName String containing the brand name
	 * @param channel String containing the channel name
	 * @param from Date containing the starting date
	 * @param to Date containing the ending date
	 */ 

	public void sentimentPipeline(String brandName,String channel,Date from, Date to) { 

		Set<DBObject> dataSet = getSentimentSet(brandName, channel, from, to);
		Sentiment sentiment;
		String text;
		String label = null; 
		String spamlabel;
		MongoBase mongo; 
		EnsembleMachineSentiment ems = new EnsembleMachineSentiment(channel,Constants.MAX_MODEL_NUM); 
		EnsembleMachineSpam spamDetect = new EnsembleMachineSpam(channel, Constants.MAX_MODEL_NUM); 

		for (DBObject temp : dataSet) { 

			sentiment = new Sentiment();

			text = (String) temp.get("Message"); 
			
			if (text != null) { 
				
				spamlabel = spamDetect.organizeAndActEnsembleWithPriors(text);

				if (spamlabel.equalsIgnoreCase(Constants.HAM_LABEL)) { 

					sentiment.setAuthor((String) temp.get("UserName"));
					sentiment.setTimestamp((Date) temp.get("TimeStamp")); 
					sentiment.setChannel(channel);
					sentiment.setProduct(brandName);

					label = ems.organizeAndActEnsembleWithPriors(text);

					sentiment.setSentimentLabel(label);
					sentiment.setContent(text);
					sentiment.setUpdateModels(ems.CORRECT_UPDATES);
					System.out.println(label);
					try {

						mongo = new MongoBase();
						mongo.setDB("Analytics");
						mongo.setCollection(brandName);
						mongo.putInDB(sentiment); 

					} catch(Exception e) { 

						e.printStackTrace();
					}
				}
			}
		}
	}

	/** 
	 * Carries out sentiment analysis for all channels of a given brand
	 * @param brandName String containing the brandName
	 * @param from Date containing the starting date
	 * @param to Date containing the ending date
	 */ 

	public void sentimentForAllChannels(String brandName,Date from,Date to) { 

		for (String channelName : channelNames) { 

			sentimentPipeline(brandName, channelName, from, to);
		}
	}

	/** 
	 * Carries out sentiment analysis for all products of a given channel
	 * @param channel String containing the channel name
	 * @param from Date containing the starting date
	 * @param to Date containing the ending date
	 */ 

	public void sentimentForAllProducts(String channel,Date from,Date to) { 

		for (String brandName : brandNames) { 

			sentimentPipeline(brandName, channel, from, to);
		}
	}

	/** 
	 * Carries out sentiment analysis for all products and channels
	 * @param from Date containing the starting date
	 * @param to Date containing the ending date
	 */ 

	public void sentimentForAll(Date from,Date to) { 

		for (String brandName : brandNames) { 

			for (String channel : channelNames) { 

				sentimentPipeline(brandName, channel, from, to);
			}
		}
	}

	/** 
	 * Returns the sentiment objects from the database
	 * @param collectionName String containing the collection name
	 * @param channel String containing the channel name
	 * @param from Date containing the starting date
	 * @param to Date containing the ending date
	 * @return Set<DBObject> containing the set of database objects
	 */ 

	public Set<DBObject> getSentimentSet(String collectionName,String channel,Date from,Date to) {

		Set<DBObject> dataSet = new HashSet<DBObject>();

		try {

			MongoBase mongo = new MongoBase();
			mongo.setCollection(collectionName);
			double fromDate;
			double toDate;
			DBObject query;
			DBCollection collection;
			DBCursor cursor;
			BasicDBList queryList; 

			if (channel.equalsIgnoreCase("Instagram")) { 

				queryList = new BasicDBList();
				queryList.add(new BasicDBObject("Type", "tag"));
				queryList.add(new BasicDBObject("Type", "image"));
				queryList.add(new BasicDBObject("Type", "video"));

				fromDate = DateConverter.getJulianDate(from);
				toDate = DateConverter.getJulianDate(to); 

				query = new BasicDBObject("Channel","Instagram").append("$or", queryList).
						append("Timestamp", new BasicDBObject("$gte",fromDate).append("$lte", toDate));
			} else { 

				query = new BasicDBObject("Channel",channel).
						append("TimeStamp", new BasicDBObject("$gte",from).append("$lte",to));
			}

			collection = mongo.getCollection();
			cursor = collection.find(query);

			while(cursor.hasNext()) { 

				dataSet.add(cursor.next());
			}

		} catch (UnknownHostException e) { 

			e.printStackTrace();
		}

		return dataSet;
	}


	public static void main(String args[]) { 

		SentimentDetector sd = new SentimentDetector();
		DateTime to = new DateTime();
		DateTime from = to.minusDays(10);
		sd.sentimentForAll(from.toDate(), to.toDate());
	}
}
