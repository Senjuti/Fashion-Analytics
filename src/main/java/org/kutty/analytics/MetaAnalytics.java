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
import org.joda.time.Hours;
import org.joda.time.Minutes;
import org.joda.time.Months;
import org.joda.time.Seconds;
import org.joda.time.Weeks;
import org.joda.time.Years;
import org.kutty.db.MongoBase;
import org.kutty.utils.DateConverter;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;

/** 
 * Analyzes the meta data of the database useful for generating overall statistics 
 * 
 * @author Senjuti Kundu
 * 
 */

public class MetaAnalytics {

	private static String channel_name;
	private static String product_name;
	private static Date start_date;
	private Date end_date; 
	public static Map<String,String> collection_names = new HashMap<String,String>();
	public static List<String> channel_list = new ArrayList<String>();

	/** 
	 * Static block to initialize the product list and collection name
	 */ 

	static { 

		channel_list.add("Twitter");
		channel_list.add("Facebook");
		channel_list.add("Youtube");
		channel_list.add("Reddit");
		channel_list.add("Instagram");

		try {
			init("product_list.txt",collection_names);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}  

	/** 
	 * Returns the channel name
	 * @return String containing the channel name
	 */ 

	public static String getChannelName() { 

		return channel_name;
	}

	/** 
	 * Sets the channel name for the analytics
	 * @param channel_name String containing the channel_name
	 */ 

	public static void setChannelName(String channel_name) { 

		MetaAnalytics.channel_name = channel_name;
	}

	/** 
	 * Returns the product name for a given feature
	 * @return String containing the product name
	 */ 

	public static String getProductName() { 

		return product_name;
	}

	/** 
	 * Sets the product name 
	 * @param product_name String containing the product name
	 */ 

	public static void setProductName(String product_name) { 

		MetaAnalytics.product_name = product_name;
	}

	/** 
	 * Returns the start date for a given period
	 * @return Date containing the date object
	 */ 

	public static Date getStartDate() { 

		return start_date;
	}

	/** 
	 * Sets the starting date for the analytics
	 * @param start_date Date containing the starting date
	 */ 

	public static void setStartDate(Date start_date) { 

		MetaAnalytics.start_date = start_date;
	}

	/** 
	 * Returns the ending date for the analytics
	 * @return Date object containing the ending date
	 */ 

	public Date getEndDate() { 

		return end_date;
	}

	/** 
	 * Sets the ending date 
	 * @param end_date Date containing the ending date
	 */ 

	public void setEndDate(Date end_date) { 

		this.end_date = end_date;
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
	 * Returns the number of posts received by a given product and a given channel in a defined interval
	 * @param product String containing the product name
	 * @param channel String containing the channel name
	 * @param from Date containing the starting date
	 * @param to Date containing the ending date
	 * @return Integer containing the count for the given interval
	 */ 

	public static int getProductChannelPostCount(String product,String channel,Date from,Date to) { 

		MongoBase mongo = null;
		BasicDBObject query;
		DBCollection collection;
		double from_date;
		double to_date;
		String collection_name = ""; 
		int size = 0; 

		from_date = DateConverter.getJulianDate(from);
		to_date = DateConverter.getJulianDate(to);
		collection_name = collection_names.get(product.toLowerCase().trim()); 

		if (!(channel.equalsIgnoreCase("Reddit") || channel.equalsIgnoreCase("Instagram"))) { 

			query = new BasicDBObject("Channel",channel).
					append("TimeStamp", new BasicDBObject("$gte",from).append("$lte",to));
		} else { 

			if (channel.equalsIgnoreCase("Reddit")) { 

				query = new BasicDBObject("Channel",channel).
						append("TimeStamp", new BasicDBObject("$gte",from_date).append("$lte",to_date));
			} else { 

				BasicDBList query_list = new BasicDBList();
				query_list = new BasicDBList();
				query_list.add(new BasicDBObject("Type", "tag"));
				query_list.add(new BasicDBObject("Type", "image"));
				query_list.add(new BasicDBObject("Type", "video"));

				query = new BasicDBObject("Channel",channel).append("$or",query_list).
						append("Timestamp", new BasicDBObject("$gte",from_date).append("$lte",to_date));
			}	
		}

		try { 

			mongo = new MongoBase();
			mongo.setCollection(collection_name);
			collection = mongo.getCollection(); 
			size = collection.find(query).size();

		} catch (Exception e) {  

			e.printStackTrace(); 

		} 

		return size;
	}

	/** 
	 * Returns the size of a given collection 
	 * @param product String containing the collection name 
	 * @return Integer containing the size of the collection
	 */ 

	public static int getCollectionSize(String product) { 

		int size;
		DBCollection collection;
		String collection_name = "";
		MongoBase mongo = null; 

		collection_name = collection_names.get(product.toLowerCase().trim());
		size = 0; 

		try { 

			mongo = new MongoBase();
			mongo.setCollection(collection_name);
			collection = mongo.getCollection();
			size = collection.find().size(); 

		} catch (Exception e) { 

			e.printStackTrace(); 

		} 

		return size;
	} 

	/** 
	 * Returns the size of the collection in a given interval
	 * @param product String containing the collection name
	 * @param from Date containing the staring date
	 * @param to Date containing the ending date
	 * @return Integer containing the count of the collection in the given period
	 */ 

	public static int getCollectionSize(String product,Date from,Date to) { 

		int size;
		int size_reddit;
		int size_instagram;
		DBCollection collection;
		String collection_name = "";
		MongoBase mongo = null; 
		double from_date;
		double to_date;  

		collection_name = collection_names.get(product.toLowerCase().trim());
		BasicDBList query_list = new BasicDBList();
		List<String> channel_list = new ArrayList<String>(); 
		BasicDBObject query; 
		from_date = DateConverter.getJulianDate(from);
		to_date = DateConverter.getJulianDate(to); 
		size = 0;
		size_reddit = 0;
		size_instagram = 0;

		query_list.add(new BasicDBObject("Type", "tag"));
		query_list.add(new BasicDBObject("Type", "image"));
		query_list.add(new BasicDBObject("Type", "video")); 

		channel_list.add("Twitter");
		channel_list.add("Facebook");
		channel_list.add("Youtube");

		query = new BasicDBObject("TimeStamp", new BasicDBObject("$gte",from).append("$lte",to)).
				append("Channel", new BasicDBObject("$in",channel_list));

		try {  

			mongo = new MongoBase(); 
			mongo.setCollection(collection_name);
			collection = mongo.getCollection();  

			size = collection.find(query).size(); 

			query = new BasicDBObject("Channel","Reddit").
					append("TimeStamp", new BasicDBObject("$gte",from_date).append("$lte", to_date)); 

			size_reddit = collection.find(query).size(); 

			query = new BasicDBObject("Channel","Instagram").append("$or",query_list).
					append("Timestamp", new BasicDBObject("$gte",from_date).append("$lte", to_date));

			size_instagram = collection.find(query).size();

		} catch (Exception e) {  

			e.printStackTrace(); 

		} 
		
		return (size + size_reddit + size_instagram);
	} 

	/** 
	 * Returns the size of all channels for a given product during a given time interval
	 * @param product String containing the name of the product
	 * @param from Date containing the starting date
	 * @param to Date containing the ending date
	 * @return Map<String,Integer> containing the mapping between the channel name and the count
	 */ 

	public static Map<String,Integer> getAllChannelCount(String product,Date from,Date to) { 

		String collection_name = "";
		double from_date = DateConverter.getJulianDate(from);
		double to_date = DateConverter.getJulianDate(to);
		int size = 0;
		MongoBase mongo = null;
		DBCollection collection;
		BasicDBList query_list = new BasicDBList(); 
		BasicDBObject query;
		Map<String,Integer> channel_count = new HashMap<String,Integer>(); 
		collection_name = collection_names.get(product.toLowerCase().trim()); 

		query_list.add(new BasicDBObject("Type", "tag"));
		query_list.add(new BasicDBObject("Type", "image"));
		query_list.add(new BasicDBObject("Type", "video")); 

		try { 

			mongo = new MongoBase();
			mongo.setCollection(collection_name);
			collection = mongo.getCollection(); 

			for (String channel : channel_list) { 

				if (!(channel.equalsIgnoreCase("Reddit") || channel.equalsIgnoreCase("Instagram"))) { 

					query = new BasicDBObject("Channel",channel).
							append("TimeStamp", new BasicDBObject("$gte",from).append("$lte",to)); 

				} else if (channel.equalsIgnoreCase("Reddit")) { 

					query = new BasicDBObject("Channel",channel).
							append("TimeStamp", new BasicDBObject("$gte",from_date).append("$lte",to_date));
				} else { 

					query = new BasicDBObject("Channel",channel).append("$or",query_list).
							append("Timestamp", new BasicDBObject("$gte",from_date).append("$lte",to_date));
				}

				size = collection.find(query).size();
				channel_count.put(channel, size);
			} 

		} catch (Exception e) {  

			e.printStackTrace(); 

		} 

		return channel_count;
	} 
	
	/** 
	 * Returns the count of the posts received by a given product per year 
	 * @param product String containing the product name
	 * @param channel String containing the channel name
	 * @param from Date containing the starting date
	 * @param to Date containing the ending date
	 * @return Map<String,Integer> containing the mapping between the month name and post count
	 */ 
	
	public static Map<String,Integer> getAllLastNYearsProductChannel(String product,String channel,Date from,Date to) { 

		Map<String,Integer> year_map = new HashMap<String,Integer>();
		String year_name = ""; 

		DateTime from_date = new DateTime(from);
		DateTime to_date = new DateTime(to);
		int years = Years.yearsBetween(from_date, to_date).getYears();
		int count = 0; 
		Date now;
		Date prev; 
		
		for (int i = 0; i <= years; i++) {  

			year_name = to_date.minusYears(i).year().getAsText();
			prev = to_date.minusYears(i).toDate();
			now = to_date.minusYears(i-1).toDate();
			count = getProductChannelPostCount(product, channel,prev,now); 
			
			year_map.put(year_name, count);

		} 

		return year_map;
	} 
	
	/** 
	 * Returns the count of the posts received by a given product 
	 * @param product String containing the product name
	 * @param channel String containing the channel name
	 * @param from Date containing the starting date
	 * @param to Date containing the ending date
	 * @return Map<String,Integer> containing the mapping between the month name and post count
	 */ 
	
	public static Map<String,Integer> getAllLastNMonthsProductChannel(String product,String channel,Date from,Date to) { 

		Map<String,Integer> month_map = new HashMap<String,Integer>();
		String month_name = ""; 

		DateTime from_date = new DateTime(from);
		DateTime to_date = new DateTime(to);
		int months = Months.monthsBetween(from_date, to_date).getMonths();
		int count = 0; 
		Date now;
		Date prev; 

		for (int i = 0; i <= months; i++) {  

			month_name = to_date.minusMonths(i).monthOfYear().getAsText();
			prev = to_date.minusMonths(i).toDate();
			now = to_date.minusMonths(i-1).toDate();
			count = getProductChannelPostCount(product, channel,prev,now); 

			month_map.put(month_name, count);

		} 

		return month_map;
	} 
	
	/** 
	 * For a given product and a channel finds the count of posts received on a weekly basis
	 * @param product String containing the product
	 * @param channel String containing the channel
	 * @param from Date containing the starting date
	 * @param to Date containing the ending date
	 * @return Map <String,Integer> containing the week number and the count of post
	 */ 
	
	public static Map <String, Integer> getAllLastNWeeksProductChannel(String product,String channel,Date from,Date to) { 
		
		Map<String,Integer> week_map = new HashMap<String,Integer>();
		String week_number = "";
		int max_week_number;
		int weeks = 0;
		int count = 0;
		DateTime from_date = new DateTime(from);
		DateTime to_date = new DateTime(to);
		weeks = Weeks.weeksBetween(from_date, to_date).getWeeks();
		max_week_number = to_date.minusWeeks(-1).getWeekOfWeekyear();
		max_week_number = max_week_number + 1; 
		Date prev;
		Date now; 
		
		for (int i = 0; i <= weeks; i++) {
			
			week_number = String.valueOf((max_week_number) - (to_date.minusWeeks(i-1).getWeekOfWeekyear()));
			now = to_date.minusWeeks(i-1).toDate();
			prev = to_date.minusWeeks(i).toDate();
			count = getProductChannelPostCount(product, channel, prev, now);
			
			week_map.put(week_number, count);
		}
		
		return week_map;
	} 
	
	/** 
	 * Returns the count of posts received per hour for a given product and channel
	 * @param product String containing the product name
	 * @param channel String containing the channel name
	 * @param from Date containing the starting date object
	 * @param to Date containing the ending date object
	 * @return Map <String, Integer> containing the mapping between the hour and the post count
	 */ 
	
	public static Map <String, Integer> getAllLastNHoursProductChannel(String product,String channel,Date from,Date to) { 
		
		Map<String,Integer> hour_map = new HashMap<String,Integer>();
		String hour_number = "";
		int max_hour_number;
		int hours = 0;
		int count = 0;
		DateTime from_date = new DateTime(from);
		DateTime to_date = new DateTime(to);
		hours = Hours.hoursBetween(from_date, to_date).getHours();
		max_hour_number = to_date.minusHours(-1).getHourOfDay();
		max_hour_number = max_hour_number + 1; 
		Date prev;
		Date now; 
		
		for (int i = 0; i <= hours; i++) {
			
			hour_number = String.valueOf((max_hour_number) - (to_date.minusHours(i-1).getHourOfDay()));
			now = to_date.minusHours(i-1).toDate();
			prev = to_date.minusHours(i).toDate();
			count = getProductChannelPostCount(product, channel, prev, now);
			
			hour_map.put(hour_number, count);
		}
		
		return hour_map;
	}
	
	/** 
	 * Returns the count of posts received per minute for a given product and channel
	 * @param product String containing the product name
	 * @param channel String containing the channel name
	 * @param from Date containing the starting date object
	 * @param to Date containing the ending date object
	 * @return Map <String, Integer> containing the mapping between the minute and the post count
	 */ 
	
	public static Map <String, Integer> getAllLastNMinutesProductChannel(String product,String channel,Date from,Date to) { 
		
		Map<String,Integer> minute_map = new HashMap<String,Integer>();
		String minute_number = "";
		int max_minute_number;
		int minutes = 0;
		int count = 0;
		DateTime from_date = new DateTime(from);
		DateTime to_date = new DateTime(to);
		minutes = Minutes.minutesBetween(from_date, to_date).getMinutes();
		max_minute_number = to_date.minusMinutes(-1).getMinuteOfDay();
		max_minute_number = max_minute_number + 1; 
		Date prev;
		Date now; 
		
		for (int i = 0; i <= minutes; i++) {
			
			minute_number = String.valueOf((max_minute_number) - (to_date.minusMinutes(i-1).getMinuteOfDay()));
			now = to_date.minusMinutes(i-1).toDate();
			prev = to_date.minusMinutes(i).toDate();
			count = getProductChannelPostCount(product, channel, prev, now);
			
			minute_map.put(minute_number, count);
		}
		
		return minute_map;
	}
	
	/** 
	 * Returns the count of posts received per second for a given product and channel
	 * @param product String containing the product name
	 * @param channel String containing the channel name
	 * @param from Date containing the starting date object
	 * @param to Date containing the ending date object
	 * @return Map <String, Integer> containing the mapping between the second and the post count
	 */ 
	
	public static Map <String, Integer> getAllLastNSecondsProductChannel(String product,String channel,Date from,Date to) { 
		
		Map<String,Integer> second_map = new HashMap<String,Integer>();
		String second_number = "";
		int max_second_number;
		int seconds = 0;
		int count = 0;
		DateTime from_date = new DateTime(from);
		DateTime to_date = new DateTime(to);
		seconds = Seconds.secondsBetween(from_date, to_date).getSeconds();
		max_second_number = to_date.minusSeconds(-1).getSecondOfDay();
		max_second_number = max_second_number + 1; 
		Date prev;
		Date now; 
		
		for (int i = 0; i <= seconds; i++) {
			
			second_number = String.valueOf((max_second_number) - (to_date.minusSeconds(i-1).getSecondOfDay()));
			now = to_date.minusSeconds(i-1).toDate();
			prev = to_date.minusSeconds(i).toDate();
			count = getProductChannelPostCount(product, channel, prev, now);
			
			second_map.put(second_number, count);
		}
		
		return second_map;
	}
	
	/** 
	 * Returns the mapping between the month name and the post count for that month
	 * @param product String containing the product name
	 * @param from Date containing the starting date
	 * @param to Date containing the ending date
	 * @return Map<String,Integer> containing the mapping between the month name and post count
	 */ 
	
	public static Map<String,Integer> getAllLastNMonthsProduct(String product,Date from,Date to) { 

		Map<String,Integer> month_map = new HashMap<String,Integer>();
		String month_name = ""; 

		DateTime from_date = new DateTime(from);
		DateTime to_date = new DateTime(to);
		int months = Months.monthsBetween(from_date, to_date).getMonths();
		int count = 0; 
		Date now;
		Date prev; 

		for (int i = 0; i <= months; i++) {  

			month_name = to_date.minusMonths(i).monthOfYear().getAsText();
			prev = to_date.minusMonths(i).toDate();
			now = to_date.minusMonths(i-1).toDate();
			count = getCollectionSize(product,prev,now); 

			month_map.put(month_name, count);

		} 

		return month_map;
	} 
	
	/** 
	 * Returns the posts received by a given channel during a given interval on a weekly basis
	 * @param product String containing the product name
	 * @param from Date containing the from date
	 * @param to Date containing the to date
	 * @return Map <String,Integer> containing the mapping of last N weeks data
	 */ 
	
	public static Map <String, Integer> getAllLastNWeeksProduct(String product,Date from,Date to) { 
		
		Map<String,Integer> week_map = new HashMap<String,Integer>();
		String week_number = "";
		int max_week_number;
		int weeks = 0;
		int count = 0;
		DateTime from_date = new DateTime(from);
		DateTime to_date = new DateTime(to);
		weeks = Weeks.weeksBetween(from_date, to_date).getWeeks();
		max_week_number = to_date.minusWeeks(-1).getWeekOfWeekyear();
		max_week_number = max_week_number + 1; 
		Date prev;
		Date now; 
		
		for (int i = 0; i <= weeks; i++) {
			
			week_number = String.valueOf((max_week_number) - (to_date.minusWeeks(i-1).getWeekOfWeekyear()));
			now = to_date.minusWeeks(i-1).toDate();
			prev = to_date.minusWeeks(i).toDate();
			count = getCollectionSize(product, prev,now);
			
			week_map.put(week_number, count);
		}
		
		return week_map;
	}
	
	/** 
	 * Returns the count of posts received by a given product on an hourly basis
	 * @param product String containing the product name
	 * @param from Date containing the starting date
	 * @param to Date containing the ending date
	 * @return Map <String, Integer> containing the hour map and the count
	 */ 
	
	public static Map <String, Integer> getAllLastNHoursProduct(String product,Date from,Date to) { 
		
		Map<String,Integer> hour_map = new HashMap<String,Integer>();
		String hour_number = "";
		int max_hour_number;
		int hours = 0;
		int count = 0;
		DateTime from_date = new DateTime(from);
		DateTime to_date = new DateTime(to);
		hours = Hours.hoursBetween(from_date, to_date).getHours();
		max_hour_number = to_date.minusHours(-1).getHourOfDay();
		max_hour_number = max_hour_number + 1; 
		Date prev;
		Date now; 
		
		for (int i = 0; i <= hours; i++) {
			
			hour_number = String.valueOf((max_hour_number) - (to_date.minusHours(i-1).getHourOfDay()));
			now = to_date.minusHours(i-1).toDate();
			prev = to_date.minusHours(i).toDate();
			count = getCollectionSize(product, prev, now);
			
			hour_map.put(hour_number, count);
		}
		
		return hour_map;
	}
	
	/** 
	 * Returns the count of posts received per second for a given product and channel
	 * @param product String containing the product name
	 * @param channel String containing the channel name
	 * @param from Date containing the starting date object
	 * @param to Date containing the ending date object
	 * @return Map <String, Integer> containing the mapping between the second and the post count
	 */ 
	
	public static Map <String, Integer> getAllLastNSecondsProduct(String product,Date from,Date to) { 
		
		Map<String,Integer> second_map = new HashMap<String,Integer>();
		String second_number = "";
		int max_second_number;
		int seconds = 0;
		int count = 0;
		DateTime from_date = new DateTime(from);
		DateTime to_date = new DateTime(to);
		seconds = Seconds.secondsBetween(from_date, to_date).getSeconds();
		max_second_number = to_date.minusSeconds(-1).getSecondOfDay();
		max_second_number = max_second_number + 1; 
		Date prev;
		Date now; 
		
		for (int i = 0; i <= seconds; i++) {
			
			second_number = String.valueOf((max_second_number) - (to_date.minusSeconds(i-1).getSecondOfDay()));
			now = to_date.minusSeconds(i-1).toDate();
			prev = to_date.minusSeconds(i).toDate();
			count = getCollectionSize(product, prev, now);
			
			second_map.put(second_number, count);
		}
		
		return second_map;
	}
	
	/** 
	 * Returns the mapping between the year name and the post count for that year
	 * @param product String containing the product name
	 * @param from Date containing the starting date
	 * @param to Date containing the ending date
	 * @return Map<String,Integer> containing the mapping between the year name and post count
	 */ 
	
	public static Map<String,Integer> getAllLastNYearsProduct(String product,Date from,Date to) { 

		Map<String,Integer> year_map = new HashMap<String,Integer>();
		String year_name = ""; 

		DateTime from_date = new DateTime(from);
		DateTime to_date = new DateTime(to);
		int years = Years.yearsBetween(from_date, to_date).getYears();
		int count = 0; 
		Date now;
		Date prev; 

		for (int i = 0; i <= years; i++) {  

			year_name = to_date.minusYears(i).year().getAsText();
			prev = to_date.minusYears(i).toDate();
			now = to_date.minusYears(i-1).toDate();
			count = getCollectionSize(product,prev,now); 

			year_map.put(year_name, count);

		} 

		return year_map;
	} 
	
	/** 
	 * Returns the count of posts received per minute for a given product
	 * @param product String containing the product name
	 * @param from Date containing the starting date object
	 * @param to Date containing the ending date object
	 * @return Map <String, Integer> containing the mapping between the minute and the post count
	 */ 
	
	public static Map <String, Integer> getAllLastNMinutesProduct(String product,Date from,Date to) { 
		
		Map<String,Integer> minute_map = new HashMap<String,Integer>();
		String minute_number = "";
		int max_minute_number;
		int minutes = 0;
		int count = 0;
		DateTime from_date = new DateTime(from);
		DateTime to_date = new DateTime(to);
		minutes = Minutes.minutesBetween(from_date, to_date).getMinutes();
		max_minute_number = to_date.minusMinutes(-1).getMinuteOfDay();
		max_minute_number = max_minute_number + 1; 
		Date prev;
		Date now; 
		
		for (int i = 0; i <= minutes; i++) {
			
			minute_number = String.valueOf((max_minute_number) - (to_date.minusMinutes(i-1).getMinuteOfDay()));
			now = to_date.minusMinutes(i-1).toDate();
			prev = to_date.minusMinutes(i).toDate();
			count = getCollectionSize(product, prev, now);
			
			minute_map.put(minute_number, count);
		}
		
		return minute_map;
	}
	
	/** 
	 * Main function to test the functionality of the class
	 * @param args
	 */ 

	public static void main(String args[]) { 

		DateTime now = new DateTime();
		DateTime prev = now.minusYears(3);
		
		System.out.println(getProductChannelPostCount("gueSS", "Instagram",prev.toDate(),now.toDate()));
		System.out.println(getCollectionSize("Guess"));
		System.out.println(getCollectionSize("gueSs",prev.toDate(),now.toDate()));
		System.out.println(getAllChannelCount("Guess",prev.toDate(),now.toDate()));
		prev = now.minusMonths(3);
		System.out.println("Forever21");
		System.out.println(getAllLastNMonthsProductChannel("Forever21","Twitter",prev.toDate(),now.toDate()));
		System.out.println(getAllLastNMonthsProduct("HandM",prev.toDate(),now.toDate())); 
		
		prev = now.minusWeeks(3);
		System.out.println(getAllLastNWeeksProductChannel("Guess", "Instagram", prev.toDate(),now.toDate()));
		System.out.println(getAllLastNWeeksProduct("Guess",prev.toDate(),now.toDate()));
		prev = now.minusHours(3);
		System.out.println(getAllLastNHoursProductChannel("Guess", "Instagram", prev.toDate(),now.toDate()));
		System.out.println(getAllLastNHoursProduct("Guess", prev.toDate(),now.toDate()));
	}
}
