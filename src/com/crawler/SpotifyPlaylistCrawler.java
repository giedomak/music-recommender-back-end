package com.crawler;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import com.crawler.toplist.objects.spotify.Playlist;
import com.crawler.toplist.objects.spotify.Track;
import com.google.gson.Gson;


public class SpotifyPlaylistCrawler {
	private String accessToken;
	private Connection MySQLCon = null;
	private String urlSQL = null;
	private String usernameSQL = null;
	private String passwordSQL = null;
	private String databaseSQL = null;
	private Boolean debug = false;
	
	public SpotifyPlaylistCrawler(Boolean debug, String OAuthToken, String urlSQL, String usernameSQL, String passwordSQL, String databaseSQL) {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			MySQLCon = DriverManager.getConnection("jdbc:mysql://"+urlSQL+"/"+databaseSQL+"?user="+usernameSQL+"&password="+passwordSQL);
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
	
	public List<Integer> Start(String playlistId, String userId) {
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
			
			if(this.debug) {
				//Out number of songs in list
				System.out.println("Number: "+playlist.getTotal());
			}
			
			//Init list for know id's
			List<Integer> listIds = new ArrayList<Integer>();
			
			//Loop trough each item in playlist 
			for(com.crawler.toplist.objects.spotify.PlaylistItem itemPlaylist : playlist.getItems()){
				//Get track out item
				Track track = itemPlaylist.getTrack();
				
				//Get song by SpotifyID
				PreparedStatement pst = MySQLCon.prepareStatement("SELECT id FROM song WHERE spotifyId = ?");
				pst.setString(1, track.getId());
				ResultSet result = pst.executeQuery();
				
				//Check if song exist.
				if(result.next()) {
					//Add list to found songs
					listIds.add(result.getInt(1));
					if(this.debug) {
						System.out.println("------------------ KNOW -------------------");
					}
				} else {
					//add song to unkowns songs
					PreparedStatement query = MySQLCon.prepareStatement("INSERT INTO unknowSong(spotifyId, artist, title) VALUES(?,?,?)");
					query.setString(1, track.getId());
					query.setString(2, track.getArtist());
					query.setString(3, track.getName());
					query.execute();
				}
				
				if(this.debug) {
					System.out.println(track.getId()+" -- "+track.getArtist()+" -- "+track.getName()+" -- "+track.getPopularity());
				}
			}
			
			return listIds;
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return null;
	}
}