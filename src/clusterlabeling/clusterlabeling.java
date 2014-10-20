package clusterlabeling;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Driver;
import java.sql.SQLException;
import java.sql.Statement;


public class clusterlabeling {
		
		public void start() {
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection MySQLCon = DriverManager.getConnection("jdbc:mysql://178.62.207.179/2id26?user=root&password=Aarde-Rond-1");
		
		
		PreparedStatement clusters = MySQLCon.prepareStatement("SELECT cluster_id, GROUP_CONCAT(lyric_id SEPARATOR ',') FROM lyricclustermember group by cluster_id");
				
		ResultSet resultClusters = clusters.executeQuery();
		while (resultClusters.next()) {
			PreparedStatement label = MySQLCon.prepareStatement("select termfrequency.term, (sum(frequency)*avg(termsCor.idf)) as Wtd"
					+ " from termfrequency"
					+ " inner join termsCor"
					+ " on termfrequency.term = termsCor.term"
					+ " where lyric_id IN ( "+resultClusters.getString(2)+" )"
					+ " group by term"
					+ " order by Wtd desc"
					+ " limit 5");
			System.out.println("-----------"+resultClusters.getString(1)+"----------------" );
			ResultSet resultLabel = label.executeQuery();
			while (resultLabel.next()){
				System.out.println(resultLabel.getString(1));
			}
		}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
	}		
		
}