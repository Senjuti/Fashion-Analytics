package org.kutty.db;

import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.kutty.dbo.Satisfaction;
import org.kutty.dbo.Sentiment;
import org.kutty.dbo.Spam;
import org.kutty.dbo.Update;

import twitter4j.GeoLocation;
import twitter4j.QueryResult;
import twitter4j.Status;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.WriteConcern;
import com.mongodb.WriteResult;

import facebook4j.Post;
import facebook4j.ResponseList;

/**
 * The basic database Interaction Module which contains a host of adaptor functions to convert a variety of query objects into
 * MongoDB objects the prefix Adaptor implies it is a adaptor function for a specific social channel. Apart from basic function of CRUD
 * (Create, Read, Update and Delete). It also checks for duplicates in the database itself
 * 
 *  @author Senjuti Kundu 
 */ 

public class MongoBase {

	private MongoClient mongoclient;
	private DB db;
	private DBCollection collection;

	/** 
	 * Public constructor for connecting to the MongoDB server 
	 * default hostname is localhost and port number is 27017
	 * @throws UnknownHostException
	 */ 

	public MongoBase() throws  UnknownHostException { 

		mongoclient = MongoClientFactory.getMongoClient();
		db = mongoclient.getDB("Central");
		mongoclient.setWriteConcern(WriteConcern.ACKNOWLEDGED);
	}

	/** 
	 * Public constructor for connecting with the MongoDB server using user specified port no and host name
	 * @param host_name String representing the host name a MongoDBUri object can also be used in its place
	 * @param port_no Integer corresponding to the port number of the MongoDB
	 * @throws UnknownHostException
	 */ 

	public MongoBase(String host_name,int port_no) throws UnknownHostException { 

		mongoclient = new MongoClient(host_name,port_no);
		db = mongoclient.getDB("Central");
		mongoclient.setWriteConcern(WriteConcern.ACKNOWLEDGED);
	}

	/** 
	 * Returns the set of collections in the database
	 * @return Set<String> containing the set of collection names
	 */ 

	public Set<String> getCollections() { 

		return db.getCollectionNames();
	} 

	/** 
	 * Sets the database name for the given mongoclient
	 * @param db_name String containing the database name
	 */ 

	public void setDB(String db_name) { 

		db = mongoclient.getDB(db_name);
	} 

	/** 
	 * Returns the database instance of the present MongoDB object
	 * @return DB instance of the MongoDB object
	 */ 

	public DB getDB() {

		return db;
	}

	/** 
	 * Sets the collection name of the present MongoDB instance
	 * @param collection_name String containing the collection name which roughly mirrors the product name
	 */ 

	public void setCollection(String collection_name) {

		collection = db.getCollection(collection_name);
	}

	/** 
	 * Returns the collection associated with present instance of the MongoDB object
	 * @return the collection object representing the present collection
	 */ 

	public DBCollection getCollection() {

		return collection;
	}

	/** 
	 * Inserts a document in the DB
	 * @param doc Document to be inserted
	 */ 

	public void insertDocument(BasicDBObject doc) {

		collection.insert(doc);
	}

	/** 
	 * Removes all the documents in the database collection which matches a given query
	 * @param remove_query BasicDBObject containing the query 
	 */ 

	public void removeDocument(BasicDBObject remove_query) { 

		collection.remove(remove_query);
	} 

	/** 
	 * Removes the documents from the database matching a given query and returns the number of documents returned
	 * @param remove_query DBObject containing the document to be removed
	 * @return Integer containing the number of documents to be removed
	 */ 

	public int removeDocument(DBObject remove_query) { 

		WriteResult result = collection.remove(remove_query); 

		return result.getN();
	}

	/** 
	 * Updates a given document matching a specific query
	 * @param query DBObject corresponding to a particular query
	 * @param update DBObject containing the update parameters
	 */ 

	public void updateDocument(DBObject query,DBObject update) { 

		collection.update(query, update, true, true);
	} 

	/** 
	 * Updates a given document matching a specific query
	 * @param query DBObject corresponding to a particular query
	 * @param update DBObject containing the update parameters
	 * @return Integer containing the number of records updated
	 */ 

	public int updateDocument(BasicDBObject query,BasicDBObject update) { 

		WriteResult write = collection.update(query, update, true, true);

		return write.getN();
	}

