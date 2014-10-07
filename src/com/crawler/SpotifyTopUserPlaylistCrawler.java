package com.crawler;

import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import com.crawler.toplist.objects.spotify.User;
import com.crawler.toplist.objects.spotify.UserPlaylist;
import com.google.gson.Gson;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class SpotifyTopUserPlaylistCrawler {
	private String accessToken;
	private Connection MySQLCon = null;
	private String urlSQL = null;
	private String usernameSQL = null;
	private String passwordSQL = null;
	private String databaseSQL = null;
	
	public SpotifyTopUserPlaylistCrawler(String OAuthToken, String urlSQL, String usernameSQL, String passwordSQL, String databaseSQL) {
		/*try {
			Class.forName("com.mysql.jdbc.Driver");
			MySQLCon = DriverManager.getConnection(urlSQL,usernameSQL, passwordSQL);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
		this.accessToken = OAuthToken;
		this.urlSQL = urlSQL;
		this.usernameSQL = usernameSQL;
		this.passwordSQL = passwordSQL;
		this.databaseSQL = databaseSQL;
	}
	
	public void Start(String userId) {
		try {
			//Init HTTP Client
			CloseableHttpClient httpclient = HttpClients.createDefault();
		
			//INIT http get client
			HttpGet httpget = new HttpGet("https://api.spotify.com/v1/users/tvliet/playlists");
		
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
			SpotifyTopPlayListCrawler spotifyPlayListCrawer = new SpotifyTopPlayListCrawler(this.accessToken, this.urlSQL,this.usernameSQL, this.passwordSQL, this.databaseSQL);
			
			//Loop trough each users playlist and retrieve it.
			for(com.crawler.toplist.objects.spotify.PlaylistSimple item : playlists.getItems()) {
				
				System.out.println("");
				System.out.println("---------------- "+item.getName()+" ----------------");
				System.out.println("");
				
				//Get owner out item
				User owner = item.getOwner();
				
				//Start playlist crawler
				spotifyPlayListCrawer.Start(item.getId(), owner.getId());
			}
		
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
