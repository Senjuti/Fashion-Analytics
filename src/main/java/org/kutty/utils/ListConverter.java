package org.kutty.utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.kutty.dbo.Update;

import com.mongodb.BasicDBList;
import com.mongodb.DBObject;

/** 
 * Utility converters for BasicDBList,converts the BasicDBList into different data types 
 * 
 * @author Senjuti Kundu
 *
 */ 

public class ListConverter {

	/** 
	 * Converts a BasicDBList and converts it into a Set<String>
	 * @param list BasicDBList which is to be converted into a set of strings
	 * @return Set<String> which is representation of the BasicDBList
	 */ 
	
	public static Set<String> getSet(BasicDBList list) { 

		List<Object> temp_list = list;
		Set<String> final_set = new HashSet<String>();

		for(Object obj : temp_list) { 

			final_set.add((String) obj);
		}

		return final_set;
	}

	/** 
	 * Converts a given BasicDBList into an ArrayList<Double>
	 * @param list BasicDBList which is to be converted
	 * @return ArrayList<Double> which is the representation of the BasicDBList
	 */ 

	public static ArrayList<Double> getArrayList(BasicDBList list) { 

		List<Object> temp_list = list;
		ArrayList<Double> final_list = new ArrayList<Double>();

		for (Object obj : temp_list) { 

			final_list.add((Double) obj);
		}

		return final_list;
	}

	/** 
	 * Converts a given BasicDBList into an ArrayList<Date>
	 * @param list BasicDBList which is to be converted
	 * @return ArrayList<Date> which is the representation of the BasicDBList
	 */ 

	public static ArrayList<Date> getArrayListDate(BasicDBList list) { 

		List<Object> temp_list = list;
		ArrayList<Date> final_list = new ArrayList<Date>();

		for (Object obj : temp_list) { 

			final_list.add((Date) obj);
		}

		return final_list;
	}

	/** 
	 * Converts a given BasicDBList into an ArrayList<String>
	 * @param list BasicDBList which is to be converted
	 * @return ArrayList<String> which is the representation of the BasicDBList
	 */ 

	public static ArrayList<String> getArrayListString(BasicDBList list) { 

		List<Object> temp_list = list;
		ArrayList<String> final_list = new ArrayList<String>();

		for (Object obj : temp_list) { 

			final_list.add((String) obj);
		}

		return final_list;
	} 

	/** 
	 * Converts a given BasicDBList into an ArrayList<Integer>
	 * @param list BasicDBList which is to be converted
	 * @return ArrayList<Integer> which is the representation of the BasicDBList
	 */ 

	public static ArrayList<Integer> getArrayListInteger(BasicDBList list) { 

		List<Object> temp_list = list;
		ArrayList<Integer> final_list = new ArrayList<Integer>();

		for (Object obj : temp_list) { 

			final_list.add((Integer) obj);
		}

		return final_list;
	}

	/** 
	 * Converts a String array into an ArrayList<String>
	 * @param arr String array which is to be converted into the ArrayList<String>
	 * @return ArrayLisy<String> containing the contents of the array
	 */ 

	public static ArrayList<String> getArrayListFromArray(String arr[]) { 

		ArrayList<String> array_list = new ArrayList<String>();

		for (String temp: arr) { 

			array_list.add(temp);
		}

		return array_list;
	} 

	/** 
	 * Converts a set into a comma separated String
	 * @param list BasicDBList which is to be converted
	 * @return String containing the CSV representation
	 */ 

	public static String getCSVSet(BasicDBList list) { 

		String csv = "";
		List<Object> temp_list = list;

		for (Object obj : temp_list) { 

			csv = csv + (String) obj + ",";
		}

		return csv;
	}
	
	/** 
	 * Given a CSV of tags converts it into a set
	 * @param tagset String containing the tagset
	 * @return Set<String> containing the tagset
	 */
	
	public static Set<String> getTagSet(String tagset) { 
		
		Set<String> tags = new HashSet<String>();
		String tag_array [] = tagset.split(",");
		
		for (int i = 0; i < tag_array.length; i++) { 
			
			tags.add(tag_array[i]);
		}
		
		return tags;
	}
	
	/** 
	 * Given a list of BasicDBObject converts it into an List<Update>
	 * @param updateList BasicDBList containing the update objects
	 * @return List<Update> containing the update list
	 */
	public static List<Update> getUpdateList(BasicDBList updateList) { 
		
		List<Update> updateObjectList = new ArrayList<Update>();
		DBObject tempObject;
		Update updateTemp;
		
		for (Object temp : updateList) { 
			
			tempObject = (DBObject) temp;
			updateTemp = new Update();
			
			updateTemp.setClassLabel((String) tempObject.get("Label"));
			updateTemp.setModelNum((int) tempObject.get("Model"));
			updateTemp.setNgramNum((int) tempObject.get("Ngram"));
			updateTemp.setProbPercent((double) tempObject.get("Probability"));
			
			updateObjectList.add(updateTemp);
		}
		
		return updateObjectList;
	}
	
	/** 
	 * Given a list of Double type numbers converts it to an array of numbers
	 * @param featureList List<Double> containing the numbers
	 * @return Double[] containing the array of double numbers
	 */
	public static Double[] getFeatureArray(List<Double> featureList) { 
		
		Double [] featureSet = new Double[featureList.size()]; 
		
		for (int i = 0; i < featureList.size(); i++) { 
			
			featureSet[i] = featureList.get(i);
		} 
		
		return featureSet;
	}
	
	/** 
	 * Given an array of values converts it to a List
	 * @param array T[] containing the generic array type
	 * @return List<T> containing the list representation of the array
	 */
	public static <T> List<T> getFeatureList(T[] array) { 
		
		List<T> list = new ArrayList<>(); 
		
		for(T temp : array) { 
			
			list.add(temp);
		}
		
		return list;
	}
}
