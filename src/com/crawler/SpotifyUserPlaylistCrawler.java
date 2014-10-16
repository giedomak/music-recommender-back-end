package com.crawler;

import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import com.crawler.toplist.objects.spotify.User;
import com.crawler.toplist.objects.spotify.UserPlaylist;
import com.google.gson.Gson;


public class SpotifyUserPlaylistCrawler {
	private String accessToken;
	private Connection MySQLCon = null;
	private String urlSQL = null;
	private String usernameSQL = null;
	private String passwordSQL = null;
	private String databaseSQL = null;
	private Boolean debug = false;
	
	public SpotifyUserPlaylistCrawler(Boolean debug, String OAuthToken, String urlSQL, String usernameSQL, String passwordSQL, String databaseSQL) {
		this.accessToken = OAuthToken;
		this.urlSQL = urlSQL;
		this.usernameSQL = usernameSQL;
		this.passwordSQL = passwordSQL;
		this.databaseSQL = databaseSQL;
		this.debug = debug;
	}
	
	public List<Integer> Start(String userId) {
		try {
			//Init HTTP Client
			CloseableHttpClient httpclient = HttpClients.createDefault();
		
			//INIT http get client
			HttpGet httpget = new HttpGet("https://api.spotify.com/v1/users/"+userId+"/playlists");
		
			//Set accesToken header, use adHeader for a second one.
			httpget.setHeader("Authorization","Bearer "+ this.accessToken);
		
			//INIT Respone and execute request 
			CloseableHttpResponse response = httpclient.execute(httpget);
		
			//Get response
			HttpEntity entity = response.getEntity();
		
			//Convert IOStream to String
			String content = IOUtils.toString(entity.getContent(), "UTF-8");
			
			//Init Json parser
			Gson gson = new Gson();
			
			//Convert json into UserPlaylist object
			UserPlaylist playlists = gson.fromJson(content,com.crawler.toplist.objects.spotify.UserPlaylist.class);
			
			//Init playlist Crawer
			SpotifyPlaylistCrawler spotifyPlaylistCrawer = new SpotifyPlaylistCrawler(this.debug, this.accessToken, this.urlSQL,this.usernameSQL, this.passwordSQL, this.databaseSQL);
			
			//Init ids 
			List<Integer> mainList = new ArrayList<Integer>();
			
			
			//Loop trough each users playlist and retrieve it.
			for(com.crawler.toplist.objects.spotify.PlaylistSimple item : playlists.getItems()) {
				
				if(this.debug) {
					System.out.println("");
					System.out.println("---------------- "+item.getName()+" ----------------");
					System.out.println("");
				}
				
				//Get owner out item
				User owner = item.getOwner();
				
				
				
				//Start playlist crawler
				List<Integer> ids = spotifyPlaylistCrawer.Start(item.getId(), owner.getId());
				
				mainList.removeAll(ids);
				mainList.addAll(ids);
			}
			
			return mainList;
		
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
}