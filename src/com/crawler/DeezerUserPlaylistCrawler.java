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






//import com.crawler.toplist.objects.Deezer.User;
import com.crawler.toplist.objects.deezer.UserPlaylist;
import com.google.gson.Gson;


public class DeezerUserPlaylistCrawler {
	private String accessToken;
	private Connection MySQLCon = null;
	private String urlSQL = null;
	private String usernameSQL = null;
	private String passwordSQL = null;
	private String databaseSQL = null;
	
	public DeezerUserPlaylistCrawler(String OAuthToken, String urlSQL, String usernameSQL, String passwordSQL, String databaseSQL) {
		this.accessToken = OAuthToken;
		this.urlSQL = urlSQL;
		this.usernameSQL = usernameSQL;
		this.passwordSQL = passwordSQL;
		this.databaseSQL = databaseSQL;
	}
	
	public List<Integer> Start(String userId) {
		try {
			//Init HTTP Client
			CloseableHttpClient httpclient = HttpClients.createDefault();
		
			//INIT http get client
			HttpGet httpget = new HttpGet("https://api.deezer.com/user/"+userId+"/playlists?access_token="+this.accessToken);
		
		
			//INIT Respone and execute request 
			CloseableHttpResponse response = httpclient.execute(httpget);
		
			//Get response
			HttpEntity entity = response.getEntity();
		
			//Convert IOStream to String
			String content = IOUtils.toString(entity.getContent(), "UTF-8");
			
			//Init Json parser
			Gson gson = new Gson();
			
			//Convert json into UserPlaylist object
			UserPlaylist playlists = gson.fromJson(content,com.crawler.toplist.objects.deezer.UserPlaylist.class);
			
			//Init playlist Crawler
			DeezerPlayListCrawler deezerPlayListCrawer = new DeezerPlayListCrawler(this.accessToken, this.urlSQL,this.usernameSQL, this.passwordSQL, this.databaseSQL);
			
			//Init ids 
			List<Integer> mainList = new ArrayList<Integer>();
			
			//Loop trough each users playlist and retrieve it.
			for(com.crawler.toplist.objects.deezer.PlaylistSimple item : playlists.getItems()) {
				
				System.out.println("");
				System.out.println("---------------- "+item.getTitle()+" ----------------");
				System.out.println("");
				
					
				//Start playlist crawler
				List<Integer> ids = deezerPlayListCrawer.Start(item.getId());
				
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
