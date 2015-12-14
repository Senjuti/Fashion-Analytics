package org.kutty.db;

import java.net.UnknownHostException;

import com.mongodb.MongoClient;

/** 
 * Defines a factory object for MongoClient connections
 * @author Senjuti Kundu
 */ 

public class MongoClientFactory {
	
	private MongoClient mongoclient;
		
	private static MongoClientFactory singleton = new MongoClientFactory();
	
	/** 
	 * private constructor to create an instance of mongoClient
	 */ 
	
	private MongoClientFactory() { 
		
		try {
			this.mongoclient = new MongoClient("localhost", 27017);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}
	
	/** 
	 * Returns the instance of MongoClient associated with the singleton
	 * @return MongoClient instance
	 */ 
	
	public static MongoClient getMongoClient() { 
		
		return singleton.mongoclient;
	} 
	
	/** 
	 * closes connection of the given client 
	 */ 
	
	public static void closeConnection() { 
		
		singleton.mongoclient.close();
	}
}
