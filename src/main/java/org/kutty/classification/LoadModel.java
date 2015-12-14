package org.kutty.classification;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/** 
 * Utility class for loading the trained models 
 * 
 * @author Senjuti Kundu
 * 
 */ 

public class LoadModel {
	
		
	/** 
	 * Returns the Map of a trained model from a given filename
	 * @param filename String containing the filename from which the model needs to be read
	 * @return Map <String, Double> containing 
	 */ 
	
	public static Map <String, Double> getTrainedModel(String filename) { 

		Map <String, Double> ngram_map; 
		FileReader fr;
		BufferedReader br;
		int index = -1;
		String temp = "";
		String ngram = "";
		double probability; 
		ngram_map = new HashMap <String, Double>(); 
		
		try { 
			
			fr = new FileReader(filename);
			br = new BufferedReader(fr);
			
			while((temp = br.readLine())!= null) { 
				
				index = temp.indexOf('=');
				
				if (index != -1) { 
					
					ngram = temp.substring(0, index);
					probability = Double.valueOf(temp.substring(index+1).trim());
					ngram_map.put(ngram,probability);
				}
			}
			
			br.close();
			fr.close();
			
		} catch (IOException e) { 
			
			e.printStackTrace();
		}
		
		return ngram_map;
	}	
}
