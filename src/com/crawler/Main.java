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
		
		SpotifyUserCrawler spotify = new SpotifyUserCrawler(true, "BQBd7RGzrKYvnGUNH0V9xKYwgnw4jcsqUU2XjkI3Kop6WOwvrF042VgtUI6LlA0yOCOU-jfhqTKjyKTzgBe_PYl77ogmYKq45fvxhMjoI6FXOE1uPnjDj4zBs12D74wu7QfeF4q733jfr9fjBiyz2r9GLiB5fVrXqONuuxgnSE2zXvfMhttqS--i0cT31D4n83tio-Y", urlSQL, usernameSQL, passwordSQL, databaseSQL);
		
		//SpotifyUserCrawler spotify = new SpotifyUserCrawler(false, "BQCcAV-eN23Ppnd_nC_OcKuLmSVwphWUoxD1v9hG1P6VazWaFeySDqtEaXKU4wKsvDeEpm2cbOS4lj5i5q34Ns9DfBkHVECi-ClBlihyLfBVXXelm_rH1k9OPNCbosuTWhdochRqHukHlBk8dITsHPIZw9mB", urlSQL, usernameSQL, passwordSQL, databaseSQL);
		
		
		//SpotifyUserCrawler spotify = new SpotifyUserCrawler(false, "BQDIFTqUDFq_gnZRooDTe4dWbWR7HFNg5QFYRjk_eK4fX-P4T0Ph0MdpgE7E6BITDfxps2VjWn4VuoiaspVkpVlie0_l3ot2VJuz1Xt17ZiNI-AwF1PWfDJLTPW0unwVCgtTt8wpN42pwq1CMhLI5LSbaUu4r4-PgSi6cme16Qg6KIZXuwvkExyndEMLh43ZT3dpv5Ahr6ZQMhZ4mI32Lft4UyXm8PRUkpZar_4B5k2aQimRqe7cWmu4PmS91bfiVMi8sqI", urlSQL, usernameSQL, passwordSQL, databaseSQL);
		
		//SpotifyUserCrawler spotify = new SpotifyUserCrawler(false, "BQBSdViD6TP0wMndmFd8xdBkKNNAXW2ZTGZgNZLhNBAIiK9jxP7FNZ5E8qO0pTIjjxnstYPxTKIsukSInBnUFhaeMp7LoA5tSQL7k_SYq8qmTfTpNpK9cWHS5ZuEpm6O2TZpMUdge-7v1RwpHOsYn8JSW-a5b9pflumNS-ABXy8YM6l-C_c-AM0_7d8MlhlXgg", urlSQL, usernameSQL, passwordSQL, databaseSQL);
		
		List<Integer> idsSpotify = spotify.Start();
		
		//idsDeezer.removeAll(idsSpotify);
		//idsDeezer.addAll(idsSpotify);
		
		MostTermsUsedCalculator calc = new MostTermsUsedCalculator(null,null,null,null);
		
		//Convert songIDs into LyricsIds
		String lyricIds = calc.startSongIds(idsSpotify);
		
		System.out.println("lyrics:"+lyricIds);
		
		//Get relevant terms from example lyrics
		List<String> terms = calc.startLyricsIds(lyricIds, 1000, 2.0);
		System.out.println("Terms:");
		
		
		for(String term : terms) {
			System.out.print("'"+term+"', ");
		}
		//Init QBE searcher
		DocumentSearchByTerms searcher = new DocumentSearchByTerms(false, urlSQL, usernameSQL, passwordSQL, databaseSQL);
		System.out.println("");
		
		//Get lyrics id From spotifyIds
		searcher.Search(terms, idsSpotify, 10);
		System.out.println("Recomends:");
		
		//Search with terms to relevant documents
		List<Integer> songIds = searcher.Search(terms, idsSpotify, 10);
		
		System.out.println("SongsId:");
		
		for(int id : songIds) {
			System.out.print("'"+id+"', ");
		}
	}

}
