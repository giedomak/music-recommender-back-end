package com.crawler;

import java.util.List;

import com.QBE.MostTermsUsedCalculator;

public class Main {

	public static void main(String[] args) {
		//TODO: Request new one each time
		//Now go to: https://developer.spotify.com/web-api/console/get-playlists and request a token and copy it here.
		String accessToken = "BQDWwpbWSL7R-OqUTE8_H1YR0Cfbe0DfX14CQfmSFY6rJybxbU1SheSwt1ekMWRO1lKQF81bQcQh71mGyVmMd9omGjSa1hGfFlNPgq7NEpDY9Si-o486zDQgGlBHA57PuvQrycGpnJUPoqnIexcnpW1-7o3WCCSITR6eVGs21wF7RAFRSfu0b6-2mBkBMGQPdJ_ZtZVRuL7e9EoTKzpBpDu_ZvIp0yriwc46_nPvr_U5ehkrcDNJeMRvykjg9Q";
		String urlSQL = "178.62.207.179";
		String usernameSQL =  "root"; 
		String passwordSQL = "Aarde-Rond-1";
		String databaseSQL = "2id26";
		
		/****************************************/
		/** BEGIN CAWLER			           **/
		/****************************************/
				
		//Init Crawler
		//SpotifyTopUserPlaylistCrawler crawler = new SpotifyTopUserPlaylistCrawler(accessToken, urlSQL, usernameSQL, passwordSQL, databaseSQL);
				
		//Start crawler for user tvliet
		//crawler.Start("tvliet");
					
		/****************************************/
		/** END CAWLER			               **/
		/****************************************/
		
		//DeezerUserCrawler deezer = new DeezerUserCrawler("frGmuSLUMY543e1b2988feckKCGt7ed543e1b2989023QpcGsEF", urlSQL, usernameSQL, passwordSQL, databaseSQL);
		
		//List<Integer> idsDeezer = deezer.Start();
		
		SpotifyUserCrawler spotify = new SpotifyUserCrawler("BQCFS12R3VoMy2mxvFvn5tNUgxY8zuU-Esf_qC7hi7F5wR1HbKsZmLQ5Y3q4tESmcffnx0X4vdHpno8hiKK9yWPh0dRzmRWywqgztToCx4y-aKoEl-Q960VhN5nnS2U_G4gfztrvkluX9NPjjMarblBCOrKXmJDf4Nuw47Ve2fPrPUqvDu2bCKjGSvV-7ox-0g", urlSQL, usernameSQL, passwordSQL, databaseSQL);
		
		List<Integer> idsSpotify = spotify.Start();
		
		
		//idsDeezer.removeAll(idsSpotify);
		//idsDeezer.addAll(idsSpotify);
		
		/*for(int id : idsSpotify) {
			System.out.print(id+"-");
		}*/
		
		MostTermsUsedCalculator calc = new MostTermsUsedCalculator(null,null,null,null);
		
		String lyricIds = calc.startSongIds(idsSpotify);
		
		//System.out.println("Test"+lyricIds);
		
		calc.startLyricsIds(lyricIds, 10, 2.0);
	}

}
