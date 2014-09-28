package com.crawler.toplist.objects;

public class Track {
	private String name;
	private int popularity;
	private String id;
	private String added_at;
	private ArtistSimple[] artists;
	
	public String getName() {
		return name;
	}
	
	public String getArtist() {
		String name = "";
		for(com.crawler.toplist.objects.ArtistSimple item : artists){
			name += item.getName();
		}
		return name;
	}
	
}