	/** 
	 * Checks whether a given object is already present in the database or not
	 * @param doc Document representing the basic database object
	 * @param channel_name String which contains the product name corresponding to the collection name
	 * @return true if the object already exists in the database false otherwise
	 */ 

	public boolean checkExists(BasicDBObject doc,String channel_name) {

		DBCursor cursor;
		BasicDBObject query = new BasicDBObject();

		query.append("Channel",channel_name).append("_id", doc.get("_id"));

		cursor = collection.find(query);

		if(cursor.hasNext()) {

			return true; 

		} else { 

			return false;
		}
	}

	/** 
	 * Prints all the entries present in a given channel to the console
	 * @param channel_name String representing the channel name for which entries are printed on the screen
	 */ 

	public void printChannelEntries(String channel_name) {

		DBCursor cursor = collection.find(new BasicDBObject("Channel",channel_name));

		while(cursor.hasNext()) { 

			System.out.println(cursor.next());
		}

		System.out.println("Number of Entries : " + cursor.size()); 

		cursor.close();
	}

	/** 
	 * Executes a query and returns the cursor corresponding to the query
	 * @param query BasicDBObject representing the database query
	 * @return DBCursor which corresponds to the results of the query fired
	 */ 

	public DBCursor getQuery(BasicDBObject query) { 

		DBCursor cursor = collection.find(query); 

		return cursor; 
	}

	/** 
	 * Adaptor function to convert the Facebook post object into a BasicDBObject 
	 * @param post Object corresponding to the facebook post
	 * @return DBObject representing the facebook post which is serializable
	 */ 

	public DBObject getFbPostAdaptor(Post post) { 

		BasicDBObject post_doc = new BasicDBObject();

		try { 

			post_doc.append("Channel", "Facebook").
			append("_id",post.getId()).
			append("Message", post.getMessage()).
			append("TimeStamp",post.getCreatedTime()).
			append("UserName", post.getFrom().getName()).
			append("Likes", post.getLikes().getCount()).
			append("Comments",post.getComments().getCount()).
			append("Shares", post.getSharesCount());			

		} catch (Exception e) { 

			e.printStackTrace();
		}

		return post_doc;
	} 

	/**
	 * Adaptor function to convert a Twitter Response object into a MongoDB object
	 * @param tweet Object representing a tweet which is to be converted into a DBObject
	 * @return DBObject corresponding to a tweet object
	 */ 

	public DBObject getTweetAdaptor(Status tweet)
	{
		BasicDBObject tweet_doc = new BasicDBObject();
		String location = "";
		GeoLocation Geo; 

		try {  

			if( (Geo = tweet.getGeoLocation()) != null) { 

				location = Geo.toString();
			} 

			tweet_doc.append("Channel","Twitter").
			append("UserName",tweet.getUser().getName()).
			append("UserScreenName",tweet.getUser().getScreenName()).
			append("UserID",tweet.getUser().getId()).
			append("UserLocation", tweet.getUser().getLocation()).
			append("Message",tweet.getText()).
			append("TimeStamp", tweet.getCreatedAt()).
			append("TweetLocation", location).
			append("_id", tweet.getCreatedAt()).
			append("RetweetCount",tweet.getRetweetCount()).
			append("DeviceUsed", tweet.getSource()); 

		} catch (Exception e) { 

			e.printStackTrace();
		}

		return tweet_doc;
	}

	/**
	 * Inserts a set of tweets into the database 
	 * @param result QueryResult object which contains the set of tweets obtained from a given query
	 * @param product_name String representing the product name whose tweet list is to be put in the collection
	 * @throws UnknownHostException
	 */

	synchronized public void putInDB(QueryResult result,String product_name) throws UnknownHostException
	{
		List<Status> tweets = result.getTweets();
		BasicDBObject tweet_doc; 
		setCollection(product_name);

		for(Status tweet: tweets) { 

			tweet_doc = (BasicDBObject) getTweetAdaptor(tweet);

			if (!checkExists(tweet_doc, "Twitter")) { 
				insertDocument(tweet_doc);
			}
		}
	} 

