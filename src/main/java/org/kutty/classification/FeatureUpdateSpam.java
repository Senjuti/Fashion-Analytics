package org.kutty.classification;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.kutty.constants.Constants;
import org.kutty.db.MongoBase;
import org.kutty.dbo.Update;
import org.kutty.features.FeatureUtil;
import org.kutty.utils.ClassificationUtils;
import org.kutty.utils.DateConverter;
import org.kutty.utils.ListConverter;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

/** 
 * Updates the knowledge base of spam features for all channels (offline update)
 * @author Senjuti Kundu
 */

public class FeatureUpdateSpam {
	
	String channelName;
	String productName; 
	
	/** 
	 * 
	 * @param channelName
	 * @param productName
	 */
	FeatureUpdateSpam(String channelName, String productName) { 
		
		this.channelName = channelName.toLowerCase().trim();
		this.productName = productName;
	} 
	
	/** 
	 * 
	 * @param channelName
	 * @param productName
	 * @param from
	 * @param to
	 * @return
	 */
	public Map<String,List<Update>> getUpdateObjects(String channelName,String productName,Date from,Date to) { 

		Map<String,List<Update>> updateMap = new HashMap<String,List<Update>>();
		BasicDBObject query;
		BasicDBObject fields;
		DBCollection collection;
		BasicDBList updateObjectList;
		BasicDBList queryList;
		DBCursor cursor; 
		DBObject temp;
		MongoBase mongo;
		String message; 

		queryList = new BasicDBList();
		queryList.add(new BasicDBObject("SpamLabel", Constants.HAM_LABEL));
		queryList.add(new BasicDBObject("SpamLabel", Constants.SPAM_LABEL));

		fields = new BasicDBObject("Message",1).append("UpdateList", 1);

		if (!(channelName.equalsIgnoreCase("Instagram") || channelName.equalsIgnoreCase("Reddit"))) {

			query = new BasicDBObject("Channel",channelName).append("Product",productName).append("$or",queryList).
					append("TimeStamp", new BasicDBObject("$gte",from).append("$lte", to));
		} else {

			double fromDate = DateConverter.getJulianDate(from);
			double toDate = DateConverter.getJulianDate(to); 

			query = new BasicDBObject("Channel",channelName).append("Product",productName).append("$or",queryList).
					append("OtherDate", new BasicDBObject("$gte",fromDate).append("$lte", toDate)); 
		}

		try {

			mongo = new MongoBase();
			mongo.setDB(Constants.ANALYTICS_DB);
			mongo.setCollection(productName);

			collection = mongo.getCollection();
			cursor = collection.find(query, fields);

			while(cursor.hasNext()) { 

				temp = cursor.next();
				message = (String) temp.get("Message");
				updateObjectList = (BasicDBList) temp.get("UpdateList");
				
				if (updateObjectList != null) { 

					updateMap.put(message, ListConverter.getUpdateList(updateObjectList));
				}
			}
		} catch (UnknownHostException e) {

			e.printStackTrace();
		}

		return updateMap;
	}
	
	/** 
	 * 
	 * @param updateMap
	 */
	public void calculateNGramProbability(Map<String,List<Update>> updateMap) { 

		String filename = "";
		int  modelNum;
		int ngramNum; 
		String classLabel; 
		String ngram;
		String processedMessage; 
		Map<String,Double> updatedFeatureMap = new HashMap<String,Double>(); 

		for(String message : updateMap.keySet()) { 

			for (Update update : updateMap.get(message)) { 

				if (update.getProbPercent() > 50.0) { 

					modelNum = update.getModelNum();
					ngramNum = update.getNgramNum(); 
					classLabel = update.getClassLabel(); 
					updatedFeatureMap.clear();
					filename = this.channelName + "/" + classLabel + "_" + modelNum + ".txt";
					Map<String,Double> featureMap = LoadModel.getTrainedModel(filename);
					Map<String,Integer> labelCountMap = Prior.generateLabelSpamCountMap(channelName, modelNum);
					processedMessage = ClassificationUtils.preProcessingPipelineForContent(message);
					ngram = FeatureUtil.getNGram(processedMessage, ngramNum);
					List<String> ngramArray = ClassificationUtils.getTokenizedString(ngram, "|");

					for (String gram : ngramArray) {  
						
						gram = gram.trim();
						gram = ClassificationUtils.getTransformedString(gram);  
						
						if(!featureMap.containsKey(gram)) {   
							
							updatedFeatureMap.put(gram, (double) (1.0/labelCountMap.get(classLabel))); 
						}
					}
					
					writeGramToFile(filename, updatedFeatureMap);
				}
			}
		}
	}
	
	/** 
	 * Given a map of NGram probabilities writes them to a file
	 * @param filename String containing the filename
	 * @param ngram_map_count Map<String,Double> containing ngram probabilities
	 */ 
	public void writeGramToFile(String filename,Map<String,Double> ngram_map_count) { 
		
		if (ngram_map_count.isEmpty()) { 
			
			return;
		}
		
		BufferedWriter bw;
		FileWriter fw;
		double count;
		String transform = ""; 

		try { 

			fw = new FileWriter(filename,true);
			bw = new BufferedWriter(fw); 

			for (String s : ngram_map_count.keySet()) { 

				count = ngram_map_count.get(s);
				transform = s;
				transform = transform + "=" + String.valueOf(count);
				bw.write(transform);
				bw.newLine();
			}

			bw.close();
			fw.close();

		} catch(Exception e) { 
			e.printStackTrace();
		}
	}
	
	/** 
	 * 
	 * @param channelName
	 * @param productName
	 * @param from
	 * @param to
	 */
	public void spamUpdatePipeline(String channelName,String productName,Date from,Date to) { 
		
		FeatureUpdateSpam featureUpdateSpam = new FeatureUpdateSpam(channelName, productName);
		Map<String,List<Update>> featureUpdateObjects = featureUpdateSpam.getUpdateObjects(channelName, productName, from, to);
		featureUpdateSpam.calculateNGramProbability(featureUpdateObjects);
	} 
	
	public static void main(String args[]) { 

		DateTime to = new DateTime();
		DateTime from = to.minusYears(10);
		FeatureUpdateSpam fs = new FeatureUpdateSpam("Twitter", "Forever21");
		fs.spamUpdatePipeline("Twitter","Forever21", from.toDate(), to.toDate());
	}
}
