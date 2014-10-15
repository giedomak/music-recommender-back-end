

	package com.crawler;

	import java.io.IOException;
import java.sql.Connection;
import java.util.List;

	import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

	import com.crawler.toplist.objects.deezer.User;
import com.google.gson.Gson;


	public class DeezerUserCrawler {
		private String accessToken;
		private Connection MySQLCon = null;
		private String urlSQL = null;
		private String usernameSQL = null;
		private String passwordSQL = null;
		private String databaseSQL = null;
		
		public DeezerUserCrawler(String OAuthToken, String urlSQL, String usernameSQL, String passwordSQL, String databaseSQL) {
			this.accessToken = OAuthToken;
			this.urlSQL = urlSQL;
			this.usernameSQL = usernameSQL;
			this.passwordSQL = passwordSQL;
			this.databaseSQL = databaseSQL;
		}
		
		public List<Integer> Start() {
			try {
				//Init HTTP Client
				CloseableHttpClient httpclient = HttpClients.createDefault();
			
				//INIT http get client
				HttpGet httpget = new HttpGet("http://api.deezer.com/user/me?access_token="+this.accessToken);
			
				//INIT Respone and execute request 
				CloseableHttpResponse response = httpclient.execute(httpget);
			
				//Get response
				HttpEntity entity = response.getEntity();
			
				//Convert IOStream to String
				String content = IOUtils.toString(entity.getContent(), "UTF-8");
				
				//Init Json parser
				Gson gson = new Gson();
				
				//Convert json into User object
				User user = gson.fromJson(content,com.crawler.toplist.objects.deezer.User.class);
				
				//Init Userplaylist Crawler
				DeezerUserPlaylistCrawler deezerUserPlayListCrawler = new DeezerUserPlaylistCrawler(this.accessToken, this.urlSQL,this.usernameSQL, this.passwordSQL, this.databaseSQL);
				
				//Start playlist crawler
				return deezerUserPlayListCrawler.Start(Integer.toString(user.getId()));
				
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			return null;
		}
	}
