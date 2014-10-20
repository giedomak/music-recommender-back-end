package lyriccrawler;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.jsoup.Jsoup;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * Lyrics crawler for lyrics.wikia.com
 * 
 * author: Erik Agterdenbos
 */
public class WikiaCrawler {

	public WikiaCrawler() {
	}
	
	public static String getLyrics(Song song) {
		// Step 1: search for the song using the API
		String lyricUrl = "";
		String lyricText = "";
		try {
			URI uri = new URI("http", "lyrics.wikia.com", "/api.php", "artist=" + song.getArtist() + "&song=" + song.getTitle() + "&fmt=xml", null);
			
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(uri.toURL().openStream());
			lyricUrl = document.getElementsByTagName("url").item(0).getTextContent();
			
			// Step 2: retrieve the lyric from their original website
			org.jsoup.nodes.Document lyricPage = Jsoup.connect(lyricUrl).get();
			org.jsoup.select.Elements lyrics = lyricPage.select(".lyricbox");
			lyricText = lyrics.text();
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		
		// return lyrics
		return lyricText;
	}
}
