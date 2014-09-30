package com.crawler.toplist.objects.spotify;

public class UserPlaylist {
	private PlaylistSimple[] items;
	private int total;
	private String name;
	
	public PlaylistSimple[] getItems() {
		return items;
	}
	
	public int getTotal() {
		return total;
	}
	
	public String getName() {
		return name;
	}
}
