package com.crawler.toplist.objects.deezer;

public class PlaylistItem {
	private int id;
	private String title;
	private Artist artist;
	
	public int getId() {
		return id;
	}
	
	public String getName() {
		return title;
	}
	
	public String getArtist() {
		return artist.getName();
	}
}
