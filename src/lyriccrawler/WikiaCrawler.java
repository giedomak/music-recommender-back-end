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

public class WikiaCrawler {

	public WikiaCrawler() {
		// TODO Auto-generated constructor stub
	}
	
	public static String getLyrics(Song song) {
		String lyricUrl = "";
		String lyricText = "";
		try {
			URI uri = new URI("http", "lyrics.wikia.com", "/api.php", "artist=" + song.getArtist() + "&song=" + song.getTitle() + "&fmt=xml", null);
			
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(uri.toURL().openStream());
			lyricUrl = document.getElementsByTagName("url").item(0).getTextContent();
			
			org.jsoup.nodes.Document lyricPage = Jsoup.connect(lyricUrl).get();
			org.jsoup.select.Elements lyrics = lyricPage.select(".lyricbox");
			lyricText = lyrics.text();
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return lyricText;
	}

}
