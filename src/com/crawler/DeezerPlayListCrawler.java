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

import com.crawler.toplist.objects.deezer.Playlist;
import com.crawler.toplist.objects.deezer.Tracks;
import com.google.gson.Gson;


public class DeezerPlayListCrawler {
	private String accessToken;
	private Connection MySQLCon = null;
	private String urlSQL = null;
	private String usernameSQL = null;
	private String passwordSQL = null;
	private String databaseSQL = null;
	
	public DeezerPlayListCrawler(String OAuthToken, String urlSQL, String usernameSQL, String passwordSQL, String databaseSQL) {
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
	
	public List<Integer> Start(String playlistId) {
		try {
			//Init HTTP Client
			CloseableHttpClient httpclient = HttpClients.createDefault();
		
			//INIT http get client
			HttpGet httpget = new HttpGet ("https://api.deezer.com/playlist/"+playlistId+"/tracks?access_token="+this.accessToken);
		
			//INIT Respone and execute request 
			CloseableHttpResponse response = httpclient.execute(httpget);
		
			//Get response
			HttpEntity entity = response.getEntity();
		
			//Convert IOStream to String
			String content = IOUtils.toString(entity.getContent(), "UTF-8");
			
			//Init Json parser
			Gson gson = new Gson();
						
			//Convert Json into playlist Object
			Playlist playlist = gson.fromJson(content,com.crawler.toplist.objects.deezer.Playlist.class);
			
			//Init list for know id's
			List<Integer> listIds = new ArrayList<Integer>();
			
			//Out number of songs in list
			System.out.println("Number: "+playlist.getTotal());
			
			//Loop trough each item in playlist 
			for(com.crawler.toplist.objects.deezer.PlaylistItem itemPlaylist : playlist.getItems()){
				
				//Search through know songs in DB.
				PreparedStatement pst = MySQLCon.prepareStatement("SELECT id FROM song WHERE LOWER(artist) LIKE ? AND LOWER(title) LIKE ?");
				pst.setString(1, "%"+itemPlaylist.getArtist().toLowerCase()+"%");
				pst.setString(2, "%"+itemPlaylist.getName().toLowerCase()+"%");
				ResultSet result = pst.executeQuery();
				
				//Check if song exist.
				if(result.next()) {
					//add Id to list of found songs
					listIds.add(result.getInt(1));
					System.out.println("------------------ KNOW -------------------");
				} else {
					//add song to unkowns songs
					PreparedStatement query = MySQLCon.prepareStatement("INSERT INTO unknowSong(artist, title, type) VALUES(?,?,2)");
					query.setString(1, itemPlaylist.getArtist());
					query.setString(2, itemPlaylist.getName());
					query.execute();
				}
				
				System.out.println(itemPlaylist.getId()+" -- "+itemPlaylist.getArtist()+" -- "+itemPlaylist.getName());
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