	/** 
	 * Inserts a list of facebook responses into the database
	 * @param response_list List of facebook posts to be inserted
	 * @param product_name String representing the product name which roughly corresponds to the collection name
	 * @throws UnknownHostException
	 */ 

	synchronized public void putInDB(ResponseList<Post> response_list,String product_name) throws UnknownHostException { 

		setCollection(product_name);

		for (Post post: response_list) { 

			BasicDBObject post_doc = (BasicDBObject)getFbPostAdaptor(post);

			if(!checkExists(post_doc, "Facebook")) { 

				insertDocument(post_doc);
			}
		}
	} 


	/** 
	 * Checks whether a given location object already exists in the database or not
	 * @param country CountryBase object which is to be inserted
	 * @return true if the object already exists false otherwise
	 */ 

	public boolean checkExists(DBObject country) { 

		BasicDBObject query;
		DBCursor cursor;

		query = new BasicDBObject("Username",country.get("Username")).
				append("TimeStamp", country.get("TimeStamp")).
				append("Country", country.get("Country")).
				append("Channel", country.get("Channel"));

		cursor = collection.find(query);

		if (cursor.hasNext()) { 

			return true;
		}

		return false;
	}

	/** 
	 * Given a sentiment object converts it into a BasicDBObject
	 * @param sentiment Sentiment object
	 * @return BasicDBObject containing the representation of the sentiment object
	 */ 

	public BasicDBObject getSentimentAdaptor(Sentiment sentiment) { 

		BasicDBObject sentimentDoc = new BasicDBObject("Channel",sentiment.getChannel()).
				append("Author",sentiment.getAuthor()).
				append("Message",sentiment.getContent()).
				append("TimeStamp", sentiment.getTimestamp()).
				append("Product",sentiment.getProduct()).
				append("SentimentLabel",sentiment.getSentimentLabel()).
				append("SentimentScore",sentiment.getSentimentScore()).
				append("OtherDate",sentiment.getOtherDate()).
				append("UpdateList",getUpdateAdaptorList(sentiment.getUpdateModels()));

		return sentimentDoc;
	}

	/** 
	 * Checks whether a given sentiment object exists in the database or not
	 * @param sentiment Sentiment object which is to be checked
	 * @return true if the object exists, false otherwise
	 */ 

	public boolean checkExists(Sentiment sentiment) { 

		DBObject query; 

		if (sentiment.getChannel().equalsIgnoreCase("Instagram")) { 

			query = new BasicDBObject("Channel",sentiment.getChannel()).
					append("Author",sentiment.getAuthor()).
					append("TimeStamp",sentiment.getTimestamp());
		} else { 

			query = new BasicDBObject("Channel",sentiment.getChannel()).
					append("Author",sentiment.getAuthor()).
					append("OtherDate",sentiment.getOtherDate());
		}

		if (collection.find(query).hasNext()) { 

			return true;
		}

		return false;
	}

	/** 
	 * Inserts a given spam object in the database
	 * @param sentiment Sentiment object which is to be inserted in the database
	 */ 

	public void putInDB(Sentiment sentiment) { 

		if (!checkExists(sentiment)) { 

			insertDocument(getSentimentAdaptor(sentiment));
		}
	}

	/** 
	 * Adaptor function for converting a spam object into a BasicDBObject
	 * @param spam Spam object containing the BasicDBObject
	 * @return BasicDBObject containing the representation of the Spam object 
	 */ 

	public BasicDBObject getSpamAdaptor(Spam spam) { 

		BasicDBObject spamDoc = new BasicDBObject("Channel",spam.getChannel()).
				append("Author",spam.getAuthor()).
				append("Message",spam.getContent()).
				append("TimeStamp", spam.getTimestamp()).
				append("Product",spam.getProduct()).
				append("SpamLabel",spam.getSpamLabel()).
				append("SpamScore",spam.getSpamScore()).
				append("OtherDate",spam.getOtherDate()).
				append("UpdateList", getUpdateAdaptorList(spam.getUpdateSet()));

		return spamDoc;
	}

	/** 
	 * Checks if a given spam object exists in the database or not
	 * @param spam Spam object which is to be inserted
	 * @return true if the object exists false otherwise
	 */ 

