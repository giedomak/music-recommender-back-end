

package com.crawler.toplist.objects.deezer;

 //https://api.deezer.com/playlist/160504851/tracks
public class Tracks {
private TracksSimple [] data;
private int total;

public TracksSimple[] getItems(){
	return data;
}
public int getTotal(){
	return total;
}

}

