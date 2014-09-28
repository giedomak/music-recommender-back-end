

public class main {
	
	public static void main(String[] args) {
		
		//TODO: Request new one each time
		//Now go to: https://developer.spotify.com/web-api/console/get-playlists and request a token and copy it here.
		String accessToken = "BQASu5GrAs0JOPoEDP0L3VyaBjoWDjZK05hwyQS72uVP0f7Q9ATqFEsDqsKJED4psw_g_3_0XENvwfAVNf7P3KdOChlOQ_TTVhY--ENII2VSLxPFad0k4Tnw7ZqcC-ye8Pr0G9dLdIskT1DK8QUHPQ";

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