	public boolean checkExists(Spam spam) { 

		DBObject query; 

		if (spam.getChannel().equalsIgnoreCase("Instagram")) { 

			query = new BasicDBObject("Channel",spam.getChannel()).
					append("Author",spam.getAuthor()).
					append("TimeStamp",spam.getTimestamp()).
					append("SpamLabel",spam.getSpamLabel());
		} else { 

			query = new BasicDBObject("Channel",spam.getChannel()).
					append("Author",spam.getAuthor()).
					append("OtherDate",spam.getOtherDate()).
					append("SpamLabel",spam.getSpamLabel());
		}

		if (collection.find(query).hasNext()) { 

			return true;
		}

		return false;
	}

	/** 
	 * Inserts a given spam object in the database if it doesn't already exist
	 * @param spam Spam object which is to be inserted
	 */

	public void putInDB(Spam spam) { 

		if (!checkExists(spam)) { 

			insertDocument(getSpamAdaptor(spam));
		}
	}

	/** 
	 * Adaptor function to convert a satisfaction object into a BasicDBObject
	 * @param satisfaction Satisfaction object which is to be converted
	 * @return BasicDBObject containing the representation of the Satisfaction object
	 */

	public BasicDBObject getSatisfactionAdaptor(Satisfaction satisfaction) { 

		BasicDBObject satDoc = new BasicDBObject("Channel",satisfaction.getChannelName()).
				append("Product",satisfaction.getBrandName()).
				append("Message", satisfaction.getContent()).
				append("FrequencyFactor", satisfaction.getFrequencyFactor()).
				append("FrequencyWeight", satisfaction.getFrequencyWeight()).
				append("ImportanceFactor", satisfaction.getImportanceFactor()).
				append("ReliabilityFactor", satisfaction.getReliabilityFactor()).
				append("ReliabilityWeight", satisfaction.getReliabilityWeight()).
				append("SatisfactionScore", satisfaction.getSatisfactionScore()).
				append("SentimentScore", satisfaction.getSentimentScore()).
				append("TimeStamp", satisfaction.getTimestamp()).
				append("OtherDate", satisfaction.getOtherDate());

		return satDoc;
	}

	/** 
	 * Checks whether a given satisfaction object exists in the database or not
	 * @param satisfaction Satisfaction object which is to be checked
	 * @return true if the object exists false otherwise
	 */

	public boolean checkExists(Satisfaction satisfaction) { 

		DBObject query; 
		DBCursor cursor; 

		if (!satisfaction.getChannelName().equalsIgnoreCase("Instagram")) { 

			query = new BasicDBObject("Channel",satisfaction.getChannelName()).
					append("Product", satisfaction.getBrandName()).
					append("TimeStamp", satisfaction.getTimestamp());

		} else { 

			query = new BasicDBObject("Channel",satisfaction.getChannelName()).
					append("Product", satisfaction.getBrandName()).
					append("OtherDate", satisfaction.getOtherDate());
		}

		cursor = collection.find(query);

		if (cursor.hasNext()) { 

			return true;
		}

		return false;
	}

	/** 
	 * Inserts a satisfaction object in the database if it already doesnot exist
	 * @param satisfaction Satisfaction object
	 */

	public void putInDB(Satisfaction satisfaction) { 

		if (!checkExists(satisfaction)) { 

			insertDocument(getSatisfactionAdaptor(satisfaction));
		}
	}

	/** 
	 * Given an update object converts it into an Update object for easy insertion
	 * @param update Update object which is to be inserted
	 * @return BasicDBObject containing the representation of the  update object
	 */

	public BasicDBObject getUpdateAdaptor(Update update) { 

		BasicDBObject updateDoc;
		updateDoc = new BasicDBObject("Model",update.getModelNum()).
				append("Ngram", update.getNgramNum()).
				append("Probability", update.getProbPercent()).
				append("Label",update.getClassLabel());

		return updateDoc;
	}

	/** 
	 * Converts a list of updates into a List of BasicDBObjects
	 * @param updates List<Update> containing the update object
	 * @return List<BasicDBObject> containing the update objects
	 */
	public Set<BasicDBObject> getUpdateAdaptorList(Set<Update> updates) { 

		Set<BasicDBObject> updateList = new HashSet<BasicDBObject>();

		for(Update update : updates) { 

			updateList.add(getUpdateAdaptor(update));
		}

		return updateList;
	}
}

