package com.crawler;

public class Main {

	public static void main(String[] args) {
		//TODO: Request new one each time
		//Now go to: https://developer.spotify.com/web-api/console/get-playlists and request a token and copy it here.
		String accessToken = "BQBS4FSpJRxnLDeBvydQc7PPmbnqUadbn8UoP1KpFBVCGXxI9aHH6xVEOGo5Z2flsNxJladFgzRidydVJ1I_dQ9l7aaC81P1t56u3K9MlSmUGdyqBlZHvJt2qvMzOVRrOOUkPXgbAQRI7jPS2jZsMahZKE3zJtx9foghgmV9_BqWn5vyc5OpysRvJ0aqtfgw0ywxv72j3hXYsz7B1_wvFEJCJfU";
		String urlSQL = "178.62.207.179";
		String usernameSQL =  "root"; 
		String passwordSQL = "Aarde-Rond-1";
		String databaseSQL = "2id26";
		
		/****************************************/
		/** BEGIN CAWLER			           **/
		/****************************************/
				
		//Init Crawler
		SpotifyTopUserPlaylistCrawler crawler = new SpotifyTopUserPlaylistCrawler(accessToken, urlSQL, usernameSQL, passwordSQL, databaseSQL);
				
		//Start crawler for user tvliet
		crawler.Start("tvliet");
					
		/****************************************/
		/** END CAWLER			               **/
				/****************************************/
	}

}
