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

public class LyricsNMusicCrawler {

	static class Lyrics {
	    String title;
	    String url;
	}
	
	public LyricsNMusicCrawler() {
		// TODO Auto-generated constructor stub
	}

	public static String getLyrics(Song song) {
		String lyricText = "";
		//http://api.lyricsnmusic.com/songs?artist=pharrell%20williams&track=happy
		try {
			URI uri = new URI("http", "api.lyricsnmusic.com", "/songs", "artist=" + song.getArtist() + "&track=" + song.getTitle(), null);
			URL url = uri.toURL();
			Reader reader = new BufferedReader(new InputStreamReader(url.openStream()));
			
			//StringBuffer buffer = new StringBuffer();
	        //int read;
	        //char[] chars = new char[1024];
	        //while ((read = reader.read(chars)) != -1)
	        //    buffer.append(chars, 0, read);
			
			Gson gson = new Gson();
	        Lyrics[] searchResult = gson.fromJson(reader, Lyrics[].class);
	        if (searchResult.length > 0) {
	        	String lyricUrl = searchResult[0].url;
	        	org.jsoup.nodes.Document lyricPage = Jsoup.connect(lyricUrl).get();
	        	org.jsoup.select.Elements lyrics = lyricPage.select("pre[itemprop=description");
				lyricText = lyrics.text();
	        }

	        return lyricText;
			
		} catch (URISyntaxException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
