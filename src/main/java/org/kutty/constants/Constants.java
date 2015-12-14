package org.kutty.constants;


/**
 * Defines a set of constants which are consistently used in different packages
 * @author Senjuti Kundu
 */

public class Constants {

	public static String [] channelNames = {"Twitter","Facebook"};
	public static String [] brandNames = {"Forever21","FreePeople","Guess","HandM","Levis","Mango",
		"RagandBone","SevenForAllMankind","TrueReligion"};
	public static String [] MONTHS_OF_YEAR = {"january","february","march","april","may","june","july","august",
		"september","october","november","december"};
	public static String [] DAYS_OF_WEEK = {"monday","tuesday","wednesday","thursday","friday","saturday","sunday"};
	public static String [] MONTH_SHORT = {"jan","feb","mar","apr","may","jun","jul","aug","sept","oct","nov","dec"};
	public static char [] PUNCT_SET  = {' ',',',':',';','\'','\t','\n','?','-','$'};
	public static String [] EMOJI_SET = {":)",":(","^_^","-_-","<3",":D",":P",":/"};
	
	public static final int DAYS = 30;
	public static final int MEAN_DAYS = 90;
	public static final int MEDIAN_DAYS = 60;
	public static final int OLD_MIN = -10;
	public static final int OLD_MAX = 10;
	public static final int NEW_MIN = 0;
	public static final int NEW_MAX = 100;
	public static final int MAX_MODEL_NUM = 5;
	
	public static final String SENTIMENT_TYPE = "sentiment";
	public static final String POSITIVE_LABEL = "positive";
	public static final String NEGATIVE_LABEL = "negative";
	public static final String NEUTRAL_LABEL = "neutral";
	public static final String SPAM_TYPE = "spam";
	public static final String SPAM_LABEL = "spam";
	public static final String HAM_LABEL = "ham";
	public static final String ANALYTICS_DB = "Analytics";
	public static final String SATISFACTION_COLLECTION = "Satisfaction";
	public static final String TWITTER_FOLDER = "twitter";
	public static final String FACEBOOK_FOLDER = "facebook";
	public static final String SENTIMENT_PRIOR_FILE = "sentiment_prior.txt";
	public static final String SPAM_PRIOR_FILE = "spam_prior.txt";
	public static final String TRAIN_FILE = "Train_data.txt";
	public static final String TWITTER_TRAIN_FILE = "Twitter_train.txt";
	public static final String FACEBOOK_TRAIN_FILE = "Facebook_train.txt";
	public static final String TWITTER_TEST_FILE = "";
	public static final String FACEBOOK_TEST_FILE = "Facebook_test_annotated.txt";
	public static final String RESOURCES_FOLDER_NAME = "resources/";
}
