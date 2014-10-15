package com.QBE;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;


public class MostTermsUsedCalculator {
	private Connection MySQLCon = null;
	private String urlSQL = null;
	private String usernameSQL = null;
	private String passwordSQL = null;
	private String databaseSQL = null;
		
	public MostTermsUsedCalculator(String urlSQL, String usernameSQL, String passwordSQL, String databaseSQL) {
		this.urlSQL = urlSQL;
		this.usernameSQL = usernameSQL;
		this.passwordSQL = passwordSQL;
		this.databaseSQL = databaseSQL;	
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
			MySQLCon = DriverManager.getConnection("jdbc:mysql://178.62.207.179/2id26?user=root&password=Aarde-Rond-1");
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			
			e.printStackTrace();
		}
	}
	
	public String startSongIds(List<Integer> ids) {
		try {
			
			String listIds = null;
			
			for(int id : ids) {
				if(listIds != null && !listIds.isEmpty()) {
						listIds += ","+Integer.toString(id);
				} else {
						listIds = Integer.toString(id);
				}
			}
			
			String query = "SELECT GROUP_CONCAT(id SEPARATOR ',') FROM lyric WHERE song_id IN (?)";
			
			PreparedStatement pst = MySQLCon.prepareStatement(query);
			
			pst.setString(1, listIds);

			ResultSet result = pst.executeQuery();
		
			result.next();
				
			return result.getString(1);
				
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public void startLyricsIds(String ids, int nr, double threshold) {
		try {
			String query = "SELECT termfrequency.term, SUM(frequency) as nr, avgIDF, terms.countnr FROM termfrequency JOIN terms ON terms.term=termfrequency.term WHERE lyric_id IN ( ? ) AND avgIDF < ? GROUP BY termfrequency.term ORDER BY nr DESC LIMIT ?;";
			
			PreparedStatement pst = MySQLCon.prepareStatement(query);
			
			pst.setString(1, ids);
			pst.setDouble(2,threshold);
			pst.setInt(3, nr);
			ResultSet result = pst.executeQuery();
		
			while(result.next()) {
				
				System.out.println(result.getString(1));
				
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void startLyricsIds(List<Integer> ids, int nr, double threshold) {
		String listIds = null;
		
		for(int id : ids) {
			if(listIds != null && !listIds.isEmpty()) {
					listIds += ","+Integer.toString(id);
			} else {
					listIds = Integer.toString(id);
			}
		}
		
		this.startLyricsIds(listIds, nr, threshold);
	}
}
