import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class PlutchikEmotions {
	
	private HashMap<String,Integer> emotionMap = new HashMap<String,Integer>();
	private String[] negations = new String[] { "n't", "0", "aint", "arent", "cant", "couldnt", "darent", "didnt", "doesnt", "dont", "hadnt", "hasnt", "havent", "isnt", "mightnt", "mustnt", "neednt", "never", "no", "not", "oughtnt", "shant", "shouldnt", "w/o", "wasnt", "werent", "without", "wont", "wouldnt", "zero" };

	public static void main(String[] args) throws IOException {
		PlutchikEmotions e = new PlutchikEmotions();
		
		BufferedReader br = new BufferedReader(new FileReader("./export100k.txt"));
		BufferedWriter bw = new BufferedWriter(new FileWriter("./export100k_emotion.txt"));
		String line;
		while ((line = br.readLine()) != null) {
			String tweet = line;
			String tweet2 = tweet.replace("~",  "").replace("#", "").replace("_", "").replace("?",  "").replace("\"", "").replace("\'", "").replace("*",  "").replaceAll("[\\s]+", " ").toLowerCase();

//			String tweet = "oh no #fml";
//			String tweet = "I am not good, so sad about it";
//			String tweet = "i love you, i dont love you";
//			String tweet = "I am not as good as I thought. At least, I am in love and get laid everyday";
//			String tweet = "0 tolerance";
			
			e.emotionMap.put("joy", 0);
			e.emotionMap.put("sadness", 0);
			e.emotionMap.put("trust", 0);
			e.emotionMap.put("disgust", 0);
			e.emotionMap.put("fear", 0);
			e.emotionMap.put("anger", 0);
			e.emotionMap.put("surprise", 0);
			e.emotionMap.put("anticipation", 0);
			
			e.executeRegex("joy", tweet2);
			e.executeRegex("sadness", tweet2);
			e.executeRegex("trust", tweet2);
			e.executeRegex("disgust", tweet2);
			e.executeRegex("fear", tweet2);
			e.executeRegex("anger", tweet2);
			e.executeRegex("surprise", tweet2);
			e.executeRegex("anticipation", tweet2);
			
			Entry<String,Integer> maxEntry = null;
			for(Entry<String,Integer> entry : e.emotionMap.entrySet()) {
			    if (maxEntry == null || entry.getValue() > maxEntry.getValue()) {
			        maxEntry = entry;
			    }
			}
			
			tweet = tweet.replaceAll("[^\\w\\s\\/\\?;:<>\\.,\\'\\)\\(\\@\\#]", "");
			
			if (maxEntry.getValue() > 0) {
				for(Entry m : e.emotionMap.entrySet()) {
					if (m.getValue() == maxEntry.getValue()) {
//						System.out.println(m.getKey() + ": " + m.getValue());
						String output = e.getEmotion(m.getKey().toString()) + "\t" + tweet + "\r\n";
//						System.out.println(output);
						bw.write(output);
					}
				}
			} else {
				String output = "0\t" + tweet + "\r\n";
//				System.out.println(output);
				bw.write(output);
			}
		}
		bw.close();
		br.close();
	}
	
	private void executeRegex(String emotion, String tweet) throws IOException
	{
		PlutchikEmotions e = new PlutchikEmotions();
		BufferedReader br = new BufferedReader(new FileReader("./src/Plutchik/" + emotion + ".txt"));
		String line;
		String regex = "";
		while ((line = br.readLine()) != null) {
			regex += line + "|";
		}
		regex = regex.substring(0, regex.length()-1);
//		System.out.println(regex);
		
		Pattern pattern = Pattern.compile(regex);
		Matcher m = pattern.matcher(tweet);
		
		while (m.find()) {
			System.out.println(m.group());
			boolean isNegation = false;
			if (m.start() > 1) {
				int index = tweet.substring(0, m.start()).lastIndexOf(" ");
				String tweet2 = tweet.substring(0, m.start());
				System.out.println(tweet2);
				if (index > 0) {
					int index2 = tweet2.substring(0, index-1).lastIndexOf(" ");
					String possibleNegation = tweet2.substring(index2+1, index);
//					System.out.println(possibleNegation);
					isNegation = isNegation(possibleNegation);
					if (!isNegation) {
						int index3 = tweet2.substring(0, m.start()-1).lastIndexOf(" ");
						String tweet3 = tweet2.substring(0, m.start()-1);
						if (index2 > 0) {
							int index4 = tweet3.substring(0, index3-1).lastIndexOf(" ");
							String possibleNegation2 = tweet3.substring(index4+1, index3);
//							System.out.println(possibleNegation2);
							isNegation = isNegation(possibleNegation2);
						} else {
							if (tweet3.substring(index3+1,1).equals("0")) {
								isNegation = true;
							}
						}
					}
				}
			}
			String emotion2 = emotion;
			if (isNegation) {
				emotion2 = getOppositeEmotion(emotion);
			}
			int c = emotionMap.get(emotion2);
			c++;			
			emotionMap.put(emotion2, c);
		}
		br.close();
	}
	
	public boolean isNegation(String possibleNegation) {
		boolean isNegation = false;
		for (String negation : negations) {
			if (possibleNegation.equals(negation)) {
				isNegation = true;
				break;
			}
		}
		return isNegation;
	}
	
	public int getOppositeEmotion(int emotion) {
		if (emotion % 2 == 0) {
			return (emotion-1);
		} else {
			return (emotion+1);
		}
	}
	
	public String getOppositeEmotion(String emotion) {
		String opposite = "";
		switch (emotion) {
			case "joy":
				opposite = "sadness";
				break;
			case "sadness":
				opposite = "joy";
				break;
			case "trust":
				opposite = "disgust";
				break;
			case "disgust":
				opposite = "trust";
				break;
			case "fear":
				opposite = "anger";
				break;
			case "anger":
				opposite = "fear";
				break;
			case "surprise":
				opposite = "anticipation";
				break;
			case "anticipation":
				opposite = "surprise";
				break;
		}
		return opposite;
	}
	
	public int getEmotion(String emotion) {
		int emotion2 = 0;
		switch (emotion) {
			case "joy":
				emotion2 = 1;
				break;
			case "sadness":
				emotion2 = 2;
				break;
			case "trust":
				emotion2 = 3;
				break;
			case "disgust":
				emotion2 = 4;
				break;
			case "fear":
				emotion2 = 5;
				break;
			case "anger":
				emotion2 = 6;
				break;
			case "surprise":
				emotion2 = 7;
				break;
			case "anticipation":
				emotion2 = 8;
				break;
		}
		return emotion2;
	}
	
}
