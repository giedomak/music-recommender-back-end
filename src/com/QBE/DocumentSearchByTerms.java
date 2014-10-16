package com.QBE;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class DocumentSearchByTerms {
	private Connection MySQLCon = null;
	private String urlSQL = null;
	private String usernameSQL = null;
	private String passwordSQL = null;
	private String databaseSQL = null;
	private Boolean debug = false;
	
	public DocumentSearchByTerms(Boolean debug, String urlSQL, String usernameSQL, String passwordSQL, String databaseSQL) {
		this.urlSQL = urlSQL;
		this.usernameSQL = usernameSQL;
		this.passwordSQL = passwordSQL;
		this.databaseSQL = databaseSQL;
		this.debug = debug;
		
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
			MySQLCon = DriverManager.getConnection("jdbc:mysql://178.62.207.179/2id26?user=root&password=Aarde-Rond-1");
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			
			e.printStackTrace();
		}
	}
	
	public void Search(List<String> terms, List<Integer> songIds, int limit) {
		String listTerms = null;
		
		for(String term : terms) {
			if(listTerms != null && !terms.isEmpty()) {
				listTerms += ", '"+term+"'";
			} else {
				listTerms = "'"+term+"'";
			}
		}
		
		this.Search(listTerms, songIds, limit);
	}
	
	public void Search(String terms, List<Integer> songIds, int limit) {
		try {
			String songIdsString = null;
		
			for(int id : songIds) {
				if(songIdsString != null && !songIdsString.isEmpty()) {
					songIdsString += ", "+Integer.toString(id);
				} else {
					songIdsString = Integer.toString(id);
				}
			}
		
		
			String query = "SELECT termfreqIdf.*, lyric.song_id, song.artist, song.title FROM termfreqIdf JOIN lyric ON lyric.id=termfreqIdf.lyric_id JOIN song ON song.id=lyric.song_id WHERE term IN ( "+terms+" ) AND song_ID NOT IN ( "+songIdsString+" ) GROUP BY lyric_id ORDER BY tf_idf DESC LIMIT ?";
		
			PreparedStatement pst = MySQLCon.prepareStatement(query);
			pst.setInt(1, limit);
			ResultSet result = pst.executeQuery();
			
			while(result.next()) {
				//6+7
				System.out.println(result.getString(6)+" -- "+result.getString(7));
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
}
