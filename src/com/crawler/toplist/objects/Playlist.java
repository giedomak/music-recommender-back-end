package com.crawler.toplist.objects;

public class Playlist {
	private PlaylistItem[] items;
	private int limit;
	private int total;
	
	public int getTotal() {
		return total;
	}
	
	public PlaylistItem[] getItems() {
		return items;
	}
}
