package com.QBE;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
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
						listIds += ", "+Integer.toString(id);
				} else {
						listIds = Integer.toString(id);
				}
			}
			
			//System.out.println(listIds);
			
			String query = "SELECT GROUP_CONCAT(id SEPARATOR ',') FROM lyric WHERE song_id IN ( "+listIds+" )";
			
			PreparedStatement pst = MySQLCon.prepareStatement(query);
		
			ResultSet result = pst.executeQuery();
		
			result.first();
			
			return result.getString(1);
				
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public List<String> startLyricsIds(String ids, int nr, double threshold) {
		try {
			String query = "SELECT termsTable.term, SUM(lyric_id) as doc, SUM(termfreqIdfTable.frequency) as occ, termsTable.totalOcc, termsTable.totalDoc, termsTable.idf FROM termfreqIdfTable JOIN termsTable ON termfreqIdfTable.term=termsTable.term WHERE termfreqIdfTable.lyric_id IN ( ? ) GROUP BY termfreqIdfTable.term HAVING (termsTable.totalOcc > SUM(termfreqIdfTable.frequency) AND (termsTable.totalDoc/2) > (SUM(lyric_id)+1)) LIMIT ?";
			
			PreparedStatement pst = MySQLCon.prepareStatement(query);
			List<Double> idfs  = new ArrayList<Double>();
			
			pst.setString(1, ids);
			
			pst.setInt(2, nr);
			ResultSet result = pst.executeQuery();
			int a = 0;
			while(result.next()) {
				if (!idfs.contains(result.getDouble(6))) 
				{
					idfs.add(result.getDouble(6));
				}
				a++;
				//System.out.println(result.getString(1)+"-"+result.getString(6));
			}
			
			double max = Collections.max(idfs);
			double min = Collections.min(idfs);
			double dif = max-min;
			int size = idfs.size();
			
			double thres = (dif/size)*(30);
			double limL = (min+thres);
			double limR = (max-thres);
			
			//System.out.println("max:"+max+"-min:"+min+"-thres:"+thres+"-limL:"+(limL)+"-limR:"+(limR));
			
			result.first();
			int i = 0;
			
			List<String> terms  = new ArrayList<String>();
			while(result.next()) {
				
				
				if ( limL < result.getDouble(6) && result.getDouble(6) < limR) 
				{
					//System.out.println(result.getString(1)+"-"+result.getString(6));
					terms.add(result.getString(1));
					i++;
				}
				
				//System.out.println(result.getString(1)+"-"+result.getString(6));
			}
			
			
			//System.out.print("a:"+a+"i:"+i);
			
			return terms;
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public void startLyricsIdsOld(String ids, int nr, double threshold) {
		try {
			String query = "SELECT termfrequency.term, SUM(frequency) as nr, avgIDF, terms.countnr FROM termfrequency JOIN terms ON terms.term=termfrequency.term WHERE lyric_id IN ( "+ids+" ) AND avgIDF > ? GROUP BY termfrequency.term ORDER BY nr DESC LIMIT ?;";
			
			PreparedStatement pst = MySQLCon.prepareStatement(query);
			
			//pst.setString(1, ids);
			pst.setDouble(1,threshold);
			pst.setInt(2, nr);
			ResultSet result = pst.executeQuery();
		
			while(result.next()) {
				
				System.out.println(result.getString(1));
				
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public List<String> startLyricsIds(List<Integer> ids, int nr, double threshold) {
		String listIds = null;
		
		for(int id : ids) {
			if(listIds != null && !listIds.isEmpty()) {
					listIds += ","+Integer.toString(id);
			} else {
					listIds = Integer.toString(id);
			}
		}
		
		return this.startLyricsIds(listIds, nr, threshold);
	}
}
