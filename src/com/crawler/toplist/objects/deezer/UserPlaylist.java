

package com.crawler.toplist.objects.deezer;


// https://api.deezer.com/user/2529/playlists
 

public class UserPlaylist {
    private PlaylistSimple[] data;
    private int total;
  
    public PlaylistSimple[] getItems() {
    	return data;
    }
    public int getTotal() {
        return total;
    }

    
   
    
    
}
