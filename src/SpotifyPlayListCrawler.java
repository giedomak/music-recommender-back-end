import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import com.crawler.toplist.objects.Playlist;
import com.crawler.toplist.objects.Track;
import com.google.gson.Gson;


public class SpotifyPlayListCrawler {
	private String accessToken;
	
	public SpotifyPlayListCrawler(String OAuthToken) {
		this.accessToken = OAuthToken;
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
			Playlist playlist = gson.fromJson(content,com.crawler.toplist.objects.Playlist.class);
			
			//Out number of songs in list
			System.out.println("Number: "+playlist.getTotal());
			
			//Loop trough each item in playlist 
			for(com.crawler.toplist.objects.PlaylistItem itemPlaylist : playlist.getItems()){
				//Get track out item
				Track track = itemPlaylist.getTrack();
				System.out.println(track.getArtist()+" -- "+track.getName());
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
