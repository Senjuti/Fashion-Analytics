package org.kutty.features;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/** 
 * Splits a given dataset into N equal parts by uniform random sampling of the master set.
 * Additionally it also writes the data to an external text file 
 * 
 * @author Senjuti Kundu
 */ 

public class SplitData {

	public String FILENAME;
	public int NUMBER_OF_SPLITS;
	public int NUMBER_OF_DATA_POINTS = 0;
	public ArrayList<String> DATA_ARRAY = new ArrayList<String>();
	public boolean [] CHECK_SAMPLED;
	public int SIZE_OF_SPLIT;

	/** 
	 * public constructor to initialize the filename the number of splits and the data array
	 * @param filename String which represents the filename
	 * @param splits Integer representing the required number of splits
	 * @throws IOException
	 */ 

	public SplitData(String filename,int splits)throws IOException { 

		this.FILENAME = filename;
		this.NUMBER_OF_SPLITS = splits;
		LoadData(FILENAME,DATA_ARRAY);
		CHECK_SAMPLED = new boolean[NUMBER_OF_DATA_POINTS];
		SIZE_OF_SPLIT = (int) Math.floor(NUMBER_OF_DATA_POINTS/NUMBER_OF_SPLITS);
	}

	/** 
	 * Loads the data from a given external file into an ArrayList
	 * @param filename String representing the file from which the data is to be loaded
	 * @param data ArrayList<String> which contains all the data points
	 * @throws IOException
	 */ 

	public void LoadData(String filename,ArrayList<String> data)throws IOException { 

		BufferedReader br;
		FileReader fr; 
		String temp = ""; 

		fr = new FileReader(filename);
		br = new BufferedReader(fr);

		while((temp = br.readLine()) != null) { 

			data.add(temp);
			NUMBER_OF_DATA_POINTS++;
		}

		br.close();
		fr.close();
	}
	
	/** 
	 * Returns a pseudo random number between 0 and an upper limit
	 * @param data_points Integer representing the upper limit of the random number generator
	 * @return Integer containing the random number
	 */ 
	
	public int generateRandomNumber(int data_points) { 

		Random random = new Random();

		return random.nextInt(data_points);
	}
	
	/** 
	 * Returns an array of indices of the master dataset
	 * @param size_of_split Integer associated with the size of each data split
	 * @return an array of data points containing unique and random indices
	 */ 
	
	public int[] getDataPoints(int size_of_split) { 

		int [] data_points = new int[size_of_split];
		int count = 0; 
		int temp; 

		while (count < size_of_split) { 

			temp = generateRandomNumber(NUMBER_OF_DATA_POINTS);

			if (!CHECK_SAMPLED[temp]) { 

				data_points[count++] = temp;
				CHECK_SAMPLED[temp] = true;
			}
		}

		return data_points;
	}
	
	/** 
	 * Creates the splits of the dataset and writes them to the file
	 * @throws IOException
	 */ 
	
	public void createSplits(String base_folder) throws IOException { 

		int data_points[]; 
		Set<String> split; 
		String filename = "";

		for (int i = 0; i < NUMBER_OF_SPLITS; i++) { 

			data_points = getDataPoints(SIZE_OF_SPLIT);
			split = new HashSet<String>(); 
			filename = base_folder + "_" + (i+1) + ".txt"; 

			for (int j = 0; j < data_points.length; j++) { 

				split.add(DATA_ARRAY.get(data_points[j]));

			}

			writeToFile(filename,split);
		}
	}
	
	/** 
	 * Utility function to write a given subset of data to an external file
	 * @param filename String containing the filename
	 * @param message_set Set<String> containing the set of messages
	 * @throws IOException
	 */ 
	
	public void writeToFile(String filename, Set<String> message_set)throws IOException { 

		BufferedWriter bw;
		FileWriter fw;

		fw = new FileWriter(filename);
		bw = new BufferedWriter(fw);

		for (String s : message_set) { 

			bw.write(s);
			bw.newLine();
		}

		bw.close();
		fw.close();
	}

	public static void main(String args[]) throws IOException { 

		SplitData splitdata = new SplitData("Twitter_train.txt",5);
		splitdata.createSplits("twitter/split");
	}
}
