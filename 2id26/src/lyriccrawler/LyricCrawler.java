package lyriccrawler;

import java.util.ArrayList;
import java.util.List;

public class LyricCrawler {

	public static void main(String[] args) {
		new LyricCrawler();
	}
	
	public LyricCrawler() {
		List<Song> songs = new ArrayList<Song>();
		songs.add(new Song("Pharrel Williams", "Happy"));
		songs.add(new Song("Pitbull Feat. John Ryan", "Fireball"));
		songs.add(new Song("Ariana Grande Featuring Zedd", "Break Free"));
		songs.add(new Song("Magic!", "Rude"));
		songs.add(new Song("Sam Smith", "Stay With Me"));
		
		for (Song song : songs) {
			System.out.println("Searching lyrics for " + song.getArtist() + " - " + song.getTitle());
			System.out.println(WikiaCrawler.getLyrics(song));
			
		}
		
	}
	
	
	
}
