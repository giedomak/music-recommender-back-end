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
			//Turn list into String with , as separator.
			for(int id : ids) {
				if(listIds != null && !listIds.isEmpty()) {
						listIds += ", "+Integer.toString(id);
				} else {
						listIds = Integer.toString(id);
				}
			}
			
			//System.out.println(listIds);
			
			//Query for all the lyrics by the list of songIds
			String query = "SELECT GROUP_CONCAT(id SEPARATOR ',') FROM lyric WHERE song_id IN ( "+listIds+" )";
			
			PreparedStatement pst = MySQLCon.prepareStatement(query);
		
			ResultSet result = pst.executeQuery();
		
			//Set result pointer to first and only item.
			result.first();
			
			return result.getString(1);
				
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public List<String> startLyricsIds(String ids, int nr, double threshold) {
		try {
			
			String query = "SELECT termsTable.term, SUM(lyric_id) as doc, SUM(termfreqIdfTable.frequency) as occ, termsTable.totalOcc, termsTable.totalDoc, termsTable.idf FROM termfreqIdfTable JOIN termsTable ON termfreqIdfTable.term=termsTable.term WHERE termfreqIdfTable.lyric_id IN ( "+ids+" ) GROUP BY termfreqIdfTable.term HAVING (termsTable.totalOcc > SUM(termfreqIdfTable.frequency)) LIMIT ?";
			
			PreparedStatement pst = MySQLCon.prepareStatement(query);
			List<Double> idfs  = new ArrayList<Double>();

			pst.setInt(1, nr);
			
			ResultSet result = pst.executeQuery();
			int a = 0;
			while(result.next()) {
				
				//Put IDF values in List if it is not already in it.
				if (!idfs.contains(result.getDouble(6))) 
				{
					idfs.add(result.getDouble(6));
				}
				a++;
				//System.out.println(result.getString(1)+"-"+result.getString(6));
			}
			
			//Get the max and min IDF
			double max = Collections.max(idfs);
			double min = Collections.min(idfs);
			
			//Determ difference between max and min and the number of different idf values.
			double dif = max-min;
			int size = idfs.size();
			
			//Calculatie treshold based on size of found terms and calculated difference.
			double thres = (dif/a)*((a/2)-2);
			
			//If size of terms is less then 0 set threshold 0.
			if(a < 3) {
				thres = (dif/size)*(0);
			}
			
			//Determ left and right limits.
			double limL = (min+thres);
			double limR = (max-thres);
			
			//System.out.println("max:"+max+"-min:"+min+"-thres:"+thres+"-limL:"+(limL)+"-limR:"+(limR));
			
			//Reset result pointer.
			result.first();
			
			List<String> terms  = new ArrayList<String>();
			while(result.next()) {
				
				//Is the IDF value between the limits put the term in de list of found terms.
				if ( limL <= result.getDouble(6) && result.getDouble(6) <= limR) 
				{
					//System.out.println(result.getString(1)+"-"+result.getString(6));
					terms.add(result.getString(1));
				}
				
				//System.out.println(result.getString(1)+"-"+result.getString(6));
			}
			
			
			//System.out.print("a:"+a+"i:"+i);
			
			//Return found terms.
			return terms;
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	

	public List<String> startLyricsIds(List<Integer> ids, int nr, double threshold) {
		String listIds = null;
		//Turn list into string with , as separator
		for(int id : ids) {
			if(listIds != null && !listIds.isEmpty()) {
					listIds += ","+Integer.toString(id);
			} else {
					listIds = Integer.toString(id);
			}
		}
		//Start the term finders with the string.
		return this.startLyricsIds(listIds, nr, threshold);
	}
}
