package lyriccrawler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class LyricCrawler {

	public static void main(String[] args) {
		new LyricCrawler();
	}
	
	public LyricCrawler() {
		try {
			List<Song> songs = new ArrayList<Song>();
			//songs.add(new Song("Pharrell Williams", "Happy"));
			//songs.add(new Song("Pitbull Feat. John Ryan", "Fireball"));
			//songs.add(new Song("Ariana Grande Featuring Zedd", "Break Free"));
			//songs.add(new Song("Magic!", "Rude"));
			//songs.add(new Song("Sam Smith", "Stay With Me"));
			
			Connection connection;
		
			connection = DriverManager.getConnection("jdbc:mysql://178.62.207.179/2id26?"
					+ "user=root&password=Aarde-Rond-1");
		
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery("SELECT id, artist, title FROM song WHERE id NOT IN (SELECT song_id FROM lyric) LIMIT 500");
			
			while (resultSet.next()) {
				int id = resultSet.getInt("id");
				String artist = resultSet.getString("artist");
				String title = resultSet.getString("title");
				songs.add(new Song(id, artist, title));
			}
			
			PreparedStatement insertLyricStatement = connection
			          .prepareStatement("INSERT INTO lyric VALUES (default, ?, ?, ?)");
			
			for (Song song : songs) {
				System.out.println("[Searching lyrics.wikia.com for " + song.getArtist() + " - " + song.getTitle() + "]");
				
				String lyric = WikiaCrawler.getLyrics(song);
				if (!lyric.equals("")) {
					System.out.println("Found!");
					
					insertLyricStatement.setInt(1, song.getId());
					insertLyricStatement.setString(2, "wikia");
					insertLyricStatement.setString(3, lyric);
					insertLyricStatement.executeUpdate();
				}
				else {
					System.out.println("No lyric found");
				}
			}
			
			for (Song song : songs) {
				System.out.println("[Searching lyricsnmusic.com for " + song.getArtist() + " - " + song.getTitle() + "]");
				
				String lyric = LyricsNMusicCrawler.getLyrics(song);
				if (!lyric.equals("")) {
					System.out.println("Found!");
					
					insertLyricStatement.setInt(1, song.getId());
					insertLyricStatement.setString(2, "lyricsnmusic.com");
					insertLyricStatement.setString(3, lyric);
					insertLyricStatement.executeUpdate();
				}
				else {
					System.out.println("No lyric found");
				}
			}	
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
