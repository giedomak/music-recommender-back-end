package com.crawler;

import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import com.crawler.toplist.objects.spotify.Playlist;
import com.crawler.toplist.objects.spotify.Track;
import com.google.gson.Gson;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class SpotifyTopPlayListCrawler {
	private String accessToken;
	private Connection MySQLCon = null;
	private String urlSQL = null;
	private String usernameSQL = null;
	private String passwordSQL = null;
	private String databaseSQL = null;
	
	public SpotifyTopPlayListCrawler(String OAuthToken, String urlSQL, String usernameSQL, String passwordSQL, String databaseSQL) {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			MySQLCon = DriverManager.getConnection("jdbc:mysql://178.62.207.179/2id26?user=root&password=Aarde-Rond-1");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		this.accessToken = OAuthToken;
		this.urlSQL = urlSQL;
		this.usernameSQL = usernameSQL;
		this.passwordSQL = passwordSQL;
		this.databaseSQL = databaseSQL;
	}
	
	public void Start(String playlistId, String userId) {
		try {
			//Init HTTP Client
			CloseableHttpClient httpclient = HttpClients.createDefault();
		
			//INIT http get client
			HttpGet httpget = new HttpGet("https://api.spotify.com/v1/users/"+userId+"/playlists/"+playlistId+"/tracks");
		
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
						
			//Convert Json into playlist Object
			Playlist playlist = gson.fromJson(content,com.crawler.toplist.objects.spotify.Playlist.class);
			
			//Out number of songs in list
			System.out.println("Number: "+playlist.getTotal());
			
			//Loop trough each item in playlist 
			for(com.crawler.toplist.objects.spotify.PlaylistItem itemPlaylist : playlist.getItems()){
				//Get track out item
				Track track = itemPlaylist.getTrack();
				System.out.println(track.getArtist()+" -- "+track.getName());
				
				PreparedStatement pst = MySQLCon.prepareStatement("SELECT * FROM song WHERE spotifyId = ?");
				pst.setString(1, track.getId());
				ResultSet result = pst.executeQuery();
				
				//Check if song exist.
				if(!result.next()) {
					PreparedStatement insert = MySQLCon.prepareStatement("INSERT INTO song(spotifyId, artist, title, status) VALUES(?,?,?,?)");
            		insert.setString(1, track.getId());
					insert.setString(2, track.getArtist());
					insert.setString(3, track.getName());
					insert.setInt(4,1);
					insert.executeUpdate();
            		
				}
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
