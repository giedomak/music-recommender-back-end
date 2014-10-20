package lyricstatistics;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map.Entry;

/**
 * Calculates term frequency for each lyrics and stores those statistics in the database
 * 
 * author: Erik Agterdenbos
 */
public class TermFrequencyCalculator {
	
	public static void main(String[] args) throws SQLException {
		new TermFrequencyCalculator();
	}

	public TermFrequencyCalculator() throws SQLException {
		
		// Create MySQL connection
		Connection connection;
		connection = DriverManager.getConnection("jdbc:mysql://178.62.207.179/2id26?"
				+ "user=root&password=Aarde-Rond-1");
	
		Statement statement = connection.createStatement();
		ResultSet resultSet = statement.executeQuery("SELECT id, text FROM lyric WHERE id NOT IN (SELECT lyric_id FROM termfrequency) LIMIT 1000");
		
		PreparedStatement insertLyricStatement = connection
		          .prepareStatement("INSERT INTO termfrequency VALUES (?, ?, ?, null)");
		
		// Iterate over all lyrics
		while (resultSet.next()) {
			
			System.out.println("<<New lyric>>");
			int lyricId = resultSet.getInt("id");
			String text = resultSet.getString("text").toLowerCase();
			
			HashMap<String, Integer> termFrequency = new HashMap<String, Integer>();
			String[] terms = text.split("[\\p{Punct}\\s]+"); // remove punctuation before splitting
			
			// Iterate over all words in the document and keep a counter for each term
			for (String term : terms) {
				if (termFrequency.containsKey(term)) {
					int newFrequency = termFrequency.get(term) + 1;
					termFrequency.put(term, newFrequency);
				} else if (term.length() <= 20 && term.length() >= 2) { // skip very small or large terms
					termFrequency.put(term, 1);
				}
			}
			
			// Iterate over counter HashTable
			for (Entry<String, Integer> entry : termFrequency.entrySet()) {
				String term = entry.getKey();
				int frequency = entry.getValue();
				
				System.out.println(term + ": " + frequency);
				
				// Insert in to termfrequency table
				insertLyricStatement.setString(1, term);
				insertLyricStatement.setInt(2, lyricId);
				insertLyricStatement.setInt(3, frequency);
				
				insertLyricStatement.addBatch();
			}
		}
		insertLyricStatement.executeBatch();
	}

}
