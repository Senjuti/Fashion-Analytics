package org.kutty.utils;

import java.util.ArrayList;

import com.cybozu.labs.langdetect.Detector;
import com.cybozu.labs.langdetect.DetectorFactory;
import com.cybozu.labs.langdetect.LangDetectException;
import com.cybozu.labs.langdetect.Language;


/**
 * Utility Class to detect the language of a given post since facebook pages get posts from 
 * all the languages it is imperative to detect the language of the post at first and then
 * proceed with the rest of the pipeline 
 *  
 * @author Senjuti Kundu
 * 
 */  

public final class LanguageDetector {

	/** Initialize the profile directory of the messages */ 

	public static boolean isInitialized = false;  

	/** 
	 * Function to initialize the Language Detection profile directory
	 * @param profileDirectory String containing the name of the profileDirectory
	 * @throws LangDetectException
	 */ 

	synchronized public static void init(String profileDirectory) { 

		if (!isInitialized) { 

			try {
				DetectorFactory.loadProfile(profileDirectory);
				isInitialized = true;
			} catch (LangDetectException lde) {
				lde.printStackTrace();
			}
		} 
	} 

	/**
	 * Function to detect the language of a given string 
	 * @param text String containing the text whose language is to be detected
	 * @return Language code of the language in which the text is in (for e.g. "en" for english)
	 * @throws LangDetectException
	 */

	public static String detect(String text) { 

		if (text == null) {  

			return "";
		} 
		String s = ""; 
		try {
			Detector detector = DetectorFactory.create();
			detector.append(text); 
			s = detector.detect(); 
		} catch (LangDetectException lde) {
			lde.printStackTrace();
		} 

		return s;
	}  

	/** 
	 * Utility function to get the probabilities of the languages likely
	 * @param text String containing the text which is to be detected
	 * @return ArrayList<Language> containing the list of matching languages
	 * @throws LangDetectException
	 */

	public ArrayList<Language> detectLangs(String text) throws LangDetectException { 

		Detector detector = DetectorFactory.create();
		detector.append(text); 

		return detector.getProbabilities();
	} 
	
	/** 
	 * Detects if a given text is in English or not  
	 * @param s String containing the text 
	 * @return true if the string is in English false otherwise
	 */ 
	
	public static boolean isEnglish(String s) { 

		try { 

			if (detect(s).equalsIgnoreCase("en")) { 	
				return true;
			} 

		} catch (Exception e) { 
			e.printStackTrace();
		}

		return false;
	} 
} 

