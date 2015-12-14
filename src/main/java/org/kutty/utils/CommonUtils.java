package org.kutty.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import org.kutty.constants.Constants;


/** 
 * Class Loader util for loading external files when the application is run on the server/as a jar
 * @author Senjuti Kundu
 *
 */ 

public class CommonUtils {
	
	/** 
	 * Returns the absolute path of a file or directory 
	 * @param string contains the name of the file or directory
	 * @return String containing the name of the file or directory
	 */ 
	
	public static String getAbsolutePath(String string) { 
		
		ClassLoader classLoader = CommonUtils.class.getClassLoader();
		URL profilesDirURL = classLoader.getResource(string); 
		String path = profilesDirURL.getPath(); 
		
		return path;
	}
	
	public static BufferedReader getBufferedInput(String filepath) { 
		
		ClassLoader classLoader = CommonUtils.class.getClassLoader();
		String fullPath = appendResourcePath(filepath);
		InputStream in = classLoader.getResourceAsStream(fullPath);
		InputStreamReader rin = new InputStreamReader(in);
		BufferedReader br = new BufferedReader(rin);
		
		return br;
	}
	
	public static String appendResourcePath(String filepath) { 
		
		String resourceFolderName = Constants.RESOURCES_FOLDER_NAME;
		String fullPath = resourceFolderName.trim() + filepath.trim();
		
		return fullPath;
	}
	
	public static void main (String args[]) { 
		System.out.println(getAbsolutePath("twitter/ham_1.txt"));
		System.out.println();
	}
}