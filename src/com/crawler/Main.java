package com.crawler;

import java.util.ArrayList;
import java.util.List;

import com.QBE.DocumentSearchByTerms;
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
		
		SpotifyUserCrawler spotify = new SpotifyUserCrawler(true, "BQCsp6s4p8Vulk_EXmpdLd2lShREEuZQYSIZL7cAg4tgKbP0HGmUxWcWTLjA-jp8bUaccp-B-cRfaXgDBfgw_13KSLj93rYrDz0roph-8Fu5foqyJUVfg_MluAGyxKNcB9hIQyKpUVv2kDcueP9tCudh1itC", urlSQL, usernameSQL, passwordSQL, databaseSQL);
		
		List<Integer> idsSpotify = spotify.Start();
		
		//idsDeezer.removeAll(idsSpotify);
		//idsDeezer.addAll(idsSpotify);
		
		MostTermsUsedCalculator calc = new MostTermsUsedCalculator(null,null,null,null);
		
		String lyricIds = calc.startSongIds(idsSpotify);
		
		List<String> terms = calc.startLyricsIds(lyricIds, 1000, 2.0);
		System.out.println("Terms:");
		for(String term : terms) {
			System.out.print("'"+term+"', ");
		}
		
		DocumentSearchByTerms searcher = new DocumentSearchByTerms(false, urlSQL, usernameSQL, passwordSQL, databaseSQL);
		System.out.println("");
		System.out.println("Recomends:");
		searcher.Search(terms, idsSpotify, 10);
	}

}
