package lyriccrawler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;


import java.net.URL;

import org.jsoup.Jsoup;

import com.google.gson.Gson;

/**
 * Lyrics crawler for LyricsNMusic.com
 * 
 * author: Erik Agterdenbos
 */
public class LyricsNMusicCrawler {

	static class Lyrics {
	    String title;
	    String url;
	}
	
	public LyricsNMusicCrawler() {
	}
	
	public static String getLyrics(Song song) {
		
		// Step 1: search for the song using the API
		String lyricText = "";
		//http://api.lyricsnmusic.com/songs?artist=pharrell%20williams&track=happy
		try {
			URI uri = new URI("http", "api.lyricsnmusic.com", "/songs", "artist=" + song.getArtist() + "&track=" + song.getTitle(), null);
			URL url = uri.toURL();
			Reader reader = new BufferedReader(new InputStreamReader(url.openStream()));
			
			
			Gson gson = new Gson();
	        Lyrics[] searchResult = gson.fromJson(reader, Lyrics[].class);
	        if (searchResult.length > 0) {
	        	// Step 2: retrieve the lyric from their original website
	        	String lyricUrl = searchResult[0].url;
	        	org.jsoup.nodes.Document lyricPage = Jsoup.connect(lyricUrl).get();
	        	org.jsoup.select.Elements lyrics = lyricPage.select("pre[itemprop=description");
				lyricText = lyrics.text();
	        }

	        // return lyrics
	        return lyricText;
			
		} catch (URISyntaxException | IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
