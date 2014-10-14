import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import uk.ac.wlv.sentistrength.*;

public class SentiStrengthDemo {

	private static Integer[] POLARITIES = new Integer[]{-1,0,1};
	
	public static void main(String[] args) throws IOException {
		SentiStrength ss = new SentiStrength();
		String init[] = {"sentidata", "./SentStrengthData/", "explain", "exclamations2", "questionsReduceNeg", "maxWordsBeforeSentimentToNegate", "2", "maxWordsAfterSentimentToNegate", "3"};
		ss.initialise(init);
		BufferedReader br = new BufferedReader(new FileReader("./export100k.txt"));
		BufferedWriter bw = new BufferedWriter(new FileWriter("./export100k_polarity.txt"));
		String line;
		while ((line = br.readLine()) != null) {
			String tweet = line;
			String tweet2 = tweet.replace("#", "").replace("_", "").replace("  ", " ").replace("\"", "").replace("\'", "").toLowerCase().replaceAll("[^\\w\\s\\/\\?;:<>\\.,\\'\\)\\(\\@\\#]", "");
//			String tweet = "I am feeling up and down"; // test line
			String ssout = ss.computeSentimentScores(tweet2);
			System.out.println(ssout);
			List<String> sentences = getSentences(ssout);
			List<Integer> scores = new ArrayList<Integer>();
			for (String sentence : sentences) {
				int score = getScore(ss, sentence);
				scores.add(score);
			}
			bw.write(getPolarity(sum(scores)) + "\t" + tweet2 + "\n");
			System.out.println(getPolarity(sum(scores)));
		}
		bw.close();
		br.close();
	}
	
	public static List<String> getSentences(String tweet) {
		String pattern = "\\[sentence|(\\[\\+1\\sn)";
		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(tweet);
		
		ArrayList<String> sentences = new ArrayList<String>();
		tweet = tweet.substring(5);
		while (m.find()) {
			if (m.group().endsWith("e")) {
				String sentence = tweet.substring(0, tweet.indexOf(m.group()));
				sentences.add(sentence);
				tweet = tweet.substring(tweet.indexOf(m.group())+16);
			} else if (m.group().endsWith("n")) {
				String sentence = sentences.get(sentences.size()-1);
				sentence = sentence.concat(tweet.substring(tweet.indexOf(m.group()),33));
				sentences.remove(sentences.size()-1);
				sentences.add(sentence);
			}
		}
		
		return sentences;
	}
	
	public static List<Integer> getScores(String scores) {
		//System.out.println(scores);
		String pattern = "(\\[-?[1-5]\\])|(\\[(\\+?-?)[0-2]\\sb)|(\\[-?[0-1]\\se)|(\\[\\*-0.5\\sa)|(\\[(-|\\+)?1\\sm)|(\\[(-|\\+)0.6\\sp)|(\\[\\+1\\sn)|(\\[=0\\sn)|(\\[(-|\\+)0.6\\ss)";
		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(scores);
		
		ArrayList<Integer> int_scores = new ArrayList<Integer>();
		
		// TODO: 
		// * add idioms, expressions, etc. (can't wait, blood out of a stone)
		while (m.find()) {
//			System.out.println(m.group().substring(1,m.group().length()-1));
			if (m.group().endsWith("b")) { // booster
				int prev = int_scores.size() - 1;
				int prevScore = int_scores.get(prev);
				String score = m.group().substring(1,m.group().length()-2);
				int booster;
				if (score.startsWith("+-")) {
					booster = Integer.parseInt(m.group().substring(2,m.group().length()-2));
				} else {
					booster = Integer.parseInt(m.group().substring(1,m.group().length()-2));
				}
				prevScore += booster;
				int_scores.remove(prev);
				int_scores.add(prevScore);
			} else if (m.group().endsWith("e")) { // emoticon
				int emoticon = Integer.parseInt(m.group().substring(1,m.group().length()-2));
				int_scores.add(emoticon);
			} else if (m.group().endsWith("a")) { // negation (approx. negated multiplier)
				int prev = int_scores.size() - 1;
				int prevScore = int_scores.get(prev);
				prevScore *= -0.5;
				int_scores.remove(prev);
				int_scores.add(prevScore);
			} else if (m.group().endsWith("m")) { // multiple positive/negative words
				String str_multiple = m.group().substring(1,m.group().length()-2);
//				str_multiple = str_multiple.replace("+", "");
				int multiple = Integer.parseInt(str_multiple);
				int_scores.add(multiple);
			} else if (m.group().endsWith("p")) { // punctuation emphasis !!!
//			    String str_punctuation = m.group().substring(1,m.group().length()-2);
			    if (sum(int_scores) > 0) {
			    	int_scores.add(1);
			    } else if (sum(int_scores) < 0) {
			    	int_scores.add(-1);
			    }
			} else if (m.group().endsWith("s")) { // spelling emphasissssss
			    String str_spelling = m.group().substring(1,m.group().length()-2);
			    double score = Double.parseDouble(str_spelling);
			    if (score > 0) {
			    	int_scores.add(1);
			    } else {
			    	int_scores.add(-1);
			    }
			} else if (m.group().endsWith("1 n")) { // question marks reduce negativity
				if (sum(int_scores) < 0) {
			    	int_scores.add(1);
			    }
			} else if (m.group().startsWith("[=0")) { // negation [=0 negation
				int prev = int_scores.size() - 1;
				int prevScore = 0;
				int_scores.remove(prev);
				int_scores.add(prevScore);
			} else {			
				int_scores.add(Integer.parseInt(m.group().substring(1,m.group().length()-1)));
			}
		}
		
		return int_scores;
	}
	
	public static int sum(List<Integer> scores) {
		int sum = 0;
		for (int score : scores) {
			sum += score;
		}
		return sum;
	}
	
	public static int getScore(SentiStrength ss, String sentence) {
		ArrayList<Integer> scores = (ArrayList<Integer>) getScores(sentence);
		int score = 0;
		for (int i : scores) {
			score += i;
		}
		
		return score;
	}
	
	public static int getPolarity(int score) {
		if (score < 0) {
			return POLARITIES[0];
		} else if (score == 0) {
			return POLARITIES[1];
		} else {
			return POLARITIES[2];
		}
	}
}
