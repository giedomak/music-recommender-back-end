package com.crawler;

public class Main {

	public static void main(String[] args) {
		//TODO: Request new one each time
		//Now go to: https://developer.spotify.com/web-api/console/get-playlists and request a token and copy it here.
		String accessToken = "BQAj7P-PKk0uqA2reuxOPSMxMzEmGlpYSiEfYmtG2SQX0cUjxqelkbU3GYZQNeWbnFIye5eta2TdzpHmhZElU6gjHWhHR2pPr5BQIzOa_AErPEyqbOasAf5Rm6-QSXJvIQWBoJRMRWVWZYlfswruRA";

		/****************************************/
		/** BEGIN CAWLER			           **/
		/****************************************/
				
		//Init Crawler
		SpotifyUserPlaylistCrawler crawler = new SpotifyUserPlaylistCrawler(accessToken);
				
		//Start crawler for user tvliet
		crawler.Start("tvliet");
					
		/****************************************/
		/** END CAWLER			               **/
				/****************************************/
	}

}
