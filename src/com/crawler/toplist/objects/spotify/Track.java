package com.crawler.toplist.objects.spotify;

public class Track {
	private String name;
	private int popularity;
	private String id;
	private ArtistSimple[] artists;
	
	public String getName() {
		return name;
	}

	public int getPopularity(){
		return popularity;
	}
	
	public String getId(){
		return id;
	}
	
	public String getArtist() {
		String name = "";
		for(com.crawler.toplist.objects.spotify.ArtistSimple item : artists){
			
			if(name.length() == 0) {
				name += item.getName();
			} else {
				name += ", "+item.getName();
			}
		}
		return name;
	}
	
}
