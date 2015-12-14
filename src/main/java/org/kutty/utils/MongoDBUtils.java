package org.kutty.utils;

import java.net.UnknownHostException;

import org.kutty.db.MongoBase;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

/** 
 * Utility functions for MongoDB i.e. to get the max/min of a given field 
 * 
 * @author Senjuti Kundu
 *
 */
public class MongoDBUtils {
	
	/** 
	 * Returns the maximum/minimum value for a given field in a collection
	 * @param dbName String associated with the database name
	 * @param collectionName String representing the collection name
	 * @param fieldName String containing the field name
	 * @param flag String containing the flag value can be max or min
	 * @return Object containing the value of the field
	 * @throws UnknownHostException
	 */
	public static Object getMaxMinValue(String dbName,String collectionName,String fieldName,String flag) throws UnknownHostException { 
		
		MongoBase mongo;
		DBCollection collection;
		DBObject sortOrder;
		
		mongo = new MongoBase();
		mongo.setDB(dbName);
		mongo.setCollection(collectionName); 
		
		collection = mongo.getCollection(); 
		
		if (flag.equalsIgnoreCase("max")) { 
			
			sortOrder = new BasicDBObject(fieldName,-1); 
			
		} else { 
			
			sortOrder = new BasicDBObject(fieldName,1);
		}
		
		return collection.find().sort(sortOrder).limit(1).next().get(fieldName);
		
	}
}
