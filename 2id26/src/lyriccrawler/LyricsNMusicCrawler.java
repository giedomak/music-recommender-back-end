package lyriccrawler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;


import java.net.URL;

import com.google.gson.Gson;

public class LyricsNMusicCrawler {

	public LyricsNMusicCrawler() {
		// TODO Auto-generated constructor stub
	}

	public static char[] getLyrics(Song song) {
		//http://api.lyricsnmusic.com/songs?artist=pharrell%20williams&track=happy
		try {
			URI uri = new URI("http", "api.lyricsnmusic.com", "/songs", "artist=" + song.getArtist() + "&track=" + song.getTitle(), null);
			URL url = uri.toURL();
			Reader reader = new BufferedReader(new InputStreamReader(url.openStream()));
			
			Gson gson = new Gson();
			
		} catch (URISyntaxException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
